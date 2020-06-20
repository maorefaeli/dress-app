// We support maximum of 5 users on a cycle
const MAX_CYCLE_PARTICIPANTS = 5;

exports.findMinimumCycle = async (userId) => {

};

exports.handleUserDeletion = async (userId) => {
    
};

exports.addItemToWishList = async (userId, productId) => {
    const User = require('../models/User');
    const Product = require('../models/Product');
    try {
        const user = await User.findById(userId);
        let { productUserId } = await Product.findById(productId);
        await Promise.all(user.wishlist.forEach(wish => {
            if (wish.user._id.equals(productUserId)) {
                if (!(wish.items.includes(productId))) {
                    wish.items.push(productId);
                    const updateUser = await User.findByIdAndUpdate(userId, user, { new: true });
                    return updateUser;
                }
            }
        }));
        let newWish = {
            "user":productUserId,
            "items":[productId]
        };
        user.wishlist.push(newWish);
        const updateUser = await User.findByIdAndUpdate(userId, user, { new: true });
        return updateUser;
    } catch (error) {
        console.log(error);
        res.status(400).json({"error":"problem adding product to wishlist"});
    }
};

exports.handleProductDeletion = async (productId, userId) => {
    const User = require('../models/User');
    try {
        const users = await User.find();
        
        await Promise.all(users.map(async user => {
            user.wishlist.forEach(wish => {
                const index = wish.items.indexOf(productId);
                if (index > -1) { wish.items.splice(index, 1); }
            });
            const updatedUser = await User.findByIdAndUpdate(userId, user, { new: true });
            return updatedUser;
        }));
    } catch (error) {
        console.log(error);
        res.status(400).json({"error":"problem deleting product from users wishlists"})
    }
};
