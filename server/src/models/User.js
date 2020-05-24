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
        items: {
            type: [Schema.Types.ObjectId],
            ref: 'Product',
        },
    }],
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

UserSchema.index({ username: 1 });
UserSchema.index({ 'wishlist.user': 1 });
UserSchema.index({ 'wishlist.items': 1 });

module.exports = User = mongoose.model('User', UserSchema, 'Users');
