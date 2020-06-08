const http = require('http');
const express = require('express');
var api = require('./myNetwork');
var bodyParser = require('body-parser');
var mysql = require('mysql');
var gameSocket = require('./gameSocket');
var chatSocket = require('./chatSocket');
var groupSocket = require('./groupSocket');
var imagesManager = require('./imagesManager');
const path = require('path');

app = express();
server = http.createServer(app);
io = require('socket.io').listen(server);

const hostname ='192.168.43.210'
//const hostname = '0.0.0.0';
const port = 3001;

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

app.get('/hello', function(req, res) {
	res.send('hello world');
});

app.post('/addUser', function(req, res) {
	api.isUserExists(con, req.body.email, req.body.username, function(result){
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
	api.userExists(con,req.body.email,req.body.username,function(result){
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


/*app.post('/newGame', function(req, res) {
	api.createGame(con,req.body,function(result){
	  	res.json({ error: false, id: userId});
	});
});

app.post('/addFriend', function(req, res) {
	u = req.body;
	api.areFriends(con,u.idSender,u.idReceiver,function(firends){
		if (firends) {
			res.json({error:true, state:4})
		}else{
			api.sendRequest(con,req.body,function(result){
				res.json(result);
			});
		}
	});
});*/


api.loadAllFriends(con, function(result) {
	for (var i=0;i<result.length;i++){
		chatSocket.createSocket(con,result[i].id);
	}
});

api.loadAllGroups(con, function(result) {
	for (var i=0;i<result.length;i++){
		groupSocket.createSocket(con,result[i].id);
	}
});


var nsp = io.of('/games')
nsp.on('connection', function (socket) {

	socket.on('join', function(userNickname) {
		console.log(userNickname +" : has logged in "  );
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
	    	gameSocket.createSocket(con,game,socket);
		});
	})

	socket.on('disconnect', function() {
	    var num_online = socket.client.conn.server.clientsCount;
	    socket.broadcast.emit('num_players',num_online);
	})

});


var nsp2 = io.of('/chats')
nsp2.on('connection', function (socket) {

	socket.on('join', function(id) {
	    api.loadRequests(con,id,function(Result){
	    	socket.emit('loadRequests',Result);
	    });
	    api.loadFriends(con,id,function(Result){
	    	socket.emit('loadFriends',Result);
	    });
	});

	socket.on('sendRequest', function(idSender,nameReceiver,nameSender) {
		api.areFriends(con,idSender,nameReceiver,function(firends){
			if (firends) {
				res.json({error:true, state:4})
			}else{
				api.sendRequest(con,
					nameReceiver,
					{idSender:idSender, nameSender:nameSender, accepted:0},
					function(result){
					socket.emit('requestState',result);
					socket.broadcast.emit('requestSent',{idSender:idSender, idReceiver:result.idReceiver, nameSender:nameSender})
				});
			}
		});
	});

	socket.on('acceptRequest', function(idSender,idReceiver,nameSender,nameReceiver){
		api.acceptRequest(con,idSender,idReceiver,nameSender,nameReceiver,function(id){
			socket.emit('accepted',{error:false, idSender:idSender, id:id});
			socket.broadcast.emit('requestAccepted',
				{id:id, idSender:idSender, idReceiver:idReceiver, nameSender:nameSender, nameReceiver:nameReceiver});
			chatSocket.createSocket(con,id);
		});
	});

	socket.on('cancelRequest', function(idSender,idReceiver,nameSender,nameReceiver){
		api.cancelRequest(con,idSender,idReceiver,nameSender,nameReceiver,function(){
			socket.emit('canceled',{error:false, id:idSender});
		});
	});

});



var nsp3 = io.of('/groups')
nsp3.on('connection', function (socket) {

	socket.on('join', function(id) {
	    api.loadGroups(con,id,function(Result){
	    	socket.emit('loadGroups',Result);
	    });
	});

	socket.on('createGroup', function(idAdmin, nameAdmin, groupName) {
		api.createGroup(con,
		 {idAdmin:idAdmin, nameAdmin:nameAdmin, name:groupName, numMembers:1},
		 function(result) {
		 	if (!result.error) {
				console.log(nameAdmin + ' created group : ' + groupName);
			}
			socket.emit('groupCreated',result);
			chatSocket.createSocket(con,result.id);
		});
	});

});


/*app.post('/uploadImage', upload.single('image'), async function (req, res) {
  await console.log('image uploaded');
});*/


app.post('/loadGroupMembers', function(req, res) {
	api.loadMembers(con,req.body.id,function(result){
		//console.log(result);
	  	if (result.length < 1) {
	  		res.json({error:true, state:1});
	  	}else{
	  		res.json(result);
	  	}
	});
});

app.post('/loadFriends', function(req, res) {
	api.loadFriends(con,req.body.id,function(Result){
		    res.json(Result);
	});
});

app.post('/addMembers', function(req, res) {
	var arr = JSON.parse(req.body.param);
	console.log(arr);
	var response = [];
	for (var i = 0; i < arr.length; i++) {
		api.addMember(con,
			{idGroup:arr[i].groupId, idUser:arr[i].userId, nameUser:arr[i].userName, isAdmin:0},
			function(m) {
			console.log(m);
			response[i] = m;
		});
	}
	res.json(response);
});





server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});