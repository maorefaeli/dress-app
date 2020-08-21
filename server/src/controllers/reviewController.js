const Product = require('../models/Product');
const Review = require('../models/Review');
const User = require('../models/User');

const getReviewedUserId = async (productId) => {
    let product = await Product.findById(productId);
    const userId = product.user;
    return userId;
};

exports.updateUserRating = async (productId, score) => {
    const userId = await getReviewedUserId(productId);
    const newUser = await User.findByIdAndUpdate(userId, { $inc: { "reviewQuantity": 1, "reviewSum": score } });
    return newUser;
};
