const http = require('http');
const express = require('express');
const path = require('path');


app = express();
server = http.createServer(app);

const port = 3001;

app.get('/hello', function(req, res) {
	res.send('hello world');
});

server.listen(port, () => {
  console.log(`Warewolf started`);
});
