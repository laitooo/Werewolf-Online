

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

exports.getWolfVoted = function(doubleVotes,arr,gameId,callback){
	console.log('game ' + gameId + ' : wolfvotes : ' , arr);
	if (doubleVotes) {
		var maxValue1 = 0;
		var maxIndex1 = 0;

		for (var i = 0; i < arr.length; i++) {
			if (arr[i] > maxValue1) {
				maxValue1 = arr[i];
				maxIndex1 = i;
			}
		}

		console.log('max value' + maxValue1);

		if (maxValue1 == 0) {
			return callback({vote1:false, vote2:false});
		}

		var maxValue2 = 0;
		var maxIndex2 = 0;
		arr.splice(maxIndex1,1);

		for (var i = 0; i < arr.length; i++) {
			if (arr[i] > maxValue2) {
				maxValue2 = arr[i];
				maxIndex2 = i;
			}
		}

		if (maxValue2 == 0) {
			return callback({vote1:true, value1: maxValue1, vote2:false});
		} else {
			return callback({vote1:true, value1:maxValue1, vote2:true, value2:maxValue2});
		}
	}else {
		var maxIndex = 0;
		var maxValue = 0;
		for (var i = 0; i < arr.length; i++) {
			if (arr[i] > maxValue) {
				maxValue = arr[i];
				maxIndex = i;
			}
		}

		if (maxValue == 0) {
			return callback({vote1:false, vote2:false});
		}else{
			return callback({vote1:true, value1: maxValue, vote2:false});
		}
	}
}

