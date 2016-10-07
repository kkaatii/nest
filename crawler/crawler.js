'use strict';

var http = require('http'),
    fs = require('fs'),
    async = require('async'),
    request = require('request'),
    AWS = require('aws-sdk'),
    assert = require('assert');

AWS.config.update({
    region: "us-west-1"
});

var docClient = new AWS.DynamoDB.DocumentClient();

function getRecommendedArticleList(pageNum, cb) {
    var self = this, error;
    request({
        url: "http://www.mafengwo.cn/ajax/ajax_fetch_pagelet.php?api=%3Amfw%3Apagelet%3ArecommendGinfoApi" +
        "&params=%7B%22type%22%3A0%2C%22objid%22%3A0%2C%22page%22%3A" + pageNum + "%2C%22ajax%22%3A1%2C%22retina%22%3A0%7D",
        headers: {
            'User-Agent': 'Mozilla/5.0'
        }
    }, function (error, response, data) {
        if (!error && response.statusCode == 200) {
            console.log('Retrieved article list on Page:', pageNum);
            var articleUrls = [];
            var re = /i\\\/(\d{7}).+?tn-place.+?data-name=\\"(.+?)\\"/g;
            for (var i = 0, match; (match = re.exec(data)) != null; i++) {
                articleUrls[i] = {urlNumber: match[1], dest: match[2]};
            }
            async.each(articleUrls, getArticle, function (err) {
                if (err)
                    console.log('err: ' + err);
                self.error = err;
            });
        }
    });
    if (error) {
        cb(error);
    } else cb();
}

function getArticle(articleInfo, cb) {
    var url = "http://www.mafengwo.cn/i/" + articleInfo.urlNumber + ".html";
    request({
        url: url,
        headers: {
            'User-Agent': 'Mozilla/5.0'
        }
    }, function (error, response, data) {
        var title, imageUrls, created, dest = unescape(articleInfo.dest.replace(/\\u/g, "%u"));
        var datestring = data.match(/time.+?(\d{4}-\d{2}-\d{2})/);
        if (datestring) {
            created = new Date(datestring[1]).valueOf();
            var offset, offsetmatch = data.match(/day".+?(\d+) 天<\/li>/);
            if (offsetmatch) offset = parseInt(offsetmatch[1]);
            title = data.match(/<h1.*>\s*.+\s*<\/h1>/).toString().replace(/\s*/g, "").replace(/$/g, "").replace(/\//g, "|").match(/>.+</).toString();
            title = title.substring(1, title.length - 1);
            imageUrls = data.match(/data-src="http.+?\.(jpeg|png|jpg).+?"/g);
            for (var i = 0, len = imageUrls.length; i < len; i++) {
                imageUrls[i] = imageUrls[i].substr(10, imageUrls[i].length - 11);
            }
            var params = {
                TableName: "mafengwo-pic-gallery",
                Item: {
                    "Destination": dest,
                    "Created": created + offset,
                    "Title": title,
                    "ArticleUrl": url,
                    "ImageUrls": imageUrls
                },
                ReturnItemCollectionMetrics: 'SIZE',
                ReturnConsumedCapacity: 'INDEXES'
            };

            docClient.put(params, function (err, data) {
                if (err) {
                    console.error("Unable to add article", title);
                } else {
                    console.log("PutItem succeeded:", title);
                    console.log(data);
                }
            });
        }
    });
}

var delay = 60 * 60 * 1000 * 6;

async.forever(function (next) {
    async.each([1, 2, 3], getRecommendedArticleList, function (err) {
    });
    setTimeout(function () {
        next();
    }, delay);
}, function (err) {
    console.log(err);
});



