/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.SignupController = function($scope, $api, $stateParams) {
  
    $scope.alreadySignOnToCurrentEvent = null;
    
    $scope.getSelectedLocationsFromStorage = function() {
        var locations = localStorage.getItem("selectedLocations");
        if (locations) {
            return JSON.parse(locations);
        }
        
        return [];
    }
    
    
    var api = $api.getApi();
    $scope.selectedLocations = $scope.getSelectedLocationsFromStorage();
    $scope.showFilterModal = false;
    
    $scope.fetchSelectedEvent = function() {
        if ($stateParams.eventId) {
            api.EventBookingManager.getEvent("booking", $stateParams.eventId).done(function (event) {
                $scope.selectedEvent = event;
                $scope.$apply();
            });
            
            api.EventBookingManager.isUserSignedUpForEvent("booking", $stateParams.eventId, $api.getLoggedInUser().id).done(function($res) {
                $scope.alreadySignOnToCurrentEvent = $res;
                $scope.$apply();
            });
            
           
        }
    }
    
    
    $scope.isSelected = function(id) {
        for (var i in $scope.selectedLocations) {
            var id2 = $scope.selectedLocations[i];
            if (id2 === id) {
                return true;
            }
        }
        
        return false;
    }
    
    $scope.toggleSelectedFilter = function(id) {
        if (!$scope.isSelected(id)) {
            $scope.selectedLocations.push(id);
        } else {
            var index = $scope.selectedLocations.indexOf(id);
            if (index > -1) {
                $scope.selectedLocations.splice(index, 1);
            }
        }
        
        localStorage.setItem("selectedLocations", JSON.stringify($scope.selectedLocations));
    }
    
    $scope.refreshEventsList = function() {
        api.EventBookingManager.getEvents("booking").done(function(events) {
            $scope.events = events;
             
            if ($scope.selectedLocations.length > 0) {
                $scope.filterEvents();
            }
            
            $scope.$apply();
        });    
    }
    
    $scope.filterEvents = function() {
        $keep = []
        for (var i in $scope.selectedLocations) {
            var locationId = $scope.selectedLocations[i];
            for (var j in $scope.events) {
                var event = $scope.events[j];
                if (event.location.id === locationId) {
                    $keep.push(event);
                }
            }
        }
        
        $scope.events = $keep;
    }
    
    $scope.toggleFilter = function() {
        $scope.showFilterModal = !$scope.showFilterModal;
        if (!$scope.showFilterModal) {
            $scope.refreshEventsList();
        }
    }
    
    $scope.signMeUp = function() {
        api.EventBookingManager.addUserToEvent("booking", $stateParams.eventId, $api.getLoggedInUser().id, false, "mobile").done(function($res) {
            $scope.fetchSelectedEvent()
        });
    }
    
    api.EventBookingManager.getAllLocations("booking").done(function(locations) {
        $scope.locations = locations;
        $scope.$apply();
    });
    
    $scope.refreshEventsList();
    $scope.fetchSelectedEvent();
    
};