/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


adata = {
    routes: [],
    exceptions: [],
    loaded: false,
    driverMessages: [],
    
    loadAllData: function ($api, $scope) {
        var me = this;
        
        me.routes = [];
        me.exceptions = [];
        me.routeLoadCompleted = false;
        me.exceptionLoadCompleted = false;
            
        $api.getApi().TrackAndTraceManager.getMyRoutes().done(function (res) {
            me.routes = res;
            me.loaded = true;
            me.save();
            
            me.routeLoadCompleted = true;
            $scope.$apply();
            if (me.routeLoadCompleted && me.exceptionLoadCompleted) {
                $('.loadingData').hide();
            }
        });
        
        $api.getApi().TrackAndTraceManager.getExceptions().done(function (res) {
            me.exceptions = res;
            me.save();
            me.exceptionLoadCompleted = true;
            $scope.$apply();
            if (me.routeLoadCompleted && me.exceptionLoadCompleted) {
                $('.loadingData').hide();
            }
        });
        
        if ($api.getLoggedOnUser()) {
            $api.getApi().TrackAndTraceManager.getDriverMessages($api.getLoggedOnUser().id).done(function (res) {
                me.driverMessages = res;
                $scope.$apply();
                me.save();
            });
        }
    },
    
    updateDestination: function(destination, $api) {
        for (var i in this.routes) {
            var route = this.routes[i];
            for (var j in route.destinations) {
                if (route.destinations[j].id === destination.id) {
                    route.destinations[j] = destination;
                    console.log("replaced");
                }
            }
        }
        this.save();
    },
    
    updateRoute: function(route, $api) {
        if (!route)
            return;
        
        var toAdd = []; 
        var found = false;
        
        for (var i in this.routes) {
            var iRoute = this.routes[i];
            if (iRoute.id === route.id) {
                found = true;
                toAdd.push(route);
            } else {
                toAdd.push(iRoute);
            }
        }
        
        if (!found && $api) {
            toAdd.push(route);
        }
        
        
        this.routes = toAdd;
        this.save();
        
        return found;
    },
    
    getRouteById: function (routeId) {
        $routeToUse = null;
        $(this.routes).each(function () {
            if (this.id === routeId) {
                $routeToUse = this;
            }
        });

        return $routeToUse;
    },
    
    getDestinationById: function (destinationId) {
        $destToUse = null;
        $(this.routes).each(function () {
            $(this.destinations).each(function () {
                if (this.id === destinationId)
                    $destToUse = this;
            })
        });
        return $destToUse;
    },
    
    getTaskById: function(taskId) {
        $taskToUse = null;
        $(this.routes).each(function () {
            $(this.destinations).each(function () {
                $(this.tasks).each(function () {
                    if (this.id === taskId)
                        $taskToUse = this;
                });
            });
        });
        return $taskToUse;
    },
    
    getDestionationsExceptions: function() {
        var retArr = [];
        for (var i in this.exceptions) {
            var exception = this.exceptions[i];
            if (exception.type === "destionation") {
                retArr.push(exception);
            }
        }
        
        return retArr;
    },
    
    save: function() {
        localStorage.setItem("aDataRoutes", JSON.stringify(this.routes));
        localStorage.setItem("aDataExceptions", JSON.stringify(this.exceptions));
        localStorage.setItem("aDriverMessages", JSON.stringify(this.driverMessages));
    },
    
    loadFromLocalStorage: function() {
        if (localStorage.getItem("aDataRoutes")) {
            this.routes = JSON.parse(localStorage.getItem("aDataRoutes"));
        }
        
        if (localStorage.getItem("aDataExceptions")) {
            this.exceptions = JSON.parse(localStorage.getItem("aDataExceptions"));
        }
        
        if (localStorage.getItem("aDriverMessages")) {
            this.driverMessages = JSON.parse(localStorage.getItem("aDriverMessages"));
        }
    }
};
    
adata.loadFromLocalStorage();