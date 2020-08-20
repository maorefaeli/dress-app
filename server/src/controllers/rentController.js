const validators = require('../utils/validators');
const Product = require('../models/Product');
const Rent = require('../models/Rent');

exports.addRent = async (userId, productId, fromdate, todate, rentFromUser) => {
    let newRent = new Rent ({
        user: userId,
        product: productId,
        fromdate,
        todate,
        rentFromUser: rentFromUser
    });

    let error = isRentContainErrors(newRent);
    if (error) {
        return res.status(400).json({ error });
    }

    let rentingDate = {
        "fromdate": fromdate,
        "todate": todate
    }
    const newProduct = await Product.findByIdAndUpdate(productId, { $push: { rentingDates: rentingDate } }, { new: true });
    console.log(newProduct);

    newRent = await newRent.save();
    return newRent;
};
