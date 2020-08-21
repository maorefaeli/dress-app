const express = require('express');
const router = express.Router();
const auth = require('../utils/auth');

// Load Product and Rent models
const User = require('../models/User');

// @route POST coins/add
// @desc Add coins to user
// @access Private
router.post('/add', auth.isLoggedIn, async (req, res) => {
    try {
        const { userId, coins } = req.body;
        const newUser = await User.findByIdAndUpdate(userId, { $inc: {"coins": coins} });
        return res.json(newUser);
    } catch (error) {
        console.log(error);
        req.status(400).json({"error":"Problem adding coins to user"});
    }
});

// @route POST coins/sub
// @desc Sub coins to user
// @access Private
router.post('/sub', auth.isLoggedIn, async (req, res) => {
    try {
        const { userId, coins } = req.body;
        coins = coins * -1;
        const newUser = await User.findByIdAndUpdate(userId, { $inc: {"coins": coins} });
        return res.json(newUser);
    } catch (error) {
        console.log(error);
        req.status(400).json({"error":"Problem adding coins to user"});
    }
});

module.exports = router;
