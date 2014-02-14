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
    waitForGoogleMapsToLoad: function() {
        var scope = this;
        if (thundashop.app.GoogleMapsCommon === false) {
            setTimeout(scope.waitForGoogleMapsToLoad, 100);
        }
    },
    checkFitToContainer: function() {
        if (this.countCheck === 12) {
            return;
        }
        this.fitToContainer();
    },
    initialize: function() {
        this.waitForGoogleMapsToLoad();
        this.checkFitToContainer();
        directionsDisplay = new google.maps.DirectionsRenderer();
        if (this.config === undefined) {
            this.config = {};
        }
        if (this.config.startlongitude !== undefined) {
            var center = new google.maps.LatLng(parseFloat(this.config.startlongitude), parseFloat(this.config.startaltitude));
        } else {
            var center = new google.maps.LatLng(1, 1);
        }
        if (this.config.zoom === undefined) {
            this.config.zoom = 3;
        }
        var mapOptions = {
            zoom: parseInt(this.config.zoom),
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            center: center,
            minZoom: parseInt(this.config.minZoom),
            maxZoom: parseInt(this.config.maxZoom)
        }
        this.mapOptions = mapOptions;
        this.mapDiv = document.getElementById(this.config.container);
        this.map = new google.maps.Map(this.mapDiv, this.mapOptions);
        $(this.mapDiv).css('height', "200px");
        directionsDisplay.setMap(this.map);
        this.initializeMarkers();
        var scope = this;
        google.maps.event.addListener(this.map, "center_changed", function() {
            scope.checkBounds.apply(scope, []);
        });
        this.boundaries = [];

        for (var key in this.config.boundaries) {
            var entry = this.config.boundaries[key];
            var allowedBounds = new google.maps.LatLngBounds(
                    new google.maps.LatLng(parseFloat(entry.sw_latitude), parseFloat(entry.sw_longitude)),
                    new google.maps.LatLng(parseFloat(entry.ne_latitude), parseFloat(entry.ne_longitude)));
            this.boundaries.push(allowedBounds);
        }

        var me = this;
        PubSub.subscribe('POSTED_DATA_WITHOUT_PRINT', function() {
            var container = $('#' + me.config.container);
            me.curSize = 0;
            container.height(1);
            $(me.mapDiv).height(1);


            me.countCheck = 0;
            setTimeout(function() {
                me.checkFitToContainer();
            }, "200");
        });
    },
    initializeMarkers: function() {
        for (var key in this.config.markers) {
            var entry = this.config.markers[key];
            this.addpoint(entry);
        }
    },
    fitToContainer: function() {
        var container = $('#' + this.config.container);
        var appid = container.closest('.app').attr('appid');
        var maxHeight = 0;
        container.closest('.gs_inner').find('.app').each(function() {
            if (appid !== $(this).attr('appid')) {
                if (maxHeight < $(this).innerHeight()) {
                    maxHeight = $(this).innerHeight();
                }
            }
        });
        if (maxHeight > 0) {
            if(this.map !== null) {
                $(this.mapDiv).width(container.width());
                $(this.mapDiv).height(maxHeight);
                google.maps.event.trigger(this.map,'resize');
                return;
            }
        }

        var check = this;
        setTimeout(function() {
            check.countCheck++;
            check.checkFitToContainer();
        }, "150");
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
        google.maps.event.addListener(marker, "click", function(e) {
            iw.open(this.map, marker);
        });
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
            maxZoom: container.find('.maxzoom').val()
        };
        var entries = [];
        container.find('.container').find('.entryrow').each(function() {
            var entry = {
                description: $(this).find('.description').val(),
                longitude: $(this).find('.longitude').val(),
                latitude: $(this).find('.latitude').val(),
                title: $(this).find('.markertitle').val()
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