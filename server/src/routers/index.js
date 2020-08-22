const passport = require('passport');
const express = require('express');
const router = express.Router();

router.use('/users', require('./users'));
router.use('/products', require('./products'));
router.use('/rents', require('./rents'));
router.use('/wishlist', require('./wishlist'));
router.use('/reviews', require('./reviews'));
router.use('/suggestions', require('./suggestions'));

router.get('/check', (req, res) => {
    res.send({ msg: 'Hello! Server is up' });
});

// Handle login
// POST /login?username=XXX&password=XXX
router.post('/login', passport.authenticate('local', { failWithError: true }),
    function(req, res, next) {
        // handle success
        return res.json(true);
    },
    function(err, req, res, next) {
        // handle error
        return res.json(err);
    }
);

// Handle logout
router.get('/logout', function(req, res) {
    req.logout();
    return res.json(true);
});

// Default request catcher
router.all('*', (req, res) => {
    res.status(404).send({msg: 'Not found'});
});

module.exports = router;
