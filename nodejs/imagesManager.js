var fs = require('fs');

/*const multer = require('multer');

const upload = multer({
  limits: {
    fileSize: 4 * 1024 * 1024,
  }
});
*/
exports.randomProfile = function (idUser,userName,callback) {
	var num = Math.floor(Math.random() * 10) + 1;
	fs.readFile('./public/icons/icon' + num + '.png' , function (err, data) {
	    if (err) throw err;
	    fs.writeFile('./public/images/image' + idUser + '.png', data, function (err) {
	        if (err) throw err;
	        callback('images/image' + idUser + '.png');
	        console.log(userName + ' : profile picture saved');
	    });
	});
}