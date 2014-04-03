app.CarTuningApplication = {
    reserialize : function() {
        var entry = $('a.jstree-clicked').closest('li');
        var data = JSON.parse(entry.attr('entry'));
        $('.CarTuningApplication .configurationbox input').each(function() {
            data[$(this).attr('gs_name')] = $(this).val();
        });
        var node = $('#html1').jstree('get_selected');
        var entryString = JSON.stringify(data);
        app.CarTuningApplication.updateNode(node, entryString);
    },
    showSubEntry : function() {
        $('.CarTuningApplication .result_panel').remove();
        var element = $(this).closest('.topLevel');
        var offsetcount = parseInt(element.attr('offsetcount'));
        var subindex = parseInt(element.attr('subindex'));
        
        subindex++;
        $('.topLevel').hide();
        $('.topLevel.subindex_'+subindex+'.offset_'+offsetcount).show();
        
        if(element.attr('data') !== undefined) {
            $('.tuningdata').append(app.CarTuningApplication.buildResultPanel(element));
        }
    },
    translateKey : function(key) {
        switch(key) {
            case "name":
                return "Navn";
            case "originalHp":
                return "Original HP";
            case "originalNm":
                return "Original NM";
            case "maxHp":
                return "Max HP";
            case "maxNw":
                return "Max NW";
            case "normalHp":
                return "Normal HP";
            case "normalNw":
                return "Normal NW";
        }
    },
    buildResultPanel : function(element) {
        var panel = $('<div class="result_panel"></div>');
        var index = parseInt(element.attr('subindex'))-1;
        var offset = parseInt(element.attr('offset'));
        panel.append($('<div class="back_button topLevel tuning_data_entry" subindex="'+index+'" offsetcount="'+offset+'" style="display: block;"><div class="back">Tilbake</div></div>'));
        var result = JSON.parse(element.attr('data'));
        for(var key in result) {
            if(key === "subEntries") {
                continue;
            }
            panel.append("<div class='result_row'>" + app.CarTuningApplication.translateKey(key) + " <span class='result'>" + result[key] + "</span></div>");
        }
        return panel;
    },
            
    updateNode : function(node, entryString) {
        var jsnode = $('#html1').jstree('get_node', node);
        jsnode.li_attr['entry'] = entryString;
        $('ul #'+node).attr('entry', entryString);
    },
    initEvents : function() {
        $(document).on('change','.CarTuningApplication .configurationbox input', app.CarTuningApplication.reserialize);
        $(document).on('keyup','.CarTuningApplication .configurationbox input', app.CarTuningApplication.reserialize);
        $(document).on('blur','.CarTuningApplication .configurationbox input', app.CarTuningApplication.reserialize);
        $(document).on('click','.CarTuningApplication .save_tuning_data', app.CarTuningApplication.saveTuningData);
        $(document).on('click','.CarTuningApplication .tuning_data_entry .tuning_data_entry_inner', app.CarTuningApplication.showSubEntry);
        $(document).on('click','.CarTuningApplication .back', app.CarTuningApplication.showSubEntry);
    },
    saveTuningData : function() {
        var objects = $('#html1').jstree('get_json');
        console.log(objects);
        var data = app.CarTuningApplication.convertList(objects[0].children);
        var event = thundashop.Ajax.createEvent('','saveTuningData',$(this),{"data" : data});
        thundashop.Ajax.post(event, function() {
            thundashop.common.hideInformationBox();
        });
    },
    convertList : function(list) {
        var result = [];
        for(var key in list) {
            var entry = list[key];
            var jsonentry = JSON.parse(entry.li_attr.entry);
            if(entry.children.length > 0) {
                jsonentry['subEntries'] = app.CarTuningApplication.convertList(entry.children);
            }
            result.push(jsonentry);
        }
        return result;
    },
    loadTuningSettings : function() {
        var event = thundashop.Ajax.createEvent("", "loadSettings", $(this), {});
        thundashop.common.showInformationBox(event);
    },
            
    loadEntry : function(entry) {
        $('.configurationbox input').val('');
        for(var key in entry) {
            $('.configurationbox input[gs_name="'+key+'"]').val(entry[key]);
        }
        $('.configurationbox .name').html(entry.name);
    },
            
    loadSettings: function(element, application) {
        var config = {
            application: application,
            draggable: true,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa fa-ellipsis-h",
                    iconsize : "30",
                    title: __f("Load settings"),
                    click: app.CarTuningApplication.loadTuningSettings,
                    extraArgs: {}
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.CarTuningApplication.initEvents();