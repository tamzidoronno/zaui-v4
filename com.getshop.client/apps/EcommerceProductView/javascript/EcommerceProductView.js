app.EcommerceProductView = {
    init: function() {
        $(document).on('click', '.EcommerceProductView .menuentry', app.EcommerceProductView.menuClicked);
        $(document).on('click', '.EcommerceProductView .showeditlist', app.EcommerceProductView.showEditList);
        $(document).on('click', '.EcommerceProductView .closeedit', app.EcommerceProductView.closeEditList);
        $(document).on('click', '.EcommerceProductView .addextraoption', app.EcommerceProductView.addExtraOption);
    },
    
    addExtraOption: function() {
        var optionId = $(this).attr('optionid');
        
        var extra = {
            optionsubid: app.EcommerceProductView.uuid4()
        };
        
        app.EcommerceProductView.addExtraOpt(extra, optionId);
    },
    
    saveOption: function(id) {
        var data = {
            id : id,
            extras: []
        };
        
        var optionDiv = $('div.optionbox[optionid="'+id+'"]');

        optionDiv.find('.optionrows .optionrow').each(function() {
            var extra = {
                id : $(this).attr('optionsubid')
            };
            
            $(this).find('input').each(function() {
                extra[$(this).attr('name')] = $(this).val();
            });
            
            data.extras.push(extra);
        });
        
        var event = thundashop.Ajax.createEvent(null, "saveOption", optionDiv, data);
        event['synchron'] = true;
        thundashop.Ajax.post(event,function(res) {
            console.log(res);
        }, {}, true, true);
    },
    
    uuid4: function() {
            //// return uuid of form xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx
        var uuid = '', ii;
        for (ii = 0; ii < 32; ii += 1) {
          switch (ii) {
          case 8:
          case 20:
            uuid += '-';
            uuid += (Math.random() * 16 | 0).toString(16);
            break;
          case 12:
            uuid += '-';
            uuid += '4';
            break;
          case 16:
            uuid += '-';
            uuid += (Math.random() * 4 | 8).toString(16);
            break;
          default:
            uuid += (Math.random() * 16 | 0).toString(16);
          }
        }
        return uuid;
    },
    
    addExtraOpt: function(optionData, optionId) {
        var optionDiv = $('div.optionbox[optionid="'+optionId+'"]');
        var optionClone = $('.optiontemplate').clone();
        optionClone.removeClass('optiontemplate');
        optionClone.attr('optionsubid', optionData.optionsubid);

        optionClone.find('[name="name"]').val(optionData.name);
        optionClone.find('[name="extraPriceDouble"]').val(optionData.extraPriceDouble);

        optionClone.find('input').change(function() {
            app.EcommerceProductView.saveOption(optionId);
        });

        optionClone.find('.deleteoptionrow').click(function() {
            $('.optionrows .optionrow[optionsubid="'+optionData.optionsubid+'"]').remove();
            app.EcommerceProductView.saveOption(optionId);
        });

        optionDiv.find('.optionrows').append(optionClone);    
    },
    
    populateExtraOptions: function(data) {
        
        
        if (!data.extras || data.extras.length == 0) {
            var extra = {
                optionsubid: app.EcommerceProductView.uuid4()
            }
         
            data.extras = [];
            data.extras.push(extra);
        }
        
        for (var i in data.extras) {
            var optionData = data.extras[i];
            app.EcommerceProductView.addExtraOpt(optionData, data.id);
        };
        
    },
    
    showEditList: function() {
        var listid = $(this).attr('listid');
        $('.editlist[listid="'+listid+'"]').show();
    },
    
    closeEditList: function() {
        $(this).closest('.editlist').hide();
    },
    
    menuClicked: function() {
        var tab = $(this).attr('tab');
        $('.EcommerceProductView .menuarea .menuentry').removeClass('active');
        $(this).addClass('active');
        
        $('.EcommerceProductView .workarea div[tab]').removeClass('active');
        $('.EcommerceProductView .workarea div[tab="'+tab+'"]').addClass('active');
        
        var data = {
            selectedTab : tab
        }
        
        var event = thundashop.Ajax.createEvent(null, "subMenuChanged", this, data);
        event['synchron'] = true;
        
        thundashop.Ajax.post(event, app.EcommerceProductView.tabChanged, data, true, true);
    },
    
    tabChanged: function(res, args) {
        if (res) {
            $('.EcommerceProductView .workarea div[tab="'+args.selectedTab+'"]').html(res);
        }
    }
};

app.EcommerceProductView.init();