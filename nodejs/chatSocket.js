var api = require('./myNetwork');
var utils = require('./utils');
var moment = require('moment');



exports.createSocket = function(con,id){

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

		socket.on('disconnect', function() {
		    //console.log('some user' +' has left ')
		    socket.broadcast.emit( "userdisconnect" ,' user has left');
		})

	});

};
