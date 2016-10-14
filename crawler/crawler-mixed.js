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

function getRecommendedArticleList(pageNum) {
    request({
        url: "http://www.mafengwo.cn/ajax/ajax_fetch_pagelet.php?api=%3Amfw%3Apagelet%3ArecommendGinfoApi" +
        "&params=%7B%22type%22%3A0%2C%22objid%22%3A0%2C%22page%22%3A" + pageNum + "%2C%22ajax%22%3A1%2C%22retina%22%3A0%7D",
        headers: {
            'User-Agent': 'Mozilla/5.0'
        }
    }, function (error, response, data) {
        if (!error && response.statusCode == 200) {
            console.log('Retrieved article list on Page:', pageNum);
            var articleIds = [], match;
            var re = /i\\\/(\d{7}).+?tn-place.+?data-name=\\"(.+?)\\"/g;
            for (var i = 0; ((match = re.exec(data)) != null) && (i < 3); i++) {
                articleIds.push([match[1], unescape(match[2].replace(/\\u/g, "%u"))]);
            }

            async.each(articleIds, function (articleId) {
                var dest = articleId[1];
                var url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + encodeURI(dest) + "&key=AIzaSyCSFkgwLSAbnYip79h9q3NvS-BP2ILIHWg";
                request({
                    url: url
                }, function (error, response, data) {
                    var geoInfo = JSON.parse(data);
                    if (geoInfo.status === "OK") {
                        var splitaddr = geoInfo.results[0].formatted_address.split(', ');
                        var country = splitaddr.pop();
                        if (/\d/.test(country)) country = splitaddr.pop();
                        if (!(country === 'China' || country === 'Yemen' || country === 'Botswana'))
                            getArticle(parseInt(articleId[0]), dest, country)
                    } else console.error(geoInfo.error_message);
                })

            }, function (err) {
                if (err)
                    console.log('err: ' + err);
            });
        }
    });
}

function getArticle(articleId, dest, country) {
    var url = "http://www.mafengwo.cn/i/" + articleId + ".html";
    request({
        url: url,
        headers: {
            'User-Agent': 'Mozilla/5.0'
        }
    }, function (error, response, data) {
        var title, imageUrls, date;
        var datestring = data.match(/time.+?(\d{4}-\d{2}-\d{2})/);
        if (datestring) {
            date = new Date(datestring[1]);
            title = data.match(/<h1.*>\s*.+\s*<\/h1>/).toString().replace(/\s*/g, "").replace(/$/g, "").replace(/\//g, "|").match(/>.+</).toString();
            title = title.substring(1, title.length - 1);
            imageUrls = data.match(/data-src="http.+?\.(jpeg|png|jpg).+?"/g);
            for (var i = 0, len = imageUrls.length; i < len; i++) {
                imageUrls[i] = imageUrls[i].substr(10, imageUrls[i].length - 11);
            }
            var params = {
                TableName: "mfw-gallery-v2",
                Item: {
                    "ArticleId": articleId,
                    "Date": date,
                    "Destination": dest,
                    "Title": title,
                    "ImageUrls": imageUrls
                }
            };

            docClient.put(params, function (err, data) {
                if (err) {
                    console.error("Unable to add article", title);
                } else {
                    console.log("PutItem succeeded:", title);
                    console.log(data);
                    request.post({
                        url: 'http://localhost:8080/api/mfw/create',
                        form: {
                            "articleId": articleId,
                            "country": country,
                            "created": new Date(),
                            "date": date
                        }
                    }, function (err, response, body) {
                        if (err) console.log(err);
                        else console.log(body);
                    })
                }
            });
        }
    });
}

var delay = 30 * 60 * 1000;

async.forever(function (next) {
    async.each([1], getRecommendedArticleList, function (err) {
    });
    setTimeout(function () {
        next();
    }, delay);
}, function (err) {
    console.log(err);
});
