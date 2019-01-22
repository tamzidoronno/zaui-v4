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
    currentVersion: "",
    
    allLoaded: function($scope, completedFunction, $state) {
        if (typeof(completedFunction) === "function") {
            completedFunction();
        }

        if ($state) {
            $state.go($state.current, {}, {reload: true});
            return;
        }

        if ($scope) {
            setTimeout($scope.$evalAsync(), 500);
        }

        localStorage.setItem("currentVersion", "1.0.15");
        }
    },
    
    loadAllData: function ($api, $scope, completedFunction, $state) {
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
            
            $api.getApi().TrackAndTraceManager.getExceptions().done(function (res) {
                me.exceptions = res;
                me.save();
                me.exceptionLoadCompleted = true;
                
                if ($api.getLoggedOnUser()) {
                    $api.getApi().TrackAndTraceManager.getDriverMessages($api.getLoggedOnUser().id).done(function (res) {
                        me.driverMessages = res;
                        me.save();
                        me.allLoaded($scope, completedFunction, $state);
                    });
                } else {
                    me.allLoaded($scope, completedFunction);
                }
            });    
            
        });
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
    
    removeRoute: function(routeId) {
        var newRoutes = [];
        for (var i in this.routes) {
            var route = this.routes[i];
            if (route.id !== routeId) {
                newRoutes.push(route);
            }
        }
        this.routes = newRoutes;
        this.save();
    },
    
    updateTask: function(destination, task, $api) {
        for (var i in this.routes) {
            var route = this.routes[i];
            for (var j in route.destinations) {
                if (route.destinations[j].id === destination.id) {
                    var found = false;
                    var kdest = route.destinations[j];
                    for (var k in kdest.tasks) {
                        var inTask = kdest.tasks[k];
                        if (inTask.id === task.id) {
                            kdest.tasks[k] = task;
                            found = true;
                        }
                    }
                    
                    if (!found) {
                        if (!route.destinations[j].tasks)
                            route.destinations[j].tasks = [];
                        
                        route.destinations[j].tasks.push(task);
                        route.destinations[j].taskIds.push(task.id);
                    }
                    
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
        if (db) {
            me = this;
            db.transaction(function(tx) {
                var insertingData = JSON.stringify(me.routes);
                
                tx.executeSql('CREATE TABLE IF NOT EXISTS DataTable (name, score)');
                tx.executeSql('delete from DataTable', []);
                tx.executeSql('INSERT INTO DataTable VALUES (?,?)', ['aDataRoutes', insertingData]);
                tx.executeSql('INSERT INTO DataTable VALUES (?,?)', ['aDataExceptions', JSON.stringify(me.exceptions)]);
                tx.executeSql('INSERT INTO DataTable VALUES (?,?)', ['aDriverMessages', JSON.stringify(me.driverMessages)]);
            }, function(error) {
                alert('Failed to store data, msg: ' + error.message);
            }, function() {
            });

            
        } else {
            localStorage.setItem("aDataRoutes", JSON.stringify(this.routes));
            localStorage.setItem("aDataExceptions", JSON.stringify(this.exceptions));
            localStorage.setItem("aDriverMessages", JSON.stringify(this.driverMessages));    
        }
        
    },
    
    clear: function() {
        this.routes = [];
        this.exceptions = [];
        this.driverMessages = [];
        this.save();
    },
    
    cleanupFaultyRoutes: function() {
        for (var i in this.routes) {
            if (i === "errorCode") {
                this.routes = [];
                break;
            }
        }
        
        for (var i in this.exceptions) {
            if (i === "errorCode") {
                this.exceptions = [];
                break;
            }
        }
        
        for (var i in this.driverMessages) {
            if (i === "errorCode") {
                this.driverMessages = [];
                break;
            }
        }
    },
    
    loadFromLocalStorage: function($state) {
        if (localStorage.getItem("currentVersion") !== "1.0.15") {
            return;
        }
        
        var me = this;
        
        if (db) {
            db.transaction(function(tx) {
                tx.executeSql('CREATE TABLE IF NOT EXISTS DataTable (name, score)');
                
                tx.executeSql('SELECT score as value FROM DataTable WHERE name = ?', ['aDataRoutes'], function(tx, rs) {
                    if (rs.rows && rs.rows.item) {
                        me.routes = JSON.parse(rs.rows.item(0).value);
                        
                        $state.go($state.current, {}, {reload: true});
                    }
                }, function(tx, error) {
                    alert("error: " + error.message);
                    this.routes = [];
                });
                
                tx.executeSql('SELECT score AS value FROM DataTable where name like "aDataExceptions" ', [], function(tx, rs) {
                    if (rs.rows && rs.rows.item) {
                        me.exceptions = JSON.parse(rs.rows.item(0).value);
                        $state.go($state.current, {}, {reload: true});
                    }
                }, function(tx, error) {
                    this.exceptions = [];
                });
                
                tx.executeSql('SELECT score AS value FROM DataTable where name like "aDriverMessages" ', [], function(tx, rs) {
                    if (rs.rows && rs.rows.item) {
                        me.driverMessages = JSON.parse(rs.rows.item(0).value);
                        $state.go($state.current, {}, {reload: true});
                    }
                }, function(tx, error) {
                    this.driverMessages = [];
                });
            });
            
        } else {
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
        
        this.cleanupFaultyRoutes();
    }
};
    
adata.loadFromLocalStorage();