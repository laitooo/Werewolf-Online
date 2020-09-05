var api = require('./myNetwork');
var utils = require('./utils');


const TESTING = true;


const STATE_DAY = 0;
const STATE_VOTE = 1;
const STATE_NIGHT = 2;
const STATE_REVEALING = 3;
const STATE_VOTE_RESULT = 4;
const STATE_GAME_OVER = 5;

const TIME_ROLES = 5000;
const TIME_DAY = 8000;
const TIME_VOTE = 10000;
const TIME_NIGHT = 12000;
const TIME_REVEAL = 5000;
const TIME_VOTE_RESULT = 5000;

/*const TIME_ROLES = 10000;
const TIME_DAY = 15000;
const TIME_VOTE = 20000;
const TIME_NIGHT = 20000;
const TIME_REVEAL = 10000;
const TIME_VOTE_RESULT = 10000;*/

const WAREWOLF = 1;
const VILLAGER = 2;
const DOCTOR = 3;
const HARLOT = 4;
const SEER = 5;
const HUNTER = 6;
const WITCH = 7;
const LITTLE_GIRL = 8;
const GUNNER = 9;
const SERIAL_KILLER = 10;
const ALPHA_WAREWOLF = 11;
const TANNER = 12;
const MAYOR = 13;
const MAD_SCIENTIST = 14;
const JUNIOR_WAREWOLF = 15;

const ROLES_TEAMS = [0,2,1,1,1,1,1,1,1,1,3,2,3,1,1,2];
const GOOD_TEAM = [2,3,4,5,6,7,8,9,13,14];
const BAD_TEAM = [1,11,15];
const OTHERS = [10,12];
const TEAMS_NAME = ["ERROR","Villagers","Warewolfs","Serial killer","Tanner"];



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

getCharOrder = function(arr,id){
	for (var i = 0; i < arr.length; i++) {
		if (arr[i].id == id) {
			return arr[i].order;
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
	}else
		return SERIAL_KILLER;
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
			arr = [1,1,1,2,2,3];
			break;
		case 7:
			arr = [1,1,1,1,2,2,3];
			break;
		case 8:
			arr = [1,1,1,1,1,2,2,3];
			break;
		case 9:
			arr = [1,1,1,1,1,1,2,2,3];
			break;
		case 10:
			arr = [1,1,1,1,1,1,2,2,2,3];
			break;
	}
	return utils.shuflle(arr);
}

exports.createSocket = function(con,game,gamesSocket){
	var playersVotes = [];
	var playersEvents = [];
	var gameId = game.id;
	var numPlayer = 0;
	var numAlive = 0;
	var numRounds = 0;
	var emptyRounds = 0;
	var maxPlayer = game.max;
	var isPlaying = false;
	var isFinished = false;
	var juniorDied = false;
	var players = [];
	var playerStates = [];
	var wolfVotes = [];
	var messages = [];
	var votes = [];
	var nightEvents = [];
	var doctors = [];
	var witches = [];
	var gunners = [];
	var mayors = [];
	var alphaWolf;
	var state = STATE_DAY;
	var exirUsed = false;	
	var convertedtoAlpha = -1;	


	var nsp = io.of('/games/game' + gameId);
	//console.log('game socket created  with id : ' + gameId);

	nsp.on('connection', function (socket) {
		//console.log('game ' + gameId + ' : connection')
		socket.on('join', function(userNickname,userId) {
			if (numPlayer >= maxPlayer) {
				if (!isPlayerExists(players,userId)) {
					socket.disconnect();
					//console.log('game ' + gameId + ' : disconnection')
					return;
				}else{
					var userOrder = getPlayerOrder(players,userId);
					if (players[userOrder].online) {
						//console.log('game ' + gameId + ' : disconnection')
						socket.disconnect();
						return;
					}
				}
			}

			if (isPlayerExists(players,userId)) {
				rejoinedPlayer(userId);
			}else {
				//console.log(userNickname +" : has joined the game "  );
		    	socket.broadcast.emit('userJoinedTheGame',{user_name:userNickname,user_id:userId, revealed:false});
		    	numPlayer = numPlayer + 1;
		    	players.push({userId:userId, userName:userNickname, online:true, order:(numPlayer-1),
		    		role:0, alive:true, revealed:false});
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

		    if (numPlayer == maxPlayer && !isPlaying) {
		    	isPlaying = true;
		    	if (TESTING) {
		    		roles = [MAYOR,ALPHA_WAREWOLF]
		    	}else {
		    		roles = getRandomRoles(randomRoles(numPlayer));
		    	}
		    	for (var i = 0; i < players.length; i++) {
		    		players[i].role = roles[i];
		    		if (roles[i] == DOCTOR) {
		    			doctors.push({id:players[i].userId, userName:players[i].userName,
		    			 lastTarget:0, order:i});
		    		}else if (roles[i] == WITCH) {
		    			witches.push({id:players[i].userId, userName:players[i].userName,
		    			 usedExir:false, usedPoison:false, order:i});
		    		}else if (roles[i] == GUNNER) {
		    			gunners.push({id:players[i].userId, userName:players[i].userName,
		    			isRevealed:false, numBullets:2, order:i});
		    		}else if (roles[i] == MAYOR) {
		    			mayors.push({id:players[i].userId, userName:players[i].userName,
		    			isRevealed:false, doubleVoted:false, order:i});
		    		}else if (roles[i] == ALPHA_WAREWOLF) {
		    			alphaWolf = {id:players[i].userId, userName:players[i].userName,
		    			converted:false};
		    		}
		    	}
		    	console.log("game " + gameId + " : started");
		    	socket.emit('rolesGenerated',players);
		    	socket.broadcast.emit('rolesGenerated',players);
		    	//socket.emit('stateChanged',0,60);
		    	//socket.broadcast.emit('stateChanged',0,60);
		    	gamesSocket.emit('gameFull',gameId);
		    	gamesSocket.broadcast.emit('gameFull',gameId);

				startVoting()

		    }
		});

		const rejoinedPlayer = function (userId) {
			//console.log(userNickname +" is online "  );
			var userOrder = getPlayerOrder(players,userId);
			players[userOrder].online = true;
			socket.broadcast.emit('playerOnline',userOrder);
			var obj = {state:state, order:userOrder, players:players, messages:messages, numPlayer:numPlayer,
			 juniorDied:juniorDied, playersVotes:playersVotes, playersEvents:playersEvents};
			if (players[userOrder].role == DOCTOR) {
				obj.doctors = doctors;
			} else if (players[userOrder].role == WITCH) {
				obj.witches = witches;
			} else if (players[userOrder].role == GUNNER) {
				obj.gunners = gunners;
			} else if (players[userOrder].role == MAYOR) {
				obj.mayors = mayors;
			} else if (players[userOrder].role == ALPHA_WAREWOLF) {
				obj.alphaWolf = alphaWolf;
			}
			console.log(playersVotes);
			console.log(playersEvents);
			socket.emit('youGotBack',obj);
		}

		const voteResult = function(){
			for (var i = mayors.length - 1; i >= 0; i--) {
				if (mayors[i].isRevealed){
					mayors[i].doubleVoted = true;
				}
			}
			playersVotes = [];
			if (votes.length == 0) {
				emptyRounds += 1;
				//console.log("rounds : " + emptyRounds);
				if (emptyRounds == 3) {
					gameAbandoned();
				} else{				
					console.log("game " + gameId + " : no one voted");
					state = STATE_VOTE_RESULT;
					socket.emit('stateChanged',state,60);
				   	socket.broadcast.emit('stateChanged',state,60);
				   	socket.emit("drawVotes");
				   	socket.broadcast.emit("drawVotes");
				   	nightStart();    
				}
			}else{
				var nv = 0;
				for (var i = 0; i < votes.length; i++) {
					if (votes[i] != 0) {
						nv += 1;
					}
				}
				if (nv == 0) {
					emptyRounds += 1;
					if (emptyRounds == 3) {
						gameAbandoned();
						return;
					}
				}else{
					emptyRounds = 0;
				}
				utils.getMostVoted(votes,gameId,function(a){
		    			if (a.draw) {
		    				if (nv == 0) {
		    					console.log("game " + gameId + " : no one voted");
		    				} else {
		    					console.log("game " + gameId + " : draw voting");
		    				}
		    				
							state = STATE_VOTE_RESULT;
							socket.emit('stateChanged',state,60);
						   	socket.broadcast.emit('stateChanged',state,60);
		    				socket.emit("drawVotes");
		    				socket.broadcast.emit("drawVotes");
		    				nightStart();
		    			}else{
		    				if (players[a.value].role == TANNER) {
		    					tannerWon();
		    				}else if (players[a.value].role == MAD_SCIENTIST) {
		    					madScientistBomb(a.value);
		    				}else {
			    				numAlive = numAlive -1;
			    				players[a.value].alive = false;
			    				console.log('Game ' + gameId + " : " + players[a.value].userName + " is dead by voting");
			    				if (numAlive == 1 || isGameOver()) {
			    					gameOver();
			    				}else{
									state = STATE_VOTE_RESULT;
									socket.emit('stateChanged',state,60);
								   	socket.broadcast.emit('stateChanged',state,60);
			    					socket.emit("playerDiedByVotes",players[a.value].userId);
			    					socket.broadcast.emit("playerDiedByVotes",players[a.value].userId);
			    					nightStart();
			    				}
			    			}
		    			}
		    		});
			}
		}

		const madScientistBomb = function(order) {
			numAlive = numAlive -1;
			players[order].alive = false;
			console.log('Game ' + gameId + " : " + players[order].userName + " is dead by voting");
			if (order != 0 ) {
				if (players[order - 1].alive) {
					players[order - 1].alive = false;
					socket.emit("playerDiedByMad",players[order-1].userId);
			    	socket.broadcast.emit("playerDiedByMad",players[order-1].userId);

				}
			}
			if (order != maxPlayer-1) {
				if (players[order +1].alive) {
					players[order +1].alive = false;
					socket.emit("playerDiedByMad",players[order-1].userId);
			    	socket.broadcast.emit("playerDiedByMad",players[order-1].userId);
				}
			}

			if (numAlive < 2 || isGameOver()) {
			    gameOver();
			}else{
				state = STATE_VOTE_RESULT;
				socket.emit('stateChanged',state,60);
				socket.broadcast.emit('stateChanged',state,60);
			    socket.emit("playerDiedByVotes",players[order].userId);
			    socket.broadcast.emit("playerDiedByVotes",players[order].userId);
			    nightStart();
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

			return false;

			if (v < w ) {
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
		    state = STATE_VOTE
		    socket.emit('stateChanged',state,60);
		    socket.broadcast.emit('stateChanged',state,60);
		    votes = [];
		    playersVotes = [];
		    for (var i = 0; i < players.length; i++) {
		    	votes[i] = 0;
		    	playersVotes[i] = false;
		    }
		    await utils.mySleep(TIME_VOTE);
		    voteResult();
		}

		const nightStart = async() =>{

			await utils.mySleep(TIME_VOTE_RESULT);
			nightEvents = [];
			playerStates = [];
			wolfVotes = [];
			playersEvents = [];

		    for (var i = 0; i < players.length; i++) {
		    	playersEvents[i] = false;
		    	if (players[i].alive) {
		    		playerStates.push({order:i, id:players[i].userId, role:players[i].role, hasAttWar:false, hasHeaDoc:false,
		    		hasSleHar:false, sleHarId:100, isKilled:false, huntedId:100, poisoned:false, isShot:false,
		    		serialKilled:false});
		    		wolfVotes[i] = 0;
		    	}
		    }
		    
		    state = STATE_NIGHT;
		    socket.emit('stateChanged', state,60);
		    socket.broadcast.emit('stateChanged', state, 60);
		    socket.emit('startNight');
		    socket.broadcast.emit('startNight');

		    await utils.mySleep(TIME_NIGHT);

		    playersEvents = [];

		    utils.getWolfVoted(juniorDied,wolfVotes,gameId,function(a){
		    	if (a.vote1) {
		    		if (a.vote2) {
		    			playerStates[a.value1].hasAttWar = true;
		    			playerStates[a.value2].hasAttWar = true;
		    			console.log(wolfVotes);
		    		}else{
		    			playerStates[a.value1].hasAttWar = true;
		    					    			console.log(wolfVotes);
		    		}
		    	}
		    	juniorDied = false;
		    });

		    for (var i = 0; i < playerStates.length; i++) {
		    	if (playerStates[i].serialKilled) {
					playerStates[i].isKilled = true;
			    	var idx = getPlayerOrder(players,playerStates[i].id);
					console.log('Game ' + gameId + " : " + players[idx].userName + " : is dead ");
					numAlive = numAlive -1;
			    	players[idx].alive = false;
			    	nightEvents.push({userId:players[idx].userId, userName:players[idx].userName, deadBy:"Gunner"});

			    	checkHunter(i);
			    	checkMadScientist(i);
			    	checkJuniorWarewolf(i);
		    	}else if (playerStates[i].isShot) {
		    		playerStates[i].isKilled = true;
			    	var idx = getPlayerOrder(players,playerStates[i].id);
					console.log('Game ' + gameId + " : " + players[idx].userName + " : is dead ");
					numAlive = numAlive -1;
			    	players[idx].alive = false;
			    	nightEvents.push({userId:players[idx].userId, userName:players[idx].userName, deadBy:"Gunner"});

			    	checkHunter(i);
			    	checkMadScientist(i);
			    	checkJuniorWarewolf(i);
		    	}else if (playerStates[i].hasAttWar) {
		    		// attacked by Warewolf
		    		if (playerStates[i].hasHeaDoc) {
		    			// saved by doctor from warewolf attack
		    		}else {
		    			if (exirUsed) {
		    				// saved by witch exir
		    			}else{
		    				// warewolf killed player
			    			playerStates[i].isKilled = true;
			    			var idx = getPlayerOrder(players,playerStates[i].id);
							console.log('Game ' + gameId + " : " + players[idx].userName + " : is dead ");
							numAlive = numAlive -1;
			    			players[idx].alive = false;
			    			nightEvents.push({userId:players[idx].userId, userName:players[idx].userName, deadBy:"Warewolf"});

			    			checkHunter(i);
			    			checkMadScientist(i);
			    			checkJuniorWarewolf(i);
			    		}
		    		}
		    	}else{
		    		// not attacked by warewolf
		    		if (playerStates[i].sleHarId != 100) {
		    			// player is a harlot
		    			var t = playerStates[i].sleHarId;
		    			if (players[getPlayerOrder(players,t)].role == WAREWOLF) {
		    				// harlot slept with warewolf
		    				playerStates[i].isKilled = true;
							console.log('Game ' + gameId + " : " + players[i].userName + " : is dead ");
							numAlive = numAlive -1;
		    				players[i].alive = false;
		    				nightEvents.push({userId:players[i].userId, userName:players[i].userName, 
		    					deadBy:"Sleeping with warewolf",killed:true});

							checkHunter(i);	
							checkMadScientist(i);   
							checkJuniorWarewolf(i); 				
		    			}else if(playerStates[t].isKilled){
		    				// harlot slept with a killed player
		    				playerStates[i].isKilled = true;
							console.log('Game ' + gameId + " : " + players[i].userName + " : is dead ");
							numAlive = numAlive -1;
		    				players[i].alive = false;
		    				nightEvents.push({userId:players[i].userId, userName:players[i].userName, 
		    					deadBy:"Slept with killed player",killed:true});

							checkHunter(i);			
							checkMadScientist(i); 
							checkJuniorWarewolf(i);   			
						}
		    		}else{
		    			// player is not a harlot
		    			if (playerStates[i].poisoned) {
		    				// kiled by witch's poison
		    				playerStates[i].isKilled = true;
							console.log('Game ' + gameId + " : " + players[i].userName + " : is dead ");
							numAlive = numAlive -1;
		    				players[i].alive = false;
		    				nightEvents.push({userId:players[i].userId, userName:players[i].userName, 
		    					deadBy:"Withs's poison",killed:true});

							checkHunter(i);	
							checkMadScientist(i);
							checkJuniorWarewolf(i);
		    			}
		    		}
		    	}
		    }

		    state = STATE_REVEALING;
			socket.emit('stateChanged',state,60);
		    socket.broadcast.emit('stateChanged',state,60);
		    socket.emit("nightEvents",nightEvents,numAlive);
		    socket.broadcast.emit("nightEvents",nightEvents,numAlive);

		    //if (numAlive < 2 || isGameOver()) {

		    exirUsed = false;
		    if (numAlive < 2 || isGameOver()) {
		    	gameOver();
		    }else{
		    	startDay();
		    }
		}

		const checkHunter = function(i){
			var hi = playerStates[i].huntedId;
			var idx2 = getPlayerOrder(players,hi);
			if (hi != 100 && players[idx2].alive) {
				playerStates[idx2].isKilled = true;
				console.log('Game ' + gameId + " : " + players[idx2].userName + " : is dead ");
				numAlive = numAlive -1;
			 	players[idx2].alive = false;
				nightEvents.push({userId:players[idx2].userId, userName:players[idx2].userName, deadBy:"Hunter"});
				checkHunter(hi);
				checkMadScientist(hi);
				checkJuniorWarewolf(i);
			}
		}

		const checkMadScientist = function(i){
			if (playerStates[i].role == MAD_SCIENTIST) {
				var a = -1;
				var b = -1;
				for (var j = 0; j < playerStates.length; j++) {
					if (j<i) {
						if (!playerStates[j].isKilled) {
							a = j;
						}
					}else if (i<j) {
						if (!playerStates[j].isKilled) {
							b = j;
							break;
						}
					}
				}
				if (a != -1) {
					playerStates[a].isKilled = true;
					var idx2 = getPlayerOrder(players,playerStates[a].id);
					console.log('Game ' + gameId + " : " + players[idx2].userName + " : is dead ");
					numAlive = numAlive -1;
				 	players[idx2].alive = false;
					nightEvents.push({userId:players[idx2].userId, userName:players[idx2].userName, deadBy:"Hunter"});
					checkHunter(a);
					checkMadScientist(a);
					checkJuniorWarewolf(i);
				}
				if (b != -1) {
					playerStates[a].isKilled = true;
					var idx2 = getPlayerOrder(players,playerStates[a].id);
					console.log('Game ' + gameId + " : " + players[idx2].userName + " : is dead ");
					numAlive = numAlive -1;
				 	players[idx2].alive = false;
					nightEvents.push({userId:players[idx2].userId, userName:players[idx2].userName, deadBy:"Hunter"});
					checkHunter(a);
					checkMadScientist(a);
					checkJuniorWarewolf(i);
				}
			}
		}

		const checkJuniorWarewolf = function(i){
			if (playerStates[i].role == JUNIOR_WAREWOLF) {
				juniorDied = true;
				socket.emit('juniorDied');
				socket.broadcast.emit('juniorDied');
			}
		}

		const startDay = async() =>{
			await utils.mySleep(TIME_REVEAL);
			state = STATE_DAY;
			socket.emit('stateChanged', state,60);
		    socket.broadcast.emit('stateChanged', state, 60);
		    await utils.mySleep(TIME_DAY);
		    startVoting();
		}

		const gameAbandoned = function(){
			api.finishGame(con,gameId,function(){
		    	console.log('Game ' + gameId + ' : abandoned');
		    	socket.emit('gameAbandoned');
		    	socket.broadcast.emit('gameAbandoned');
		    	gamesSocket.emit('gameAbandoned',gameId);
		    	gamesSocket.broadcast.emit('gameAbandoned',gameId);
		    });
		}

		const tannerWon = function(){
			var winners = [];
			for (var i = 0; i < players.length; i++) {
				if (players[i].role == TANNER) {
					winners.push({userId:players[i].userId, userName:players[i].userName, role:players[i].role,
					alive:players[i].alive, winner:true});
				}else {
					winners.push({userId:players[i].userId, userName:players[i].userName, role:players[i].role,
					alive:players[i].alive, winner:false});
				}
			}

			var obj = {players:players, winners:winners};
		    gamesSocket.emit('gameOver',gameId);
		    gamesSocket.broadcast.emit('gameOver',gameId);
			socket.emit("gameOver",obj);
		    socket.broadcast.emit("gameOver",obj);
		    api.finishGame(con,gameId,function(){
		    	console.log('Game ' + gameId + ' : finished    Tanner won');
		    });
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

			var obj = {players:players, winners:winners};
		    gamesSocket.emit('gameOver',gameId);
		    gamesSocket.broadcast.emit('gameOver',gameId);
			socket.emit("gameOver",obj);
		    socket.broadcast.emit("gameOver",obj);
		    api.finishGame(con,gameId,function(){
		    	if (numAlive == 0) {
		    		console.log('Game ' + gameId + ' : finished    Everyone is dead');	
		    	}else {
		    		console.log('Game ' + gameId + ' : finished    The ' + TEAMS_NAME[winTeam] + " won");	
		    	}
		    	
		    });
		}

		socket.on('hunterHunt', function(hunterId, targetId) {
			playerStates[getPlayerOrder(players, hunterId)].huntedId = targetId;
			playersEvents[getPlayerOrder(players, hunterIds)] = true;
		});

		socket.on('witchExir', function(witchId) {
			exirUsed = true;
			witches[getCharOrder(witches, witchId)-1].usedExir = true;
			playersEvents[getPlayerOrder(players, witchId)] = true;
		});

		socket.on('witchPoison', function(witchId, targetId) {
			playerStates[getPlayerOrder(players, targetId)].poisoned = true;
			witches[getCharOrder(witches, witchId)-1].usedPoison = true;
			playersEvents[getPlayerOrder(players, witchId)] = true;
		});

		socket.on('harlotSleep', function(harlotId ,targetId) {
			playerStates[getPlayerOrder(players, targetId)].hasSleHar = true;
			playerStates[getPlayerOrder(players, harlotId)].sleHarId = targetId;
			playersEvents[getPlayerOrder(players, harlotId)] = true;
		});

		socket.on('doctorHeal', function(doctorId, targetId) {
			playerStates[getPlayerOrder(players, targetId)].hasHeaDoc = true;
			doctors[getCharOrder(doctors, doctorId)-1].lastTarget = targetId;
			playersEvents[getPlayerOrder(players, doctorId)] = true;
		});

		socket.on('doctorSkip', function(doctorId) {
			doctors[getCharOrder(doctors, doctorId)-1].lastTarget = -1;
			playersEvents[getPlayerOrder(players, doctorId)] = true;
		});

		socket.on('warewolfKill', function(warewolfId, targetId){
			wolfVotes[getPlayerOrder(players, targetId)] = wolfVotes[getPlayerOrder(players, targetId)] + 1;
			playersEvents[getPlayerOrder(players, warewolfId)] = true;
		});

		socket.on('warewolfConvert', function(warewolfId, targetId){
			players[getPlayerOrder(players, targetId)].role = WAREWOLF;
			socket.emit('convertedtoWolf',targetId);
			socket.broadcast.emit('convertedtoWolf',targetId);
			convertedtoAlpha = targetId;
			playersEvents[getPlayerOrder(players, warewolfId)] = true;
		});

		socket.on('serialKill', function(killerId, targetId){
			playerStates[getPlayerOrder(players, targetId)].serialKilled = true;
			playersEvents[getPlayerOrder(players, killerId)] = true;
		});

		socket.on('revealMayor', function(mayorId){
			var may = mayors[getCharOrder(mayors, mayorId)];
			if (!may.isRevealed) {
				socket.emit('revealPlayer',{id:may.id, name:may.userName, role:MAYOR});
				socket.broadcast.emit('revealPlayer',{id:may.id, name:may.userName, role:MAYOR});
				may.isRevealed = true;
			}
			players[getPlayerOrder(players, mayorId)].revealed = true;
			playersEvents[getPlayerOrder(players, mayorId)] = true;
		});

		socket.on('gunnerShoot', function(gunnerId, targetId){
			playerStates[getPlayerOrder(players, targetId)].isShot = true;
			var gunn = gunners[getCharOrder(gunners, gunnerId)];
			//console.log(gunners);
			//console.log(gunners[getCharOrder(gunners, gunnerId)]);
			//console.log(gunn);
			if (!gunn.isRevealed) {
				socket.emit('revealPlayer',{id:gunn.id, name:gunn.userName, role:GUNNER});
				socket.broadcast.emit('revealPlayer',{id:gunn.id, name:gunn.userName, role:GUNNER});
				gunn.isRevealed = true;
			}
			gunn.numBullets = gunn.numBullets -1;
			players[getPlayerOrder(players, gunnerId)].revealed = true;
			playersEvents[getPlayerOrder(players, gunnerId)] = true;
		});

		socket.on('vote', function(userId,userNickname,selectedOrder){
			//console.log(userNickname + " voted to kill : " + players[selectedOrder].userName);
			votes[selectedOrder] = votes[selectedOrder] + 1;
			playersVotes[selectedOrder] = true;
		});

		socket.on('messagedetection', (senderNickname,messageContent,senderId) => {
			console.log('Game ' + gameId + " : " + senderNickname+" : " +messageContent);
		    let  message = {"id":messages.length+1, "message":messageContent, "senderNickname":senderNickname,
		     "senderId":senderId};
		    socket.emit('messageSent', message );
		    socket.broadcast.emit('message', message );
		    messages.push(message);
		});

		socket.on('getOffline', function(userId,userNickname,userOrder){
			//console.log(userNickname + ' is offline');
			players[userOrder].online = false;
			socket.broadcast.emit('playerOffline',userOrder);
			socket.disconnect();
		});

		socket.on('disconnect', function() {
			//numPlayer = numPlayer - 1;
		    //socket.emit('num_players',numPlayer);
		    //socket.broadcast.emit('num_players',numPlayer);
		});

	});
}
