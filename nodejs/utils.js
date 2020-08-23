

exports.shuflle = function(array) {
	var currentIndex = array.length, temporaryValue, randomIndex;

	// While there remain elements to shuffle...
	while (0 !== currentIndex) {

	    // Pick a remaining element...
	    randomIndex = Math.floor(Math.random() * currentIndex);
	    currentIndex -= 1;

	    // And swap it with the current element.
	    temporaryValue = array[currentIndex];
	    array[currentIndex] = array[randomIndex];
	    array[randomIndex] = temporaryValue;
    }

    return array;
}

exports.mySleep = function(milliseconds) {
  return new Promise(resolve => setTimeout(resolve, milliseconds))
}

exports.getMostVoted = function(arr,gameId,callback){
	console.log('game ' + gameId + ' : votes : ' , arr);
	var maxIndex = 0;
	var maxValue = 0;
	for (var i = 0; i < arr.length; i++) {
		if (arr[i] > maxValue) {
			maxValue = arr[i];
			maxIndex = i;
		}
	}

	for (var i = 0; i < arr.length; i++) {
		if (maxIndex != i) {
			if (arr[i] >= maxValue) {
				return callback({draw:true, value:-1})
			}
		}
	}
	return callback({draw:false, value:maxIndex});
}

