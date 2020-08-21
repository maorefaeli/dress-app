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
        requestedProduct: {
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

// Calculate hash based on the user participating the cycle
PendingCycleSchema.methods.calculateHash = function() {
    if (!this.participants || !this.participants.length) {
        throw new Error('Cannot calculate hash with no users');
    }

    // Make sure to sort in order to get the same hash on different appearances order
    const component = this.participants.map(p => p.user.toString()).sort().join();

    this.hash = crypto.encodeSHA256(component);
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
