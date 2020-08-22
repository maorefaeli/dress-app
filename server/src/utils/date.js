/**
 * Returns a Date object without time component
 */
exports.getDateComponent = (date) => new Date(new Date(date).toDateString());

/**
 * Returns amount of days of a date representing as milliseconds
 */
exports.getAmountOfDays = (milliseconds) => milliseconds / 1000 / 60 / 60 / 24;
