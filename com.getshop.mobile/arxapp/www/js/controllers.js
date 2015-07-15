var arxappControllers = angular.module('ArxAppControllers', ['ArxAppServices', 'ionic']);

arxappControllers.controller('LoginCtrl', function($scope, LoginService, LocalStorage, $ionicPopup, $state) {

  function Credentials(host, username, password) {
    this.host = host;
    this.username = username;
    this.password = password;
  }

  // load credentials from local storage
  $scope.userData = LocalStorage.getObject('userData');
  console.log($scope.userData);
  if ($scope.userData.credentials === undefined) {
    $scope.userData.credentials = [];
  }
 
  // create data object that will store values from the view
  $scope.data = {};


  // define a function to login with stored credentials
  $scope.loginWithSavedCredentials = function() {
    // find credentials for selected host
    var i = -1;
    for (var index = 0; index < $scope.userData.credentials.length; index++) {
      if ($scope.userData.credentials[index].host == $scope.data.selectedHost) {
        i = index;
        break;
      }
    }
    if (i == -1) {
      var alertPopup = $ionicPopup.alert({
        title: 'Login failed!',
        template: "Can't find credentials for this host. Please re-enter it."
      });      
    } else {
      // try to login using saved credentials
      LoginService.loginUser($scope.userData.credentials[i].host, $scope.userData.credentials[i].username, $scope.userData.credentials[i].password).success(function(data) {
        // go to menu
        $state.go('menu');
      }).error(function(data) {
        var alertPopup = $ionicPopup.alert({
          title: 'Login failed!',
          template: "Those credentials don't work anymore!"
        });
      });      
    }
  }
 
  // define a function to login with new credentials given by the user
  $scope.login = function() {
    LoginService.loginUser($scope.data.enteredHost, $scope.data.username, $scope.data.password).success(function(data) {
      var index = 0;
      // check if we already have credentials for this host
      for (index; index < $scope.userData.credentials.length; index++) {
        if ($scope.userData.credentials[index].host == $scope.data.host)
          break; // credentials will be updated at this index
      }
      // add or update credentials
      $scope.userData.credentials[index] = new Credentials($scope.data.enteredHost, $scope.data.username, $scope.data.password);
      // save to local storage
      LocalStorage.setObject('userData', $scope.userData);
      // go to menu
      $state.go('menu');
    }).error(function(data) {
      var alertPopup = $ionicPopup.alert({
        title: 'Login failed!',
        template: 'Please check your credentials!'
      });
    });
  }
});

arxappControllers.controller('MenuCtrl', function($scope) {});

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

  $scope.onDoorsFetched = function(result) {
    console.log(result);
    $scope.doors = result;
    $scope.$apply();
  }

  getshop.client.ArxManager.getAllDoors().done($scope.onDoorsFetched);
}]);

