function init() {

	var rootpath = "//" + window.location.host + "/_ah/api";

	gapi.client.load('planitendpoints', 'v1', loadCallback, rootpath);
}

function loadCallback() {
	enableButtons();
}


function enableButtons() {
	
	// Set the onSubmit action for the name submission button
	$('#input-greeting-by-name')
		.on('click', function () {
			greetByName(); })
		.val('Click for a personal greeting.');

	
	$('#form-submit').on('click', function(event) {
		event.preventDefault();
		greetByPeriod();
	});
		
}

/*
 * Execute a request to the sayHelloByName() endpoints function.
 * Illustrates calling an endpoints function that takes an argument.
 */
function greetByName() {
	var name = $("#name-field").val();

	// Call the sayHelloByName() function.
	// It takes one argument: "name"
	// On success, pass the response to the sayHelloCallback()

	var request = gapi.client.planitendpoints.sayHelloByName( {'name': name});
	request.execute(sayHelloCallback);
}

function greetByPeriod() {
	var name = $("#name-form-field").val();
	var period = $("#period-field").val();
	
	var request = gapi.client.planitendpoints.greetByPeriod( {'name': name,
															  'period': period} );

	request.execute(sayHelloCallback);
}

// Process the JSON response
// In this case, just show an alert dialog box
// displaying the value fo the message field in the response. 
function sayHelloCallback(response) {
	alert(response.message); // the sayHelloByName method sets the object's message property... which we can access here 
}