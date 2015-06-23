thundashop.Namespace.Register('thundashop.app.GoogleMaps');
thundashop.app.GoogleMaps = function(config) {
    this.config = config;
};

app.GoogleMaps = {
    loadSettings: function(element, application) {
        var config = {
            draggable: true,
            app: true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize: "30",
                    title: __f("Edit map"),
                    click: thundashop.app.GoogleMapsCommon.loadSettings
                }
            ]
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }

}

thundashop.app.GoogleMaps.prototype = {
    config: null,
    directionsDisplay: null,
    directionsService: null,
    map: null,
    allowedBounds: null,
    boundsToChoose: null,
    mapDiv: null,
    boundaries: null,
    countCheck: 0,
    curSize: 0,
    waitForGoogleMapsToLoad: function(loaded) {
        var scope = this;
        
        if (thundashop.app.GoogleMapsCommon === false || typeof(google.maps.DirectionsRenderer) === "undefined") {
            console.log("Waiting " + thundashop.app.GoogleMapsCommon + " " + google.maps.DirectionsRenderer);
            setTimeout(function() {
                scope.waitForGoogleMapsToLoad(loaded);
            }, 100);
        } else {
            setTimeout(function() {
                loaded.resolve();
            }, "500");
        }
    },
    initialize: function(draggable) {
        var loaded = $.Deferred();
        this.waitForGoogleMapsToLoad(loaded);
        var scope = this;
        loaded.done(function() {
            directionsDisplay = new google.maps.DirectionsRenderer();
            if (scope.config === undefined) {
                scope.config = {};
            }
            if (scope.config.startlongitude !== undefined) {
                var center = new google.maps.LatLng(parseFloat(scope.config.startlongitude), parseFloat(scope.config.startaltitude));
            } else {
                var center = new google.maps.LatLng(1, 1);
            }
            if (scope.config.zoom === undefined) {
                scope.config.zoom = 3;
            }
            var mapOptions = {
                draggable: draggable,
                scaleControl: draggable,
                scrollwheel: draggable,
                zoom: parseInt(scope.config.zoom),
                mapTypeId: google.maps.MapTypeId.ROADMAP,
                center: center,
                minZoom: parseInt(scope.config.minZoom),
                maxZoom: parseInt(scope.config.maxZoom)
            }
            scope.mapOptions = mapOptions;
            console.log("container: " + scope.config.container);
            scope.mapDiv = document.getElementById(scope.config.container);
            scope.map = new google.maps.Map(scope.mapDiv, scope.mapOptions);
            var box = $(scope.mapDiv);
            for(var i = 0; i < 10; i++) {
                box = box.parents('.gsucell');
                var height = box.height();
                if(height !== 0) {
                    break;
                }
            }
            $(scope.mapDiv).css('height',height);
            google.maps.event.trigger(scope.map,'resize');            
            scope.map.setCenter(center);
            directionsDisplay.setMap(scope.map);
            scope.initializeMarkers();

            google.maps.event.addListener(scope.map, "center_changed", function() {
                scope.checkBounds.apply(scope, []);
            });
            scope.boundaries = [];

            for (var key in scope.config.boundaries) {
                var entry = scope.config.boundaries[key];
                var allowedBounds = new google.maps.LatLngBounds(
                        new google.maps.LatLng(parseFloat(entry.sw_latitude), parseFloat(entry.sw_longitude)),
                        new google.maps.LatLng(parseFloat(entry.ne_latitude), parseFloat(entry.ne_longitude)));
                scope.boundaries.push(allowedBounds);
            }

            var me = this;
            PubSub.subscribe('POSTED_DATA_WITHOUT_PRINT', function() {
                if (!me.config) {
                    return;
                }
                
                var container = $('#' + me.config.container);
                me.curSize = 0;
                $(me.mapDiv).height(1);

            });
        });
    },
    initializeMarkers: function() {
        for (var key in this.config.markers) {
            var entry = this.config.markers[key];
            this.addpoint(entry);
        }
    },
    addpoint: function(entry) {
        var point = new google.maps.LatLng(parseFloat(entry.latitude), parseFloat(entry.longitude));
        var marker = new google.maps.Marker({
            position: point,
            draggable: false,
            map: this.map,
            labelStyle: {opacity: 0.75}
        });
        var iw = new google.maps.InfoWindow({
            content: entry.description
        });
        if(entry.leaveOpen && entry.leaveOpen === "true") {
            iw.open(this.map, marker);
        } else {
            google.maps.event.addListener(marker, "click", function(e) {
                iw.open(this.map, marker);
            });
        }
        google.maps.event.addListener(marker, "mouseover", function(e) {
            $('.GoogleMaps .googlemaptooltip').tooltip({track: true, content: entry.title, hide: {duration: 0}});
            $('.GoogleMaps .googlemaptooltip').mouseover();
        });
        google.maps.event.addListener(marker, "mouseout", function(e) {
            $('.GoogleMaps .googlemaptooltip').mouseout();
        });
    },
    // If the map position is out of range, move it back
    checkBounds: function() {
        if (this.boundaries.length === 0) {
            return;
        }

        for (var index in this.boundaries) {
            var allowedBounds = this.boundaries[index];
            if (allowedBounds.contains(this.map.getCenter())) {
                this.boundsToChoose = allowedBounds;
                return;
            }
        }

        var C = this.map.getCenter();
        var X = C.lng();
        var Y = C.lat();

        var AmaxX = this.boundsToChoose.getNorthEast().lng();
        var AmaxY = this.boundsToChoose.getNorthEast().lat();
        var AminX = this.boundsToChoose.getSouthWest().lng();
        var AminY = this.boundsToChoose.getSouthWest().lat();

        if (X < AminX) {
            X = AminX;
        }
        if (X > AmaxX) {
            X = AmaxX;
        }
        if (Y < AminY) {
            Y = AminY;
        }
        if (Y > AmaxY) {
            Y = AmaxY;
        }
        this.map.setCenter(new google.maps.LatLng(Y, X));


    }
};

thundashop.app.GoogleMapsCommon = {
    inizialized: false,
    loadSettings: function() {
        var event = thundashop.Ajax.createEvent('', 'configure', $(this), {});
        thundashop.common.showInformationBox(event);
    },
    addMarker: function() {
        var entry = $('#informationbox.GoogleMaps .container .templaterow').clone();
        entry.removeClass('templaterow');
        entry.addClass('entryrow');
        $('#informationbox.GoogleMaps .container .templaterow').after(entry);
    },
    addBoundary: function() {
        var entry = $('#informationbox.GoogleMaps .boundary_container .templaterow').clone();
        entry.removeClass('templaterow');
        entry.addClass('entryrow');
        $('#informationbox.GoogleMaps .boundary_container .templaterow').after(entry);
    },
    removeMarker: function() {
        $(this).closest('.row').remove();
    },
    saveConfiguration: function() {
        var container = $('#informationbox.GoogleMaps');
        var savedata = {
            zoom: container.find('.mapzoom').val(),
            startlongitude: container.find('.startlongitude').val(),
            startaltitude: container.find('.startaltitude').val(),
            width: container.find('.mapwidth').val(),
            height: container.find('.mapheight').val(),
            minZoom: container.find('.minzoom').val(),
            maxZoom: container.find('.maxzoom').val(),
            draggable : container.find('.draggable').val()
        };
        var entries = [];
        container.find('.container').find('.entryrow').each(function() {
            var entry = {
                description: $(this).find('.description').val(),
                longitude: $(this).find('.longitude').val(),
                latitude: $(this).find('.latitude').val(),
                title: $(this).find('.markertitle').val(),
                leaveOpen : $(this).find('.opendefault').is(':checked')
            }
            entries.push(entry);
        });
        savedata.markers = entries;

        var boundaries = [];
        container.find('.boundary_container').find('.entryrow').each(function() {
            var entry = {
                sw_latitude: $(this).find('.sw_latitude').val(),
                sw_longitude: $(this).find('.sw_longitude').val(),
                ne_latitude: $(this).find('.ne_latitude').val(),
                ne_longitude: $(this).find('.ne_longitude').val()
            }
            boundaries.push(entry);
        });
        savedata.boundaries = boundaries;


        var data = {
            "config": savedata
        }

        var event = thundashop.Ajax.createEvent('', 'saveConfiguration', $(this), data);
        thundashop.Ajax.post(event);
        thundashop.common.hideInformationBox();
    },
    initInfoBoxEvents: function() {
        var infobox = $(document);
        infobox.on('click', '#informationbox.GoogleMaps .add_marker', thundashop.app.GoogleMapsCommon.addMarker);
        infobox.on('click', '#informationbox.GoogleMaps .add_boundary', thundashop.app.GoogleMapsCommon.addBoundary);
        infobox.on('click', '#informationbox.GoogleMaps .remove_marker', thundashop.app.GoogleMapsCommon.removeMarker);
        infobox.on('click', '#informationbox.GoogleMaps .saveconfiguration', thundashop.app.GoogleMapsCommon.saveConfiguration);
    }
};
thundashop.app.GoogleMapsCommon.initInfoBoxEvents();

googleMapsInitialized = function() {
    thundashop.app.GoogleMapsCommon.inizialized = true;
}