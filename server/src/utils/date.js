/**
 * Returns a Date object without time component
 */
exports.getDateComponent = (date) => new Date(new Date(date).toDateString());
