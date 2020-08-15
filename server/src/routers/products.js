const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');

// Load Product model
const Product = require('../models/Product');
const WishlistController = require('../controllers/wishlist');

const isProductContainErrors = (product) => {
    //if (!validators.isNonEmptyString(product.user)) return 'User cannot be empty';
    if (!validators.isNonEmptyString(product.name)) return 'Name cannot be empty';
    if (!validators.isPositiveNumber(product.price)) return 'Price must be positive';
    return '';
};

// @route GET /products
// @desc Get all products
// @access Public
router.get('/', async (req, res) => {
    try {
        const products = await Product.find();
        return res.json(products);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting products"});
    }
});

// @route GET /products/:id
// @desc Get product by its ID
// @access Public
router.get('/:id', async (req, res) => {
    try {
        const product = await Product.findById(req.params.id);
        return res.json(product);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting product"});
    }
});

// @route GET /products/user/:id
// @desc Get user's product by their ID
// @access Private
router.get('/user/:user', auth.isLoggedIn, async (req, res) => {
    try {
        const products = await Product.find({user:req.params.user});
        return res.json(products);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting products"});
    }
});

// @route POST /products/addwish
// @desc Add new product to wishlist
// @access Private
router.post('/addwish', auth.isLoggedIn, async (req, res) => {
    try {
        const { user, product } = req.body;
        await WishlistController.addProductToWishList(user, product);
        return res.json(true);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem adding product to wishlist"});
    }
});

// @route POST /products/add
// @desc Add new product
// @access Private
router.post('/add', auth.isLoggedIn, async (req, res) => {
    try {
        const user = req.user.id;
        const { name, price, image, fromdate, todate } = req.body;
        
        const toDateFrom = new Date(fromdate);
        const toDateTo = new Date(todate);

        let rentingDates = {
            "fromdate": new Date(toDateFrom.getTime() - (48*60*60*1000)),
            "todate": new Date(toDateFrom.getTime() - (24*60*60*1000))
        }

        let newProduct = new Product ({
            user,
            name,
            price,
            image,
            fromdate,
            todate,
            rentingDates
        });

        let error = isProductContainErrors(newProduct);
        if (error) {
            return res.status(400).json({ error });
        }

        newProduct = await newProduct.save();
        res.json(newProduct);
    } catch (e) {
        console.log(e);
        error = 'Problem saving product';
        res.status(400).json({ error });
    }
});

//  @route DELETE /products/:id
//  @desc Delete specific product
//  @access Private
router.delete('/:id', auth.isLoggedIn, async (req, res) => {
    try {
        await Product.findByIdAndRemove(req.params.id);
        await WishlistController.handleProductDeletion(req.params.id);
        return res.json(true);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem removing product"});
    }
});

//  @route POST /products/:id
//  @desc Edit specific product
//  @access Private
router.post('/:id', auth.isLoggedIn, async (req, res) => {
    try {
        const UserId = req.user.id;
        const { name, price, image, fromdate, todate } = req.body;
        const product = {
            UserId,
            name,
            price,
            image,
            fromdate,
            todate
        };

        const error = isProductContainErrors(product);
        if (error) {
            return res.status(400).json({ error });
        }

        const newProduct = await Product.findByIdAndUpdate(req.params.id, product, { new: true });
        return res.json(newProduct);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem editing product"});
    }
});

module.exports = router;
