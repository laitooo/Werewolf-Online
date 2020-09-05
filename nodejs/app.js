const http = require('http');
const express = require('express');
var api = require('./myNetwork');
var bodyParser = require('body-parser');
var mysql = require('mysql');
var gameSocket = require('./gameSocket');
var chatSocket = require('./chatSocket');
var groupSocket = require('./groupSocket');
var imagesManager = require('./imagesManager');
var moment = require('moment');
const path = require('path');

app = express();
server = http.createServer(app);
io = require('socket.io').listen(server);

const hostname ='192.168.43.211'
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
	    		imagesManager.randomProfile(userId,req.body.username,function(imageFile){
	    			console.log(req.body.username + ' : signed up');
	    			res.json({ error: false, id: userId, pic:imageFile});
	    		});
	    	});
		}else{
			res.json({ error: true, state:1})
		}
	});
});

app.post('/isUser', function(req,res) {
	//console.log(req.body);
	api.userExists(con,req.body.email,req.body.username,function(result){
		if(result.length == 1){
			if (result[0].password == req.body.password) {
				console.log(req.body.username + ' : logged in');
				res.json({error:false,user:result[0]});
			}else{
				res.json({error:true,state:2})
			}
		}else{
			res.json({error:true,state:1})
		}
	});
});

app.post('/addReview', function(req,res) {
	api.addReview(con,req.body,function(error){
		if(!error){
			console.log(req.body.username + ' : added a review (Version ' + req.body.version + ')');
			res.json({error:false})
		}else{
			res.json({error:true})
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

var nsp = io.of('/main')
var num = 0; 
nsp.on('connection', function (socket) {

	//    MAIN ACTIVITY

	socket.on('join', function(userNickname) {
		console.log(userNickname +" : online "  );
		num = num + 1;
		console.log("Num onine : " + num);
	    socket.broadcast.emit('num_players',num);
	    socket.emit('num_players',num);
	    api.loadActiveGames(con,function(Result){
	    	socket.emit('load_games',Result);
	    });
	})

	socket.on('disconnect', function() {
	    num = num - 1;
	    if (num < 0) {
	    	num = 0;
	    }
	    console.log("Num onine : " + num);
	    socket.broadcast.emit('num_players',num);
	})

	//         GAMES SOCKET

	socket.on('createGame', function(game) {
		api.createGame(con,game,function(id){
			game.id = id;
			console.log(game.nameOwner + " : has created game " + id);
	    	socket.emit('youCreatedGame',game);
	    	socket.broadcast.emit('gameCreated',game);
	    	gameSocket.createSocket(con,game,socket);
		});
	})

	// 	         CHATS SOCKET

	socket.on('joinChats', function(id) {
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
			chatSocket.createSocket(con,id,nsp);
		});
	});

	socket.on('cancelRequest', function(idSender,idReceiver,nameSender,nameReceiver){
		api.cancelRequest(con,idSender,idReceiver,nameSender,nameReceiver,function(){
			socket.emit('canceled',{error:false, id:idSender});
		});
	});

	//          GROUPS SOCKET

	socket.on('joinGroups', function(id) {
	    api.loadGroups(con,id,function(Result){
	    	socket.emit('loadGroups',Result);
	    });
	});

	socket.on('createGroup', function(idAdmin, nameAdmin, groupName) {
		api.createGroup(con,
		 {idAdmin:idAdmin, nameAdmin:nameAdmin, name:groupName, numMembers:1},
		 function(result) {
		 	if (!result.error) {
				let messageTime = moment().format('YYYY-MM-DD HH:mm')
				let content = nameAdmin + ' : created this group';
				api.addInfoGroupMessage(con, 
				{idGroup:result.id, userName:"", userId:0, content:content, time:messageTime, info:true},
				function(id) {
					console.log(nameAdmin + ' : created group ( ' + groupName + ' )');
				});
			}
			socket.emit('groupCreated',result);
			groupSocket.createSocket(con,groupName,result.id,result.numMembers,nsp);
		});
	});

	socket.on('refresh', function(id) {
		api.loadGroups(con,id,function(Result){
	    	socket.emit('reloadGroups',Result);
	    });
	})

});


/*var nsp2 = io.of('/chats')
nsp2.on('connection', function (socket) {

	

});



var nsp3 = io.of('/groups')
nsp3.on('connection', function (socket) {

	

});*/


api.loadAllGroups(con, function(result) {
	for (var i=0;i<result.length;i++){
		groupSocket.createSocket(con,result[i].name,result[i].id,result[i].numMembers,nsp);
			//console.log(result[i]);
	}
	console.log('groups sockets started');
});			

api.loadAllFriends(con, function(result) {
	for (var i=0;i<result.length;i++){
		chatSocket.createSocket(con,result[i].id,nsp);
	}
	console.log('chats sockets started');
});

api.deleteGames(con, function(result) {
	console.log('deleted unfinished games');
});



/*app.post('/uploadImage', upload.single('image'), async function (req, res) {
  await console.log('image uploaded');
});*/

server.listen(port, hostname, () => {
  console.log(`Warewolf started`);
  console.log(`server url : http://${hostname}:${port}/`);
  console.log("started at " + moment().format('YYYY-MM-DD HH:mm Z'));
});