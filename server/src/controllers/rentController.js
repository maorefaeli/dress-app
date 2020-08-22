const validators = require('../utils/validators');
const Product = require('../models/Product');
const Rent = require('../models/Rent');
const User = require('../models/User');
const { getDateComponent } = require('../utils/date');

const isRentContainErrors = (rent) => {
    if (!validators.isNonEmptyString(rent.user)) return 'User cannot be empty';
    if (!validators.isNonEmptyString(rent.product)) return 'Product cannot be empty';
    if (!validators.isNonEmptyString(rent.fromdate)) return 'From date cannot be empty';
    if (!validators.isNonEmptyString(rent.todate)) return 'To date cannot be empty';
    return '';
};

exports.addRent = async (userId, productId, fromdate, todate, isFree) => {
    const validFromDate = getDateComponent(fromdate);
    const validToDate = getDateComponent(todate);

    let newRent = new Rent ({
        user: userId,
        product: productId,
        fromdate: validFromDate,
        todate: validToDate
    });

    let error = isRentContainErrors(newRent);
    if (error) {
        throw new Error(error);
    }

    const fromUser = await User.findById(userId);
    if (!fromUser) {
        throw new Error("User not found");
    }

    const product = await Product.findById(productId);
    if (!product) {
        throw new Error("Product not found");
    }

    if (!isRentDatesValid(product, newRent.fromdate, newRent.todate)) {
        throw new Error(product.name, "is taken on specified dates");
    }

    const toUser = await User.findById(product.user);
    if (!toUser) {
        throw new Error("Product's owner not found");
    }
    const rentingDays = rentingDate.todate - rentingDate.fromdate;

    if (!isFree) {
        // TODO: Check coins of fromUser: throw if not valid
        
        if (fromUser.coins < product.price*rentingDays)
            throw new Error('Not enough coins');
    }

    try {
        const rentingDate = {
            "fromdate": newRent.fromdate,
            "todate": newRent.todate
        }
        await Product.findByIdAndUpdate(productId, { $push: { rentingDates: rentingDate } });

        newRent = await newRent.save();

        if (!isFree) {
            // TODO: Transfer coins from fromUser to toUser
            const coins = rentingDays*product.price;
            const negPrice = coins * -1;
            const newFromUser = await User.findByIdAndUpdate(userId, { $inc: {"coins": negPrice} });
            const newToUser = await User.findByIdAndUpdate(product.user, { $inc: {"coins": coins} });
            console.log(newFromUser);
            console.log(newToUser);
        }

        return newRent;
    } catch (error) {
        throw new Error('Save failed');
    }    
};

exports.isRentDatesValid = (product, fromDate, toDate) => {
    if (product.fromdate > fromDate || product.todate < toDate) return false;

    if (product.rentingDates && product.rentingDates.length) {
        for (let index = 0; index < product.rentingDates.length; index++) {
            const rt = product.rentingDates[index];
            if (!(rt.todate < fromDate && rt.fromdate > toDate)) {
                return false;
            }
        }
    }

    return true;  
};
