const express = require('express');
const router = express.Router();
const validators = require('../utils/validators');
const auth = require('../utils/auth');

// Load Product model
const Rent = require('../models/Rent');

const isRentContainErrors = (rent) => {
    if (!validators.isNonEmptyString(rent.user)) return 'User cannot be empty';
    if (!validators.isNonEmptyString(rent.product)) return 'User cannot be empty';
    if (!validators.isNonEmptyString(rent.fromdate)) return 'From date availible cannot be empty';
    if (!validators.isNonEmptyString(rent.todate)) return 'To date availible cannot be empty';
    return '';
};

// @route POST api/rents/add
// @desc Add rent
// @access Private
router.post('/add', auth.isLoggedIn, async (req, res) => {
    try {
        const { user, product, fromdate, todate } = req.body;
        let newRent = new Rent ({
            user,
            product,
            fromdate,
            todate
        });

        let error = isRentContainErrors(newRent);
        if (error) {
            return res.status(400).json({ error });
        }

        newRent = await newRent.save();
        res.json(newRent);
    } catch (e) {
        console.log(e);
        error = 'Problem place an order';
        res.status(400).json({ error });
    }
});

module.exports = router;
