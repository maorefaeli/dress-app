const express = require('express');
const router = express.Router();
const auth = require('../utils/auth');
const WishlistController = require('../controllers/wishlistController');

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
