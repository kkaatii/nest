'use strict';

module.exports = function (req, res, next) {
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