GetShop = {};

likebefore = null;
likebeforeStyles = null;

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
        $(document).on('click', '.gscellsettings', function () {
            thundashop.framework.showCellSettingsPanel($(this));
        });
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
        $(document).on('click', '.gsresizingpanel .gssavechanges', this.saveCellChanges);
        $(document).on('keyup', '.gsresizingpanel .gsbgcolorinput', this.setBgColor);
        $(document).on('change', '.gsresizingpanel .gsbgcolorinput', this.setBgColor);
        $(document).on('keyup', '.gsresizingpanel .gsbgopacityinput', this.setOpacity);
        $(document).on('change', '.gsresizingpanel .gsbgopacityinput', this.setOpacity);
        $(document).on('change', '.gsresizingpanel .gsbgimageselection', this.loadImage);
        $(document).on('click', '.gsresizingpanel .gsremoveopacity', this.setOpacity);
        $(document).on('click', '.gsresizingpanel .gsremovebgimage', this.loadImage);
        $(document).on('keyup', '.gsresizingpanel .gsbgopacityinput', this.setOpacity);
        $(document).on('input', '.gsresizingpanel .gsbgopacityinput', this.setOpacity);
        $(document).on('change', '.gsresizingpanel .gsbgopacityinput', this.setOpacity);
        $(document).on('click', '.gsresizingpanel .gsremovebgcolor', this.setBgColor);
        $(document).on('click', '.gsshowvisualization', this.toggleVisualization);
    },
    loadImage: function (evt) {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        var cell = $('.gscell[cellid="' + cellid + '"]');

        var level = $(this).closest('.gscolorselectionpanel').attr('level');
        if (level) {
            cell = cell.find(level);
        }

        if ($(this).hasClass('gsremovebgimage')) {
            cell.css('background-repeat', "");
            cell.css('background-position', "");
            cell.css('background-size', "");
            cell.css('background-image', "");
            return;
        }

        if (window.File && window.FileReader && window.FileList && window.Blob) {
            cell.css('background-color', "");

            var target = $(this);
            target.closest('.gscolorselectionpanel').find('.gschoosebgimagebutton').hide();
            target.closest('.gscolorselectionpanel').find('.gsuploadimage').show();
            var files = evt.target.files; // FileList object
            var file = files[0];
            // Only process image files.
            if (!file.type.match('image.*')) {
                alert('You can only select an image');
                return;
            }
            var reader = new FileReader();
            reader.onload = (function (theFile) {
                return function (e) {
                    var data = {
                        "data": e.target.result
                    }
                    var event = thundashop.Ajax.createEvent('', 'saveBackgroundImage', target, data);
                    thundashop.Ajax.postWithCallBack(event, function (id) {
                        cell.css('background-repeat', 'no-repeat');
                        cell.css('background-position', 'center');
                        cell.css('background-size', '100%');
                        cell.css('background-image', 'url("/displayImage.php?id=' + id + '")');
                        target.closest('.gscolorselectionpanel').find('.gschoosebgimagebutton').show();
                        target.closest('.gscolorselectionpanel').find('.gsuploadimage').hide();
                    });
                };
            })(file);

            reader.readAsDataURL(file);
        } else {
            alert('The File APIs are not fully supported in this browser, please upgrade your browser.');
        }

    },
    setBgColor: function () {

        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        var cell = $('.gscell[cellid="' + cellid + '"]');

        var level = $(this).closest('.gscolorselectionpanel').attr('level');
        if (level) {
            cell = cell.find(level);
        }

        cell.css('background-repeat', "");
        cell.css('background-position', "");
        cell.css('background-size', "");
        cell.css('background-image', "");

        if ($(this).hasClass('gsremovebgcolor')) {
            cell.css('background-color', "");
        } else {
            cell.css('background-color', $(this).val());
        }
    },
    setOpacity: function () {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        var cell = $('.gscell[cellid="' + cellid + '"]');

        var level = $(this).closest('.gscolorselectionpanel').attr('level');
        if (level) {
            cell = cell.find(level);
        }

        var bgcolor = cell.css('background-color');
        var val = $(this).val() / 10;
        if ($(this).hasClass('gsremoveopacity')) {
            val = 1;
        }

        if (bgcolor.indexOf("rgba") !== 0) {
            var newcolor = bgcolor.replace(')', ', ' + val + ')').replace('rgb', 'rgba');
        } else {
            var newcolor = bgcolor.substring(0, bgcolor.lastIndexOf(',')) + ', ' + val + ')';
        }
        cell.css('background-color', newcolor);
    },
    saveCellChanges: function () {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        var cell = $('.gscell[cellid="' + cellid + '"]');
        var tosavecell = cell.clone();
        tosavecell.css('width', null);
        var styles = tosavecell.attr('style');

        var tosavecell = cell.clone();
        tosavecell.css('width', null);
        var stylesInner = tosavecell.find('.gsinner').attr('style');

        var colsizes = {};
        cell.children('.gsinner').children('.gscell').each(function () {
            colsizes[$(this).attr('cellid')] = $(this).attr('width');
        });

        var data = {
            "cellid": cellid,
            "styles": styles,
            "stylesInner": stylesInner,
            "colsizes": colsizes
        }
        var event = thundashop.Ajax.createEvent('', 'saveColChanges', $(this), data);
        thundashop.Ajax.post(event);
    },
    toggleVisualization: function () {
        if ($(this).is(":checked")) {
            $('.gseditrow').css('padding', '5px');
            $('.gseditrowouter .gscell').css('border', 'solid 1px');
        } else {
            $('.gseditrow').css('padding', '0px');
            $('.gseditrowouter .gscell').css('border', 'solid 0px');
        }
    },
    closeResizing: function () {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        if(!likebeforeStyles) {
            likebeforeStyles = "";
        }
        $('.gscell[cellid="' + cellid + '"]').html(likebefore);
        $('.gscell[cellid="' + cellid + '"]').attr('style',likebeforeStyles);
        $('.gsoverlay').fadeOut(function () {
            $(this).remove();
        });
        $('.gsresizingpanel').fadeOut();
    },
    setValue: function () {
        $(this).closest('tr').find('.sizetxt').val($(this).val());
        var cellid = $('.gsresizingpanel').attr('cellid');
        var cell = $('.gscell[cellid="' + cellid + '"]');
        $('.gsresizingpanel input').each(function () {
            var type = $(this).attr('data-csstype');
            if (type && $(this).val()) {
                var level = $(this).attr('level');
                var value = $(this).val();
                var toset = cell;
                if (level) {
                    toset = cell.find(level).first();
                }
                if (value === "-1") {
                    toset.css(type, "");
                } else {
                    toset.css(type, value + "px");
                }
            }
        });
    },
    switchtab: function () {
        var target = $(this).attr('target');
        var type = $(this).attr('type');
        $('.gsresizingpanel .gspage').hide();
        $('.gsresizingpanel .gspage[target="' + target + '"]').show();
        $('.gsresizingpanel .heading').html($(this).html());
        $('.gsresizingpanel').attr('type', type);
    },
    rgb2hex: function (rgb) {
        rgb = rgb.match(/^rgba?[\s+]?\([\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?/i);
        return (rgb && rgb.length === 4) ? "#" +
                ("0" + parseInt(rgb[1], 10).toString(16)).slice(-2) +
                ("0" + parseInt(rgb[2], 10).toString(16)).slice(-2) +
                ("0" + parseInt(rgb[3], 10).toString(16)).slice(-2) : '';
    },
    showCellResizing: function () {
        var resizingpanel = $('.gsresizingpanel');
        var cellid = $(this).closest('.gscellsettingspanel').attr('cellid');
        resizingpanel.attr('cellid', cellid);
        $('.gsoverlay').remove();
        var cell = $('.gscell[cellid="' + cellid + '"]');
        likebefore = cell.html();
        likebeforeStyles = cell.attr('style');

        $('.gscellsettingspanel').fadeOut();
        resizingpanel.css('top', $(this).offset().top);
        resizingpanel.css('left', $(this).offset().left - 100);
        resizingpanel.find('.tabbtn[target="padding"]').first().click();
        resizingpanel.fadeIn();
        cell.find('.gscell.gshorisontal').resizable({grid: [10000, 1]});

        $('.gsbgcolorinput.gsbgcouter').val(thundashop.framework.rgb2hex(cell.css('background-color')));
        $('.gsbgcolorinput.gsbginner').val(thundashop.framework.rgb2hex(cell.find('.gsinner').first().css('background-color')));

        $('.gsresizingpanel input').each(function () {
            var type = $(this).attr('data-csstype');
            var level = $(this).attr('level');
            var value = "";
            if (type) {
                if (level) {
                    if(cell.find(level).attr('style').indexOf($(this).attr('data-csstype')) >= 0) {
                        value = cell.find(level).css($(this).attr('data-csstype'));
                    }
                } else {
                    if(cell.attr('style').indexOf($(this).attr('data-csstype')) >= 0) {
                        value = cell.css($(this).attr('data-csstype'));
                    }
                }
                value = value.replace("px", "");
                $(this).closest('tr').find('input[type="range"]').attr('min', -1);
                $(this).closest('tr').find('input[type="range"]').attr('max', 100);
                $(this).val(value);
                $(this).closest('tr').find('input[type="range"]').val(value);
            }
        });
        var children = cell.children('.gsinner').children('.gsvertical');
        if (children.length > 1) {
            var table = $('<table style="width: 100%;" class="range" dragCursor="pointer" cellspacing="0" cellpadding="0" border="0"></table>');
            var row = $('<tr></tr>');
            children.each(function () {
                var width = $(this).attr('width');
                row.append('<td  width="' + width + '%"></td>');
            });
            table.append(row);

            var tablecontainer = $('<div style="padding:5px;"></div>"');
            tablecontainer.append(table);
            cell.children(".gsinner").prepend(table);


            table.colResizable({
                liveDrag: true,
                dragCursor: 'auto',
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
                        ranges[i] = Math.round(ranges[i] * 100) / 100;
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
    activateMoveApplication: function () {

    },
    operateCell: function () {
        var cellid = $(this).closest('.gscellsettingspanel').attr('cellid');

        if ($(this).closest('.gseditrowheading').length > 0) {
            cellid = $(this).closest('.gseditrowheading').attr('cellid');
        }

        var type = $(this).attr('type');

        if (type === "settings") {
            $(this), thundashop.framework.showCellSettingsPanel($(this));
            return;
        }

        var data = {
            "cellid": cellid,
            "type": type
        }

        var cellobj = $('.gscell[cellid="' + cellid + '"]');

        if (type === "addbefore") {
            var newcellid = cellobj.parent().closest('.gscell').attr('cellid');
            if (!newcellid) {
                newcellid = "";
            }
            data['before'] = cellid;
            data['cellid'] = newcellid;
        }

        if (type === "addafter") {
            var newcellid = cellobj.parent().closest('.gscell').attr('cellid');
            if (!newcellid) {
                newcellid = "";
            }
            var before = cellobj.next().attr('cellid');
            if (cellobj.next().hasClass("gseditinfo")) {
                before = cellobj.next().next().attr('cellid');
            }

            data['before'] = before;
            data['cellid'] = newcellid;
        }


        var event = thundashop.Ajax.createEvent('', 'operateCell', $(this), data);
        thundashop.Ajax.post(event);
    },
    showCellSettingsPanel: function (element) {
        $('.gscellsettingspanel').find('.gsrowmenu').hide();
        $('.gscellsettingspanel').find('.gscolumnmenu').hide();

        $('.gscellsettingspanel').fadeIn();
        var cell = element.closest('.gscell');
        if (cell.hasClass('gseditinfo')) {
            cell = cell.next(".gscell");
        }

        if (cell.hasClass('gscolumn')) {
            $('.gscellsettingspanel').find('.gscolumnmenu').show();
        } else {
            $('.gscellsettingspanel').find('.gsrowmenu').show();
        }

        var overlay = $('<span class="gsoverlay" style="filter: blur(5px);width:100%; height:100%; background-color:#bbb; opacity:0.6; position:absolute; left:0px; top:0px;display:inline-block;"></span>');
        cell.append(overlay);

        $('.gscellsettingspanel').attr('cellid', cell.attr('cellid'));
        var offset = element.offset();
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