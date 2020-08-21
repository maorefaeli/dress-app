const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');
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
            return res.status(400).json({"error": "First name cannot be empty"});
        }

        if (!validators.isNonEmptyString(lastName)) {
            return res.status(400).json({"error": "last name cannot be empty"});
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
        const { firstName, lastName } = req.body;

        if (!validators.isNonEmptyString(firstName)) {
            return res.status(400).json({"error": "First name cannot be empty"});
        }

        if (!validators.isNonEmptyString(lastName)) {
            return res.status(400).json({"error": "last name cannot be empty"});
        }

        await User.findByIdAndUpdate(userId, { firstName, lastName });
        return res.json(true);
        
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem editing product"});
    }
});

//  @route GET users/profile/:id
//  @desc Get specific user
//  @access Private
router.get('/profile/:username', async (req, res) => {
    try {
        const profile = await User.findOne({ username: req.params.username });
        delete profile.password
        return res.json(profile);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting profile"})
    }
});

module.exports = router;
