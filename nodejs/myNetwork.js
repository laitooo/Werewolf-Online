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
