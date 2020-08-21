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
    },
    reviewQuentity: {
        type: Number,
        required: false,
        default: 1
    },
    //  remove avg when refactor the calculation of user's rating
    avg: {
        type: Number,
        required: false
    },
    coins: {
        type: Number,
        required: false,
        default: 0
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
    return this.reviewSum/this.reviewQuentity;
});

UserSchema.index({ username: 1 }, { unique: true });
UserSchema.index({ 'wishlist.user': 1 });
UserSchema.index({ 'wishlist.products': 1 });

module.exports = User = mongoose.model('User', UserSchema, 'Users');
