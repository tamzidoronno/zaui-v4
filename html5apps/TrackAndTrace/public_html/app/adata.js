/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


adata = {
    routes: [],
    exceptions: [],
    loaded: false,
    
    loadAllData: function ($api, $scope) {
        var me = this;
        
        $api.getApi().TrackAndTraceManager.getMyRoutes().done(function (res) {
            me.routes = res;
            me.loaded = true;
            me.save();
            $scope.$apply();
        });
        
        $api.getApi().TrackAndTraceManager.getExceptions().done(function (res) {
            me.exceptions = res;
            me.save();
            $scope.$apply();
        });
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
    
    save: function() {
        localStorage.setItem("aDataRoutes", JSON.stringify(this.routes));
        localStorage.setItem("aDataExceptions", JSON.stringify(this.exceptions));
    },
    
    loadFromLocalStorage: function() {
        if (localStorage.getItem("aDataToSave")) {
            this.routes = JSON.parse(localStorage.getItem("aDataRoutes"));
        }
        
        if (localStorage.getItem("aDataExceptions")) {
            this.exceptions = JSON.parse(localStorage.getItem("aDataExceptions"));
        }
    }
};
    
adata.loadFromLocalStorage();