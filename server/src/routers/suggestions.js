const express = require('express');
const router = express.Router();
const auth = require('../utils/auth');
const WishlistController = require('../controllers/wishlistController');
const PendingCycle = require('../models/PendingCycle');
const ObjectID = require('mongodb').ObjectID;
const UserController = require('../controllers/userController');

// @route GET /suggestions
// @desc Get free suggestions for the user. Output: [{cycleId, product}]
// @access Private
router.get('/', auth.isLoggedIn, async (req, res) => {
    try {
        let result = [];
        const userId = ObjectID(req.user.id);

        // Search for pending cycles that the user participates and has not requested a product yet
        const cycles = await PendingCycle.find({
            participants: {
                $elemMatch: { user: userId, requestedProduct: null }
            }
        }).populate({
            path: 'participants.products',
            model: 'Product',
            populate: {
                path: 'user',
                model: 'User',
                select: UserController.partialUserFields
            }
        })
        
        cycles.forEach(cycle => {
            for (const participant of cycle.participants) {
                if (participant.user.equals(userId)) {
                    result = [...result, ...participant.products];
                    
                    // User participates only once
                    break;
                }
            }
        });

        return res.json(result);
    } catch (error){
        console.log(error);
        res.status(400).json({"error":"Problem getting suggestions"});
    }
});

// @route POST /suggestions/request
// @desc Request a suggestion
// @access Private
router.post('/request', auth.isLoggedIn, async (req, res) => {
    try {
        const { product, fromdate, todate } = req.body;
        const userId = ObjectID(req.user.id);
        const productId = ObjectID(product);

        // Find all open cycles that
        const cycles = await PendingCycle.find({
            participants: {
                $elemMatch: { user: userId, requestedProduct: null, products: { $in: productId } }
            }
        }) || [];

        await Promise.all(cycles.map(cycle => WishlistController.requestProductOnCycle(
            cycle._id, userId, productId, Number(fromdate), Number(todate)
        )));

        return res.json(true);
    } catch (error){
        console.log(error);
        res.status(400).json({ error: "Problem requesting a suggestion:" + error.message });
    }
});

module.exports = router;
