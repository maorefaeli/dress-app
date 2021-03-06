const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');
const User = require('../models/User');
const keys = require('../config/keys');
const UserController = require('../controllers/userController');

router.get('/', async (req, res) => {
    try {
        const all = await User.find().sort({_id: -1});
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
            return res.status(400).json({"error": "First name cannot be empty"});
        }

        if (!validators.isNonEmptyString(lastName)) {
            return res.status(400).json({"error": "Last name cannot be empty"});
        }

        if (!validators.isNonEmptyString(password)) {
            return res.status(400).json({"error": "password cannot be empty"});
        }

        let user = await User.findOne({ username });

        if (user) {
            return res.status(403).json({"error": "username already exist"});
        }

        user = new User({
            username,
            firstName,
            lastName,
            password: User.encryptPassword(password),
            coins: keys.coinsNewUser,
        });

        await user.save();
        return res.json(true);
    } catch (error) {
        console.log(error);
        res.status(400).json({'error': 'problem saving user'});
    }
});

//  @route POST /products/:id
//  @desc Edit specific product
//  @access Private
router.post('/edit', auth.isLoggedIn, async (req, res) => {
    try {
        const userId = req.user.id;
        const { firstName, lastName, address, longitude, latitude } = req.body;
        
        if (!validators.isNonEmptyString(firstName)) {
            return res.status(400).json({"error": "First name cannot be empty"});
        }

        if (!validators.isNonEmptyString(lastName)) {
            return res.status(400).json({"error": "Last name cannot be empty"});
        }

        const updateCommand = { firstName, lastName };

        // Optional fields

        if (address) {
            updateCommand['address'] = address;
        }

        if (longitude && latitude) {
            updateCommand['location'] = {
                type: 'Point',
                coordinates: [longitude, latitude]
            };
        }

        await User.findByIdAndUpdate(userId, updateCommand);
        return res.json(true);
        
    } catch (error){
        console.log(error);
        res.status(400).json({"error": "Problem editing product"});
    }
});

//  @route GET users/profile
//  @desc Get logged in user profile
//  @access Private
router.get('/profile', auth.isLoggedIn, async (req, res) => {
    try {
        return res.json(await User.findById(req.user.id).select(UserController.fullUserFields));
    } catch (error) {
        console.log(error);
        res.status(400).json({"error":"Problem getting profile"})
    }
});

//  @route GET users/profile/:id
//  @desc Get specific user profile
//  @access Public
router.get('/profile/:id', async (req, res) => {
    try {
        return res.json(await User.findById(req.user.id).select(UserController.partialUserFields));
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting profile"})
    }
});

module.exports = router;
