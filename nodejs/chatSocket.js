var api = require('./myNetwork');
var utils = require('./utils');
var moment = require('moment');



exports.createSocket = function(con,id,nsp3){
	var nsp = io.of('/chats/chat' + id);
	//console.log('chat socket created  with id : ' + id);

	nsp.on('connection', function (socket) {

		//console.log('user connected')

		socket.on('join', function(userNickname) {
			//console.log(userNickname +" : has joined the chat "  );
		    socket.broadcast.emit('userjoinedthechat',userNickname +" : has joined the chat ");
		    api.loadChatMessages(con, id, function(messages) {
		    	socket.emit('loadChatMessages',messages);
		    })
		})

		socket.on('sendMessage', (senderNickname, senderId, messageContent) => {
			let messageTime = moment().format('YYYY-MM-DD HH:mm');
			console.log(senderNickname+" : " +messageContent);
			api.addChatMessage(con, 
				{idchat:id, sender:senderId, content:messageContent, time:messageTime},
				function(idMessage) {
					socket.emit('message', {id:idMessage, idsender:senderId, content:messageContent, time:messageTime});
		    		socket.broadcast.emit('message', {id:idMessage, idsender:senderId, content:messageContent, time:messageTime});
				});
			});

		socket.on('unfriend', function(id1,id2,name1,name2) {
			api.unfriend(con,
				{id1:id1,id2:id2,name1:name1,name2},
				function(error){
					if (!error) {
						console.log(name1 + " and " + name2 + " are no longer friends");
						socket.emit("unfriended",{id1:id1, id2:id2});
						socket.broadcast.emit("unfriended",{id1:id1, id2:id2});
						nsp3.emit("deleteChat",{id1:id1, id2:id2});
					}else {
						console.log(name1 + " and " + name2 + " are not even friends");
					}
				});
		});

		socket.on('disconnect', function() {
		    //console.log('some user' +' has left ')
		    socket.broadcast.emit( "userdisconnect" ,' user has left');
		})

	});

};
