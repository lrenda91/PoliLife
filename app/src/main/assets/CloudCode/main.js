
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
	response.success({});
});


var daysInMillis = function(days){
	return days * 24 * 60 * 60 * 1000;
}

/**
 * This event makes sure to send push notification to all users whenever a 'didactical' Notice object is created or updated.
 * In addition, no push must be sent for 'old' notices.
 * An 'old' notice is the one which was updated more than 7 days after its first creation time  
 */
Parse.Cloud.afterSave('Notice', function(request) {
	var notice = request.object;
	if (notice.get('type') !== 'didactical'){
		return;
	}
	var updateDate = notice.get('updatedAt');
	var creationDate = notice.get('createdAt');
	var expirationDate = new Date(creationDate.getTime() + daysInMillis(7));
	if (updateDate.getTime() >= expirationDate.getTime()){
		return;
	}
	Parse.Push.send({
		channels: [ "public" ],
		data:{
			feature: 'didactical'
		}
	});

});

