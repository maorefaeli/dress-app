const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');
const Product = require('../models/Product');
const WishlistController = require('../controllers/wishlistController');
const PendingCycle = require('../models/PendingCycle');

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

module.exports = router;
