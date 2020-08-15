const User = require('../models/User');
const PendingCycle = require('../models/PendingCycle');
const Product = require('../models/Product');

// Maximum depth of users in a cycle
const MAX_CYCLE_PARTICIPANTS = 5;


/**
 * Add a product to a user's wishlist and find new cycles
 */
exports.addProductToWishlist = async (userId, productId) => {
    const user = await User.findById(userId);
    const product = await Product.findById(productId);
    if (!user || !product) return;

    if (!user.wishlist) {
        user.wishlist = [];
    }

    let isFound = false;
    user.wishlist.forEach(wish => {
        if (wish.user.equals(product.user)) {
            isFound = true;

            if (!wish.products) {
                wish.products = [];
            }

            if (!wish.products.includes(productId)) {
                wish.products.push(productId);
            }
        }
    })

    if (!isFound) {
        user.wishlist.push({
            user: product.user,
            products: [productId]
        });
    }

    await User.findByIdAndUpdate(userId, user);
    await findCycles(userId);
};

const removeProductFromPendingCycles = async (userId, productId) => {
    const cycles = await PendingCycle.find({ 'participants.products': { $in: [productId] } });
    const cyclesToUpdate = [];
    const cyclesToDelete = [];

    cycles.forEach(cycle => {
        cycle.participants.forEach(participant => {
            // If user id is sent and is not a match, skip it
            if (userId && !participant.user.equals(userId)) return;

            if (participant.products.includes(productId)) {
                // Remove the product
                participant.products = participant.products.filter(p => !p.equals(productId))

                // If not more products, mark for deletion
                if (participant.products.length === 0) {
                    cyclesToDelete.push(cycle);

                } else {
                    cyclesToUpdate.push(cycle);

                    // If it was accepted already, null it and its dates
                    if (participant.acceptedProduct.equals(productId)) {
                        participant.acceptedProduct = null;
                        participant.fromDate = null;
                        participant.toDate = null;
                    }
                }
            }
        });
    });

    if (cyclesToUpdate.length) {
        await Promise.all(cyclesToUpdate.map(cycle => PendingCycle.findByIdAndUpdate(cycle._id, cycle)));
    }

    if (cyclesToDelete.length) {
        await PendingCycle.deleteMany({ _id: { $in: cyclesToDelete.map(cycle => cycle._id)} });
    }
};

const removeProductFromUserWishlist = (user, productId) => {
    if (!user || !user.wishlist) return;

    // Remove product from the wishlist it's on
    user.wishlist.forEach(wish => {
        wish.products = wish.products.filter(p => !p.equals(productId))
    })

    // If the user's wishlist is empty, delete it
    user.wishlist = user.wishlist.filter(wish => wish.products.length > 0);

    // Delete the wishlist if needed
    if (user.wishlist.length === 0) {
        user.wishlist = null;
    }

    // Return Promise when user is updated
    return User.findByIdAndUpdate(userId, user);
};

/**
 * Remove a product from a user's wishlist and adjust pending cycles
 */
exports.removeProductFromWishlist = async (userId, productId) => {
    if (!userId || !productId) return;

    await removeProductFromUserWishlist(await Users.find(userId), productId);
    await removeProductFromPendingCycles(userId, productId);
};

/**
 * When a product is deleted from the system, take it out from the existing cycles
 * and possibly delete them if there is no other product to replace it
 */
exports.handleProductDeletion = async (productId) => {
    if (!productId) return;

    // Find all users that contain this product in their wishlist
    const users = await Users.find({
        wishlist: { $elemMatch: {
            products: { $in: [productId] }
        }}
    });
    
    await Promise.all(users.map(user => removeProductFromUserWishlist(user, productId)));
    await removeProductFromPendingCycles(null, productId);
};

/**
 * When a user is deleted from the system, delete all the cycles he was participated at
 */
exports.handleUserDeletion = async (userId) => { 
    await PendingCycle.deleteMany({ 'participants.user': { $in: [userId] } });
};
