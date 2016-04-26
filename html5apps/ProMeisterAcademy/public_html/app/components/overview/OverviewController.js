/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.OverviewController = function($scope, $api) {
    var api = $api.getApi();
    
    $scope.oldEvents = [];
    $scope.newEvents = [];
    $scope.tests = [];

    
    $scope.fetchMyEvents = function() {
        
        api.EventBookingManager.getMyEvents("booking").done(function(res) {
            $scope.myEvents = res;
            
            for (var i in $scope.myEvents) {
                if ($scope.myEvents[i].isInFuture) {
                    $scope.newEvents.push($scope.myEvents[i]);
                } else {
                    $scope.oldEvents.push($scope.myEvents[i]);
                }
            }
            
            $scope.$apply();
        });
    }
    
    $scope.fetchMyTest = function() {
        api.QuestBackManager.getTests().done(function(res) {
            console.log(res[0]);
            $scope.tests = res;
            $scope.$apply();
        });
    }
    
    $scope.fetchMyEvents();
    $scope.fetchMyTest();
}