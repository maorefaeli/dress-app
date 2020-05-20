const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');

// Load User model
const Product = require('../models/Product');

const isProductContainErrors = (product) => {
    if (!validators.isNonEmptyString(product.user)) return 'User cannot be empty';
    if (!validators.isNonEmptyString(product.name)) return 'Name cannot be empty';
    if (!validators.isPositiveNumber(product.price)) return 'Price must be positive';
    if (!validators.isNonEmptyString(product.image)) return 'Image mcannot be empty';
    if (!validators.isNonEmptyString(product.fromdate)) return 'From date availible cannot be empty';
    if (!validators.isNonEmptyString(product.todate)) return 'To date availible cannot be empty';
    return '';
};


// @route POST api/products/add
// @desc Add product
// @access Public
router.get('/:id', async (req, res) => {
    try {
        const product = await Product.findById(req.params.id);
        return res.json(product);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem removing product"})
    }
});

// @route POST api/products/add
// @desc Add product
// @access Public
router.post('/add', auth.isAdminLoggedIn, async (req, res) => {
    try {
        const { user, name, price, image, fromdate, todate } = req.body;
        let newProduct = new Product ({
            user,
            name,
            price,
            image,
            fromdate,
            todate
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

//  @route DELETE api/products/:id
//  @desc Delete specific product
//  @access Public
router.delete('/:id', auth.isAdminLoggedIn, async (req, res) => {
    try {
        await Product.findByIdAndRemove(req.params.id);
        return res.json(true);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem removing product"})
    }
});

//  @route POST api/products/:id
//  @desc Edit specific product
//  @access Public
router.post('/:id', auth.isAdminLoggedIn, async (req, res) => {
    try {
        const { user, name, price, image, fromdate, todate } = req.body;
        const product = {
            user,
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
        res.status(400).json({"error":"Problem editing product"})
    }
});

module.exports = router;
