
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
	response.success({});
});


var daysInMillis = function(days){
	return days * 24 * 60 * 60 * 1000;
};

/**
 * This event makes sure to send push notification to all users whenever a "didactical" Notice object is created or updated.
 * In addition, no push must be sent for "old" notices.
 * An "old" notice is the one which was updated more than 7 days after its first creation time  
 */
Parse.Cloud.afterSave("Notice", function(request) {
	var notice = request.object;
	if (notice.get("type") !== "didactical"){
		return;
	}
	var updateDate = notice.get("updatedAt");
	var creationDate = notice.get("createdAt");
	var expirationDate = new Date(creationDate.getTime() + daysInMillis(7));
	if (updateDate.getTime() >= expirationDate.getTime()){
		return;
	}
	Parse.Push.send({
		where: new Parse.Query(Parse.Installation),
		//channels: ["public"],
		data:{
			feature: "didactical"
		}
	});
});

/**
 * This event makes sure that whatever a job offer is created/updated every potentially interested user will be notified.
 * Interested users are the ones who match the new offer"s required skills
 * First, get a list of queries about all StudentInfos which contains at least one of new skills,
 * then find their relative ParseUser instances,
 * and finally find all ParseInstallation instances which match these users
 */
Parse.Cloud.afterSave("Position", function(request) {
	var job = request.object
	var newSkills = job.get("skills")	//strings array
	var queriesBasedOnSingleSkill = []	//Parse.Query("StudentInfo") array
	for (var i=0; i < newSkills.length; i++) {
		var values = [ newSkills[i] ]
		queriesBasedOnSingleSkill.push(
			new Parse.Query("StudentInfo").containsAll("skills", values)
		)
	}
	var interestedUsersInfoQuery = new Parse.Query("StudentInfo")._orQuery(queriesBasedOnSingleSkill)
	var interestedUsersQuery = new Parse.Query(Parse.User).matchesQuery("studentInfo", interestedUsersInfoQuery)
	var interestedInstallations = new Parse.Query(Parse.Installation).matchesQuery("user", interestedUsersQuery)
	Parse.Push.send({
		where: interestedInstallations,
		data:{
			feature: "job"
		}
	});
});





