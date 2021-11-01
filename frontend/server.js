// const http = require('http');
// const fs = require('fs');
//
// const requestListener = function (req, res) {
//   console.log(req);
//   res.writeHead(200, {'Content-Type': 'text/html'});
//   fs.readFile('./index.html', null, function(error, data) {
//     if(error) {
//       res.writeHead(404);
//       res.write('File not found');
//     } else {
//       res.write(data);
//     }
//     res.end();
//   })
// }
//
// http.createServer(requestListener).listen(24567);
const express = require("express");
const bodyParser = require("body-parser");
const app = express();
let requestedJSON = {};

app.listen(24567, () => {
  console.log("Application started and Listening on port 24567");
});

// loading css and js using this
app.use(express.static(__dirname));

// get our app to use body parser
app.use(bodyParser.urlencoded({ extended: true }))

app.post("/query", (req, res) => {
  requestedJSON = req.body;
  res.sendFile(__dirname + "/index.html");
});

app.get("/query", (req, res) => {
  res.status(200).send(requestedJSON);
})