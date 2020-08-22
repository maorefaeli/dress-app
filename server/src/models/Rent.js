const mongoose = require('mongoose');
const Schema = mongoose.Schema;

// Create Schema
const RentSchema = new Schema({
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
    fromdate: {
        type: Date,
        required: true
    },
    todate: {
        type: Date,
        required: true
    },
    isFinished: {
        type: Boolean,
        required: false
    }
});

RentSchema.set('toJSON', {
    virtuals: true,
    versionKey: false,
    transform: function (doc, ret) { delete ret._id }
});

RentSchema.index({ user: 1, isFinished: 1 });
RentSchema.index({ product: 1 });

module.exports = Rent = mongoose.model('Rent', RentSchema, 'Rents');
