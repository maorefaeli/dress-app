const express = require('express');
const router = express.Router();
const auth = require('../utils/auth');
const ObjectID = require('mongodb').ObjectID;
const keys = require('../config/keys');

// Load models
const Rent = require('../models/Rent');
const User = require('../models/User');
const RentController = require('../controllers/rentController');
const UserController = require('../controllers/userController');

router.get('/all', async (req, res) => {
    try {
        const all = await Rent.find().populate('user', UserController.fullUserFields)
        .populate({
            path: 'product',
            model: 'Product',
            populate: {
                path: 'user',
                model: 'User',
                select: UserController.fullUserFields
            }
        }).sort({_id: -1});
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

router.get('/disputes', async (req, res) => {
    try {
        const all = await Rent.find({ inDispute: true }).populate('user', UserController.fullUserFields)
        .populate({
            path: 'product',
            model: 'Product',
            populate: {
                path: 'user',
                model: 'User',
                select: UserController.fullUserFields
            }
        }).sort({_id: -1});
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

// @route GET rents/
// @desc Get all user rents
// @access Private
router.get('/', auth.isLoggedIn, async (req, res) => {
    try {
        const userRents = await Rent.find({ user: ObjectID(req.user.id) })
            .populate('user', UserController.fullUserFields)
            .populate({
                path: 'product',
                model: 'Product',
                populate: {
                    path: 'user',
                    model: 'User',
                    select: UserController.fullUserFields
                }
            }).sort({isFinished: 1, _id: -1});
        return res.json(userRents || []);
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
        const rentingHistory = await Rent.find({ product: ObjectID(req.params.id) })
            .populate('user', UserController.fullUserFields)
            .populate({
                path: 'product',
                model: 'Product',
                populate: {
                    path: 'user',
                    model: 'User',
                    select: UserController.fullUserFields
                }
            }).sort({isFinished: 1, _id: -1});
        return res.json(rentingHistory || []);
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
        await RentController.addRent(req.user.id, product, fromdate, todate);
        res.json(true);
    } catch (e) {
        console.log(e);
        error = 'Problem place an order: ' + e.message;
        res.status(400).json({ error });
    }
});

// @route POST rents/finish
// @desc Finish an existing rent
// @access Private
router.post('/finish', auth.isLoggedIn, async (req, res) => {
    try {
        const userId = ObjectID(req.user.id);
        const { score, rent } = req.body;

        const rentId = ObjectID(rent);

        const rentEntity = await Rent.findById(rentId).populate('product');

        // Check rent belongs to logged in user
        if (!rentEntity.user.equals(userId)) {
            return res.status(401).json({"error": "Order not belongs to user"});
        }

        if (rent.isFinished) {
            return res.status(400).json({error: "Order already finished"});
        }

        // Close order
        await Rent.findByIdAndUpdate(rentId, { isFinished: true, score });

        // Reward user for give a review
        await User.findByIdAndUpdate(userId, { $inc: { coins: keys.coinsRewardForReview } });

        // Update product's owner rating
        const user = await User.findById(rentEntity.product.user);
        const reviewQuantity = user.reviewQuantity + 1;
        const reviewSum = user.reviewSum + score;
        const averageScore = reviewSum / reviewQuantity;
        await User.findByIdAndUpdate(rentEntity.product.user, { reviewQuantity, reviewSum, averageScore });

        console.log("Rent", rentEntity.id, "was closed");

        res.json(true);
    } catch (e) {
        console.log(e);
        error = 'Problem finishing rent';
        res.status(400).json({ error });
    }
});

// @route POST rents/add
// @desc Add rent
// @access Private
router.post('/dispute/:id', auth.isLoggedIn, async (req, res) => {
    try {
        const rentId = req.params.id;
        const rent = await Rent.findById(rentId);
        if (!rent.user.equals(req.user.id)) {
            return res.status(401).json({error: "Order not belongs to user"});
        }

        if (rent.isFinished) {
            return res.status(400).json({error: "Order already finished"});
        }
        
        await Rent.findByIdAndUpdate(rentId, { inDispute: true });
        console.log('Open dispute for rent', rent.id);

        res.json(true);
    } catch (e) {
        console.log(e);
        res.status(400).json({ error: 'Problem open dispute' });
    }
});

module.exports = router;
