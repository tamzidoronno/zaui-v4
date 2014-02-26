thundashop.app.Settings = {};

thundashop.app.Settings = {
    
    updateValueField : function() {
        $(this).closest('.setting').find('.value').html($(this).val());
    },
    
    checkRowsTable: function() {
        var table = $(this).closest('table');
        var emtyRow = 0;

        $(table).find('tr').each(function() {
            if ($(this).find('td').size() == 0) {
                return;
            }

            var innerEmptyRow = true;
            $(this).find('td').each(function() {
                if ($(this).find('input').val() !== "") {
                    innerEmptyRow = false;
                }
            });

            if (innerEmptyRow) {
                emtyRow++;
            }
        });

        if (emtyRow === 0) {
            var clone = null;

            $(table).find('tr').each(function() {
                if ($(this).find('td').size() == 0) {
                    return;
                }

                clone = $(this).clone();
                $(clone).find('input').each(function() {
                    $(this).val("");
                });
            });

            $(table).append(clone);
        }

        if (emtyRow > 1) {
            var remove = null;

            $(table).find('tr').each(function() {
                if ($(this).find('td').size() == 0) {
                    return;
                }

                var innerEmptyRow = true;
                $(this).find('td').each(function() {
                    if ($(this).find('input').val() !== "") {
                        innerEmptyRow = false;
                    }
                });

                if (innerEmptyRow) {
                    remove = $(this);
                }
            });

            remove.remove();

            var focusfield = null;

            $(table).find('tr').each(function() {
                if ($(this).find('td').size() == 0) {
                    return;
                }

                var innerEmptyRow = true;
                $(this).find('td').each(function() {
                    if ($(this).find('input').val() !== "") {
                        innerEmptyRow = false;
                    }
                });

                if (innerEmptyRow) {
                    focusfield = $(this);
                }
            });

            focusfield.find('input:first').focus();
        }
    },
    initDropDown: function(parent) {
        parent.find(".dropdown").each(function() {
            var value = "";
            if (currentSettings) {
                if (currentSettings[$(this).attr('id')] !== undefined) {
                    value = currentSettings[$(this).attr('id')].value;
                }
            }

            $(this).find('.config').val(value);
        });
    },
    activateExtendedMode: function(target) {
        $('.Settings .sent_request').slideDown();

        var event = thundashop.Ajax.createEvent(null, "SendExtendedModeRequest", target, {});
        thundashop.Ajax.postSynchron(event);
    },
    initOnOff: function(parent) {
        parent.find(".setting").each(function() {
            if ($(this).hasClass('onoff')) {
                var options = "";
                if (currentSettings != null && typeof(currentSettings[$(this).attr('id')]) != "undefined") {
                    var value = currentSettings[$(this).attr('id')].value;
                }
                if (value == "true") {
                    $(this).find('.onoff').addClass('on');
                } else {
                    $(this).find('.onoff').addClass('off');
                }
            }
        });

    },
    initTextfield: function(parent) {
        parent.find(".textfield").each(function() {
            if (currentSettings != null && currentSettings[$(this).attr('id')] !== undefined) {
                var value = currentSettings[$(this).attr('id')].value;
                $($(this).find('input')[0]).val(value);
                $($(this).find('.value')).html(value);
            }
        });
    },
    initTextArea: function(parent) {
        parent.find(".textarea").each(function() {
            if (typeof(currentSettings[$(this).attr('id')]) != "undefined") {
                var value = currentSettings[$(this).attr('id')].value;
                $($(this).find('textarea[type=textfield]')[0]).val(value);
            }
        });
    },
    initTabs: function(parent) {
        parent.find('.tabs').each(function() {
            if (typeof(currentSettings[$(this).attr('id')]) != "undefined") {
                var activate = currentSettings.shippingtype.value;
                $(this).find("[activate=" + activate + "]").click();
            }
        });
    },
    initTables: function(parent) {
        parent.find('.settingstable').each(function() {
            if (typeof(currentSettings[$(this).attr('id')]) != "undefined") {
                var value = currentSettings[$(this).attr('id')].value;
                var trs = $(this).find('tr');
                var y = 1;
                for (var key in value) {
                    var row = value[key];
                    var i = 0;
                    $(trs[y]).find('input').each(function() {
                        $(this).val(row['col' + i]);
                        i++;
                    });
                    y++;
                }
                ;
            }
        });
    },
    initLists: function(parent) {
        parent.find('.list').each(function() {
            var list = [];
            var box = $(this);
            if (currentSettings[$(this).attr('id')] !== undefined) {
                list = currentSettings[$(this).attr('id')].value;
                if(list.trim().length > 0) {
                    list = JSON.parse(list);
                    for (var entry in list) {
                        var name = box.find('.listentry[value="'+list[entry]+'"]').attr('name');
                        box.find('.selected_entries').append($('<span class="selected" value="'+list[entry]+'">' + name + "</span>"));
                    }
                }
            }
            var entries = [];
            $(this).find('.listentry').each(function() {
                var name = $(this).attr('name');
                if(name !== undefined) {
                    entries.push($(this).attr('name'));
                }
            });
            $(this).find('.text').autocomplete({
                source: entries,
                select: function(event, ui) {
                    var value = box.find('.listentry[name="'+ui.item.value+'"]').attr('value');
                    box.find('.selected_entries').append($('<span class="selected" value="'+value+'">' + ui.item.value + "</span>"));
                    $(this).val('');
                    return false;
                }
            });
        });
    },
    init: function(parent) {
        if (!parent) {
            parent = $('.Settings');
        }
        this.initDropDown(parent);
        this.initTextfield(parent);
        this.initOnOff(parent);
        this.initTextArea(parent);
        this.initTabs(parent);
        this.initTables(parent);
        this.initLists(parent);
        
        $(document).on('click','.Settings .list .selected_entries .selected', function() {
            $(this).remove();
        });
    },
    getDropDownValue: function(searchFrom) {
        var selected = searchFrom.find('select :selected')[0];
        return $(selected).val();
    },
    getTabs: function(settings) {
        $('.Settings .tabs').each(function() {
            var key = $(this).attr('id');
            var secure = $(this).attr('secure');
            secure = (typeof(secure) == "undefined") ? false : true;

            var data = {}
            data.secure = secure;
            data.value = $(this).find('.tab.active').attr('activate');

            settings[key] = data;
        });

        return settings;
    },
    getTables: function(settings) {
        $('.settingstable').each(function() {
            var i = 0;
            var table = {};
            var key = $(this).attr('id');

            $(this).find('tr').each(function() {
                if ($(this).find('td').size() == 0) {
                    return;
                }

                var row = 'row' + i;
                var rowData = {};
                y = 0;
                $(this).find('td').each(function() {
                    var col = "col" + y;
                    var value = $(this).find('input').val();
                    rowData[col] = value;
                    y++;
                });

                table[row] = rowData;
                i++;
            });

            var secure = $(this).attr('secure');
            secure = (typeof(secure) == "undefined") ? false : true;

            var data = {}
            data.secure = secure;
            data.type = "table";
            data.value = table;
            settings[key] = data;
        });

        return settings;
    },
    save: function(fromElement) {
        var settings = {};
        var obj = $(this);
        settings['appid'] = fromElement.closest('#settingsarea').attr('appsettingsid');
        var parent = $('.Settings');
        var functionName = 'SaveSettings';
        var close = false;
        
        if ($(fromElement).closest('#informationbox').length > 0) {
            parent = $(fromElement).closest('#informationbox');
            settings['appid'] = parent.attr('appid');
            functionName = 'saveGsInstanceSettings';
            close = true;
        }
        
        parent.find('.setting').each(function() {
            var key = $(this).attr('id');
            var secure = $(this).attr('secure');
            secure = (typeof(secure) === "undefined") ? false : true;

            var data = {}
            data.secure = secure;

            if ($(this).hasClass('dropdown')) {
                settings[key] = obj[0].getDropDownValue($(this));
            }

            if ($(this).hasClass('textfield')) {
                settings[key] = $(this).find('input')[0].value
            }

            if ($(this).hasClass('textarea')) {
                settings[key] = $(this).find('textarea[type=textfield]').val();
            }

            if ($(this).hasClass('list')) {
                var entries = [];
                $(this).find('.selected').each(function() {
                    entries.push($(this).attr('value'));
                });
                settings[key] = entries;

            }

            if ($(this).find('.onoff').length > 0) {
                var onofftoggle = $(this).find('.onoff');
                if (onofftoggle.hasClass('on')) {
                    settings[key] = true;
                } else {
                    settings[key] = false;
                }
            }

            data.value = settings[key];
            settings[key] = data;
        });


        settings = thundashop.app.Settings.getTabs(settings);
        settings = thundashop.app.Settings.getTables(settings);

        var event = thundashop.Ajax.createEvent('', functionName, fromElement, settings);
        
        if (close) {
            thundashop.Ajax.post(event);
            thundashop.common.hideInformationBox();
        } else {
            thundashop.Ajax.postSynchron(event);
            thundashop.common.Alert(__f("Settings saved"), __f("Your settings have been saved."));    
        }
    }
}

$('.gs_button.savebutton').live('click', function() {
    var fromElement = $(this);
    thundashop.app.Settings.save(fromElement);
    
});

$('.Settings .savebutton').live('click', function() {
    var fromElement = $(this);
    thundashop.app.Settings.save(fromElement);
    if ($(this).hasClass('needreload')) {
        document.location.href = '';
    }
    if ($(this).hasClass('needreloadCurrentSettings')) {
        document.location.href = '?page=settings&applicationId=' + fromElement.closest('#settingsarea').attr('appsettingsid');
        ;
    }

});

$('.Settings .display_theme_selection').live('click', function(e) {
    thundashop.MainMenu.displayAllTheemes();
});

$('.Settings .activate_extended').live('click', function(e) {
    $(this).parent().hide();
    thundashop.app.Settings.activateExtendedMode($(e.target));
});

$('.Settings .settingstable td input').live('blur', thundashop.app.Settings.checkRowsTable);
$('.Settings .settingstable td input').live('click', thundashop.app.Settings.checkRowsTable);
$('.Settings .settingstable td input').live('keyup', thundashop.app.Settings.checkRowsTable);
$(document).on('change', '.Settings input', thundashop.app.Settings.updateValueField);