const express = require('express');
const router = express.Router();
const auth = require('../utils/auth');
const ObjectID = require('mongodb').ObjectID;

// Load models
const Rent = require('../models/Rent');
const User = require('../models/User');
const RentController = require('../controllers/rentController');

// @route GET rents/
// @desc Get all user rents
// @access Private
router.get('/', auth.isLoggedIn, async (req, res) => {
    try {
        const userRents = await Rent.find({ user: ObjectID(req.user.id), isFinished: { $ne: true } }) || [];
        return res.json(userRents);
    } catch (error) {
        console.log(error);
        req.status(400).json({"error":"Problem getting user rents"});
    }
});

// @route GET rents/history/:id
// @desc Get product renting history
// @access Public
router.get('/history/:id', async (req, res) => {
    try {
        const rentingHistory = await Rent.find({ product: ObjectID(req.params.id) }) || [];
        return res.json(rentingHistory);
    } catch (error) {
        console.log(error);
        req.status(400).json({"error":"Problem getting product renting history"});
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
        error = 'Problem place an order: ' + e.message;
        res.status(400).json({ error });
    }
});

// @route POST rents/finish/:id
// @desc Finish an existing rent
// @access Private
router.post('/finish/:id', auth.isLoggedIn, async (req, res) => {
    try {
        const userId = ObjectID(req.user.id);
        const rentId = ObjectID(req.params.id);
        const { score } = req.body;

        const rent = await Rent.findById(rentId).populate('product');

        // Check rent belongs to logged in user
        if (!rent.user.equals(userId)) {
            return res.status(401).json({"error": "Rent not belongs to user"});
        }

        // Close order
        await Rent.findByIdAndUpdate(rentId, { isFinished: true, score });

        // Update user rating
        await User.findByIdAndUpdate(rent.product.user, { $inc: { reviewQuantity: 1, reviewSum: score } });

        console.log("Rent", rent.id, "was closed");

        res.json(true);
    } catch (e) {
        console.log(e);
        error = 'Problem finishing rent';
        res.status(400).json({ error });
    }
});

module.exports = router;
