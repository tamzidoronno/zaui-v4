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
        
        $(document).on('click', '.SedoxUserPanel .transferCreditToSlave', this.showTransferCreditToSlave);
        $(document).on('click', '.SedoxUserPanel .slaveslist .fa-expand', this.showPartnerHistory);
        $(document).on('click', '.SedoxUserPanel .transferCreditButton', this.doTransferCredit);
        $(document).on('click', '.SedoxUserPanel .filter_slave_button', this.filterSlaveHistory);
        
        $(document).on('keyup', '.SedoxUserPanel #filtercredithistory', this.filterCreditHistory);
    },
    
    filterSlaveHistory: function() {
        if ($(this).hasClass("selected")) {
            $(this).removeClass("selected")
        } else {
            $(this).addClass("selected")
        }
        
        var selected = $('.SedoxUserPanel .filter_slave_button.selected');
        if (!selected.length) {
            $('.slaveinterallist').show();
        } else {
            $('.slaveinterallist').hide();
            
            $(selected).each(function() {
                $('.slaveinterallist.'+$(this).attr('slaveid')).show();
            });
        }
    },
    
    showPartnerHistory: function() {
        var data = {
            userId : $(this).attr('userid')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showAllSlaveHistory", this, data);
        thundashop.common.showInformationBox(event, 'Partner history');
    },
    
    filterCreditHistory: function() {
         var value = $('.SedoxUserPanel #filtercredithistory').val();
         if (!value) {
             $('.SedoxUserPanel .credithistory_list .row').show();
         } else {
            $('.SedoxUserPanel .credithistory_list .row').each(function() {
                var found = false;
                $(this).find('.col').each(function() {
                    var innerVal = $(this).html();
                    if (innerVal.toLowerCase().indexOf(value.toLowerCase()) > -1) {
                        found = true;
                    }
                })
                
                if (found) {
                    $(this).show();
                } else {
                    $(this).hide();
                }
                
            });
         }
         
    },
    
    doTransferCredit: function() {
        var creditAmount = $('#creditToTransfer').val();
        
        if (creditAmount === "") {
            alert('Can not be blank.');
            return;
        }
        if (isNaN(creditAmount)) {
            alert('Only numbers valid here');
            return;
        }
        
        creditAmount = parseInt(creditAmount);
        
        if (creditAmount === 0) {
            alert('Must be more then 0 credit.');
            return;
        }
        
        if (creditAmount < 0) {
            alert('Do you think you can transfer a negative amount of credit to your slave?');
            return;
        }
        
        var data = {
            amount : creditAmount,
            slaveId : $(this).attr('slaveId')
        };
        
        var me = this;
        var event = thundashop.Ajax.createEvent(null, "transferCreditToSlave", this, data);
        thundashop.Ajax.post(event, function() {
            var event = thundashop.Ajax.createEvent(null, "showTransferCredit", me, data);
            thundashop.common.showInformationBox(event, 'Transfer credit to slave');
            
            thundashop.common.Alert("Credit transferred", "You have transferred the credit now", false);
        });
    },
    
    showTransferCreditToSlave: function() {
        var data = {
            masterId : $(this).attr('masterId'),
            slaveId : $(this).attr('slaveId')
        };
        
        var event = thundashop.Ajax.createEvent(null, "showTransferCredit", this, data);
        thundashop.common.showInformationBox(event, 'Transfer credit to slave');
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