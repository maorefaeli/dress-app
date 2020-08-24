const User = require('../models/User');

const partialUserFields = 'id firstName lastName averageScore reviewQuantity address';
exports.partialUserFields = partialUserFields;

const fullUserFields = `${partialUserFields} coins username`;
exports.fullUserFields = fullUserFields;
