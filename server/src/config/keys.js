const devKeys = require('./keys.dev.js');
const prodKeys = require('./keys.prod.js');

const keys = process.env.NODE_ENV === 'production' ? prodKeys : devKeys ;
keys.coinsRewardForReview = 10;
keys.coinsNewUser = 300;

module.exports = keys;
