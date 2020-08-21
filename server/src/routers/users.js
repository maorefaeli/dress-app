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

const getUserById = async (userId) => {
    const user = await User.findById(userId);
    delete user.username;
    delete user.password;
    delete user.wishlist;
    return user;
}

//  @route GET users/profile
//  @desc Get logged in user profile
//  @access Private
router.get('/profile', auth.isLoggedIn, async (req, res) => {
    try {
        return res.json(getUserById(req.user.id));
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting profile"})
    }
});

//  @route GET users/profile/:id
//  @desc Get specific user profile
//  @access Private
router.get('/profile/:id', async (req, res) => {
    try {
        return res.json(getUserById(req.params.id));
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting profile"})
    }
});

module.exports = router;
