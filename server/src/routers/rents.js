const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');

// Load Product and Rent models
const Product = require('../models/Product');
const Rent = require('../models/Rent');

const isRentContainErrors = (rent) => {
    if (!validators.isNonEmptyString(rent.user)) return 'User cannot be empty';
    if (!validators.isNonEmptyString(rent.product)) return 'User cannot be empty';
    if (!validators.isNonEmptyString(rent.fromdate)) return 'From date availible cannot be empty';
    if (!validators.isNonEmptyString(rent.todate)) return 'To date availible cannot be empty';
    return '';
};

router.post('/test', async (req, res) => {
    let rentingProduct = Product.findById(req.params.product);
    let rentDates = {
        "fromdate": "20/06/2020",
        "todate": "30/06/2020"
    }
    rentingProduct.rentingDates.push(rentDates);
    const newProduct = await Product.findByIdAndUpdate(req.params.product, rentingProduct, { new: true });
    res.json(newProduct);
});

// @route GET /history/:id
// @desc Get product renting history
// @access Private
router.get('/history/:id', auth.isLoggedIn, async (req, res) => {
    try {
        const rentingHistory = await Rent.find({"product":req.params.id});
        return res.json(rentingHistory);
    } catch (error) {
        console.log(error);
        req.status(400).json({"error":"Problem getting renting history"});
    }
});

// @route POST rents/add
// @desc Add rent
// @access Private
router.post('/add', auth.isLoggedIn, async (req, res) => {
    try {
        const user = req.user.id;
        const { product, fromdate, todate } = req.body;
        let newRent = new Rent ({
            user,
            product,
            fromdate,
            todate
        });

        let error = isRentContainErrors(newRent);
        if (error) {
            return res.status(400).json({ error });
        }

        let rentingProduct = Product.findById(product);
        let rentingDate = {
            "fromdate": fromdate,
            "todate": todate
        }
        rentingProduct.rentingDates.push(rentingDate);
        const newProduct = await Product.findByIdAndUpdate(product, rentingProduct, { new: true });
        console.log(newProduct);

        newRent = await newRent.save();
        res.json(newRent);
    } catch (e) {
        console.log(e);
        error = 'Problem place an order';
        res.status(400).json({ error });
    }
});

module.exports = router;
