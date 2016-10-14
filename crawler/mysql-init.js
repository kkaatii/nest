'use strict';

var http = require('http'),
    fs = require('fs'),
    async = require('async'),
    AWS = require('aws-sdk'),
    assert = require('assert'),
    mysql = require('mysql');

var connection = mysql.createConnection({
    host: '',
    user: 'sa',
    password: 'GXd4uu!DMM'
});

connection.connect();



