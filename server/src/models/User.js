const crypto = require('../utils/crypto');
const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const encryptPassword = (password) => crypto.encodeSHA256(password);

// Create Schema
const UserSchema = new Schema({
    username: {
        type: String,
        required: true,
    },
    firstName: {
        type: String,
        required: true,
    },
    lastName: {
        type: String,
        required: true,
    },
    password: {
        type: String,
        required: true,
    },
    isAdmin: {
        type: Boolean,
        required: false,
    },
    wishlist: [{
        _id: false,
        user: {
            type: Schema.Types.ObjectId,
            ref: 'User',
        },
        products: [{
            type: Schema.Types.ObjectId,
            ref: 'Product',
        }],
    }],
    reviewSum: {
        type: Number,
        required: false,
        default: 0
    },
    reviewQuantity: {
        type: Number,
        required: false,
        default: 0
    },
    coins: {
        type: Number,
        required: false,
        default: 0
    },
    address: {
        type: String,
        required: false
    }
});

UserSchema.methods.isPasswordValid = function(password) {
    return this.password === encryptPassword(password);
};

UserSchema.statics.encryptPassword = function(password) {
    return encryptPassword(password);
};

UserSchema.set('toJSON', {
    virtuals: true,
    versionKey: false,
    transform: function (doc, ret) { delete ret._id }
});

UserSchema.virtual('avg').get(function () {
    return this.reviewQuantity ? this.reviewSum / this.reviewQuantity : 0;
});

UserSchema.index({ username: 1 }, { unique: true });
UserSchema.index({ 'wishlist.user': 1 });
UserSchema.index({ 'wishlist.products': 1 });

module.exports = User = mongoose.model('User', UserSchema, 'Users');
