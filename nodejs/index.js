const express = require('express');
http = require('http');
app = express();
server = http.createServer(app);
io = require('socket.io').listen(server);

const hostname = '192.168.43.211';
const port = 3001;


app.get('/', (req, res) => {
	res.send('Chat Server is running on port 3001');
});

io.on('connection', (socket) => {

	console.log('user connected')

	socket.on('join', function(userNickname) {
		console.log(userNickname +" : has joined the chat "  );
	    socket.broadcast.emit('userjoinedthechat',userNickname +" : has joined the chat ");
	})

	socket.on('messagedetection', (senderNickname,messageContent) => {
		console.log(senderNickname+" : " +messageContent)
	    let  message = {"message":messageContent, "senderNickname":senderNickname}
	    io.emit('message', message )
	})

	socket.on('disconnect', function() {
	    console.log(userNickname +' has left ')
	    socket.broadcast.emit( "userdisconnect" ,' user has left')
	})
})

server.listen(port, hostname, ()=>{
	console.log('Node app is running on port 3001')
})