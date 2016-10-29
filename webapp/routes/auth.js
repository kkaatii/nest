'use strict';

var authEnabled = JSON.parse(process.env.ENABLE_AUTH);

module.exports = authEnabled ? function (req, res, next) {
  if (typeof req.user !== 'undefined' && validUser(req.user))
    next();
  else {
    req.logout();
    res.redirect('/login');
  }
} : function (req, res, next) {
  next();
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