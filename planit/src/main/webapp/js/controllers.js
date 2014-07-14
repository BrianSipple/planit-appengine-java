'use strict';

/**
 * The root planitApp module.
 *
 * @type {planitApp|*|{}}
 */

var planitApp = planitApp || {};


 /**
 * @ngdoc module
 * @name planitControllers
 *
 * @description
 * Angular module for controllers.
 *
 */
planitApp.controllers = angular.module('planitControllers', ['ui.bootstrap']);


/**
 * Quick hack for preloading the logo in the header
 */
planitApp.controllers.controller('ImageController', 
		function() {
	this.logo = "../img/TransparentLogoBig.jpg";
})


/**
 * @ngdoc controller
 * @name MyProfileCtrl
 *
 * @description
 * A controller used for the My Profile page.
 */
planitApp.controllers.controller('MyProfileCtrl',
	function ($scope, $log, oauth2Provider, HTTP_ERRORS) {
		$scope.submitted = false;
		$scope.loading = false;

        /**
         * The initial profile retrieved from the server to know the dirty state.
         * @type {{}}
         */
        $scope.initialProfile = {};

        /**
         * Candidates for the teeShirtSize select box.
         * @type {string[]}
         */
        $scope.teeShirtSize = [
        	'XS',
        	'S',
        	'M',
        	'L',
        	'XL',
        	'XXL',
        	'XXXL'
        ];

        /**
         * Initializes the My profile page.
         * Update the profile if the user's profile has been stored.
        */
        $scope.init = function () {
        	var retrieveProfileCallback = function () {
        		$scope.profile = {};
        		$scope.loading = true;
        		gapi.client.planit.getProfile()
        			.execute(function (response) {
        				$scope.$apply(function () {
        					$scope.loading = false;
        					if (response.error) {
        						// Failed to get a user profile
        					} else {
        						// Succeeded to get the user profile
        						$scope.profile.displayName = response.result.displayName;
        						$scope.profile.age = response.result.age;
        						$scope.profile.mainEmail = response.result.mainEmail;
        						$scope.profile.teeShirtSize = response.result.teeShirtSize;
        						$scope.initialProfile = response.result;
        					}
        				});
        			}
        		);
        	};
        	if (!oauth2Provider.signedIn) {
        		var modalInstance = oauth2Provider.showLoginModal();
        		modalInstance.result.then(retreiveProfileCallback);
        	} else {
        		retrieveProfileCallback();
        	}
        };

        /**
         * Invokes the planit.saveProfile API.
         *
         */
        $scope.saveProfile = function () {
        	$scope.submitted = true;
        	$scope.loading = true;
        	gapi.client.planit.saveProfile($scope.profile)
        		.execute(function (resp) {
        			$scope.$apply(function () {
        				$scope.loading = false;
        				if (resp.error) {
        					// The request has failed
        					var errorMessage = resp.error.message || '';
        					$scope.messages = 'Failed to update a profile: ' + errorMessage
        					$scope.alertStatus = 'warning';
        					$log.error($scope.messages + 'Profile : ' + JSON.stringify($scope.profile));

        					if (resp.code && resp.code == HTTP_ERRORS.UNAUTHORIZED) {
        						oauth2Provider.showLoginModal();
        						return;
        					}
        				} else {
        					// The request has succeeded.
        					$scope.message = 'The profile has been updated';
        					$scope.alertStatus = 'success';
        					$scope.submitted = false;
        					$scope.initialProfile = {
        						displayName: $scope.profile.displayName,
        						mainEmail: $scope.profile.mainEmail,
        						age: $scope.profile.age,
        						teeShirtSize: $scope.profile.teeShirtSize
        					};

        					$log.info($scope.messages + JSON.stringify(resp.result));
        				}
        			});
        		});

        };
});


/**
 * @ngdoc controller
 * @name CreateEventCtrl
 *
 * @description
 * A controller used for the Create conferences page.
 */
planitApp.controllers.controller('CreateEventCtrl',
	function ($scope, $log, oauth2Provider, HTTP_ERRORS) {

		/**
         * The event object being edited in the page.
         * @type {{}|*}
         */
        $scope.event = $scope.event || {};

        /**
         * Holds the default values for the input candidates for city select.
         * @type {string[]}
         */
        $scope.cities = [
            'Chicago',
            'London',
            'Minneapolis',
            'Paris',
            'San Francisco',
            'Seattle',
            'Tokyo'
        ];

        /**
         * Holds the default values for the input candidates for topics select.
         * @type {string[]}
         */
        $scope.categories = [
        	'Responsive Web Design',
            'Programming Languages',
            'Web Technologies',
            'Movie Making',
            'Data Science',
            'Artificial Intelligence',
            'Robotics',
            'Open Data',
            'Medical Innovations',
            'Other'
        ];
        
        
        /**
         * Holds the list of selection options for US states
         * 
         */
        $scope.states = [
             "AK",
             "AL",
             "AR",
             "AZ",
             "CA",
             "CO",
             "CT",
             "DC",
             "DE",
             "FL",
             "GA",
             "GU",
             "HI",
             "IA",
             "ID",
             "IL",
             "IN",
             "KS",
             "KY",
             "LA",
             "MA",
             "MD",
             "ME",
             "MH",
             "MI",
             "MN",
             "MO",
             "MS",
             "MT",
             "NC",
             "ND",
             "NE",
             "NH",
             "NJ",
             "NM",
             "NV",
             "NY",
             "OH",
             "OK",
             "OR",
             "PA",
             "PR",
             "PW",
             "RI",
             "SC",
             "SD",
             "TN",
             "TX",
             "UT",
             "VA",
             "VI",
             "VT",
             "WA",
             "WI",
             "WV",
             "WY",
          ];
        

        /**
         * Tests if the arugment is an integer and not negative.
         * @returns {boolean} true if the argument is an integer, false otherwise.
         */
        $scope.isValidMaxAttendees = function () {
        	if (!$scope.event.maxAttendees || $scope.event.maxAttendees.length == 0) {
        		return true;
        	}
        	return /^[\d]+$/.test($scope.event.maxAttendees) && $scope.event.maxAttendees >= 0;
        }

        /**
         * Tests if the event.startDate and event.endDate are valid.
         * @returns {boolean} true if the dates are valid, false otherwise.
         */
        $scope.isValidDates = function () {
        	if (!$scope.event.startDate && !$scope.event.endDate) {
        		return true;
        	}
        	if ($scope.event.startDate && !$scope.event.endDate) {
        		return true;
        	}
        	return $scope.event.startDate <= $scope.event.endDate;
        }

        /**
         * Tests if the event.startTime and event.endTime are valid.
         * @returns {boolean} true if the dates are valid, false otherwise.
         */
        $scope.isValidTimes = function () {
        	if (!$scope.event.startTime && !$scope.event.endTime) {
        		return true;
        	}
        	if ($scope.event.startTime && !$scope.event.endTime) {
        		return true;
        	}
        	return $scope.event.startTime <= $scope.event.endTime;
        }

        /**
         * Tests if $scope.event is valid.
         * @param eventForm the form object from the create_events.html page.
         * @returns {boolean|*} true if valid, false otherwise.
         */
        $scope.isValidEvent = function (eventForm) {
            return !eventForm.$invalid &&
                $scope.isValidMaxAttendees() &&
                $scope.isValidDates();
                //&& $scope.isValidTimes();
        }

        /**
         * Invokes the event.createEvent API.
         *
         * @param eventForm the form object.
         */

        $scope.createEvent = function (eventForm) {
        	if (!$scope.isValidEvent(eventForm)) {
        		return;
        	}

        	$scope.loading = true;
        	gapi.client.planit.createEvent($scope.event)
        		.execute(function (resp) {
        			$scope.$apply(function () {
        				$scope.loading = false;
        				if (resp.error) {
        					// The request has failed
        					var errorMessage = resp.error.message || '';
        					$scope.messages = 'Failed to create an event : ' + errorMessage;
        					$scope.alertStatus = 'warning';
        					$log.error($scope.messages + ' Event : ' + JSON.stringify($scope.event));

        					if (resp.code && resp.code == HTTP_ERRORS.UNAUTHORIZED) {
        						oauth2Provider.showLoginModal();
        						return;
        					}
         				} else {
         					// The request has succeeded
         					$scope.messages = 'The event has been created : ' + resp.result.title;
         					$scope.alertStatus = 'success';
         					$scope.submitted = false;
         					$scope.event = {};
         					$log.info($scope.messages + " : " + JSON.stringify(resp.result));
         				}
        			});
        		});
        };
});




/**
 * @ngdoc controller
 * @name ShowEventCtrl
 *
 * @description
 * A controller used for the Show events page.
 */
planitApp.controllers.controller('ShowEventCtrl', function ($scope, $log, oauth2Provider, HTTP_ERRORS) {

    /**
     * Holds the status if the query is being executed.
     * @type {boolean}
     */
    $scope.submitted = false;

    $scope.selectedTab = 'ALL';

    /**
     * Holds the filters that will be applied when queryEventsAll is invoked.
     * @type {Array}
     */
    $scope.filters = [
    ];

    $scope.filtereableFields = [
        {enumValue: 'CITY', displayName: 'City'},
        {enumValue: 'CATEGORY', displayName: 'Category'},
        {enumValue: 'MONTH', displayName: 'Start month'},
        {enumValue: 'MAX_ATTENDEES', displayName: 'Max Attendees'},
        {enumValue: 'ATTENDEES', displayName: 'Attendees'},
        {enumValue: 'REGISTRATIONS_AVAILABLE', displayName: 'Registrations Available'}
    ]

    /**
     * Possible operators.
     *
     * @type {{displayName: string, enumValue: string}[]}
     */
    $scope.operators = [
        {displayName: '=', enumValue: 'EQ'},
        {displayName: '>', enumValue: 'GT'},
        {displayName: '>=', enumValue: 'GTEQ'},
        {displayName: '<', enumValue: 'LT'},
        {displayName: '<=', enumValue: 'LTEQ'},
        {displayName: '!=', enumValue: 'NE'}
    ];

    /**
     * Holds the events currently displayed in the page.
     * @type {Array}
     */
    $scope.events = [];

    /**
     * Holds the state if offcanvas is enabled.
     *
     * @type {boolean}
     */
    $scope.isOffcanvasEnabled = false;

    /**
     * Sets the selected tab to 'ALL'
     */
    $scope.tabAllSelected = function () {
        $scope.selectedTab = 'ALL';
        $scope.queryEvents();
    };

    /**
     * Sets the selected tab to 'YOU_HAVE_CREATED'
     */
    $scope.tabYouHaveCreatedSelected = function () {
        $scope.selectedTab = 'YOU_HAVE_CREATED';
        if (!oauth2Provider.signedIn) {
            oauth2Provider.showLoginModal();
            return;
        }
        $scope.queryEvents();
    };

    /**
     * Sets the selected tab to 'YOU_WILL_ATTEND'
     */
    $scope.tabYouWillAttendSelected = function () {
        $scope.selectedTab = 'YOU_WILL_ATTEND';
        if (!oauth2Provider.signedIn) {
            oauth2Provider.showLoginModal();
            return;
        }
        $scope.queryEvents();
    };

    /**
     * Toggles the status of the offcanvas.
     */
    $scope.toggleOffcanvas = function () {
        $scope.isOffcanvasEnabled = !$scope.isOffcanvasEnabled;
    };

    /**
     * Namespace for the pagination.
     * @type {{}|*}
     */
    $scope.pagination = $scope.pagination || {};
    $scope.pagination.currentPage = 0;
    $scope.pagination.pageSize = 20;
    /**
     * Returns the number of the pages in the pagination.
     *
     * @returns {number}
     */
    $scope.pagination.numberOfPages = function () {
        return Math.ceil($scope.events.length / $scope.pagination.pageSize);
    };

    /**
     * Returns an array including the numbers from 1 to the number of the pages.
     *
     * @returns {Array}
     */
    $scope.pagination.pageArray = function () {
        var pages = [];
        var numberOfPages = $scope.pagination.numberOfPages();
        for (var i = 0; i < numberOfPages; i++) {
            pages.push(i);
        }
        return pages;
    };

    /**
     * Checks if the target element that invokes the click event has the "disabled" class.
     *
     * @param event the click event
     * @returns {boolean} if the target element that has been clicked has the "disabled" class.
     */
    $scope.pagination.isDisabled = function (event) {
        return angular.element(event.target).hasClass('disabled');
    }

    /**
     * Adds a filter and set the default value.
     */
    $scope.addFilter = function () {
        $scope.filters.push({
            field: $scope.filtereableFields[0],
            operator: $scope.operators[0],
            value: ''
        })
    };

    /**
     * Clears all filters.
     */
    $scope.clearFilters = function () {
        $scope.filters = [];
    };

    /**
     * Removes the filter specified by the index from $scope.filters.
     *
     * @param index
     */
    $scope.removeFilter = function (index) {
        if ($scope.filters[index]) {
            $scope.filters.splice(index, 1);
        }
    };

    /**
     * Query the events depending on the tab currently selected.
     *
     */
    $scope.queryEvents = function () {
        $scope.submitted = false;
        if ($scope.selectedTab == 'ALL') {
            $scope.queryEventsAll();
        } else if ($scope.selectedTab == 'YOU_HAVE_CREATED') {
            $scope.getEventsCreated();
        } else if ($scope.selectedTab == 'YOU_WILL_ATTEND') {
            $scope.getEventsToAttend();
        }
    };

    /**
     * Invokes the planit.queryEvents API.
     */
    $scope.queryEventsAll = function () {
        var sendFilters = {
            filters: []			// This acts as the API's eventQueryForm
        };
        for (var i = 0; i < $scope.filters.length; i++) {
            var filter = $scope.filters[i];
            if (filter.field && filter.operator && filter.value) {
                sendFilters.filters.push({
                    field: filter.field.enumValue,
                    operator: filter.operator.enumValue,
                    value: filter.value
                });
            }
        }
        $scope.loading = true;
        gapi.client.planit.queryEvents(sendFilters).
            execute(function (resp) {
                $scope.$apply(function () {
                    $scope.loading = false;
                    if (resp.error) {
                        // The request has failed.
                        var errorMessage = resp.error.message || '';
                        $scope.messages = 'Failed to query events : ' + errorMessage;
                        $scope.alertStatus = 'warning';
                        $log.error($scope.messages + ' filters : ' + JSON.stringify(sendFilters));
                    } else {
                        // The request has succeeded.
                        $scope.submitted = false;
                        $scope.messages = 'Query succeeded : ' + JSON.stringify(sendFilters);
                        $scope.alertStatus = 'success';
                        $log.info($scope.messages);

                        $scope.events = [];
                        angular.forEach(resp.items, function (event) {
                            $scope.events.push(event);
                        });
                    }
                    $scope.submitted = true;
                });
            });
    }

    /**
     * Invokes the event.getEventsCreated method.
     */
    $scope.getEventsCreated = function () {
        $scope.loading = true;
        gapi.client.planit.getEventsCreated().
            execute(function (resp) {
                $scope.$apply(function () {
                    $scope.loading = false;
                    if (resp.error) {
                        // The request has failed.
                        var errorMessage = resp.error.message || '';
                        $scope.messages = 'Failed to query the events created : ' + errorMessage;
                        $scope.alertStatus = 'warning';
                        $log.error($scope.messages);

                        if (resp.code && resp.code == HTTP_ERRORS.UNAUTHORIZED) {
                            oauth2Provider.showLoginModal();
                            return;
                        }
                    } else {
                        // The request has succeeded.
                        $scope.submitted = false;
                        $scope.messages = 'Query succeeded : Events you have created';
                        $scope.alertStatus = 'success';
                        $log.info($scope.messages);

                        $scope.events = [];
                        angular.forEach(resp.items, function (event) {
                            $scope.events.push(event);
                        });
                    }
                    $scope.submitted = true;
                });
            });
    };

    /**
     * Retrieves the events to attend by calling the event.getProfile method and
     * invokes the event.getEvent method n times where n == the number of the events to attend.
     */
    $scope.getEventsToAttend = function () {
        $scope.loading = true;
        gapi.client.planit.getEventsToAttend().
            execute(function (resp) {
                $scope.$apply(function () {
                    if (resp.error) {
                        // The request has failed.
                        var errorMessage = resp.error.message || '';
                        $scope.messages = 'Failed to query the conferences to attend : ' + errorMessage;
                        $scope.alertStatus = 'warning';
                        $log.error($scope.messages);

                        if (resp.code && resp.code == HTTP_ERRORS.UNAUTHORIZED) {
                            oauth2Provider.showLoginModal();
                            return;
                        }
                    } else {
                        // The request has succeeded.
                        $scope.events = resp.result.items;
                        $scope.loading = false;
                        $scope.messages = 'Query succeeded : Events you will attend (or you have attended)';
                        $scope.alertStatus = 'success';
                        $log.info($scope.messages);
                    }
                    $scope.submitted = true;
                });
            });
    };
});


/**
 * @ngdoc controller
 * @name EventDetailCtrl
 *
 * @description
 * A controller used for the event detail page.
 */
planitApp.controllers.controller('EventDetailCtrl', function ($scope, $log, $routeParams, HTTP_ERRORS) {
    $scope.event = {};

    $scope.isUserAttending = false;

    /**
     * Initializes the event detail page.
     * Invokes the event.getEvent method and sets the returned conference in the $scope.
     *
     */
    $scope.init = function () {
        $scope.loading = true;
        gapi.client.planit.getEvent({
            websafeEventKey: $routeParams.websafeEventKey
        }).execute(function (resp) {
            $scope.$apply(function () {
                $scope.loading = false;
                if (resp.error) {
                    // The request has failed.
                    var errorMessage = resp.error.message || '';
                    $scope.messages = 'Failed to get the event : ' + $routeParams.websafeEventKey
                        + ' ' + errorMessage;
                    $scope.alertStatus = 'warning';
                    $log.error($scope.messages);
                } else {
                    // The request has succeeded.
                    $scope.alertStatus = 'success';
                    $scope.event = resp.result;
                }
            });
        });

        $scope.loading = true;
        // If the user is attending the event, updates the status message and available function.
        gapi.client.planit.getProfile().execute(function (resp) {
            $scope.$apply(function () {
                $scope.loading = false;
                if (resp.error) {
                    // Failed to get a user profile.
                } else {
                    var profile = resp.result;
                    for (var i = 0; i < profile.eventsToAttendKeys.length; i++) {
                        if ($routeParams.websafeEventKey == profile.eventsToAttendKeys[i]) {
                            // The user is attending the event.
                            $scope.alertStatus = 'info';
                            $scope.messages = 'You are attending this event';
                            $scope.isUserAttending = true;
                        }
                    }
                }
            });
        });
    };


    /**
     * Invokes the event.registerForEvent method.
     */
    $scope.registerForEvent = function () {
        $scope.loading = true;
        gapi.client.planit.registerForEvent({
            websafeEventKey: $routeParams.websafeEventKey
        }).execute(function (resp) {
            $scope.$apply(function () {
                $scope.loading = false;
                if (resp.error) {
                    // The request has failed.
                    var errorMessage = resp.error.message || '';
                    $scope.messages = 'Failed to register for the event : ' + errorMessage;
                    $scope.alertStatus = 'warning';
                    $log.error($scope.messages);

                    if (resp.code && resp.code == HTTP_ERRORS.UNAUTHORIZED) {
                        oauth2Provider.showLoginModal();
                        return;
                    }
                } else {
                    if (resp.result) {
                        // Register succeeded.
                        $scope.messages = 'Registered for the event';
                        $scope.alertStatus = 'success';
                        $scope.isUserAttending = true;
                        $scope.event.registrationsAvailable = $scope.event.regsitrationsAvailable - 1;
                    } else {
                        $scope.messages = 'Failed to register for the event';
                        $scope.alertStatus = 'warning';
                    }
                }
            });
        });
    };

    /**
     * Invokes the event.unregisterForEvent method.
     */
    $scope.unregisterFromEvent = function () {
        $scope.loading = true;
        gapi.client.planit.unregisterFromEvent({
            websafeEventKey: $routeParams.websafeEventKey
        }).execute(function (resp) {
            $scope.$apply(function () {
                $scope.loading = false;
                if (resp.error) {
                    // The request has failed.
                    var errorMessage = resp.error.message || '';
                    $scope.messages = 'Failed to unregister from the event : ' + errorMessage;
                    $scope.alertStatus = 'warning';
                    $log.error($scope.messages);
                    if (resp.code && resp.code == HTTP_ERRORS.UNAUTHORIZED) {
                        oauth2Provider.showLoginModal();
                        return;
                    }
                } else {
                    if (resp.result) {
                        // Unregister succeeded.
                        $scope.messages = 'Unregistered from the event';
                        $scope.alertStatus = 'success';
                        $scope.event.registrationsAvailable = $scope.event.registrationsAvailable + 1;
                        $scope.isUserAttending = false;
                        $log.info($scope.messages);
                    } else {
                        var errorMessage = resp.error.message || '';
                        $scope.messages = 'Failed to unregister from the event : ' + $routeParams.websafeEventKey +      // might also be just "websafeKey"
                            ' : ' + errorMessage;
                        $scope.messages = 'Failed to unregister from the event';
                        $scope.alertStatus = 'warning';
                        $log.error($scope.messages);
                    }
                }
            });
        });
    };


    /**
     * Logic for setting and checking which tab in an
     * event's display view is currently set/active
     */
    $scope.tab = 1;

    $scope.setTab = function(tab) {
        $scope.tab = tab;
    };

    $scope.isSet = function(tab) {
        return $scope.tab === tab;
    };

    
    /**
     * Get a list of the confirmed attendees for the current event
     *
     */

    $scope.attendees = {};
    
    $scope.getAttendeeProfiles = function() {
        $scope.loading = true;
        gapi.client.planit.getAttendeeProfiles({
            websafeEventKey: $routeParams.websafeEventKey
        }).execute(function(resp) {
            $scope.$apply(function() {
                $scope.loading = false;
                if (resp.error) {
                    var errorMessage = resp.error.message || " ";
                    $scope.messages = "Failed to find any users attending this event : " + errorMessage;
                    $scope.alertStatus = "warning";
                    $log.error($scope.messages);
                    if (resp.code && resp.code == HTTP_ERRORS.UNAUTHORIZED) {
                        oauth2Provider.showLoginModal();
                        return;
                    }
                } else {
                    // we have our list of profiles
                    $scope.alertStatus = 'success';
                    $scope.attendees = resp.result;
                }
            });
        });
    };


});


/**
 * @ngdoc controller
 * @name RootCtrl
 *
 * @description
 * The root controller having a scope of the body element and methods used in the application wide
 * such as user authentications.
 *
 */
planitApp.controllers.controller('RootCtrl', function ($scope, $location, oauth2Provider) {

    /**
     * Returns if the viewLocation is the currently viewed page.
     *
     * @param viewLocation
     * @returns {boolean} true if viewLocation is the currently viewed page. Returns false otherwise.
     */
    $scope.isActive = function (viewLocation) {
        return viewLocation === $location.path();
    };

    /**
     * Returns the OAuth2 signedIn state.
     *
     * @returns {oauth2Provider.signedIn|*} true if siendIn, false otherwise.
     */
    $scope.getSignedInState = function () {
        return oauth2Provider.signedIn;
    };

    /**
     * Calls the OAuth2 authentication method.
     */
    $scope.signIn = function () {
        oauth2Provider.signIn(function () {
            gapi.client.oauth2.userinfo.get().execute(function (resp) {
                $scope.$apply(function () {
                    if (resp.email) {
                        oauth2Provider.signedIn = true;
                        $scope.alertStatus = 'success';
                        $scope.rootMessages = 'Logged in with ' + resp.email;
                    }
                });
            });
        });
    };

    /**
     * Render the signInButton and restore the credential if it's stored in the cookie.
     * (Just calling this to restore the credential from the stored cookie. So hiding the signInButton immediately
     *  after the rendering)
     */
    $scope.initSignInButton = function () {
        gapi.signin.render('signInButton', {
            'callback': function () {
                jQuery('#signInButton button').attr('disabled', 'true').css('cursor', 'default');
                if (gapi.auth.getToken() && gapi.auth.getToken().access_token) {
                    $scope.$apply(function () {
                        oauth2Provider.signedIn = true;
                    });
                }
            },
            'clientid': oauth2Provider.CLIENT_ID,
            'cookiepolicy': 'single_host_origin',
            'scope': oauth2Provider.SCOPES
        });
    };

    /**
     * Logs out the user.
     */
    $scope.signOut = function () {
        oauth2Provider.signOut();
        $scope.alertStatus = 'success';
        $scope.rootMessages = 'Logged out';
    };

    /**
     * Collapses the navbar on mobile devices.
     */
    $scope.collapseNavbar = function () {
        angular.element(document.querySelector('.navbar-collapse')).removeClass('in');
    };

});


/**
 * @ngdoc controller
 * @name OAuth2LoginModalCtrl
 *
 * @description
 * The controller for the modal dialog that is shown when an user needs to login to achive some functions.
 *
 */
planitApp.controllers.controller('OAuth2LoginModalCtrl',
    function ($scope, $modalInstance, $rootScope, oauth2Provider) {
        $scope.signInViaModal = function () {
            oauth2Provider.signIn(function () {
                gapi.client.oauth2.userinfo.get().execute(function (resp) {
                    $scope.$root.$apply(function () {
                        oauth2Provider.signedIn = true;
                        $scope.$root.alertStatus = 'success';
                        $scope.$root.rootMessages = 'Logged in with ' + resp.email;
                    });

                    $modalInstance.close();
                });
            });
        };
    });

/**
 * @ngdoc controller
 * @name DatepickerCtrl
 *
 * @description
 * A controller that holds properties for a datepicker.
 */
planitApp.controllers.controller('DatepickerCtrl', function ($scope) {
    $scope.today = function () {
        $scope.dt = new Date();
    };
    $scope.today();

    $scope.clear = function () {
        $scope.dt = null;
    };

    // Disable weekend selection
    $scope.disabled = function (date, mode) {
        return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
    };

    $scope.toggleMin = function () {
        $scope.minDate = ( $scope.minDate ) ? null : new Date();
    };
    $scope.toggleMin();

    $scope.open = function ($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.opened = true;
    };

    $scope.dateOptions = {
        'year-format': "'yy'",
        'starting-day': 1
    };

    $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'shortDate'];
    $scope.format = $scope.formats[0];
});

















