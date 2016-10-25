var express = require('express');
var passport = require('passport');
var request = require('request');
var router = express.Router();

var env = {
  AUTH0_CLIENT_ID: process.env.AUTH0_CLIENT_ID,
  AUTH0_DOMAIN: process.env.AUTH0_DOMAIN,
  AUTH0_CALLBACK_URL: process.env.AUTH0_CALLBACK_URL || 'http://localhost:3000/callback'
};

var auth = require('./auth');

/* GET home page. */
router.get('/', function (req, res) {
  res.render('index', {env: env});
});

router.get('/login', function (req, res) {
  res.render('login', {env: env});
});

router.get('/logout', function (req, res) {
  req.logout();
  res.redirect('https://kkaatii.auth0.com/v2/logout?federated&returnTo=https%3A%2F%2Fwww.artificy.com');
});

router.get('/callback',
  passport.authenticate('auth0', {failureRedirect: '/url-if-something-fails'}),
  function (req, res) {
    res.redirect(req.session.returnTo || '/mfw');
  });

router.get('/mfw', auth, function (req, res) {
  res.render('mfw');
});

router.get('/api/*', auth, function (req, res) {
  request({url: 'http://localhost:8080' + req.originalUrl},
    function (error, response, data) {
      if (!error && response.statusCode == 200) {
        res.send(data);
      }
    }
  );
});

module.exports = router;
