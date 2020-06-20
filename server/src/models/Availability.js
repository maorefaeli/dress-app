module.exports = class Availability {
    constructor(fromDate, toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
 
    display() {
        console.log(this);
        console.log(this.fromDate + " " + this.toDate);
    }

    addNew(productAvailabilityDates) {
        productAvailabilityDates.push(this)
        console.log()
    }
}