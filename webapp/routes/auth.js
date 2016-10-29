'use strict';

var devMode = JSON.parse(process.env.DEV_MODE);

module.exports = devMode ? function (req, res, next) {
  next();
} : function (req, res, next) {
  if (typeof req.user !== 'undefined' && validUser(req.user))
    next();
  else {
    req.logout();
    res.redirect('/login');
  }
};

function validUser(user) {
  switch (user.nickname) {
    case "finn199411":
      user.name = "finn199411";
      return true;
    case "bulb.dan":
    case "evomjv":
      user.name = "evomjv";
      return true;
    default:
      return false;
  }
}