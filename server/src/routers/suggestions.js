const express = require('express');
const router = express.Router();
const auth = require('../utils/auth');
const WishlistController = require('../controllers/wishlistController');
const User = require('../models/User');
const ObjectID = require('mongodb').ObjectID;

const MAXIMUM_MILLISECONDS_FOR_SUGGESTION_REQUEST = 7 /* days */ * 24 * 60 * 60 * 1000;

// @route POST /suggestions/request
// @desc Request a suggestion
// @access Private
router.post('/suggestions/request', auth.isLoggedIn, async (req, res) => {
    try {
        const { cycle, product, fromdate, todate } = req.body;

        const fromDate = new Date(fromdate);
        const toDate = new Date(todate);

        const delta = fromDate - toDate;
        if (delta > MAXIMUM_MILLISECONDS_FOR_SUGGESTION_REQUEST) {
            throw new Error('Order dates are more than 7 days');
        }

        await WishlistController.requestProductOnCycle(
            ObjectID(cycle), ObjectID(re.user.id), ObjectID(product), fromDate,toDate
        );
        return res.json(true);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem requesting a suggestion"});
    }
});

module.exports = router;
