const crypto = require('crypto');
exports.encodeSHA256 = (input) => crypto.createHash('sha256').update(input).digest('base64');
