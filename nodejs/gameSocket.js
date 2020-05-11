var api = require('./myNetwork');

isPlayerExists = function(arr,id){
	for (var i = 0; i < arr.length; i++) {
		if (arr[i].userId == id) {
			return true;
		}
	}
	return false;
}

getPlayerOrder = function(arr,id){
	for (var i = 0; i < arr.length; i++) {
		if (arr[i].userId == id) {
			return i;
		}
	}
	return 1000;
}

exports.createSocket = function(con,game){
	var gameId = game.id;
	var numPlayer = 0;
	var maxPlayer = game.max;
	var isPlaying = false;
	var isFinished = false;
	var players = [];
	var messages = [];


	var nsp = io.of('/games/game' + gameId);
	console.log('game socket created  with id : ' + gameId);

	nsp.on('connection', function (socket) {

		if (numPlayer >= maxPlayer) {
			socket.disconnect();
			return;
		}

		socket.on('join', function(userNickname,userId) {
			if (isPlayerExists(players,userId)) {
				console.log(userNickname +" is online "  );
				var userOrder = getPlayerOrder(players,userId);
				players[userOrder].online = true;
				socket.broadcast.emit('playerOnline',userOrder);
				socket.emit('num_players',numPlayer);
				socket.emit('youGotBack',players);
				socket.emit('yourOrder',userOrder);
				socket.emit('loadMessages',messages);
			}else {
				console.log(userNickname +" : has joined the game "  );
		    	socket.broadcast.emit('userJoinedTheGame',{user_name:userNickname,user_id:userId});
		    	numPlayer = numPlayer + 1;
		    	players.push({userId:userId, userName:userNickname, online:true, order:(numPlayer-1)});
		    	socket.emit('num_players',numPlayer);
		    	socket.broadcast.emit('num_players',numPlayer);
		    	if (numPlayer != 1) {
		    		socket.emit('youJoined',players);
		    		socket.emit('loadMessages',messages);
		    	}
		    }
		});

		socket.on('messagedetection', (senderNickname,messageContent,senderId) => {
			console.log(senderNickname+" : " +messageContent)
		    let  message = {"message":messageContent, "senderNickname":senderNickname, "senderId":senderId};
		    socket.emit('message', message )
		    socket.broadcast.emit('message', message )
		    messages.push(message)
		});

		socket.on('getOffline', function(userId,userNickname,userOrder){
			console.log(userNickname + ' is offline');
			players[userOrder].online = false;
			socket.broadcast.emit('playerOffline',userOrder);
		});

		socket.on('disconnect', function() {
			//numPlayer = numPlayer - 1;
		    //socket.emit('num_players',numPlayer);
		    //socket.broadcast.emit('num_players',numPlayer);
		});

	});

}
