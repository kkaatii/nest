'use strict';

var http = require('http'),
    fs = require('fs'),
    async = require('async'),
    AWS = require('aws-sdk'),
    assert = require('assert');


AWS.config.update({
    region: "us-west-1"
});

var docClient = new AWS.DynamoDB.DocumentClient();

var scanparams = {
    TableName: "mafengwo-pic-gallery"
};

docClient.scan(scanparams, onScan);

function onScan(err, data) {
    if (err) {
        console.error("Unable to scan the table. Error JSON:", JSON.stringify(err, null, 2));

    } else {
        console.log("Scan succeeded.");
        async.each(data.Items, function(article) {

            var params = {
                TableName: "mfw-gallery",
                Item: {
                    "Destination": article.Destination,
                    "Created": 24201,
                    "Title": article.Title,
                    "ArticleUrl": article.ArticleUrl,
                    "ImageUrls": article.ImageUrls,
                    "Date": article.Created
                }
            };
            docClient.put(params, function (err, data) {
                if (err) {
                    console.error("Update error:", JSON.stringify(err, null, 2));
                }
            });
        }, function (err) {

        });
        if (typeof data.LastEvaluatedKey != "undefined") {
            console.log("Scanning for more...");
            scanparams.ExclusiveStartKey = data.LastEvaluatedKey;
            setTimeout(function () {
                docClient.scan(scanparams, onScan);
            }, 60000);
        }
    }
}