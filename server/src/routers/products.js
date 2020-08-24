const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');
const ObjectID = require('mongodb').ObjectID;
const { getDateComponent, parseSearch } = require('../utils/date');

// Load Product model
const Product = require('../models/Product');
const User = require('../models/User');
const WishlistController = require('../controllers/wishlistController');
const UserController = require('../controllers/userController');

const isProductContainErrors = (product) => {
    //if (!validators.isNonEmptyString(product.user)) return 'User cannot be empty';
    if (!validators.isNonEmptyString(product.name)) return 'Name cannot be empty';
    if (!validators.isPositiveNumber(product.price)) return 'Price must be positive';
    if (!validators.isDate(product.fromdate)) return 'From date cannot be empty';
    if (!validators.isDate(product.todate)) return 'To date cannot be empty';
    return '';
};

const kilometersToRadian = function(kilometers){
    const earthRadiusInKilometers = 6371;
    return kilometers / earthRadiusInKilometers;
};


router.get('/', async (req, res) => {
    try {
        const all = await Product.find().populate('user').sort({_id: -1});
        res.set("x-total-count", all.length);
        res.set("Content-Range", all.length);
        res.header('Access-Control-Expose-Headers', 'X-Total-Count');
        res.header('Access-Control-Expose-Headers', 'Content-Range');
        return res.json(all);
    } catch (error) {
        console.log(error);
        res.status(400).json({error});
    }
});

// @route POST /products
// @desc Search for products
// @access Public
router.post('/', async (req, res) => {
    try {
        let { name, radius, minimumPrice, maximumPrice, fromDate, toDate, minimumRating } = req.body;
        console.log('Search:', name, radius, minimumPrice, maximumPrice, fromDate, toDate, minimumRating)

        const query = {};
        const userQuery = {};

        if (name) {
            query.name = { $regex: `.*${name}.*`, $options: 'i' };
        }

        if (minimumPrice) {
            query.price = { $gte: Number(minimumPrice) };
        }

        if (maximumPrice) {
            query.price = { ...query.price, $lte: Number(maximumPrice) };
        }

        if (minimumRating) {
            query.averageScore = { $gte: Number(minimumRating) };
        }

        // Default to today if 'fromDate' not provided
        fromDate = getDateComponent(fromDate ? parseSearch(fromDate) : Date.now());

        // Search for products that end after fromDate
        query.todate = { $gte: fromDate };

        if (toDate) {
            // Search for products that start before toDate
            query.fromdate = { $lte: getDateComponent(parseSearch(toDate)) };
        }

        if (req.user && req.user.id) {
            const userId = ObjectID(req.user.id);

            // Filter out own user products if logged in
            query.user = { $ne: ObjectID(req.user.id)};

            // For logged in user that has location, search only products of nearby users
            if (radius) {
                const user = await User.findById(userId);

                if (user.location && user.location.coordinates) {
                    userQuery.location = {
                        $geoWithin : {
                            $centerSphere : [user.location.coordinates, kilometersToRadian(radius) ]
                        }
                    };
                }
            }
        }

        if (!validators.isObjectEmpty(userQuery)) {
            const users = await User.find(userQuery, 'id');
            query.user['$in'] = users.map(q => q._id);
        }

        // Sort by _id descending: which will return the newly created products first
        let products = await Product.find(query).populate('user', UserController.partialUserFields).sort({_id: -1}) || [];

        console.log('Returned', products.length, 'products on search');

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
        const product = await Product.findById(req.params.id).populate('user', UserController.partialUserFields);
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
        const products = await Product.find({user:req.user.id}).populate('user', UserController.partialUserFields).sort({_id: -1});
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
        const products = await Product.find({user: req.params.user}).populate('user', UserController.partialUserFields);
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

        await newProduct.save();
        res.json(true);
    } catch (e) {
        console.log(e);
        error = 'Problem saving product';
        res.status(400).json({ error });
    }
});

const getMinFreeDateForClosing = (product) => {
    // First try fromdate
    let closeDate = product.fromdate;

    // Find latest date the product is already rented
    if (product.rentingDates && product.rentingDates.length) {
        closeDate = new Date(Math.max(...product.rentingDates.map(rt => rt.todate)));
    }

    return closeDate;
};

const getMaxFreeDateForOpening = (product) => {
    // First try todate
    const openDate = product.todate;

    // Find first date the product is already rented
    if (product.rentingDates && product.rentingDates.length) {
        openDate = new Date(Math.min(...product.rentingDates.map(rt => rt.fromdate)));
    }

    return openDate;
};

// @route POST /products/close
// @desc Close products for future orders
// @access Private
router.post('/close/:id', auth.isLoggedIn, async (req, res) => {
    try {
        const userId = ObjectID(req.user.id);
        const productId = ObjectID(req.params.id);
        const product = await Product.findById(productId);

        if (!product.user.equals(userId)) {
            return res.status(401).json({ error: "Product not belongs to user" });
        }

        const closeDate = getMinFreeDateForClosing(product);

        // Decide if delete or close the product for future orders
        if (closeDate = product.fromDate) {
            // Delete the product since it has never been ordered
            await Product.findByIdAndRemove(productId);
            await WishlistController.handleProductDeletion(productId);
            console.log("Deleting product", product.id);
        } else {
            await Product.findByIdAndUpdate(productId, { todate: closeDate });
            close.log("Update product", product.id, ": todate changed to", closeDate);
        }
        
        res.json(true);
    } catch (e) {
        console.log(e);
        error = 'Problem saving product';
        res.status(400).json({ error });
    }
});

//  @route POST /products/:id
//  @desc Edit specific product
//  @access Private
router.post('/edit', auth.isLoggedIn, async (req, res) => {
    try {
        const userId = ObjectID(req.user.id);
        const { id, name, price, fromdate, todate } = req.body;
        const productId = ObjectID(id);

        let product = await Product.findById(productId);
        if (!product.user.equals(userId)) {
            return res.status(401).json({"error": "Product not belongs to user"});
        }

        product = {
            name,
            price,
            fromdate: getDateComponent(fromdate),
            todate: getDateComponent(todate)
        };

        const error = isProductContainErrors(product);
        if (error) {
            return res.status(400).json({ error });
        }

        const minDateForClosing = getMinFreeDateForClosing(product);
        if (minDateForClosing > product.todate) {
            return res.status(400).json({ error: `Product is rented until ${minDateForClosing.toDateString()}` });
        }

        const maxDateForOpening = getMaxFreeDateForOpening(product);
        if (maxDateForOpening < product.fromdate) {
            return res.status(400).json({ error: `Product is first rented on ${maxDateForOpening.toDateString()}` });
        }

        await Product.findByIdAndUpdate(productId, product);
        return res.json(true);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem editing product"});
    }
});

module.exports = router;
