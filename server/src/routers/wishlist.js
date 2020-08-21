const express = require('express');
const router = express.Router();
const auth = require('../utils/auth');
const WishlistController = require('../controllers/wishlistController');
const User = require('../models/User');

// @route POST /wishlist/add
// @desc Add new product to wishlist
// @access Private
router.post('/add', auth.isLoggedIn, async (req, res) => {
    try {
        const { product } = req.body;
        await WishlistController.addProductToWishlist(req.user.id, product);
        return res.json(true);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem adding product to wishlist"});
    }
});

// @route POST /wishlist/add
// @desc Add new product to wishlist
// @access Private
router.post('/remove', auth.isLoggedIn, async (req, res) => {
    try {
        const { product } = req.body;
        await WishlistController.removeProductFromWishlist(req.user.id, product);
        return res.json(true);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem removing product from wishlist"});
    }
});

// @route GET /wishlist
// @desc Get the logged in user wishlist
// @access Private
router.get('/', auth.isLoggedIn, async (req, res) => {
    try {
        let result = [];

        const user = await User.findById(req.user.id).populate('wishlist.products');
        if (user.wishlist) {
            for (const wish of user.wishlist) {
                result = [...result, ...wish.products];
            }
        }

        return res.json(result);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem finding user wishlist"});
    }
});

module.exports = router;
