// We support maximum of 5 users on a cycle
const MAX_CYCLE_PARTICIPANTS = 5;

exports.findMinimumCycle = async (userId) => {

};

exports.handleUserDeletion = async (userId) => {
    
};

exports.handleProductDeletion = async (productId, userId) => {
    const User = require('../models/User');
    try {
        const users = await User.find();
        users.forEach(user => {
            user.wishlist.forEach(wish => {
                const index = wish.items.indexOf(productId);
                if (index > -1) { wish.items.splice(index, 1); }
            });
            const updatedUser = await User.findByIdAndUpdate(userId, user, { new: true });
            return updatedUser;
        });
    } catch (error) {
        console.log(error);
        res.status(400).json({"error":"problem deleting product from users wishlists"})
    }
};
