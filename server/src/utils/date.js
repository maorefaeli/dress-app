/**
 * Returns a Date object without time component
 */
exports.getDateComponent = (date) => new Date(new Date(date).toDateString());

/**
 * Returns amount of days of a date representing as milliseconds
 */
exports.getAmountOfDays = (milliseconds) => milliseconds / 1000 / 60 / 60 / 24;

exports.parseSearch = (value) => {
    const parts = value.split("/");
    return Date.parse(`${parts[1]}-${parts[0]}-${parts[2]}`);
}
