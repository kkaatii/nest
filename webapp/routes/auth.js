'use strict';

module.exports = function (req, res, next) {
  if (typeof req.user !== 'undefined' && req.user.id === "windowslive|a0d76fa73ee8c755")
    next();
  else {
    req.logout();
    res.redirect('/login');
  }
};