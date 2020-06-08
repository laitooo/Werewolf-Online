var mysql = require('mysql');



exports.addUser = function(con,u,callback) {
    con.query("INSERT INTO users SET ?", 
	u,
	function (err, result) {
        if (err) throw err;
    	console.log("user joined");
    	var id = result.insertId;
    	return callback(id)
    });
}

exports.userExists = function(con,email,callback) {
	con.query("SELECT * FROM users WHERE email = ?", email, function (err, result) {
	    if (err) throw err;
	    //console.log(result);
	    return callback(result);
	});
}

exports.isUserExists = function(con,email,username,callback) {
	con.query("SELECT * FROM users WHERE email = '" + email + "' OR username = '" + username + "'", function (err, result) {
	    if (err) throw err;
	    //console.log(result);
	    return callback(result);
	});
}



exports.createGame = function(con,u,callback) {
    con.query("INSERT INTO games SET ?", 
	u,
	function (err, result) {
        if (err) throw err;
    	console.log("game created, ID: " + result.insertId);
    	var id = result.insertId;
    	return callback(id)
    });
}

exports.loadActiveGames = function(con,callback) {
    con.query("SELECT * FROM games WHERE finished = 0", 
	function (err, result) {
        if (err) throw err;
        //console.log(result);
    	return callback(result);
    });
}

exports.updateGamePlayers = function(con,id,num,callback) {
    con.query("UPDATE games SET ? WHERE id = " + id , 
	{"num":num},
	function (err, result) {
        if (err) throw err;
    	//console.log("game created, ID: " + result.insertId);
    	//var id = result.insertId;
    	return callback()
    });
}

exports.finishGame = function(con,id,callback) {
    con.query("UPDATE games SET ? WHERE id = " + id , 
	{"finished":true},
	function (err, result) {
        if (err) throw err;
    	//console.log("game created, ID: " + result.insertId);
    	//var id = result.insertId;
    	return callback()
    });
}






exports.sendRequest = function(con, nameReceiver, friend, callback) {
	con.query("SELECT * FROM users WHERE username = '" + nameReceiver + "'", function (err, result) {
	    if (err) throw err;
	    // friend.idReceiver  friend.idSender  friend.accepted
	    if (result.length == 1) {
	    	friend.idReceiver = result[0].id;
	    	con.query("SELECT * FROM requests WHERE idReceiver = " + friend.idReceiver + " AND idSender = " + friend.idSender ,
	    		function (err, result){
	    			if (err) throw err;
	    			if (result.length > 0) {
	    				return callback({error:true, state:2});
	    			}else{
	    				con.query("SELECT * FROM requests WHERE idSender = " + friend.idReceiver + " AND idReceiver = " 
	    					+ friend.idSender ,
				    		function (err, result){
				    			if (err) throw err;
				    			if (result.length > 0) {
				    				return callback({error:true, state:3});
				    			}else{
				    				con.query("INSERT INTO requests SET ?", 
									friend,
									function (err, result3) {
										if (err) throw err;
								    	console.log("friend request sent with id : " + result3.insertId);
								    	var id = result3.insertId;
								    	return callback({error:false, id:id, idReceiver:friend.idReceiver});
								    });
				    			}
				    		});
	    			}
	    		});
	    }else{
	    	return callback({error:true, state:1});
	    }
	});
}

exports.loadRequests = function(con,id,callback) {
	con.query("SELECT * FROM requests WHERE idReceiver = " + id ,
	    function (err, result){
	    if (err) throw err;		
	    return callback(result);	
	});
}





exports.addFriend = function(con,u,callback) {
    con.query("INSERT INTO friends SET ?", 
	u,
	function (err, result) {
        if (err) throw err;
    	console.log(u.name1 + " and " + u.name2 + " are now friends");
    	var id = result.insertId;
    	return callback(id)
    });
}

exports.areFriends = function(con,id1,name2,callback) {
	con.query("SELECT * FROM friends WHERE id1 = " + id1 + " AND name2 = '" + name2 + "'", function (err, result) {
	    if (err) throw err;
	    //console.log(result);
	    if (result.length > 0) {
	    	return callback(true);
	    }else{
	    	con.query("SELECT * FROM friends WHERE id2 = " + id1 + " AND name1 = '" + name2 + "'", function (err, result) {
	    		if (err) throw err;

	    		if (result.length > 0) {
	    			return callback(true);
	    		}else{
	    			return callback(false);
	    		}
	    	});
	    }
	});
}


exports.loadFriends = function(con,id,callback) {
	con.query("SELECT * FROM friends WHERE id1 = " + id + " or id2 = " + id,
	    function (err, result){
	    if (err) throw err;		
	    //console.log(result);
	    return callback(result);	
	});
}

exports.loadAllFriends = function(con,callback) {
	con.query("SELECT * FROM friends ",
	    function (err, result){
	    if (err) throw err;		
	    //console.log(result);
	    return callback(result);	
	});
}


exports.acceptRequest = function(con,idSender,idReceiver,nameSender,nameReceiver,callback) {
	con.query("DELETE FROM requests WHERE idSender = " + idSender + " AND idReceiver = " + idReceiver,
		function (err, result){
			if (err) throw err;

			var u = 
				{id1:idSender, id2:idReceiver, name1:nameSender, name2:nameReceiver };

			con.query("INSERT INTO friends SET ?", 
			u,
			function (err, result) {
		        if (err) throw err;
		    	console.log(u.name1 + " and " + u.name2 + " are now friends");
		    	var id = result.insertId;
		    	return callback(id)
		    });
		});
}

exports.cancelRequest = function(con,idSender,idReceiver,nameSender,nameReceiver,callback) {
	con.query("DELETE FROM requests WHERE idSender = " + idSender + " AND idReceiver = " + idReceiver,
		function (err, result){
			if (err) throw err;

			return callback();
		});
}







exports.addChatMessage = function(con,m,callback) {
    con.query("INSERT INTO chatmessages SET ?", 
	m,
	function (err, result) {
        if (err) throw err;
    	var id = result.insertId;
    	return callback(id)
    });
}

exports.loadChatMessages = function(con,id,callback) {
	con.query("SELECT * FROM chatmessages WHERE idchat = " + id ,
	    function (err, result){
	    if (err) throw err;		
	    return callback(result);	
	});
}











exports.createGroup = function(con,m,callback) {
	con.query("SELECT * FROM groups WHERE name = '" + m.name + "'", function (err, result) {
	    if (err) throw err;
	    //console.log(result);
	    if (result.length > 0) {
	    	return callback({error:true, state:1});
	    }else{
		    con.query("INSERT INTO groups SET ?", 
			m,
			function (err, result) {
		        if (err) throw err;
		    	var id = result.insertId;
		    	con.query("INSERT INTO members SET ?", 
				{idGroup:id, idUser:m.idAdmin, nameUser:m.nameAdmin, isAdmin:true},
				function (err, result) {
					if (err) throw err;
					m.id = id;
		    		m.error = false;
		    		return callback(m)
		    	});
		    });
		}
	});
}

exports.loadGroups = function(con,id,callback) {
	var groups = [];
	con.query("SELECT * FROM groups" ,
	    function (err, result){
	    if (err) throw err;	
	    for (var i = 0; i < result.length; i++) {
	    	con.query("SELECT * FROM members WHERE idUser = " + id ,
			    function (err, result2){
			    if (err) throw err;
			    if (result2.length == 1) {
			    	groups.push(result[i]);
				}
			});	
	    }	
	    return callback(result);	
	});
}

exports.addMember = function(con,m,callback) {
	con.query("SELECT * FROM members WHERE idGroup = " + m.idGroup  + " AND idUser = " + m.idUser
		, function (err, result) {
	    if (err) throw err;
	    //console.log(result);
	    if (result.length > 0) {
	    	return callback({error:true, state:1});
	    }else{
		    con.query("INSERT INTO members SET ?", 
			m,
			function (err, result) {
		        if (err) throw err;
		    	var id = result.insertId;
		    	m.id = id;
		    	m.error = false;
		    	return callback(m)
		    });
		}
	});
}











exports.loadMembers = function(con,id,callback) {
	con.query("SELECT * FROM members WHERE idGroup = " + id ,
	    function (err, result){
	    if (err) throw err;		
	    return callback(result);	
	});
}

exports.loadAllGroups = function(con,callback) {
	con.query("SELECT * FROM groups ",
	    function (err, result){
	    if (err) throw err;		
	    //console.log(result);
	    return callback(result);	
	});
}









exports.addGroupMessage = function(con,m,callback) {
    con.query("INSERT INTO groupmessages SET ?", 
	m,
	function (err, result) {
        if (err) throw err;
    	var id = result.insertId;
    	return callback(id)
    });
}

exports.loadGroupMessages = function(con,id,callback) {
	con.query("SELECT * FROM groupmessages WHERE idGroup = " + id ,
	    function (err, result){
	    if (err) throw err;		
	    //console.log('group id : ' + id);
	    //console.log(result)
	    return callback(result);	
	});
}