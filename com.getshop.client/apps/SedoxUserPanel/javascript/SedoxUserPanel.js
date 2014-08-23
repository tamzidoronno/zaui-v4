app.SedoxUserPanel = {
     loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: []
        }

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    },
            
    init: function() {
        $(document).on('click', '.SedoxUserPanel .filehistory .next', $.proxy(this.nextFileHistory, this));
        $(document).on('click', '.SedoxUserPanel .filehistory .prev', $.proxy(this.prevFileHistory, this));
        
        $(document).on('click', '.SedoxUserPanel .credithistory .next', $.proxy(this.nextCreditHistory, this));
        $(document).on('click', '.SedoxUserPanel .credithistory .prev', $.proxy(this.prevCreditHistory, this));
        
        $(document).on('click', '.SedoxUserPanel .downloadhistory .next', $.proxy(this.nextDownloadHistory, this));
        $(document).on('click', '.SedoxUserPanel .downloadhistory .prev', $.proxy(this.prevDownloadHistory, this));
        
        $(document).on('click', '.SedoxUserPanel .show_slave_credit_history', this.showSlaveCreditHistory);
        $(document).on('click', '.SedoxUserPanel .fa-expand', this.expandBox);
    },
    expandBox: function() {
	var box = $(this).closest('.sedox_box');
	
	if (box.hasClass('credithistory')) {
	    var event = thundashop.Ajax.createEvent(null, "showFullCreditHistory", box, null);
	    thundashop.common.showInformationBox(event, 'Transaction history');
	}
	if (box.hasClass('downloadhistory')) {
	    var event = thundashop.Ajax.createEvent(null, "showFullDownloadHistory", box, null);
	    thundashop.common.showInformationBox(event, 'Download history');
	}
	if (box.hasClass('filehistory')) {
	    var event = thundashop.Ajax.createEvent(null, "showFullFileHistory", box, null);
	    thundashop.common.showInformationBox(event, 'Upload history');
	}
    },
    showSlaveCreditHistory: function() {
        var data = {
            userId : $(this).attr('userid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showSlaveHistory", this, data);
        thundashop.common.showInformationBox(event, 'Credit history');
    },
            
    nextFileHistory: function() {
        var event = thundashop.Ajax.createEvent(null, "nextFileHistory", $('.SedoxUserPanel'), {});
        thundashop.Ajax.post(event);
    },
            
    prevFileHistory: function() {
        var event = thundashop.Ajax.createEvent(null, "prevFileHistory", $('.SedoxUserPanel'), {});
        thundashop.Ajax.post(event);
    },
            
    nextCreditHistory: function() {
        var event = thundashop.Ajax.createEvent(null, "nextTransactionHistory", $('.SedoxUserPanel'), {});
        thundashop.Ajax.post(event);
    },
            
    prevCreditHistory: function() {
        var event = thundashop.Ajax.createEvent(null, "prevTransactionHistory", $('.SedoxUserPanel'), {});
        thundashop.Ajax.post(event);
    },
            
    nextDownloadHistory: function() {
        var event = thundashop.Ajax.createEvent(null, "nextDownloadHistory", $('.SedoxUserPanel'), {});
        thundashop.Ajax.post(event);
    },
            
    prevDownloadHistory: function() {
        var event = thundashop.Ajax.createEvent(null, "prevDownloadHistory", $('.SedoxUserPanel'), {});
        thundashop.Ajax.post(event);
    }
};

app.SedoxUserPanel.init();