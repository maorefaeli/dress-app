const passport = require('passport');
const express = require('express');
const router = express.Router();

router.use('/users', require('./users'));

router.get('/', (req, res) => {
    res.send({ msg: 'Hello! Server is up' });
});

// Handle login
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
});

// Default request catcher
router.all('*', (req, res) => {
    res.status(404).send({msg: 'Not found'});
});

module.exports = router;
