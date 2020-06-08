const http = require('http');
var url = require('url');
var us = require('upper-case');

const hostname = '192.168.43.211';
const port = 3000;


const server = http.createServer((req, res) => {
  var q = url.parse(req.url, true);
  
    if (q.pathname == '/api') {
        res.writeHead(200, {'Content-Type': 'text/html'});
        id = my.addUser(q.query, function(id) {
            res.write('added user successfully \n' + 'id = ' + id);
            res.end
        });
    }else if(q.pathname == '/socket') {
        console.log('socket '); 
        var cp = require('child_process');

        var n = cp.fork('index.js');

        n.on('message', function(m) {
          console.log('PARENT got message:', m);
        });

        n.send({ hello: 'world' });
        res.end();
    }else{
        res.writeHead(404, {'Content-Type': 'text/html'});
        res.end(us.upperCase("404 Not Found"));
    }
    //return res.end();
  

});

server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});



