const Product = require('../models/Product');
const Review = require('../models/Review');
const User = require('../models/User');

const getReviewedUserId = async (productId) => {
    let product = await Product.findById(productId);
    const userId = product.user;
    return userId;
};

//  TODO: refactor the calculation of user's rating
exports.updateUserRating = async (productId, score) => {
    const userId = await getReviewedUserId(productId);
    const newUser = await User.findByIdAndUpdate(userId, { $inc: {"reviewQuentity": 1, "reviewSum":score} });
    return newUser;
};