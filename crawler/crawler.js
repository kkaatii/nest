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
        var articleUrls = [];
        var res = data.match(/i\\\/\d{7}/g);
        var dest = data.match(/tn-place.+?<\\\/a>/g);
        for (var i = 0; i < 12; i++) {
            articleUrls[i] = {urlNumber: res[i * 3].substr(3, 7), dest: dest[i].substr(dest[i].length - 17, 12)};
        }
        async.each(articleUrls, getArticle, function (err) {
            if (err)
                console.log('err: ' + err);
            self.error = err;
        });
    });
    if (error) {
        cb(error);
    } else cb();
}

function getArticle(articleInfo, cb) {
    var url = "http://www.mafengwo.cn/i/" + articleInfo.urlNumber + ".html";
    var data = {};
    var self = this;
    request({
        url: url,
        headers: {
            'User-Agent': 'Mozilla/5.0'
        }
    }, function (error, response, data) {
        var title, imageUrls, dest = articleInfo.dest;
        /*获取标题*/
        title = data.match(/<h1.*>\s*.+\s*<\/h1>/).toString().replace(/\s*/g, "").replace(/$/g, "").replace(/\//g, "|").match(/>.+</).toString();
        title = title.substring(1, title.length - 1);
        imageUrls = data.match(/data-src="http.+?\.(jpeg|png|jpg).+?"/g);
        for (var i = 0, len = imageUrls.length; i < len; i++) {
            imageUrls[i] = imageUrls[i].substr(10, imageUrls[i].length - 11);
        }
        var params = {
            TableName: "mafengwo-pic-gallery",
            Item: {
                "ArticleUrl": url,
                "Title": title,
                "ImageUrls": imageUrls,
                "Destination": dest
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
                if (data.ItemCollectionMetrics.SizeEstimateRangeGB && data.ItemCollectionMetrics.SizeEstimateRangeGB<4)
                    cb(data.ItemCollectionMetrics.SizeEstimateRangeGB);
            }
        });
    });

}

!function (pageList) {
    async.each(pageList, getRecommendedArticleList, function (err) {
    });
}([1]);

/*
 var delay = 600000;

 async.forever(function(cb) {
 request({

 }, function (error, response, data) {
 if (true) {
 crawl([1]);
 }
 setTimeout(function() { cb(); }, delay);
 });
 });
 */


