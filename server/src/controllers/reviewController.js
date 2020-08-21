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
    let avgRating, avgSum, avgQuentity = 0;
    let userReviews = [];
    
    let reviews = await Review.find();
    reviews.forEach(async review => {
        // Get the reviewed userId
        const user = await getReviewedUserId(review.rent.product);
        if (user.equals(userId))
            userReviews.push(review);
    });

    userReviews.forEach(userReview => {
        avgSum += userReview.score;
        avgQuentity++;
    });

    avgRating = avgSum/avgQuentity;
    const newUser = await User.findByIdAndUpdate(userId, {"avg": avgRating});
    return newUser;
};