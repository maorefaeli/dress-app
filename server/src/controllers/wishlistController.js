const User = require('../models/User');
const PendingCycle = require('../models/PendingCycle');
const Product = require('../models/Product');
const RentController = require('./rentController');

// Maximum depth of users in a cycle
const MAX_CYCLE_PARTICIPANTS = 5;

// logged in user added new item to wishlist
// Find all cycles in depth 5.
// For each cycle found:
//      check if cycle exist via hash code (participants users hashing)
//          if exist then skip
//      check all products available at some time
//          if available then add PendingCycle

const findCycles = async (userId) => {

};

/**
 * Add a product to a user's wishlist and find new cycles
 */
exports.addProductToWishlist = async (userId, productId) => {
    const user = await User.findById(userId);
    if (!user) {
        throw new Error('Invalid user');
    };
    
    const product = await Product.findById(productId);
    if (!product) {
        throw new Error('Invalid product');
    };

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

                    // If it was requested already, null it and its dates
                    if (participant.requestedProduct.equals(productId)) {
                        participant.requestedProduct = null;
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

const isOrderValid = (product, fromDate, toDate) => {
    if (product.fromdate > fromDate || product.todate < toDate) return false;

    if (product.rentingDates && product.rentingDates.length) {
        for (let index = 0; index < product.rentingDates.length; index++) {
            const rt = product.rentingDates[index];
            if (!(rt.todate < fromDate && rt.fromdate > toDate)) {
                return false;
            }
        }
    }

    return true;  
};

const validateCycle = async (cycleId) => {
    const cycle = await PendingCycle.findById(cycleId).populate('requestedProduct');

    let isCycleValid = true;
    let isCycleChanged = false;

    cycle.participants.forEach(participant => {
        // If there still a participant not requested a product the cycle is not completed
        if (!participant.requestedProduct) {
            isCycleValid = false;
            return;
        }

        // If order not valid due to dates, reset the requested product
        if (!isOrderValid(participant.requestedProduct, participant.fromDate, participant.toDate)) {
            isCycleValid = false;
            
            participant.requestedProduct = null;
            participant.fromDate = null;
            participant.toDate = null;
            isCycleChanged = true;
        }

        // Save the product id and not the entire entity
        participant.requestedProduct = participant.requestedProduct._id;
    });

    if (isCycleChanged) {
        await PendingCycle.findOneAndUpdate(cycle._id, cycle);
    } else if (isCycleValid) {
        await Promise.all(participant.map(p => RentController.addRent(p.user, p.requestedProduct, p.fromDate, p.toDate)));
        await PendingCycle.findByIdAndDelete(cycle._id);
        console.log("Cycle", cycle, "is completed and deleted");
    }
};

exports.requestProductOnCycle = async (cycleId, userId, productId, fromDate, toDate) => {
    const cycle = await PendingCycle.findById(cycleId);
    const product = await Product.findById(productId);

    if (isOrderValid(product, fromDate, toDate)) {
        cycle.participants.forEach(p => {
            if (p.user.equals(userId) && p.products.includes(productId)) {
                p.requestedProduct = productId;
                p.fromDate = fromDate;
                p.toDate = toDate;
            }
        });
        await PendingCycle.findByIdAndUpdate(cycleId, cycle);
        await validateCycle(cycle);
    }
};
