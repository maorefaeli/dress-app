const crypto = require('../utils/crypto');
const mongoose = require('mongoose');
const Schema = mongoose.Schema;

// Create Schema
const PendingCycleSchema = new Schema({
    // Users hash that is calculated from the entity for easy matching on DB
    hash: {
        type: String,
        required: true,
    },
    participants: [{
        _id: false,
        // User that wants the products
        user: {
            type: Schema.Types.ObjectId,
            ref: "User",
            required: true
        },
        // The products that are offered for the user
        products: [{
            type: Schema.Types.ObjectId,
            ref: "Product",
        }],
        acceptedProduct: {
            type: Schema.Types.ObjectId,
            ref: "Product",
            required: false
        },
        fromDate: {
            type: Date,
            required: false
        },
        toDate: {
            type: Date,
            required: false
        }
    }]
});

PendingCycleSchema.statics.calculateHash = function(cycle) {
    // Hash is calculated based only on participants.
    if (cycle.participants && cycle.participants.length) {

        // Make sure the sort when needed to get the same hash even on different orders
        const component = cycle.participants.map(p => ({
            user: p.user,
        })).sort(); // Sort here because order of users is not important in a cycle

        return crypto.encodeSHA256(JSON.stringify(component));
    }

    return '';
};

PendingCycleSchema.set('toJSON', {
    virtuals: true,
    versionKey: false,
    transform: function (doc, ret) { delete ret._id }
});

PendingCycleSchema.index({ hash: 1 }, { unique: true });
PendingCycleSchema.index({ 'participants.user': 1 });
PendingCycleSchema.index({ 'participants.products': 1 });

module.exports = Product = mongoose.model('PendingCycle', PendingCycleSchema, 'PendingCycles');
