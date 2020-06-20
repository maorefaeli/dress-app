const crypto = require('../utils/crypto');
const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const ParticipantState = Object.freeze({
    Waiting: 0,
    Approved: 1,
    Declined: 2,
});

// Create Schema
const PendingCycleSchema = new Schema({
    hash: {
        type: String,
        required: true,
    },
    isDeclined: {
        type: Boolean,
        required: false,
    },
    participants: [{
        _id: false,
        user: {
            type: Schema.Types.ObjectId,
            ref: "User",
            required: true
        },
        product: {
            type: Schema.Types.ObjectId,
            ref: "Product",
            required: true
        },
        state: {
            type: Number,
            enum: Object.values(ParticipantState),
            required: true,
            default: ParticipantState.Waiting,
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

PendingCycleSchema.statics.calculateHash = function(pendingCycle) {
    if (pendingCycle.participants && pendingCycle.participants.length) {
        const component = pendingCycle.participants.map(p => ({
            user: p.user,
            product: p.product,
        }));
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

module.exports = Product = mongoose.model('PendingCycle', PendingCycleSchema, 'PendingCycles');
exports.ParticipantState = ParticipantState;
