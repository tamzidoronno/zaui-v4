/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


if(typeof(controllers) === "undefined") { var controllers = {}; }

controllers.MessagesController = function($scope, $api, $rootScope, $state, $stateParams, datarepository) {
    $scope.loadingMessages = true;
    $scope.pageNumber = 1;
    $scope.messagePage = null;
    
    $scope.goToHome = function() {
        $state.transitionTo('base.home');
    };
    
    $scope.messagesLoaded = function(res) {
        $scope.messagePage = res;
        $scope.loadingMessages = false;
        $scope.$evalAsync();
        console.log($scope.messagePage);
    }
    
    $scope.loadMessages = function() {
        $scope.loadingMessages = true;
        $api.getApi().MessageManager.getIncomingMessages($scope.pageNumber - 1).done($scope.messagesLoaded);
    }
    
    $scope.canGoToNextPage = function() {
        return $scope.messagePage.maxPages > $scope.pageNumber;
    }
    
    $scope.canGoToPrevPage = function() {
        return $scope.pageNumber > 1;
    }
    
    $scope.goToPrevPage = function() {
        $scope.pageNumber = $scope.pageNumber - 1;
        $scope.loadMessages();
    }
    
    $scope.goToNextPage = function() {
        $scope.pageNumber = $scope.pageNumber + 1;
        $scope.loadMessages();
    }
    
    $scope.loadMessages();
};