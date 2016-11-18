'use strict';

var authEnabled = JSON.parse(process.env.ENABLE_AUTH);

module.exports = authEnabled ? function (req, res, next) {
  if (typeof req.user !== 'undefined' && validUser(req.user))
    next();
  else {
    req.logout();
    res.redirect('/login?returnTo=' + encodeURI(req.originalUrl));
  }
} : function (req, res, next) {
  req.user = {tube: {id: 1, nickname: 'Guest'}};
  next();
};

function validUser(user) {
  return user.tube !== null;
}