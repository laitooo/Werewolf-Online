var api = require('./myNetwork');
var utils = require('./utils');



exports.createSocket = function(con,groupId){

	var nsp = io.of('/groups/group' + groupId);
	console.log('group socket created with id : ' + groupId);

	nsp.on('connection', function (socket) {

		console.log('user connected')

		socket.on('join', function(userNickname) {
			console.log(userNickname +" : has joined the group "  );
		    socket.broadcast.emit('userjoinedthechat',userNickname +" : has joined the group ");
		    api.loadGroupMessages(con, groupId, function(messages) {
		    	socket.emit('loadGroupMessages',messages);
		    })
		})

		socket.on('sendMessage', (senderNickname, senderId, messageContent, messageTime) => {
			console.log(senderNickname+" : " +messageContent)
			api.addGroupMessage(con, 
				{idGroup:groupId, userName:senderNickname, userId:senderId, content:messageContent, time:messageTime},
				function(id) {
					socket.broadcast.emit('message', 
						{id:id, userName:senderNickname, userId:senderId, messageContent:messageContent, time:messageTime} )
				});
		})

		socket.on('disconnect', function() {
		    console.log('some user' +' has left ')
		    socket.broadcast.emit( "userdisconnect" ,' user has left')
		})

	});

};
