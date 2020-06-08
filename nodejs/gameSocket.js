var api = require('./myNetwork');
var utils = require('./utils');

const STATE_DAY = 0;
const STATE_VOTE = 1;
const STATE_NIGHT = 2;

const TIME_ROLES = 10000;
const TIME_DAY = 15000;
const TIME_VOTE = 20000;
const TIME_NIGHT = 20000;
const TIME_REVEAL = 10000;

const WAREWOLF = 1;
const VILLAGER = 2;
const DOCTOR = 3;
const HARLOT = 4;

const ROLES_TEAMS = [0,2,1,1,1];
const GOOD_TEAM = [2,3,4];
const BAD_TEAM = [1];


isPlayerExists = function(arr,id){
	for (var i = 0; i < arr.length; i++) {
		if (arr[i].userId == id) {
			return true;
		}
	}
	return false;
}

getPlayerRole = function(arr,id){
	for (var i = arr.length - 1; i >= 0; i--) {
		if (id == arr[i].userId) {
			return arr[i].order;
		}
	}
	return 0;
}

getPlayerOrder = function(arr,id){
	for (var i = 0; i < arr.length; i++) {
		if (arr[i].userId == id) {
			return i;
		}
	}
	return 1000;
}

getRandomTeamRole = function(r){
	if (r == 1) {
		var num = Math.floor(Math.random() * GOOD_TEAM.length);
		return GOOD_TEAM[num];
	}else if (r == 2) {
		var num = Math.floor(Math.random() * BAD_TEAM.length);
		return BAD_TEAM[num];
	}
}

getRandomRoles = function(arr){
	var ro = [];
	for (var i = 0; i < arr.length; i++) {
		ro.push(getRandomTeamRole(arr[i]));
	}
	return ro;
}

randomRoles = function(a){
	var arr = [];
	switch(a){
		case 2:
			arr = [1,2];
			break;
		case 3:
			arr = [1,1,2];
			break;
		case 4:
			arr = [1,1,2,2];
			break;
		case 5:
			arr = [1,1,1,2,2];
			break;
		case 6:
			arr = [1,1,1,2,2,2];
			break;
		case 7:
			arr = [1,1,1,1,2,2,2];
			break;
		case 8:
			arr = [1,1,1,1,1,2,2,2];
			break;
		case 9:
			arr = [1,1,1,1,1,1,2,2,2];
			break;
		case 10:
			arr = [1,1,1,1,1,1,2,2,2,2];
			break;
	}
	return utils.shuflle(arr);
}

exports.createSocket = function(con,game,gamesSocket){
	var gameId = game.id;
	var numPlayer = 0;
	var numAlive = 0;
	var numRounds = 0;
	var maxPlayer = game.max;
	var isPlaying = false;
	var isFinished = false;
	var players = [];
	var playerStates = [];
	var messages = [];
	var votes = [];
	var nightEvents = [];
	var state = STATE_DAY;


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
		    	players.push({userId:userId, userName:userNickname, online:true, order:(numPlayer-1),
		    		role:0, alive:true});
		    	numAlive = numAlive + 1;
		    	socket.emit('num_players',numPlayer);
		    	socket.broadcast.emit('num_players',numPlayer);
		    	if (numPlayer != 1) {
		    		socket.emit('youJoined',players);
		    		socket.emit('loadMessages',messages);
		    	}

		    	api.updateGamePlayers(con,gameId,numPlayer,function(){
		    		gamesSocket.emit('numChanged',gameId,numPlayer);
		    		gamesSocket.broadcast.emit('numChanged',gameId,numPlayer);
		    	});
		    }

		    if (numPlayer == maxPlayer) {
		    	isPlaying = true;
		    	roles = getRandomRoles(randomRoles(numPlayer));
		    	for (var i = 0; i < players.length; i++) {
		    		players[i].role = roles[i];
		    	}
		    	socket.emit('rolesGenerated',players);
		    	socket.broadcast.emit('rolesGenerated',players);
		    	//socket.emit('stateChanged',0,60);
		    	//socket.broadcast.emit('stateChanged',0,60);
		    	gamesSocket.emit('gameFull',gameId);
		    	gamesSocket.broadcast.emit('gameFull',gameId);

				startVoting()

		    }
		});

		const voteResult = function(){
			if (votes.length == 0) {
				console.log("draw voting");
		    				socket.emit("drawVotes");
		    				socket.broadcast.emit("drawVotes");k
		    				nightStart();
			}else{
				utils.getMostVoted(votes,function(a){
		    			if (a.draw) {
		    				console.log("draw voting");
		    				socket.emit("drawVotes");
		    				socket.broadcast.emit("drawVotes");
		    				nightStart();
		    			}else{
		    				numAlive = numAlive -1;
		    				players[a.value].alive = false;
		    				console.log("player " + players[a.value].userName + " by voting");
		    				if (numAlive == 1 || isGameOver()) {
		    					gameOver();
		    				}else{
		    					socket.emit("playerDiedByVotes",players[a.value].userId);
		    					socket.broadcast.emit("playerDiedByVotes",players[a.value].userId);
		    					nightStart();
		    				}
		    			}
		    		});
			}
		}

		const isGameOver = function() {
			var v=0,w=0,o=0;
			for (var i = 0; i < players.length; i++) {
				if (players[i].alive) {
					var t = ROLES_TEAMS[players[i].role];
					if (t == 0) {
						v = v + 1;
					}else if(t == 1){
						w = w + 1;
					}else {
						o = o + 1;
					}
				}
			}

			if (v == 1 && w == 2) {
				return true;
			}else if(v == 0){
				return true;
			}else if (w == 0 && o == 0) {
				return true;
			}

			return false;
		}

		const startVoting = async () => {
			if (numRounds == 0) {
			    await utils.mySleep(TIME_ROLES);
			    numRounds = numRounds + 1;
			}
			socket.emit('startVoting');
		    socket.broadcast.emit('startVoting');
		    socket.emit('stateChanged',STATE_VOTE,60);
		    socket.broadcast.emit('stateChanged',STATE_VOTE,60);
		    votes = [];
		    for (var i = 0; i < players.length; i++) {
		    	votes[i] = 0;
		    }
		    await utils.mySleep(TIME_VOTE);
		    voteResult();
		}

		const nightStart = async() =>{

			await utils.mySleep(TIME_REVEAL);
			socket.emit('stateChanged',2,60);
		    socket.broadcast.emit('stateChanged',2,60);
			nightEvents = [];
			playerStates = [];
		    for (var i = 0; i < players.length; i++) {
		    	if (players[i].alive) {
		    		playerStates.push({order:i, id:players[i].userId, hasAttWar:false, hasHeaDoc:false,
		    		hasSleHar:false, sleHarId:100, isKilled:false});
		    	}
		    }
		    
		    socket.emit('stateChanged', STATE_NIGHT,60);
		    socket.broadcast.emit('stateChanged', STATE_NIGHT, 60);
		    socket.emit('startNight');
		    socket.broadcast.emit('startNight');

		    await utils.mySleep(TIME_NIGHT);

		    for (var i = 0; i < playerStates.length; i++) {
		    	if (playerStates[i].hasAttWar) {
		    		if (playerStates[i].hasHeaDoc) {
		    			// saved by doctor from warewolf attack
		    		}else {
		    			playerStates[i].isKilled = true;
		    			var idx = getPlayerOrder(players,playerStates[i].id);
						console.log(players[idx].userName + " is dead ");
						numAlive = numAlive -1;
		    			players[idx].alive = false;
		    			nightEvents.push({userId:players[idx].userId, userName:players[idx].userName, deadBy:"Warewolf"});
		    			//socket.emit("playerDiedByWarewolf",players[idx].userId);
		    			//socket.broadcast.emit("playerDiedByWarewolf",players[idx].userId);
		    		}
		    	}else{
		    		if (playerStates[i].sleHarId != 100) {
		    			var t = playerStates[i].sleHarId;
		    			if (players[getPlayerOrder(players,t)].role == 1) {
		    				playerStates[i].isKilled = true;
							console.log(players[i].userName + " is dead ");
							numAlive = numAlive -1;
		    				players[i].alive = false;
		    				nightEvents.push({userId:players[i].userId, userName:players[i].userName, deadBy:"Warewolf",
		    					killed:true});
		    				//socket.emit("playerDiedByWarewolf",players[i].userId);
		    				//socket.broadcast.emit("playerDiedByWarewolf",players[i].userId);
		    				
		    			}else if(playerStates[t].isKilled){
		    				layerStates[i].isKilled = true;
							console.log(players[i].userName + " is dead ");
							numAlive = numAlive -1;
		    				players[i].alive = false;
		    				nightEvents.push({userId:players[i].userId, userName:players[i].userName, deadBy:"Warewolf",
		    					killed:true});
		    				//socket.emit("playerDiedByWarewolf",players[i].userId);
		    				//socket.broadcast.emit("playerDiedByWarewolf",players[i].userId);
		    			}
		    		}
		    	}
		    }

		    socket.emit("nightEvents",nightEvents,numAlive);
		    socket.broadcast.emit("nightEvents",nightEvents,numAlive);

		    //if (numAlive < 2 || isGameOver()) {
		    if (numAlive < 2) {
		    	gameOver();
		    }else{
		    	startDay();
		    }
		}

		const startDay = async() =>{
			await utils.mySleep(TIME_REVEAL);
			socket.emit('stateChanged', STATE_DAY,60);
		    socket.broadcast.emit('stateChanged', STATE_DAY, 60);
		    await utils.mySleep(TIME_DAY);
		    startVoting();
		}

		const gameOver = function(){
			var winners = [];
			var winRole = 0;
			for (var i = 0; i < players.length; i++) {
				if (players[i].alive) {
					winRole = players[i].role;
				}
			}
			winTeam = ROLES_TEAMS[winRole];

			for (var i = 0; i < players.length; i++) {
				if (ROLES_TEAMS[players[i].role] == winTeam) {
					winners.push({userId:players[i].userId, userName:players[i].userName, role:players[i].role,
					alive:players[i].alive, winner:true});
				}else {
					winners.push({userId:players[i].userId, userName:players[i].userName, role:players[i].role,
					alive:players[i].alive, winner:false});
				}
			}

			socket.emit("gameOver",players);
		    socket.broadcast.emit("gameOver",players);
			socket.emit("winners",winners);
		    socket.broadcast.emit("winners",winners);
		    api.finishGame(con,gameId,function(){
		    	console.log('Game with id ' + gameId + ' finished');
		    });
		}

		socket.on('harlotSleep', function(harlotId ,targetId) {
			playerStates[getPlayerOrder(players, targetId)].hasSleHar = true;
			playerStates[getPlayerOrder(players, harlotId)].sleHarId = targetId;
		});

		socket.on('doctorHeal', function(doctorId, targetId) {
			playerStates[getPlayerOrder(players, targetId)].hasHeaDoc = true;
		});

		socket.on('warewolfKill', function(warewolfId, targetId){
			playerStates[getPlayerOrder(players, targetId)].hasAttWar = true;
		})

		socket.on('vote', function(userId,userNickname,selectedOrder){
			console.log(userNickname + " voted to kill : " + players[selectedOrder].userName);
			votes[selectedOrder] = votes[selectedOrder] + 1;
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
