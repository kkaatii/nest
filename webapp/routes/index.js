var express = require('express');
var passport = require('passport');
var request = require('request');
var router = express.Router();

var env = {
  AUTH0_CLIENT_ID: process.env.AUTH0_CLIENT_ID,
  AUTH0_DOMAIN: process.env.AUTH0_DOMAIN,
  AUTH0_CALLBACK_URL: process.env.AUTH0_CALLBACK_URL || 'http://localhost:3000/callback'
};

var LOCAL_API_SERVER = process.env.LOCAL_API_SERVER;
var REMOTE_API_SERVER = process.env.REMOTE_API_SERVER;

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

router.get('/mfw',
  auth,
  function (req, res) {
    request.get(appendParameter(LOCAL_API_SERVER + '/api/mfw/init', 'name', req.user.name));
    res.render('mfw', {api_url: REMOTE_API_SERVER});
  });

router.all('/api/*', auth, function (req, res) {
  request({url: appendParameter(LOCAL_API_SERVER + req.url, 'name', req.user.name), method: req.method},
    function (error, response, data) {
      if (!error && response.statusCode == 200) {
        res.send(data);
      }
    }
  );
});

function appendParameter(url, paramname, paramvalue) {
  var matched = url.match(/.+\?.+/);
  if (matched !== null) return url + '&' + paramname + '=' + paramvalue;
  else return url + '?' + paramname + '=' + paramvalue;
}

module.exports = router;
