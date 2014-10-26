app.SedoxLatestProducts = {
	init: function() {
		$(document).on('click', '.SedoxLatestProducts .latest_prev', app.SedoxLatestProducts.prev);
		$(document).on('click', '.SedoxLatestProducts .latest_next', app.SedoxLatestProducts.next);
	},
	
	loadSettings: function(element, application) {
        var config = {
            application: application,
            draggable: true,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
	},
	
	prev: function() {
		var firstVisible = null;
		var nextToView = null;
		var firstVisibleIndex = null;
		
		var prods = $('.latestlist .entry');
		var i = 0;
		prods.each(function() {
			if ($(this).is(":visible") && firstVisible === null) {
				firstVisible = $(this);
				firstVisibleIndex = i;
			}
			i++;
		});
		
		if (firstVisibleIndex) {
			$(prods[firstVisibleIndex-1]).show();
			$(prods[firstVisibleIndex+2]).hide();
		}
	},
	
	next: function() {
		var firstVisible = null;
		var nextToView = null;
		
		$('.latestlist .entry').each(function() {
			if (!$(this).is(":visible") && nextToView === null && firstVisible !== null) {
				nextToView = $(this);
			}
			
			if ($(this).is(":visible") && firstVisible === null) {
				firstVisible = $(this);
			}
		});
		
		if (nextToView) {
			firstVisible.hide();
			nextToView.show();
		}
	}
};

app.SedoxLatestProducts.init();