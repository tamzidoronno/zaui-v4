/*** GetShop Z-Way HA module ****************************************************

Version: 1.0
(c) GetShop , 2019
-----------------------------------------------------------------------------
Author: Kai Toender (ktonder@gmail.com)

 *****************************************************************************/

getshopconfig = {};
devicesInitted = [];


// ----------------------------------------------------------------------------
// --- Class definition, inheritance and setup
// ----------------------------------------------------------------------------

function GetShop (id, controller) {
    this.lastFetchedOnDevice = "0_close";
    this.initted = [];
    console.log("Initializing GetShop module");
    GetShop.super_.call(this, id, controller);
}

inherits(GetShop, AutomationModule);

_module = GetShop;

GetShop.prototype.init = function (config) {
     GetShop.super_.prototype.init.call(this, config);
     this.controller.devices.on('change:metrics:level', this.lockStatusChanged);
     getshopconfig = {
	url : config.url,
        domainname : config.bookingname,
        ip : config.ip
     };
};

GetShop.prototype.stop = function () {
    var self = this;
    
    self.controller.devices.off("modify:metrics:level", self.lockStatusChanged);

    GetShop.super_.prototype.stop.call(this);
};

// ----------------------------------------------------------------------------
// --- Module methods
// ----------------------------------------------------------------------------


GetShop.prototype.lockStatusChanged = function(eDvc) {
     var state = eDvc.get("metrics:level");

     if (eDvc.get("deviceType") !== "doorlock" ) {
           return;
     }

     var newId = eDvc.get('id').split('_');
     newId = newId[2];
     newId = newId.split("-");
     newId = newId[0];
     var deviceId = newId;
     var lastFetchMatch = deviceId + "_open";

     var found = false;
     for (var i in devicesInitted) {
         var devId = devicesInitted[i];
         if (devId == deviceId) {
             found = true;
         }
     }
   
     if (!found) {
         devicesInitted.push(deviceId);
         return;
     }

     if (state === "open") {
          zway.devices[deviceId].TimeParameters.Set();
          zway.devices[deviceId].DoorLockLogging.Get(1);
          zway.devices[deviceId].DoorLockLogging.Get(2);
          zway.devices[deviceId].DoorLockLogging.Get(3);
          zway.devices[deviceId].DoorLockLogging.Get(4);
          zway.devices[deviceId].DoorLockLogging.Get(5);
          zway.devices[deviceId].DoorLockLogging.Get(6);
          zway.devices[deviceId].DoorLockLogging.Get(7);
          zway.devices[deviceId].DoorLockLogging.Get(8);
          zway.devices[deviceId].DoorLockLogging.Get(9);
          zway.devices[deviceId].DoorLockLogging.Get(10);
          zway.devices[deviceId].DoorLockLogging.Get(11);
          zway.devices[deviceId].DoorLockLogging.Get(12);
          zway.devices[deviceId].DoorLockLogging.Get(13);
          zway.devices[deviceId].DoorLockLogging.Get(14);
          zway.devices[deviceId].DoorLockLogging.Get(15);
          zway.devices[deviceId].DoorLockLogging.Get(16);
          zway.devices[deviceId].DoorLockLogging.Get(17);
          zway.devices[deviceId].DoorLockLogging.Get(18);
          zway.devices[deviceId].DoorLockLogging.Get(19);
          zway.devices[deviceId].DoorLockLogging.Get(20);

          if (getshopconfig.url) {
              var address = getshopconfig.url+"/scripts/lockActivity.php?deviceId="+deviceId+"&domainname="+getshopconfig.domainname+"&ip="+getshopconfig.ip;

              var req = {
                  url: address,
                  method: "GET"
              };

              req.headers = {};
              http.request(req);
          }
     }       
}

