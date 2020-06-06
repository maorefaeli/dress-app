const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');
const wishlist = require('../controllers/wishlist');
const ObjectID = require('mongodb').ObjectID;
const User = require('../models/User');

// @route POST users/register
// @desc Register user
// @access Public
router.post('/register', async (req, res) => {
    try {
        const { username, password, firstName, lastName } = req.body;

        if (!validators.isNonEmptyString(username)) {
            return res.status(400).json({"error": "name cannot be empty"});
        }

        if (!validators.isNonEmptyString(firstName)) {
            return res.status(400).json({"error": "First name connot be empty"});
        }

        if (!validators.isNonEmptyString(lastName)) {
            return res.status(400).json({"error": "last name connot be empty"});
        }

        if (!validators.isNonEmptyString(password)) {
            return res.status(400).json({"error": "password cannot be empty"});
        }

        let user = await User.findOne({ username });

        if (user) {
            return res.status(403).json({"error": "user already exist"});
        }

        user = new User({
            username,
            firstName,
            lastName,
            password: User.encryptPassword(password)
        });

        await user.save();
        res.json(true);
    } catch (error) {
        console.log(error);
        res.status(400).json({'error': 'problem saving user'});
    }
});

//  @route GET users/profile/:id
//  @desc Get specific user
//  @access Private
router.get('/profile/:username', async (req, res) => {
    try {
        const profile = await User.find({username: req.params.username});
        delete profile.password
        return res.json(profile);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting profile"})
    }
});

//  @route GET users/cycle/:id
//  @desc Get cycle of wishlist items by users
//  @access Public
router.get('/cycle/:id', async (req, res) => {
    try {
        const userId = new ObjectID(req.params.id);
        const cycle = await wishlist.findMinimumCycle(userId);
        return res.json(cycle || []);
    } catch (error){
        console.log(error);
        res.status(400).json({'error': 'Problem finding cycle'});
    }
});

//  @route GET users/populateTestData
//  @desc Populate test data for cycle detection
//  @access Public
router.get('/populateTestData', async (req, res) => {
    try {
        const populate = async (index, target) => {
            const user = {
                username: `user${index}`,
                password: '10/w7o2juYBrGMh32/KbveULW9jk2tejpyUAD+uC6PE=', // password is 'pass'
            };

            if (target) {
                user.wishlist = [{
                    user: target,
                    items: [index],
                }];
            }

            return await User.create(user);
        };
        
        // 0 <-- 1 <-- 2 <-- 3 <-- 4
        //             |---------->
        const amount = 5;
        const cycleSize = 3;
        let prevUserId;
        let cycleStartId;

        for (let index = 0; index < amount; index++) {
            const user = await populate(index, prevUserId);
            prevUserId = user.id;

            if (amount - index === cycleSize) {
                cycleStartId = prevUserId;
            }
        }

        await User.findByIdAndUpdate(cycleStartId, { $push: { wishlist: { user: prevUserId, items: [amount] } }});

        return res.json(true);
    } catch (error){
        console.log(error);
        res.status(400).json({'error': 'Problem creating data'});
    }
});

module.exports = router;
