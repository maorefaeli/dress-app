const isUndefined = (value) => typeof value === 'undefined';
exports.isUndefined = isUndefined;

exports.isDefined = (value) => !isUndefined(value);

const isString = (value) => typeof value === 'string';
exports.isString = isString;

exports.isNonEmptyString = (value) => isString(value) && Boolean(value.trim());

const isNumber = (value) => typeof value === 'number' && !Number.isNaN(value);
exports.isNumber = isNumber;

exports.isPositiveNumber = (value) => isNumber(value) && value > 0;

exports.isInteger = (value) => isNumber(value) && value % 1 === 0;

exports.isBoolean = (value) => typeof value === 'boolean';

exports.isDate = (value) => value instanceof Date;

exports.isObject = (value) => value === Object(value);

exports.isArray = (value) => Array.isArray(value);

const isFunction = (value) => typeof value === 'function';
exports.isFunction = isFunction;

exports.isPromise = (value) => !!value && isFunction(value.then) && isFunction(value.catch);

exports.isObjectEmpty = (value) => Object.keys(value).length === 0;
