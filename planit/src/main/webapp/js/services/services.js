var app = angular.module("customServices", []);

// Template object that our loggers will inherit properties and functions from
var baseLogger = function() {
	this.messageCount = 0;
	this.log = function (msg) {
		console.log(this.msgType + " " + (this.messageCount++) + ": " + msg);
	}
};


var debugLogger = function() {};
debugLogger.prototype = new baseLogger();  // inherit baseLogger's properties and functions
debugLogger.prototype.msgType = "Debug";

var errorLogger = function() {};
errorLogger.prototype = new baseLogger();  // inherit baseLogger's properties and functions
errorLogger.prototype.msgType = "Error";


app.service("debugLogService", debugLogger);
app.service("errorLogService", errorLogger);