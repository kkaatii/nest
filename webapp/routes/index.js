var express = require('express');
var passport = require('passport');
var router = express.Router();

var env = {
  AUTH0_CLIENT_ID: process.env.AUTH0_CLIENT_ID,
  AUTH0_DOMAIN: process.env.AUTH0_DOMAIN,
  AUTH0_CALLBACK_URL: process.env.AUTH0_CALLBACK_URL || 'http://localhost:3000/callback'
};

/* GET home page. */
router.get('/', function (req, res, next) {
  //res.render('index', { title: 'Express' });
  res.render('index', {env: env});
});

router.get('/login', function (req, res) {
  res.render('login', {env: env});
});

router.get('/logout', function (req, res) {
  req.logout();
  res.redirect('/');
});

router.get('/callback',
  passport.authenticate('auth0', {failureRedirect: '/url-if-something-fails'}),
  function (req, res) {
    res.redirect(req.session.returnTo || '/mfw');
  });

router.get('/mfw', function (req, res, next) {
  if (typeof req.user !== 'undefined' && req.user.id === "windowslive|a0d76fa73ee8c755")
    res.render('mfw');
  else {
    req.logout();
    res.redirect('/login');
  }
});

module.exports = router;
