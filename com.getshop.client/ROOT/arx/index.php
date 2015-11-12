<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="initial-scale=1, maximum-scale=1, user-scalable=no, width=device-width">
    <title></title>

    <link href="lib/ionic/css/ionic.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">

    <!-- IF using Sass (run gulp sass first), then uncomment below and remove the CSS includes above
    <link href="css/ionic.app.css" rel="stylesheet">
    -->

    <!-- ionic/angularjs js -->
    <script src="lib/ionic/js/ionic.bundle.js"></script>

    <!-- cordova script (this will be a 404 during development) -->
    <script src="cordova.js"></script>

    <!-- your app's js -->
    <script src="lib/jquery/dist/jquery.min.js"></script>
    <script src="js/getshopapi.js"></script>
    <script src="js/services.js"></script>
    <script src="js/controllers.js"></script>
    <script src="js/app.js"></script>
  </head>
  <body ng-app="arxapp">
     
    <ion-nav-view></ion-nav-view>


   


    <!-- Here are the page partials -->
    <script id="login.html" type="text/ng-template">
        
        
      <ion-view title="Login">
        <ion-header-bar class="bar bar-header bar-light">
          <h1 class="title">Arx Services</h1>
        </ion-header-bar>
        <ion-content>
          <div class="list list-inset">
            <div class="item item-divider">Choose host:</div>
            <div class="item item-input item-button-right">
              <div class="input-label">
                Host
              </div>
              <select ng-model="data.selectedHost" ng-change="updateFields()">
                <option value="" disabled>Saved credentials</option>
                <option ng-repeat="creds in userData.credentials">{{creds.host}} - {{creds.alias}}</option>
              </select>
            </div>
            <div class="item item-divider">Or add new login</div>
            <label class="item item-input">
              <span class="input-label">Hostname</span>
              <input type="text" ng-model="data.enteredHost">
            </label>
            <label class="item item-input">
              <span class="input-label">Username</span>
              <input type="text" ng-model="data.username">
            </label>
            <label class="item item-input">
              <span class="input-label">Password</span>
              <input type="password" ng-model="data.password">
            </label>
            <label class="item item-input">
              <span class="input-label">Alias
              <span class="input-label-desc">* Not required<br>* Used for more profiles</span>
              </span>
              <input type="text" ng-model="data.alias">
            </label>
          </div>
          <div class="padding">
            <button class="button button-block button-positive" ng-click="login()" ng-disabled="isProcessing">
              <span>Sign in</span>
              <ion-spinner ng-if="isProcessing == true" icon="ripple"></ion-spinner>
            </button>
          </div>
      </ion-content>
      </ion-view>
    </script>
    
    <script id="about.html" type="text/ng-template">
      <ion-view title="About">
        <!--Header bar-->
        <ion-header-bar class="bar bar-header bar-light">
          <button class="button button-icon icon ion-arrow-left-c" ui-sref="menu"></button>
          <h1 class="title">About</h1> 
        </ion-header-bar>
        <ion-content>
          <!--Separator row-->
          <div class="separator-15vh"></div>
          <div class="list">
            <div class="item item-thumbnail-left">
              <img src="img/getshop.png">
              <h2>Created by GetShop AS</h2>
              <p>http://www.getshop.com</p>
              <p>Phone: 0047 483 11 484</p>
      <br>
              If you have feedback to this application or<br> would like to get more features added to it. <br><br>Contact us by calling the phone number above, <br>or simply send us an email to post@getshop.com.<br>
                    </div>
          </div>
        </ion-content>
      </ion-view>
    </script>
    <script id="menu.html" type="text/ng-template">
      <ion-view title="Menu">
        <!--Header bar-->
        <ion-header-bar class="bar bar-header bar-light">
          <h1 class="title">Arx Services</h1>
        </ion-header-bar>
        <ion-content>

          <!--Separator row-->
          <div class="separator-15vh"></div>
          <!--Two middle rows for menu items-->
          <div class="row">
<!--            <button class="button button-block button-clear button-dark high" ui-sref="login"><i class="icon ion-ios-cart big-icon"></i><br>Bestill</button> -->
            <button class="button button-block button-clear button-dark high" ui-sref="users"><i class="icon ion-person big-icon"></i><br>People</button>
            <button class="button button-block button-clear button-dark high" ui-sref="doors"><i class="icon ion-locked big-icon"></i><br>Doors</button>
          </div>
          <div class="row">
<!--            <button class="button button-block button-clear button-dark high" ui-sref="login"><i class="icon ion-cloud big-icon"></i><br>Cloud</button> -->
            <button class="button button-block button-clear button-dark high" ui-sref="about"><i class="icon ion-help big-icon"></i><br>About</button>
            <button class="button button-block button-clear button-dark high" ui-sref="login"><i class="icon ion-log-out big-icon"></i><br>Log out</button>
          </div>
          <!--Separator row-->
          <div class="separator-15vh"></div>
          <!--Row with number-->
        </ion-content>

        <ion-footer-bar align-title="left" class="bar-assertive">
          <h1 class="title footercontent"></h1>
        </ion-footer-bar>
      </ion-view>
    </script>
    <script id="users.html" type="text/ng-template">
      <ion-view title="Users">
        <ion-header-bar class="bar bar-header bar-light">
          <button class="button button-icon icon ion-arrow-left-c" ui-sref="menu"></button>
          <h1 class="title">People</h1> 
        </ion-header-bar>
        <ion-content>
            <div ng-if="isProcessing" style="text-align:center; padding-top: 20px;">
                <ion-spinner icon="ripple" class="largeripple"></ion-spinner>
            </div>

          <div class="list">

            <a class="item item-icon-left" ng-repeat="user in persons" href="#/users/{{user.firstName}}">
              <i class="icon ion-person"></i>
              <span class="item-note item-note-small">
                Access categories <br>
                <span class="group-badge">{{user.accessCategories.length}}</span>
              </span>
              <h2>{{user.firstName}} {{user.lastName}}</h2>
              <p>{{user.streetAddress}}</p>
              <p>{{user.endDate}}</p>
            </a>
          </div>
          </ion-content>
        <!--
        <ion-footer-bar class="bar bar-footer">
          <button class="button button-full" ui-sref="menu">Tilbake</button>
        </ion-footer-bar>
        -->
      </ion-view>
    </script>
    <script id="user-detail.html" type="text/ng-template">
      <ion-view title="{{user.firstName}} {{user.lastName}}">
        <ion-header-bar class="bar bar-header bar-light">
          <button class="button button-icon icon ion-arrow-left-c" ui-sref="users"></button>
          <h1 class="title">{{user.firstName}} {{user.lastName}}</h1>
        </ion-header-bar>
        <ion-content>
          <div class="list">

            <div class="item item-divider">
              Access categories
            </div>            
            <div class="item item-icon-left" ng-repeat="ac in user.accessCategories">
              <i class="icon ion-home"></i>
              <h2>{{ac.name}}</h2>
              <p>{{ac.description}}</p>
              <p>{{ac.startDate}} - {{ac.endDate}}</p>
            </div>
            <div class="item item-divider">
              Cards
            </div>
            <div class="item item-icon-left" ng-repeat="card in user.cards">
              <i class="icon ion-card"></i>
              <h2>{{card.cardid}}</h2>
              <p>{{card.format}}</p>
              <p>{{card.description}}</p>
            </div>
          </div>
  
            <div ng-if="isProcessing" style="text-align:center; padding-top: 20px;">
                <ion-spinner icon="ripple" class="largeripple"></ion-spinner>
            </div>
  
          </ion-content>
        <!--
        <ion-footer-bar class="bar bar-footer">
          <button class="button button-full" ui-sref="users">Tilbake</button>
        </ion-footer-bar>
        -->
      </ion-view>
    </script>
    <script id="doors.html" type="text/ng-template">
      <ion-view title="Doors">
       <ion-header-bar class="bar bar-header bar-light">
          <button class="button button-icon icon ion-arrow-left-c" ui-sref="menu"></button>
          <h1 class="title">Doors</h1>
        </ion-header-bar>
        <ion-content>
        
          <div style="text-align:center; padding-top: 20px;display:none;" class='loadingdoorspinner'>
              <ion-spinner icon="ripple" class="largeripple"></ion-spinner>
          </div>
          
          <div class="list">
            <div class="listentry item item-icon-left" ng-repeat="door in doors" >
              <span class="item-note item-note-small">
                <button class="button button-positive" ng-click="forceOpen(door.externalId)" ng-class="{green: door.forcedOpen}"><i class="icon ion-unlocked"></i><span class='btn-text'>Force open</span></button>
                <button class="button button-positive" ng-click="forceClose(door.externalId)" ng-class="{green: door.forcedClose}"><i class="icon ion-locked"></i><span class='btn-text'>Force close</span></button>
                <button class="button button-positive" ng-click="pulseOpen(door.externalId)" ><i class="icon ion-lock-combination"></i><span class='btn-text'>Pulse open</span></button>
                <a href="#/door?id={{door.externalId}}&from={{getYesterday()}}&to={{getToday()}}">
                    <button class="button button-positive"><i class="icon ion-arrow-right-b"></i><span class='btn-text'>Log</span></button>
                </a>
                
              </span>
              <i class="icon ion-key"></i>
              <h2>{{door.name}}</h2>
            </div>
          </div>
        </ion-content>

        <ion-footer-bar align-title="left" class="bar-assertive">
          <h1 class="title"></h1>
          <div class="buttons" ng-click="refreshDoorList()">
            <button class="button">Refresh door list</button>
          </div>
        </ion-footer-bar>
 
      </ion-view>
    </script>
    <script id="door-detail.html" type="text/ng-template">
      <ion-view title="{{door.name}} access log">
        <ion-header-bar class="bar bar-header bar-light">
          <button class="button button-icon icon ion-arrow-left-c" ui-sref="doors"></button>
          <h1 class="title">{{door.name}} access log</h1>
        </ion-header-bar>
        <ion-content>
        <div ng-if="isProcessing" style="text-align:center; padding-top: 20px;">
                <ion-spinner icon="ripple" class="largeripple"></ion-spinner>
        </div>
        <div ng-if="isEmpty" style="text-align:center; padding-top: 20px;">
            No entries where found.
        </div>
          <div class="list">

            <div class="item item-icon-left" ng-repeat="record in accessLog">
              <i class="icon ion-key"></i>
              <h2>{{record.personName}}</h2>
              <p>{{record.timestamp}} - {{record.type}} {{record.door}} with {{record.card}}</p>
            </div>

          </div>
        </ion-content>
         <ion-footer-bar align-title="left" class="bar-assertive">
          <div class="buttons">
            <input type="date" class="startDate"/>
          </div>&nbsp;&nbsp;&nbsp;
          <div class="buttons">
            <input type="date" class="endDate" />
          </div>
            <input type="hidden" class="doorId" />
          <div class="buttons">
            &nbsp;&nbsp;&nbsp;<span class="button"  ng-click="onChangeDates()" ng-disabled="isProcessing">Change date</span>
          </div>
        </ion-footer-bar>
      </ion-view>
      
      
      
    

    </script>

  </body>
</html>
