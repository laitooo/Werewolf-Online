const http = require('http');
const express = require('express');
var api = require('./myNetwork');
var bodyParser = require('body-parser');
var mysql = require('mysql');
var gameSocket = require('./gameSocket');
var imagesManager = require('./imagesManager');
const path = require('path');

app = express();
server = http.createServer(app);
io = require('socket.io').listen(server);

const hostname = '192.168.43.211';
const port = 3000;

var con = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "",
  database: "mydb"
});

con.connect(function(err) {
    if (err) throw err;
});

var dir = path.join('./public/images');

app.use('*/images',express.static(dir));


app.use(bodyParser.json()); 
app.use(bodyParser.urlencoded({ extended: false })); 


app.post('/addUser', function(req, res) {
	api.userExists(con,req.body.email,function(result){
		if(result.length == 0){
	    	api.addUser(con,req.body, function(userId){
	    		imagesManager.randomProfile(userId,function(imageFile){
	    			res.json({ error: false, id: userId, pic:imageFile});
	    		});
	    	});
		}else{
			res.json({ error: true, state:1})
		}
	});
});

app.post('/isUser', function(req,res) {
	console.log(req.body);
	api.userExists(con,req.body.email,function(result){
		if(result.length == 1){
			if (result[0].password == req.body.password) {
				console.log('user logged');
				res.json({error:false,user:result[0]});
			}else{
				res.json({error:true,state:2})
			}
		}else{
			res.json({error:true,state:1})
		}
	});
});


app.post('/newGame', function(req, res) {
	api.createGame(con,req.body,function(result){
	  	res.json({ error: false, id: userId});
	});
});




var nsp = io.of('/games')
nsp.on('connection', function (socket) {

	socket.on('join', function(userNickname) {
		console.log(userNickname +" : has joined the chat "  );
		var num_online = socket.client.conn.server.clientsCount;
	    socket.broadcast.emit('num_players',num_online);
	    socket.emit('num_players',num_online);
	    api.loadActiveGames(con,function(Result){
	    	socket.emit('load_games',Result);
	    });
	    
	})

	socket.on('createGame', function(game) {
		api.createGame(con,game,function(id){
			game.id = id;
			console.log(game.nameOwner + " : has created new game "  );
	    	socket.emit('youCreatedGame',game);
	    	socket.broadcast.emit('gameCreated',game);
	    	gameSocket.createSocket(con,game);
		});
	})

	socket.on('disconnect', function() {
	    var num_online = socket.client.conn.server.clientsCount;
	    socket.broadcast.emit('num_players',num_online);
	})

});



/*app.post('/uploadImage', upload.single('image'), async function (req, res) {
  await console.log('image uploaded');
});*/





server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});
