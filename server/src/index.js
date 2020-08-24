const passport = require('passport');
const LocalStrategy = require('passport-local').Strategy;
const express = require('express');
const session = require('express-session');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const MongoStore = require('connect-mongo')(session);
const cors = require('cors');
const morgan = require('morgan');
const keys = require('./config/keys');

const app = express();
const port = process.env.PORT || 3000;

// Load User model
const User = require('./models/User');
const validators = require('./utils/validators');

passport.use(new LocalStrategy(
    function(username, password, done) {
        if (!validators.isNonEmptyString(username)) {
            return done(null, false, { message: 'Username cannot be empty.' });
        }
        if (!validators.isNonEmptyString(password)) {
            return done(null, false, { message: 'Password cannot be empty.' });
        }

        User.findOne({ username }).then((user) => {
            if (!user || !user.isPasswordValid(password)) {
                return done(null, false, { message: 'Incorrect username or password.' });
            }
            return done(null, user);
        });
    }
));

passport.serializeUser(function(user, done) {
    if (user) done(null, user.id);
});
  
passport.deserializeUser(function(id, done) {
    User.findById(id, function(err, user) {
        done(err, user);
    });
});

// Connect to mongoDB
mongoose
    .connect(keys.mongoURI, {
        useNewUrlParser: true,
        useFindAndModify: false,
        useCreateIndex: true,
        useUnifiedTopology:  true,
        autoCreate: true,
        autoIndex: true,
    })
    .then(() => console.log('MongoDB Connected'))
    .catch(err => console.log(err));

// Body parser middleware
app.use(express.static('public'));
app.use(session({ 
    name: 'dressapp.sid',
    secret: 'dressapp',
    resave: true,
    saveUninitialized: false,
    cookie: { secure: false },
    store: new MongoStore({ mongooseConnection: mongoose.connection }),
}));
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(passport.initialize());
app.use(passport.session());
app.use(morgan('dev'))
app.disable('x-powered-by');
app.use(cors());

// Load all routers
app.use(require('./routers'));

// Start server
app.listen(port, () => console.log(`Server running on port ${port}`));
