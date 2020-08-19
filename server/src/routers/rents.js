const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');

// Load Product and Rent models
const Product = require('../models/Product');
const Rent = require('../models/Rent');
const RentController = require('../controllers/rentController');

const isRentContainErrors = (rent) => {
    if (!validators.isNonEmptyString(rent.user)) return 'User cannot be empty';
    if (!validators.isNonEmptyString(rent.product)) return 'Product cannot be empty';
    if (!validators.isNonEmptyString(rent.fromdate)) return 'From date available cannot be empty';
    if (!validators.isNonEmptyString(rent.todate)) return 'To date available cannot be empty';
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
        const { product, fromdate, todate } = req.body;
        const newRent = await RentController.addRent(req.user.id, product, fromdate, todate);
        res.json(newRent);
    } catch (e) {
        console.log(e);
        error = 'Problem place an order';
        res.status(400).json({ error });
    }
});

module.exports = router;
