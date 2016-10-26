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
  return user.id === "windowslive|a0d76fa73ee8c755" || user.nickname === "finn199411" || user.nickname === "bulb.dan" || user.nickname === "evomjv";
}