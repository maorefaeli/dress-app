const express = require('express');
const router = express.Router();
const auth = require('../utils/auth');
const WishlistController = require('../controllers/wishlistController');
const PendingCycle = require('../models/PendingCycle');
const ObjectID = require('mongodb').ObjectID;

// @route GET /suggestions
// @desc Get smart suggestions for the user. Output: [{cycleId, product}]
// @access Private
router.get('/', auth.isLoggedIn, async (req, res) => {
    try {
        const result = [];
        const userId = ObjectID(req.user.id);
        const cycles = await PendingCycle.find({ 'participants.user': userId }).populate('participants.products') || [];

        for (const cycle of cycles) {
            for (const participant of cycle.participants) {
                if (participant.user.equals(userId)) {
                    participant.products.forEach(product => {
                        result.push({
                            cycleId: cycle._id,
                            product
                        });
                    });
                    break;
                }
            }
        }

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
        const { cycle, product, fromdate, todate } = req.body;

        await WishlistController.requestProductOnCycle(
            ObjectID(cycle), ObjectID(re.user.id), ObjectID(product), fromdate, todate
        );
        return res.json(true);
    } catch (error){
        console.log(error);
        res.status(400).json({ error: "Problem requesting a suggestion:" + error.message });
    }
});

module.exports = router;
