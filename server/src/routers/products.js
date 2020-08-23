const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');
const ObjectID = require('mongodb').ObjectID;
const { getDateComponent } = require('../utils/date');

// Load Product model
const Product = require('../models/Product');
const User = require('../models/User');
const WishlistController = require('../controllers/wishlistController');

const DEFAULT_SEARCH_SPHERE_KM = 100;

const isProductContainErrors = (product) => {
    //if (!validators.isNonEmptyString(product.user)) return 'User cannot be empty';
    if (!validators.isNonEmptyString(product.name)) return 'Name cannot be empty';
    if (!validators.isPositiveNumber(product.price)) return 'Price must be positive';
    if (!product.fromdate) return 'fromdate cannot be empty';
    if (!product.todate) return 'fromdate cannot be empty';
    return '';
};

const kilometersToRadian = function(kilometers){
    const earthRadiusInKilometers = 6371;
    return kilometers / earthRadiusInKilometers;
};

// @route GET /products
// @desc Search for products
// @access Public
router.get('/', async (req, res) => {
    try {
        const { location } = req.query;

        const query = {};

        if (req.user && req.user.id) {
            const userId = ObjectID(req.user.id);

            // Filter out own user products if logged in
            query['user'] = { $ne: ObjectID(req.user.id)};

            // For logged in user that has location, search only products of nearby users
            const user = await User.findById(userId);

            if (user.location && user.location.coordinates) {
                const users = await User.find({
                    location : {
                        $geoWithin : {
                            $centerSphere : [user.location.coordinates, kilometersToRadian(location || DEFAULT_SEARCH_SPHERE_KM) ]
                        }
                    }
                }, 'id');

                query['user']['$in'] = users.map(q => q._id);
            }
            
        }
        const products = await Product.find(query);
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
        const product = await Product.findById(req.params.id).populate('user', 'firstName lastName averageScore reviewQuantity address');
        return res.json(product);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting product"});
    }
});

// @route GET /products/user/me
// @desc Get logged in user products list.
// @access Private
router.get('/user/me', auth.isLoggedIn, async (req, res) => {
    try {
        const products = await Product.find({user:req.user.id});
        return res.json(products);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting products"});
    }
});

// @route GET /products/user/:id
// @desc Get user's product by their ID
// @access Private
router.get('/user/:user', auth.isLoggedIn, async (req, res) => {
    try {
        const products = await Product.find({user: req.params.user});
        return res.json(products);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting products"});
    }
});

// @route POST /products/add
// @desc Add new product
// @access Private
router.post('/add', auth.isLoggedIn, async (req, res) => {
    try {
        const user = req.user.id;
        const { name, price, image, fromdate, todate } = req.body;
        
        let newProduct = new Product({
            user,
            name,
            price,
            image,
            fromdate: getDateComponent(fromdate),
            todate: getDateComponent(todate),
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

// @route POST /products/close
// @desc Close products for future orders
// @access Private
router.post('/close/:id', auth.isLoggedIn, async (req, res) => {
    try {
        const userId = ObjectID(req.user.id);
        const productId = ObjectID(req.params.id);
        const product = await Product.findById(productId);

        if (!product.user.equals(userId)) {
            return res.status(401).json({"error": "Product not belongs to user"});
        }

        // Try close the product as soon as possible
        let closeDate = product.fromdate;

        // Find latest date the product is already rented
        if (product.rentingDates && product.rentingDates.length) {
            closeDate = new Date(Math.max(...product.rentingDates.map(rt => rt.todate)));
        }
        
        const newProduct = await Product.findByIdAndUpdate(productId, { todate: closeDate }, { new: true });
        close.log("Product", product.id, "todate changed to", closeDate);
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
        const userId = ObjectID(req.user.id);
        const productId = req.params.id;

        const product = await Product.findById(productId);
        if (!product.user.equals(userId)) {
            return res.status(401).json({"error": "Product not belongs to user"});
        }

        await Product.findByIdAndRemove(productId);
        await WishlistController.handleProductDeletion(productId);
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
        const userId = ObjectID(req.user.id);
        const productId = ObjectID(req.params.id);
        const { name, price, image, fromdate, todate } = req.body;

        let product = await Product.findById(productId);
        if (!product.user.equals(userId)) {
            return res.status(401).json({"error": "Product not belongs to user"});
        }

        product = {
            user: userId,
            name,
            price,
            image,
            fromdate: getDateComponent(fromdate),
            todate: getDateComponent(todate)
        };

        const error = isProductContainErrors(product);
        if (error) {
            return res.status(400).json({ error });
        }

        const newProduct = await Product.findByIdAndUpdate(productId, product, { new: true });
        return res.json(newProduct);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem editing product"});
    }
});

module.exports = router;
