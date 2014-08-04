var app = angular.module('customServices', []);


/*
var baseLogger = function () {
	this.messageCount = 0;
	this.log = function (msg) {
		console.log(this.msgType + " " + (this.messageCount++) + ": " + msg);
	}
};

var debugLogger = function () {};
debugLogger.prototype = new baseLogger();
debugLogger.prototype.msgType = "Debug";

var errorLogger = function() {};
errorLogger.prototype = new baseLogger();
errorLogger.prototype.msgType = "Error";


app.service("debugLogService", debugLogger);
app.service("errorLogService", errorLogger);


/*
app.factory('logService', function ($log) {
	var messageCount = 0;  // placing this outside of the returned service object means that consumers of the service don't have direct access to the messageCount variable
	return {
		log: function(msg) {
			$log.log("(LOG " + messageCount++ + ") " + msg);
		}
	};
});

*/



/**
 * The convention for provider objects methods is to allow them 
 * to be used to set the configuration when an argument 
 * is provided and query the configuration when there is no argument.
 *
 *
 * Taking advantage of the Fluent API, we can have the methods
 * return the object when said argument is provided... so that way
 * multiple configurations can be chained together!
 *
 *
 * Any module that injects this provider will do so by 
 * attaching "Provider" to the end of the name... i.e: debugLogServiceProvider...
 * ...typically using Module.config
 */
app.provider("debugLogService", function () {
	// a few options for configuring the service object
	var counter = true;
	var debug = true;
	return {
		messageCounterEnabled: function (setting) {
			if (angular.isDefined(setting)) {
				counter = setting;
				return this;
			} else {
				return counter;
			}
		},
		debugEnabled: function (setting) {
			if (angular.isDefined(setting)) {
				debug = setting;
				return this;
			} else {
				return debug;
			}
		},
		$get: function ($log) {
			return {
				messageCount: 0,
				log: function (msg) {
					if (debug) {
						$log.log("(LOG" + (counter ? 
													" " + this.messageCount++ + ") " : 
													") ")
											+ msg);
					}
				}
			};
		}
	}
});




