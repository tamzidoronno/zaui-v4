GetShop.SedoxCarList = {
    init : function() {
        $('.SedoxCarList #flowtabs li').live('click', GetShop.SedoxCarList.navigateTabs);
        $('.SedoxCarList #manufacturer').live('change', GetShop.SedoxCarList.loadModel);
        $('.SedoxCarList #model').live('change', GetShop.SedoxCarList.loadEngine);
        $('.SedoxCarList #engine').live('change', GetShop.SedoxCarList.loadTuningData);
    },
    
    loadData: function() {
        GetShop.SedoxCarList.loadPane("t1");
    },
    
    disable : function() {
        $('.SedoxCarList #manufacturer').prop('disabled', true);
        $('.SedoxCarList #engine').prop('disabled', true);
        $('.SedoxCarList #model').prop('disabled', true);
    },
    
    enable : function() {
        $('.SedoxCarList #manufacturer').prop('disabled', false);
        $('.SedoxCarList #engine').prop('disabled', false);
        $('.SedoxCarList #model').prop('disabled', false);
    },
    
    navigateTabs : function() {
        GetShop.SedoxCarList.disable();
        $(this).closest('ul').find('span').removeClass('current');
        $(this).find('span').addClass('current');
        var id = $(this).find('span').attr('id');
        
        $('.SedoxCarList #flowpanes .tabarea').hide();
        $('.SedoxCarList #flowpanes .' + id).show();
        
        $('.SedoxCarList .name_type').hide();
        $('.SedoxCarList .name_box .'  +id).show();
        
        GetShop.SedoxCarList.loadPane(id, $(this));
    },
        
    loadPane : function(target) {
        var data = {
            "target" : target
        };
        
        var fromEvent = $('.SedoxCarList');
        GetShop.SedoxCarList.clearEngineData();
        GetShop.SedoxCarList.clearManufacturerData();
        GetShop.SedoxCarList.clearModelData();
        
        var dropDown = $('.SedoxCarList #engine');
        dropDown.find('option').remove();
        dropDown.append("<option value=''>-- please Select --</option>");
        
        var event = thundashop.Ajax.createEvent("", "loadManufacturer", fromEvent, data);
        thundashop.Ajax.postWithCallBack(event, GetShop.SedoxCarList.fillPane);
    },
        
    clearModelData : function() {
        var dropDown = $('.SedoxCarList #model');
        dropDown.find('option').remove();
        dropDown.append("<option value=''>-- please Select --</option>");
        $('.SedoxCarList .result').hide();
    },
        
    clearManufacturerData : function() {
        var dropDown = $('.SedoxCarList #manufacturer');
        dropDown.find('option').remove();
        dropDown.append("<option value=''>-- please Select --</option>");
        $('.SedoxCarList .result').hide();
    },
        
        
    clearEngineData : function() {
        var dropDown = $('.SedoxCarList #engine');
        dropDown.find('option').remove();
        dropDown.append("<option value=''>-- please Select --</option>");
        $('.SedoxCarList .result').hide();
    },
        
    fillPane : function(data) {
        data = JSON.parse(data);
        var dropDown = $('.SedoxCarList #manufacturer');
        
        for(var key in data) {
            dropDown.append("<option value='"+data[key].category_id+"'>"+data[key].name+"</option>");
        }
        
        GetShop.SedoxCarList.enable();
    },
        
    loadModel : function() {
        GetShop.SedoxCarList.disable();
        var val = $(this).val();
        var data = {
            "target" : val
        };
    
        GetShop.SedoxCarList.clearModelData();
        GetShop.SedoxCarList.clearEngineData();

        var event = thundashop.Ajax.createEvent("", "loadChildren", $(this), data);
        thundashop.Ajax.postWithCallBack(event, GetShop.SedoxCarList.fillMode);
    },
        
    loadTuningData : function() {
        var val = $(this).val();
        var data = {
            "target" : val
        };
        
        if ($(this).val() == "")
            return;
        
        var event = thundashop.Ajax.createEvent("", "loadTuningData", $(this), data);
        thundashop.Ajax.postWithCallBack(event, GetShop.SedoxCarList.printTuningData);
    },
        
    loadEngine : function() {
        GetShop.SedoxCarList.disable();
        var val = $(this).val();
        var data = {
            "target" : val
        };
    
        GetShop.SedoxCarList.clearEngineData();

        var event = thundashop.Ajax.createEvent("", "loadChildren", $(this), data);
        thundashop.Ajax.postWithCallBack(event, GetShop.SedoxCarList.fillEngine);
    },
        
    fillMode : function(data) {
        data = JSON.parse(data);
        var dropDown = $('.SedoxCarList #model');
        
        for(var key in data) {
            dropDown.append("<option value='"+data[key].category_id+"'>"+data[key].name+"</option>");
        }
        GetShop.SedoxCarList.enable();
    },
        
    fillEngine : function(data) {
         data = JSON.parse(data);
        var dropDown = $('.SedoxCarList #engine');
        
        for(var key in data) {
            dropDown.append("<option value='"+data[key].category_id+"'>"+data[key].name+"</option>");
        }
        GetShop.SedoxCarList.enable();
    },
        
    printTuningData : function(data) {
        data = JSON.parse(data);
        var hp = data.stage1_hp;
        var nm = data.stage1_nm;
        
        data['selected'] = $('#manufacturer :selected').text()+ " " + $('#model :selected').text() + " " + $('#engine :selected').text();
        data['image'] = "http://www.tuningfiles.com/showAppImage.php?catid="+$('#model :selected').val();
        var event = thundashop.Ajax.createEvent("SedoxCarList", "showTuningData", $('.SedoxCarList'), data);
        thundashop.common.showInformationBox(event, data['selected']);
    }
};

GetShop.SedoxCarList.init();