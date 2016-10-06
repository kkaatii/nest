var express = require('express');

var app = express();

app.get("/", function(req,res){
  res.send("<h1>Let me see...</h1>");
});

app.listen(3000);