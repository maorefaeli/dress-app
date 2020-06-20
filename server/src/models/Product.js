const mongoose = require('mongoose');
const Schema = mongoose.Schema;

// Create Schema
const ProductSchema = new Schema({
    user: {
        type: Schema.Types.ObjectId,
        ref: "User",
        required: true
    },
    name: {
        type: String,
        required: true
    },
    price: {
        type: Number,
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
    image: {
        type: String,
        required: false
    },
    rentingDates: {
        type: [{
            fromdate: Date,
            todate: Date
        }],
        required: false
    }
});

ProductSchema.set('toJSON', {
    virtuals: true,
    versionKey: false,
    transform: function (doc, ret) { delete ret._id }
});

module.exports = Product = mongoose.model('Product', ProductSchema, 'Products');