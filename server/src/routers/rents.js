const express = require('express');
const router = express.Router();
const auth = require('../utils/auth');

// Load models
const Rent = require('../models/Rent');
const RentController = require('../controllers/rentController');

// @route GET /history/:id
// @desc Get product renting history
// @access Private
router.get('/history/:id', auth.isLoggedIn, async (req, res) => {
    try {
        const rentingHistory = await Rent.find({ product: req.params.id});
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
        error = 'Problem place an order: ' + e.message;
        res.status(400).json({ error });
    }
});

module.exports = router;
