const isLoggedIn = (req, res, next) => {
    if (req.isAuthenticated()){
        return next()
    }
    return res.status(401).json({"message": "not authenticated"})
}
exports.isLoggedIn = isLoggedIn;

const isAdminLoggedIn = (req, res, next) => {
    if (req.isAuthenticated() && req.user.isAdmin){
        return next()
    }
    return res.status(401).json({"message": "not admin"})
}
exports.isAdminLoggedIn = isAdminLoggedIn;
