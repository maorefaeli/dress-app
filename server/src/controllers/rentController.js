const validators = require('../utils/validators');
const Product = require('../models/Product');
const Rent = require('../models/Rent');
const User = require('../models/User');
const { getDateComponent, getAmountOfDays } = require('../utils/date');

const isRentContainErrors = (rent) => {
    if (!validators.isObject(rent.user)) return 'User cannot be empty';
    if (!validators.isObject(rent.product)) return 'Product cannot be empty';
    if (!validators.isDate(rent.fromdate)) return 'From date cannot be empty';
    if (!validators.isDate(rent.todate)) return 'To date cannot be empty';
    return '';
};

const isRentDatesValid = (product, fromDate, toDate) => {
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

exports.isRentDatesValid = isRentDatesValid;

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

    const rentingDays = getAmountOfDays(newRent.todate - newRent.fromdate) + 1;
    const coins = rentingDays * product.price;

    if (!isFree && coins > fromUser.coins) {
        throw new Error('Missing', coins - fromUser.coins, 'coins. Try add it to wishlist');
    }

    try {
        const rentingDate = {
            fromdate: newRent.fromdate,
            todate: newRent.todate
        }
        await Product.findByIdAndUpdate(productId, { $push: { rentingDates: rentingDate } });

        newRent = await newRent.save();
        console.log("New rent:", rent.id);

        if (!isFree) {
            await User.findByIdAndUpdate(fromUser.id, { $inc: { coins: coins * -1 } });
            await User.findByIdAndUpdate(toUser.id, { $inc: { coins: coins } });
            console.log("Transferred", coins, "coins from", fromUser.id, "to", toUser.id);
        }
        
        return newRent;
    } catch (error) {
        throw new Error('Save failed');
    }    
};
