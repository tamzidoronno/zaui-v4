var arxappControllers = angular.module('ArxAppControllers', ['ArxAppServices', 'ionic']);

arxappControllers.controller('LoginCtrl', function($scope, LoginService, LocalStorage, $ionicPopup, $state) {

  function Credentials(host, username, password, alias) {
    this.host = host;
    this.username = username;
    this.password = password;
    this.alias = alias;
  }

  // load credentials from local storage
  $scope.userData = LocalStorage.getObject('userData');
  if ($scope.userData.credentials === undefined) {
    $scope.userData.credentials = [];
  }
 
  // create data object that will store values from the view
  $scope.data = {};


  // define a function to login with new credentials given by the user
  $scope.updateFields = function() {
      var credentials = $scope.userData.credentials;
      for(var key in credentials) {
          var alias = credentials[key].alias;
          if(alias === undefined) {
              alias = "";
          }
          var index = credentials[key].host + " - " + alias;
          if(index === $scope.data.selectedHost) {
              var credential = credentials[key];
              $scope.data.enteredHost = credential.host;
              $scope.data.username = credential.username;
              $scope.data.password = credential.password;
              $scope.data.alias = credential.alias;
          }
      }
  }
  
  $scope.login = function() {
      
    var result = LoginService.loginUser($scope.data.enteredHost, $scope.data.username, $scope.data.password);
    
    $scope.isProcessing = true;
    
    result.done(function(data) {
      var index = 0;
      // check if we already have credentials for this host
      for (index; index < $scope.userData.credentials.length; index++) {
        if ($scope.userData.credentials[index].host == $scope.data.enteredHost)
          break; // credentials will be updated at this index
      }
      // add or update credentials
      var credentials = new Credentials($scope.data.enteredHost, $scope.data.username, $scope.data.password, $scope.data.alias);
      $scope.userData.credentials[index] = credentials;
      // save to local storage
      LocalStorage.setObject('userData', $scope.userData);
      LocalStorage.setObject('currentCredentials', credentials);
      // go to menu
      $state.go('menu');
    });

    result.always(function() {
        $scope.isProcessing = false;
    });

    result.fail(function() {
        var alertPopup = $ionicPopup.alert({
            title: 'Login failed!',
            template: 'Please check your credentials!'
        });
    });
  }
});

arxappControllers.controller('MenuCtrl',  function($scope, LocalStorage) {
    var curCredentials = LocalStorage.getObject('currentCredentials');
    $('.footercontent').html(curCredentials.host + " - " + curCredentials.alias);
});
arxappControllers.controller('AboutCtrl', function($scope) {});

arxappControllers.controller('UsersCtrl', ['GetshopService', '$scope', function(getshop, $scope) {

  $scope.onPersonsFetched = function(result) {
    $scope.persons = result;
    $scope.$apply();
  }

  getshop.client.ArxManager.getAllPersons().done($scope.onPersonsFetched);

}]);

arxappControllers.controller('UserDetailCtrl', ['GetshopService', '$scope', '$stateParams', function(getshop, $scope, $stateParams) {

  // This should be replaced with api call to get only one person details
  $scope.onPersonFetched = function(result) {
    //console.log(result);
    $scope.persons = result;

  	for (var i = 0; i < $scope.persons.length; i++) {
  		var person = $scope.persons[i];
  		if (person.firstName == $stateParams.userId) {
  			$scope.user = person;
  			break;
  		}
  	}

    $scope.$apply();
  	
  }
  getshop.client.ArxManager.getAllPersons().done($scope.onPersonFetched);

}]);

arxappControllers.controller('DoorsCtrl', ['GetshopService', '$scope', function(getshop, $scope) {

  $scope.getToday = function() {
    var d = new Date();
    return d.getTime();
  }

  $scope.getYesterday = function() {
    var d = new Date();
    d.setDate(d.getDate()-100);
    return d.getTime();
  }

  $scope.forceOpen = function(externalId) {
    getshop.client.ArxManager.doorAction(externalId, 'forceOpen');
  }

  $scope.forceClose = function(externalId) {
    getshop.client.ArxManager.doorAction(externalId, 'forceClose');
  }

  $scope.pulseOpen = function(externalId) {
    getshop.client.ArxManager.doorAction(externalId, 'open');
  }

  $scope.onDoorsFetched = function(result) {
    $scope.doors = result;
    $scope.$apply();
  }

  getshop.client.ArxManager.getAllDoors().done($scope.onDoorsFetched);
}]);

arxappControllers.controller('DoorDetailCtrl', ['GetshopService', '$scope', '$stateParams', function(getshop, $scope, $stateParams) {

  $scope.onAccessLogFetched = function(result) {
    $scope.accessLog = result;
    $scope.$apply();
  }

  getshop.client.ArxManager.getLogForDoor($stateParams.id, $stateParams.from, $stateParams.to).done($scope.onAccessLogFetched);

}]);
