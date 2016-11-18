var express = require('express');
var passport = require('passport');
var request = require('request');
var router = express.Router();
var path=require('path');

var env = {
  AUTH0_CLIENT_ID: process.env.AUTH0_CLIENT_ID,
  AUTH0_DOMAIN: process.env.AUTH0_DOMAIN,
  AUTH0_CALLBACK_URL: process.env.AUTH0_CALLBACK_URL || 'http://localhost:3000/callback'
};

var LOCAL_API_SERVER = process.env.LOCAL_API_SERVER;
var REMOTE_API_SERVER = process.env.REMOTE_API_SERVER;

var auth = require('./auth');

function cleanseUrl(url) {
  var urlparts = url.split('?');
  if (urlparts.length >= 2) {

    var prefix1 = 'oid=';
    var prefix2 = 'on=';
    var pars = urlparts[1].split(/[&;]/g);

    //reverse iteration as may be destructive
    for (var i = pars.length; i-- > 0;) {
      //idiom for string.startsWith
      if ((pars[i].lastIndexOf(prefix1, 0) !== -1) || (pars[i].lastIndexOf(prefix2, 0) !== -1)) {
        pars.splice(i, 1);
      }
    }

    url = urlparts[0] + (pars.length > 0 ? '?' + pars.join('&') : "");
    return url;
  } else {
    return url;
  }
}

function appendParameter(url, params) {
  var matched = url.match(/.+\?.+/);
  if (matched !== null) url += '&' + params[0] + '=' + params[1];
  else url += '?' + params[0] + '=' + params[1];
  for (var i = 1; i < params.length / 2; i++)
    url += '&' + params[i * 2] + params [i * 2 + 1];
  return url;
}

/* GET home page. */
router.get('/', auth, function (req, res) {
  res.render('tube', {server: REMOTE_API_SERVER});
});

router.get('/login', function (req, res) {
  env.RETURN_TO = req.query.returnTo;
  res.render('login', {env: env});
});

router.get('/logout', function (req, res) {
  req.logout();
  res.redirect('https://kkaatii.auth0.com/v2/logout?federated&returnTo=https%3A%2F%2Fwww.artificy.com');
});

router.get('/callback',
  passport.authenticate('auth0', {failureRedirect: '/url-if-something-fails'}),
  function (req, res) {
    res.redirect(req.session.returnTo || req.query.returnTo);
  });

router.get('/mfw',
  auth,
  function (req, res) {
    if (typeof req.user !== 'undefined') request.get(appendParameter(
      LOCAL_API_SERVER + '/api/mfw/init',
      ['oid', req.user.tube.id, 'on', req.user.tube.nickname]
    ));
    res.render('mfw', {server: REMOTE_API_SERVER});
  });

router.get('/api/*', auth, function (req, res) {
  request({
      url: appendParameter(
        LOCAL_API_SERVER + cleanseUrl(req.url),
        ['oid', req.user.tube.id, 'on', req.user.tube.nickname]
      ),
      method: req.method
    },
    function (error, response, data) {
      if (!error && response.statusCode == 200) {
        res.send(data);
      }
    }
  );
});

router.post('/api/*', auth, function (req, res) {
  if (req.headers['content-type'].startsWith('application/json')) {
    var body = req.body;
    body['ownerId'] = req.user.tube.id;
    body.frame = body.frame === null ? '@' + req.user.tube.nickname : body.frame;
    var options = {
      url: LOCAL_API_SERVER + cleanseUrl(req.url),
      json: true,
      body: body,
      method: 'post'
    };
    request(options, function (error, response, data) {
      if (!error && response.statusCode == 200) {
        res.send(data);
      }
    });
  }
});

router.get('/betrue', function (req, res) {
  request({
    url: appendParameter(
      LOCAL_API_SERVER + '/api/tube/point-get-frame',
      ['frame', 'BeTrue@Dun']
    ),
    method: 'get'
  }, function (error, response, data) {
    if (!error && response.statusCode == 200){
      data = JSON.parse(data);
      res.render('betrue', {articles: data})
    }
  })
});

module.exports = router;
