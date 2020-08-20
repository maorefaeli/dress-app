const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');

// Load Review model
const Review = require('../models/Review');

// @route POST /reviews/add
// @desc Add new review about rent
// @access Private
router.post('/add', auth.isLoggedIn, async (req, res) => {
    try {
        const user = req.user.id;
        const { rent, score } = req.body;
        
        let newReview = new Review({
            user,
            rent,
            score,
        });

        newReview = await newReview.save();
        res.json(newReview);
    } catch (e) {
        console.log(e);
        error = 'Problem saving review';
        res.status(400).json({ error });
    }
});
