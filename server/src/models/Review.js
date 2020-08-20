const mongoose = require('mongoose');
const Schema = mongoose.Schema;

// Create Schema
const ReviewSchema = new Schema({
    user: {
        type: Schema.Types.ObjectId,
        ref: "User",
        required: true
    },
    rent: {
        type: Schema.Types.ObjectId,
        ref: "Rent",
        required: true
    },
    score: {
        type: Number,
        required: true
    },
});

ReviewSchema.set('toJSON', {
    virtuals: true,
    versionKey: false,
    transform: function (doc, ret) { delete ret._id }
});

module.exports = Review = mongoose.model('Review', ReviewSchema, 'Reviews');