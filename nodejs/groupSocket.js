var api = require('./myNetwork');
var utils = require('./utils');
var moment = require('moment');



exports.createSocket = function(con,groupName,groupId,numMembers){

	var nsp3 = io.of('/groups')
	var nsp = io.of('/groups/group' + groupId);
	//console.log('group socket created with id : ' + groupId);

	nsp.on('connection', function (socket) {

		//console.log('user connected')

		socket.on('join', function(userNickname) {
			//console.log(userNickname +" : has joined the group "  );
		    socket.broadcast.emit('userjoinedthechat',userNickname +" : has joined the group ");
		    api.loadGroupMessages(con, groupId, function(messages) {
		    	socket.emit('loadGroupMessages',messages);
		    })
		})

		socket.on('sendMessage', (senderNickname, senderId, messageContent) => {
			let messageTime = moment().format('YYYY-MM-DD HH:mm')
			console.log("group " + groupName + " : " + senderNickname+" : " +messageContent)
			api.addGroupMessage(con, 
				{idGroup:groupId, userName:senderNickname, userId:senderId, content:messageContent, time:messageTime, info:false},
				function(id) {
					socket.emit('message', 
						{id:id, userName:senderNickname, userId:senderId, messageContent:messageContent, time:messageTime, 
							info:false} )
					socket.broadcast.emit('message', 
						{id:id, userName:senderNickname, userId:senderId, messageContent:messageContent, time:messageTime,
							info:false} )
				});
		});

		socket.on('disconnect', function() {
		    //console.log('some user' +' has left ')
		    socket.broadcast.emit( "userdisconnect" ,' user has left')
		});

		socket.on('loadGroupMembers', function() {
			api.loadMembers(con,groupId,function(result){
				//console.log(result);
				socket.emit("GroupMembers",result);
			});
		});

		socket.on('loadFriends', function(userId) {
			api.loadFriends(con,userId,function(result){
				//console.log(result);
				socket.emit("Friends",result);
			});
		});

		socket.on('addMember', function(nameAdmin,groupId,userId,userName) {
				api.addMember(con,
					{idGroup:groupId, idUser:userId, nameUser:userName, isAdmin:0},
					function(m) {
						//console.log(m);
					if (!m.error) {
						let messageTime = moment().format('YYYY-MM-DD HH:mm')
						let content = nameAdmin + ' added ' + userName;
						api.addInfoGroupMessage(con, 
						{idGroup:groupId, userName:"", userId:0, content:content, time:messageTime, info:true},
						function() {
							socket.emit('message', 
								{id:0, userName:"", userId:0, messageContent:content, time:messageTime, 
									info:true} )
							socket.broadcast.emit('message', 
								{id:0, userName:"", userId:0, messageContent:content, time:messageTime,
									info:true} )
							socket.emit("addingMember", m);
							//groupsSocket.broadcast.emit("addedMember",userId);
							nsp3.emit("addedMember",userId);
						});
						numMembers = numMembers + 1;
						api.updateNumMembers(con,groupId,numMembers, function() {
							console.log('Group ' + groupId + " : has " + numMembers + " members . ");
						});
					}
				});
			});

		socket.on("removeMember", function(nameAdmin,userId,userName) {
			api.deleteMember(con,groupId,userId,function(){
				//socket.emit('removedMember',userId);
				//socket.broadcast.emit('removedMember',userId);
				let messageTime = moment().format('YYYY-MM-DD HH:mm')
				let content = nameAdmin + ' removed ' + userName;
				api.addInfoGroupMessage(con, 
				{idGroup:groupId, userName:"", userId:0, content:content, time:messageTime, info:true},
				function() {
					socket.emit('message', 
						{id:0, userName:"", userId:0, messageContent:content, time:messageTime, 
							info:true} )
					socket.broadcast.emit('message', 
						{id:0, userName:"", userId:0, messageContent:content, time:messageTime,
							info:true} )
					socket.emit("removingMember", userId);
					socket.broadcast.emit("removingMember", userId);
					socket.broadcast.emit("closeGroup", userId);
					//groupsSocket.broadcast.emit("removedMember",userId);
					nsp3.emit("removedMember",userId);
					numMembers = numMembers - 1;
					api.updateNumMembers(con,groupId,numMembers, function() {
						console.log('Group ' + groupId + " : has " + numMembers + " members");
					});
				});
			});
		});

		socket.on("leaveGroup", function(userId, userName, isAdmin) {
			if (numMembers >1) {
				api.deleteMember(con,groupId,userId,function(){
					//socket.emit('removedMember',userId);
					//socket.broadcast.emit('removedMember',userId);
					let messageTime = moment().format('YYYY-MM-DD HH:mm')
					let content = userName + ' left the group ';
					api.addInfoGroupMessage(con, 
					{idGroup:groupId, userName:"", userId:0, content:content, time:messageTime, info:true},
					function() {
						socket.emit('message', 
							{id:0, userName:"", userId:0, messageContent:content, time:messageTime, 
								info:true} )
						socket.broadcast.emit('message', 
							{id:0, userName:"", userId:0, messageContent:content, time:messageTime,
								info:true} )
						socket.emit("removingMember", userId);
						socket.broadcast.emit("removingMember", userId);
						socket.emit("closeGroup", userId);
						socket.broadcast.emit("closeGroup", userId);
						//groupsSocket.broadcast.emit("removedMember",userId);
						nsp3.emit("removedMember",userId);
						numMembers = numMembers - 1;
						api.updateNumMembers(con,groupId,numMembers, function() {
							console.log('Group ' + groupId + " : has " + numMembers + " members");
						});
						if (isAdmin) {
							// TODO: Change the admin
							api.randomAdmin(con,groupId,function(adminId){
								console.log('Group ' + groupId + " : user " + adminId + " is now an admin");
							});
						}
					});
				});
			}else{
				api.deleteGroup(con, groupId, function () {
					console.log("Group " + groupId + " is deleted");
					socket.emit("groupDeleted");
					socket.emit("deletedGroup");
					nsp3.emit("groupDeleted");
				});
			}
		});

		socket.on("deleteGroup", function(idUser, nameUser) {
			api.deleteGroup(con, groupId, function () {
					console.log("Group " + groupId + " is deleted");
					socket.emit("groupDeleted");
					socket.emit("deletedGroup");
					nsp3.emit("groupDeleted");
				});
		});

		socket.on("renameGroup", function(nameUser, newName) {
			api.updateName(con, groupId, newName, function () {
					console.log("Group " + groupId + " : " + nameUser + " changed name to " + newName);
					socket.emit("newName",newName);
					socket.emit("nameChanged",newName);
					socket.broadcast.emit("nameChanged",newName);
					nsp3.emit("nameChanged",{groupId:groupId, newName:newName});

					let messageTime = moment().format('YYYY-MM-DD HH:mm')
					let content = nameUser + ' changed the group name to ' + newName;
					api.addInfoGroupMessage(con, 
					{idGroup:groupId, userName:"", userId:0, content:content, time:messageTime, info:true},
					function() {
						socket.emit('message', 
							{id:0, userName:"", userId:0, messageContent:content, time:messageTime, 
								info:true} )
						socket.broadcast.emit('message', 
							{id:0, userName:"", userId:0, messageContent:content, time:messageTime,
								info:true} )
					});

				});
		});

	});

};
