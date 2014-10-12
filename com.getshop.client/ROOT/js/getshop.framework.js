GetShop = {};

likebefore = null;

thundashop.framework = {
    bindEvents: function () {
        $('*[gstype="form"] *[gstype="submit"]').live('click', function (e) {
            thundashop.framework.submitFromEvent(e);
        });
        $('*[gstype="changesubmit"]').live('change', function (e) {
            if ($(this).attr('method')) {
                thundashop.framework.submitElement(e);
            } else {
                thundashop.framework.submitFromEvent(e);
            }
        });
        $('*[gstype="form"] *[gstype="submitenter"], *[gstype="clicksubmit"]').live('keyup', function (e) {
            if (e.keyCode == 13) {
                if ($(e.target).attr('gsType') == "clicksubmit") {
                    thundashop.framework.submitElement(e);
                } else {
                    thundashop.framework.submitFromEvent(e);
                }
            }
        });
        $('*[gstype="clicksubmit"]').live('click', function (e) {
            var target = $(e.target);
            if (target.prop("tagName") == "INPUT") {
                return;
            }
            thundashop.framework.submitElement(e);
        });
        $(document).on('click', '.gs_addcell', this.addCell);
        $(document).on('click', '.toogleDeepfreeze', this.showDeepFreezOption);
        $(document).on('click', '.savedeepfreeze', this.toggleDeepFreeze);
        $(document).on('click', '.gscellsettings', this.showCellSettingsPanel);
        $(document).on('click', '.gs_splithorizontally', this.operateCell);
        $(document).on('click', '.gs_splitvertically', this.operateCell);
        $(document).on('click', '.gs_removerow', this.operateCell);
        $(document).on('click', '.gs_closecelledit', this.closeCellEdit);
        $(document).on('mouseover', '.gseditrowouter', this.showEditIcon);
        $(document).on('click', '.gseditrowbutton', this.startEditRow);
        $(document).on('click', '.gsdoneeditbutton', this.startEditRow);
        $(document).on('click', '.gseditrowheading .fa', this.operateCell);
        $(document).on('click', '.gs_resizing', this.showCellResizing);
        $(document).on('click', '.gsresizingpanel .gstabmenu .tabbtn', this.switchtab);
        $(document).on('change', '.gsresizingpanel input[type="range"]', this.setValue);
        $(document).on('keyup', '.gsresizingpanel input[type="range"]', this.setValue);
        $(document).on('input', '.gsresizingpanel input[type="range"]', this.setValue);
        $(document).on('keyup', '.gsresizingpanel input.sizetxt', this.setValue);
        $(document).on('click', '.gsresizingpanel .closeresizing', this.closeResizing);
        $(document).on('click', '.gsshowvisualization', this.toggleVisualization);
    },
    toggleVisualization : function() {
        if($(this).is(":checked")) {
            $('.gseditrow').css('padding','5px');
            $('.gseditrowouter .gscell').css('border','solid 1px');
        } else {
            $('.gseditrow').css('padding','0px');
            $('.gseditrowouter .gscell').css('border','solid 0px');
        }
    },
    closeResizing : function() {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        $('.gscell[cellid="'+cellid+'"]').html(likebefore);
        $('.gsoverlay').fadeOut(function() {
            $(this).remove();
        });
        $('.gsresizingpanel').fadeOut();
    },
    setValue: function () {
        $(this).closest('tr').find('.sizetxt').val($(this).val());
        var cellid = $('.gsresizingpanel').attr('cellid');
        var cell = $('.gscell[cellid="' + cellid + '"]');
        var result = {};
        $('.gsresizingpanel input').each(function () {
            var type = $(this).attr('data-csstype');
            if (type && $(this).val()) {
                result[type] = $(this).val();
                if (type.toLowerCase().indexOf("width") >= 0) {
                    cell.css(type, $(this).val() + "%");
                } else {
                    cell.css(type, $(this).val() + "px");
                }
            }
        });
    },
    switchtab: function () {
        var target = $(this).attr('target');
        $('.gsresizingpanel .gspage').hide();
        $('.gsresizingpanel .gspage[target="' + target + '"]').show();

    },
    showCellResizing: function () {
        var resizingpanel = $('.gsresizingpanel');
        var cellid = $(this).closest('.gscellsettingspanel').attr('cellid');
        resizingpanel.attr('cellid', cellid);
        
        var cell = $('.gscell[cellid="' + cellid + '"]');
        likebefore = cell.html();
        
        $('.gscellsettingspanel').fadeOut();
        resizingpanel.css('top',$(this).offset().top);
        resizingpanel.css('left',$(this).offset().left-100);
        resizingpanel.find('.tabbtn[target="padding"]').click();
        resizingpanel.fadeIn();
        cell.find('.gscell.gshorisontal').resizable({ grid: [10000, 1]});
        $('.gsresizingpanel input').each(function () {
            var type = $(this).attr('data-csstype');
            if (type) {
                var value = cell.css($(this).attr('data-csstype'));
                value = value.replace("px", "");
                if (value && value !== "none") {
                    var maxvalue = value * 3;
                    if (value === "0" || type.toLowerCase().indexOf("width") >= 0) {
                        maxvalue = 100;
                    }
                    $(this).val(value);
                    $(this).closest('tr').find('input[type="range"]').attr('max', maxvalue);
                    $(this).closest('tr').find('input[type="range"]').val(value);
                }
            }
        });
        var children = cell.children('.gsinner').children('.gsvertical');
        if (children.length > 1) {
            var table = $('<table style="width: 100%;" class="range" dragCursor="pointer" cellspacing="0" cellpadding="0" border="0"></table>');
            var row = $('<tr></tr>');
            children.each(function() {
                var width = $(this).attr('width');
                row.append('<td  width="'+width+'%"></td>');
            });
            table.append(row);
            
            var tablecontainer = $('<div style="padding:5px;"></div>"');
            tablecontainer.append(table);
            cell.children(".gsinner").prepend(table);
            
            
            table.colResizable({
                liveDrag: true,
                dragCursor : 'auto',
                draggingClass: "rangeDrag",
                gripInnerHtml: "<div class='rangeGrip'></div>",
                onResize: function (e) {
                    document.body.style.cursor = "crosshair";
                    var columns = $(e.currentTarget).find("td");
                    var ranges = [], total = 0, i, s = "Ranges: ", w;
                    for (i = 0; i < columns.length; i++) {
                        w = columns.eq(i).width() - 14 - (i == 0 ? 1 : 0);
                        ranges.push(w);
                        total += w;
                    }
                    var i = 0;
                    cell.children('.gsinner').children('.gsvertical').each(function () {
                        ranges[i] = 100 * ranges[i] / total;
                        ranges[i] = Math.round(ranges[i]*100)/100;
                        $(this).css('width', ranges[i] + "%");
                        $(this).attr('width', ranges[i]);
                        i++;
                    });
                }
            });

        }

    },
    closeCellEdit: function () {
        $('.gscellsettingspanel').hide();
        $('.gscell .gsoverlay').remove();
    },
    startEditRow: function () {
        if ($(this).attr('done') === "true") {
            cellid = "";
        } else {
            cellid = $(this).closest('.gscell').attr('cellid');
        }
        var event = thundashop.Ajax.createEvent('', 'startEditRow', $(this), {"cellid": cellid });
        thundashop.Ajax.post(event);
    },
    showEditIcon: function (event) {
        var target = $(event.target);
        if (!target.hasClass('gscell')) {
            for (var i = 0; i < 20; i++) {
                target = target.parent();
                if (target.hasClass('gscell')) {
                    break;
                }
            }
        }
        if (!target.hasClass('gscell')) {
            return;
        }

        $('.gscellsettings').hide();
        target.find('.gscellsettings').first().show();
    },
    operateCell: function () {
        var cellid = $(this).closest('.gscellsettingspanel').attr('cellid');

        if ($(this).closest('.gseditrowheading').length > 0) {
            cellid = $(this).closest('.gseditrowheading').attr('cellid');
        }

        var data = {
            "cellid": cellid,
            "type": $(this).attr('type')
        }
        var event = thundashop.Ajax.createEvent('', 'operateCell', $(this), data);
        thundashop.Ajax.post(event);
    },
    showCellSettingsPanel: function () {
        $('.gscellsettingspanel').fadeIn();
        var cell = $(this).closest('.gscell');
        var overlay = $('<span class="gsoverlay" style="filter: blur(5px);width:100%; height:100%; background-color:#bbb; opacity:0.6; position:absolute; left:0px; top:0px;display:inline-block;"></span>');
        cell.append(overlay);

        $('.gscellsettingspanel').attr('cellid', cell.attr('cellid'));
        var offset = $(this).offset();
        $('.gscellsettingspanel').css('display', 'inline-block');
        $('.gscellsettingspanel').css('top', offset.top + 10);
        $('.gscellsettingspanel').css('left', (offset.left - 170));

    },
    addCell: function () {
        var data = {};
        $(this).each(function () {
            $.each(this.attributes, function () {
                data[this.name] = this.value;
            });
        });
        var event = thundashop.Ajax.createEvent('', 'addCell', $(this), data);
        thundashop.Ajax.post(event);
    },
    showDeepFreezOption: function () {
        var event = thundashop.Ajax.createEvent(null, "showDeepFreeze", null);
        thundashop.common.showInformationBox(event, "Lock/Unlock deepfreeze");
    },
    submitElement: function (event) {
        var element = $(event.target);
        var name = element.attr('gsname');
        var value = element.attr('gsvalue');
        var method = element.attr('method');
        if (!value) {
            value = element.val();
        }

        var data = {}
        data[name] = value;

        var event = thundashop.Ajax.createEvent("", method, element, data);
        thundashop.framework.postToChannel(event, element);
    },
    getCallBackFunction: function (element) {
        var appName = element.closest('.app').attr('app');
        var appContext = GetShop[appName]
        if (appContext && appContext.formPosted)
            return appContext.formPosted

        if (element && typeof (element.callback) !== "undefined") {
            return element.callback;
        }

        return null;
    },
    postToChannel: function (event, element) {
        thundashop.common.destroyCKEditors();
        var callback = this.getCallBackFunction(element);
        if (!element.attr('output')) {
            thundashop.Ajax.post(event, callback, event);
            thundashop.common.hideInformationBox(null);
        } else if (element.attr('output') == "informationbox") {
            var informationTitle = element.attr('informationtitle');
            var box = thundashop.common.showInformationBox(event, informationTitle);
            box.css('min-height', '10px');
            if (typeof (callback) == "function") {
                callback(box.html(), event);
            }
        }
    },
    submitFromEvent: function (event) {
        var target = $(event.target);
        thundashop.framework.submitFromElement(target);
    },
    createGsArgs: function (form) {
        var args = {};
        var ckeditors = thundashop.common.destroyCKEditors();

        form.find('*[gsname]').each(function (e) {
            var name = $(this).attr('gsname');
            if (!name || name.trim().length == 0) {
                alert('Name attribute is missing for gstype value, need to be fixed');
                return;
            }
            var value = $(this).attr('gsvalue');
            if (!value || value === undefined) {
                value = $(this).val();
            }

            if ($(this).is(':checkbox')) {
                value = $(this).is(':checked');
            }
            if ($(this).attr('gstype') == "ckeditor") {
                value = ckeditors[$(this).attr('id')];
            }
            if ($(this).is(':radio')) {
                if ($(this).is(':checked')) {
                    args[name] = $(this).val();
                }
            } else {
                args[name] = value;
            }
        });
        return args;
    },
    submitFromElement: function (element) {
        var form = element.closest('*[gstype="form"]');
        var method = form.attr('method');
        var args = thundashop.framework.createGsArgs(form);
        form.callback = element.callback;
        var event = thundashop.Ajax.createEvent("", method, element, args);
        thundashop.framework.postToChannel(event, form);
    },
    reprintPage: function () {
        var event = thundashop.Ajax.createEvent("", "systemReloadPage", null, null);
        thundashop.Ajax.post(event);
    },
    toggleDeepFreeze: function () {
        var data = {
            password: $('#deepfreezepassword').val()
        };

        var event = thundashop.Ajax.createEvent(null, "toggleDeepfreeze", null, data);
        thundashop.Ajax.post(event, function (response) {
            if (response.errorCodes.length === 0) {
                location.reload();
            }
        });
    }
};

thundashop.framework.bindEvents();