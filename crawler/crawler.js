'use strict';

var http = require('http'),
    fs = require('fs'),
    async = require('async'),
    request = require('request'),
    AWS = require('aws-sdk'),
    assert = require('assert');

var MAX_STORAGE = 100, MAX_PAGE_NUM = 1;
var articleUrls = [], pageList = [];
for (var i = 0; i < MAX_PAGE_NUM; i++) {
    pageList[i] = i + 1;
};

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
        var res = data.match(/i\\\/\d{7}/g);
        var dest = data.match(/tn-place.+?<\\\/a>/g);
        for (var i = 0; i < 12; i++) {
            articleUrls[i] = {urlNumber: res[i * 3].substr(3, 7), dest: dest[i].substr(dest[i].length - 17, 12)};
        }
        ;
        async.each(articleUrls, getArticle, function (err) {
            console.log('err: ' + err);
        });
    });
}

function getArticle(articleInfo) {
    var url = "http://www.mafengwo.cn/i/" + articleInfo.urlNumber + ".html";
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
            }
        };

        docClient.put(params, function(err, data) {
            if (err) {
                console.error("Unable to add article", title);
            } else {
                console.log("PutItem succeeded:", title);
            }
        });
    });
}

async.each(pageList, getRecommendedArticleList, function(err) {
    console.log('err: ' + err);
});



