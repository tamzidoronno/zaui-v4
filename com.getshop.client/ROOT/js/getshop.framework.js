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
        $(document).on('click', '.gscellsettings .gsoperate', this.operateCell);
        $(document).on('click', '.gsrotateleft', this.rotateCell);
        $(document).on('click', '.gsrotateright', this.rotateCell);
        $(document).on('click', '.gscarouseldot', this.showCarouselEntry);
        $(document).on('click', '.carouselsettings', this.showCarouselSettings);
        $(document).on('click', '.savecarouselconfig', this.saveCarouselSettings);
        $(document).on('click', '.gscellsettings .fa-cogs', function () {
            thundashop.framework.showCellSettingsPanel($(this));
        });
        $(document).on('click', '.gs_splithorizontally', this.operateCell);
        $(document).on('click', '.gs_addrotating', this.operateCell);
        $(document).on('click', '.gs_splitvertically', this.operateCell);
        $(document).on('click', '.gs_removerow', this.operateCell);
        $(document).on('click', '.gs_closecelledit', this.closeCellEdit);
        $(document).on('click', '.gs_closecarouselsettings', this.closeCarouselSettings);
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
        $(document).on('change', '.gsdisplaygridcheckbox', this.toggleVisualization);
    },
    closeCarouselSettings : function() {
        $('.carouselsettingspanel').fadeOut();
    },
    
    calculateColumnSizes: function (table) {
        var ranges = [], total = 0, i, s = "Ranges: ", w;
        var columns = table.find("td");
        for (i = 0; i < columns.length; i++) {
            w = columns.eq(i).width() - (i == 0 ? 1 : 0);
            w = w / (table.width() - 1);
            w = Math.round(w * 10000) / 100;
            total += w;
            if (total > 100) {
                w = w - (total - 100);
            }
            ranges.push(w);
        }
        return ranges;
    },
    resetCarouselTimer: function (container) {
        var event = container.attr('timerevent');
        if (event) {
            clearTimeout(parseInt(event));
        } else {
            return;
        }
        setTimeout(function () {
            thundashop.framework.activateCarousel(container, parseInt(container.attr('timer')));
        }, "10000");
    },
    showCarouselEntry: function () {
        $(this).attr('pushed', 'true');
        var offset = $(this).closest('.gscarouseldots');
        count = 0;
        var offsetcount = 0;
        offset.find('.gscarouseldot').each(function () {
            if ($(this).attr('pushed') === "true") {
                offsetcount = count;
            }
            count++;
        });
        $(this).attr('pushed', null);
        var cell = $(this).closest('.rotatingcontainer');

        thundashop.framework.resetCarouselTimer(cell);
        thundashop.framework.rotateCellDirection(cell, offsetcount);
        var rotatecell = $('.gscell[cellid="' + $('.gseditrowheading').attr('cellid') + '"]');
        if (rotatecell.hasClass('gseditrowouter')) {
            thundashop.framework.loadResizing(rotatecell, true);
            thundashop.framework.lastRotatedCell = $('.gseditrowheading').attr('cellid');
        }

    },
    rotateCell: function () {
        var cell = $(this).closest('.rotatingcontainer');
        thundashop.framework.resetCarouselTimer(cell);
        if ($(this).hasClass('gsrotateright')) {
            thundashop.framework.rotateCellDirection(cell, "right");
        } else {
            thundashop.framework.rotateCellDirection(cell, "left");
        }
        var rotatecell = $('.gscell[cellid="' + $('.gseditrowheading').attr('cellid') + '"]');
        if (rotatecell.hasClass('gseditrowouter')) {
            thundashop.framework.loadResizing(rotatecell, true);
            thundashop.framework.lastRotatedCell = $('.gseditrowheading').attr('cellid');
        }
    },
    saveCarouselSettings: function () {
        var data = {
            height: $('.carouselsettingspanel').find('.gscarouselheight').val(),
            timer: $('.carouselsettingspanel').find('.gscarouseltimer').val(),
            type: $('.carouselsettingspanel').find('.gscarouseltype').val(),
            cellid: $(this).closest('.carouselsettingspanel').attr('cellid')
        }
        var event = thundashop.Ajax.createEvent('', 'updateCarouselConfig', $(this), data);
        thundashop.Ajax.post(event);
    },
    showCarouselSettings: function () {
        var cellid = $(this).closest('.rotatingcontainer').attr('cellid');
        $('.carouselsettingspanel').css('left', $(this).offset().left);
        $('.carouselsettingspanel').css('top', $(this).offset().top + 15);
        $('.carouselsettingspanel').css('top', $(this).offset().top + 15);
        $('.carouselsettingspanel').attr('cellid', cellid);

        //populate values
        $('.carouselsettingspanel').find('.gscarouselheight').val($(this).closest('.rotatingcontainer').attr('height'))
        $('.carouselsettingspanel').find('.gscarouseltimer').val($(this).closest('.rotatingcontainer').attr('timer'))
        $('.carouselsettingspanel').find('.gscarouseltype').val($(this).closest('.rotatingcontainer').attr('type'))
        $(this).closest('.rotatingcontainer').attr('timer')
        $(this).closest('.rotatingcontainer').attr('timertype')

        $('.carouselsettingspanel').fadeIn();
    },
    rotateCellDirection: function (cell, direction) {
        var found = 0;
        var before = null;
        var newcellid = "";
        var count = 0;
        cell.children('.gsrotating').each(function () {
            if (direction === "right") {
                if (found === 1) {
                    $(this).css('opacity', '1');
                    $(this).css('z-index', '2');
                    newcellid = $(this).attr('cellid');
                    found = 2;
                }
                if ($(this).css('z-index') === "2" && found === 0) {
                    $(this).css('opacity', '0');
                    $(this).css('z-index', '0');
                    found = 1;
                }
            } else if (direction === "left") {
                if ($(this).css('z-index') === "2" && found === 0) {
                    $(this).css('opacity', '0');
                    $(this).css('z-index', '0');
                    before.css('opacity', '1');
                    before.css('z-index', '2');
                    newcellid = before.attr('cellid');
                    found = 1;
                }
            } else if (direction === count) {
                newcellid = $(this).attr('cellid');
                cell.children('.gsrotating').css('opacity', '0');
                cell.children('.gsrotating').css('z-index', '0');
                $(this).css('opacity', '1');
                $(this).css('z-index', '2');
            }
            count++;
            before = $(this);
        });

        if (direction === "right" && found !== 2) {
            cell.children('.gsrotating').css('opacity', '0');
            cell.children('.gsrotating').css('z-index', '0');
            cell.children('.gsrotating').first().css('z-index', '2');
            cell.children('.gsrotating').first().css('opacity', '1');
        }

        if ($('.gscell[cellid="' + newcellid + '"]').hasClass('gseditrowouter')) {
            $('.gseditrowheading').attr('cellid', newcellid);
        }
    },
    loadResizing: function (cell, saveonmove) {
        if (cell.find('.range').length > 0) {
            return;
        }
        if (cell.hasClass('gsrotating') && !cell.hasClass('gsdepth_0')) {
            return;
        }

        $('.gsresizetable').remove();
        $('.CRC').remove();
        var children = cell.children('.gsinner').children('.gsvertical');
        if (children.length > 1) {
            var table = $('<table style="width: 100%;" class="range gsresizetable" dragCursor="pointer" cellspacing="0" cellpadding="0" border="0"></table>');
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

                    var ranges = thundashop.framework.calculateColumnSizes($(e.currentTarget));
                    var i = 0;
                    var columns = {};
                    cell.children('.gsinner').children('.gsvertical').each(function () {
                        $(this).css('width', ranges[i] + "%");
                        $(this).attr('width', ranges[i]);
                        columns[$(this).attr('cellid')] = $(this).attr('width');
                        i++;
                    });
                    if (saveonmove) {
                        var data = {
                            "colsizes": columns,
                            "cellid": cell.attr('cellid')
                        }
                        var event = thundashop.Ajax.createEvent('', 'saveColChanges', $(this), data);
                        thundashop.Ajax.postWithCallBack(event, function () {

                        });
                    }
                }
            });
        }
    },
    loadImage: function (evt) {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        var cell = $('.gscell[cellid="' + cellid + '"]');

        var level = $(this).closest('.gscolorselectionpanel').attr('level');
        if (level) {
            cell = cell.find(level).first();
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
    activateCarousel: function (container, timer) {
        if(container.hasClass('editcontainer')) {
            return;
        }
        var timerevent = setInterval(function () {
            thundashop.framework.rotateCellDirection(container, "right");
        }, timer);
        container.attr('timerevent', timerevent);
        container.attr('timer', timer);
    },
    setOpacity: function () {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        var cell = $('.gscell[cellid="' + cellid + '"]');

        var bgcolor = $(this).closest('table').find('.gsbgcolorinput').val();
        bgcolor = thundashop.framework.hexToRgb(bgcolor);

        var level = $(this).closest('.gscolorselectionpanel').attr('level');
        if (level) {
            cell = cell.find(level);
        }

        var val = $(this).val() / 10;
        if ($(this).hasClass('gsremoveopacity')) {
            val = 1;
        }
        var newcolor = "rgba(" + bgcolor.r + "," + bgcolor.g + "," + bgcolor.b + ', ' + val + ')';
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
            $('.gseditrowouter .gscell').addClass("gsborders");
        } else {
            $('.gseditrow').css('padding', '0px');
            $('.gseditrowouter .gscell').removeClass("gsborders");
        }
    },
    closeResizing: function () {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        if (!likebeforeStyles) {
            likebeforeStyles = "";
        }
        $('.gscell[cellid="' + cellid + '"]').html(likebefore);
        $('.gscell[cellid="' + cellid + '"]').attr('style', likebeforeStyles);
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
    hexToRgb: function (hex) {
        // Expand shorthand form (e.g. "03F") to full form (e.g. "0033FF")
        var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i;
        hex = hex.replace(shorthandRegex, function (m, r, g, b) {
            return r + r + g + g + b + b;
        });

        var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
        return result ? {
            r: parseInt(result[1], 16),
            g: parseInt(result[2], 16),
            b: parseInt(result[3], 16)
        } : null;
    },
    rgb2hex: function (rgb) {
        rgb = rgb.match(/^rgba?[\s+]?\([\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?/i);
        return (rgb && rgb.length === 4) ? "#" +
                ("0" + parseInt(rgb[1], 10).toString(16)).slice(-2) +
                ("0" + parseInt(rgb[2], 10).toString(16)).slice(-2) +
                ("0" + parseInt(rgb[3], 10).toString(16)).slice(-2) : '';
    },
    get_inherited_bg: function (jquery_object) {
        if (jquery_object.css("background-color") !== "rgba(0, 0, 0, 0)") {
            return jquery_object.css("background-color");
        }
        return thundashop.framework.get_inherited_bg(jquery_object.parent());
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
        var cellcount = cell.children('.gsinner').first().children('.gscell.gsvertical').length;
        if(cellcount > 1) {
            thundashop.framework.loadResizing(cell, false);
       }

        cell.find('.overlay').hide();
        var bgouter = thundashop.framework.get_inherited_bg(cell);
        var bginner = thundashop.framework.get_inherited_bg(cell.find('.gsinner').first());
        $('.gsbgcolorinput.gsbgcouter').val(thundashop.framework.rgb2hex(bgouter));
        $('.gsbgcolorinput.gsbginner').val(thundashop.framework.rgb2hex(bginner));
        cell.find('.overlay').show();

        var opacityinner = 10;
        var opacityouter = 10;

        if (bginner.indexOf("rgba") >= 0) {
            var opacity = bginner.substring(bginner.lastIndexOf(",") + 1, bginner.length - 1);
            opacityinner = parseInt(parseFloat(opacity) * 10);
        }
        if (bgouter.indexOf("rgba") >= 0) {
            var opacity = bgouter.substring(bgouter.lastIndexOf(",") + 1, bgouter.length - 1);
            opacityouter = parseInt(parseFloat(opacity) * 10);
        }

        $('.gscolorselectionpanel[level=".gsinner"] .gsbgopacityinput').val(opacityinner);
        $('.gscolorselectionpanel[level=""] .gsbgopacityinput').val(opacityouter);


        $('.gsresizingpanel input').each(function () {
            var type = $(this).attr('data-csstype');
            var level = $(this).attr('level');
            var value = "";
            if (type) {
                if (level) {
                    if (thundashop.framework.hasAttribute(cell.find(level), $(this).attr('data-csstype'))) {
                        value = cell.find(level).css($(this).attr('data-csstype'));
                    }
                } else {
                    if (thundashop.framework.hasAttribute(cell, $(this).attr('data-csstype'))) {
                        value = cell.css($(this).attr('data-csstype'));
                    }
                }
                value = value.replace("px", "");
                $(this).closest('tr').find('input[type="range"]').attr('min', -1);
                $(this).closest('tr').find('input[type="range"]').attr('max', 100);
                if(value) {
                    $(this).val(value);
                    $(this).closest('tr').find('input[type="range"]').val(value);
                } else {
                    $(this).val(-1);
                    $(this).closest('tr').find('input[type="range"]').val(-1);
                }
            }
        });
    },
    
    hasAttribute : function(cell, attribute) {
        var style = cell.attr('style');
        if(style.indexOf(attribute) >= 0) {
            return true;
        }
        
        if(attribute === "padding-left" || attribute === "padding-right" || attribute === "padding-top" || attribute === "padding-bottom") {
            if(style.indexOf("padding:")) {
                return true;
            }
        }
        if(attribute === "margin-left" || attribute === "margin-right" || attribute === "margin-top" || attribute === "margin-bottom") {
            if(style.indexOf("margin:") >= 0) {
                return true;
            }
        }
        
        return false;
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

        var cell = $('.gscell[cellid="' + cellid + '"]');
        if (cell.hasClass('gsrotating')) {
            cellid = cell.closest('.rotatingcontainer').attr('cellid');
        }


        var event = thundashop.Ajax.createEvent('', 'startEditRow', $(this), {"cellid": cellid});
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
        $('.gsvisualizeedit').removeClass('gsvisualizeedit');
        target.find('.gscellsettings').first().show();
        target.find('.gscellsettings').first().closest('.gscell').addClass('gsvisualizeedit');
    },
    operateCell: function () {
        var type = $(this).attr('type');
        if (type === "settings") {
            $(this), thundashop.framework.showCellSettingsPanel($(this));
            return;
        }

        var panel = $(this).closest('.gscellsettingspanel');
        var cellid = panel.attr('cellid');

        if ($(this).closest('.gseditrowheading').length > 0) {
            cellid = $(this).closest('.gseditrowheading').attr('cellid');
        }

        if (!cellid) {
            cellid = $('.gsvisualizeedit').attr('cellid');
        }

        var cell = $('.gscell[cellid="' + cellid + '"]');


        if (panel.attr('topmenu') === "true") {
            alert('3');
            if (!$(this).attr('subtype') || $(this).attr('subtype') !== "carousel") {
                if (type === "addbefore" || type === "addafter" || type === "moveup" || type === "movedown" || type === "delete") {
                    cellid = cell.closest('.rotatingcontainer').attr('cellid');
                }
            }
        }


        if (type === "delete") {
            if (!confirm("Are you sure you want to delete this cell and all its content?")) {
                return;
            }
        }
        
        var cellobj = $('.gscell[cellid="' + cellid + '"]');

        if(type === "delete" && cellobj.closest('.rotatingcontainer').length > 0) {
            if($(this).attr('subtype') !== "carousel" && cellobj.hasClass('gsdepth_0')) {
                cellid = cellobj.closest('.rotatingcontainer').attr('cellid');
            }
        }

        var data = {
            "cellid": cellid,
            "type": type,
            "area": cellobj.closest('.gsarea').attr('area')
        }


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
        var panel = $('.gscellsettingspanel');
        panel.find('.gsrowmenu').hide();
        panel.find('.gscolumnmenu').hide();
        panel.fadeIn();
        var cell = element.closest('.gscell');
        $('.gscellsettingspanel').attr('topmenu', 'false');
        if (cell.hasClass('gseditinfo')) {
            cell = $(".gscell[cellid='" + $('.gseditrowheading').attr('cellid') + "']");
        }

        if (cell.hasClass('gsvertical')) {
            panel.find('.gscolumnmenu').show();
        } else {
            panel.find('.gsrowmenu').show();
        }

        panel.find('.carouselsettings').hide();
        if (cell.hasClass('gsrotating')) {
            panel.find('.carouselsettings').show();
        }


        $('.gsoverlay').remove();
        var overlay = $('<span class="gsoverlay" style="filter: blur(5px);width:100%; height:100%; background-color:#bbb; opacity:0.6; position:absolute; left:0px; top:0px;display:inline-block;"></span>');
        cell.append(overlay);

        panel.attr('cellid', cell.attr('cellid'));
        var offset = element.offset();
        panel.css('display', 'inline-block');
        panel.css('top', offset.top + 10);
        panel.css('left', (offset.left - 230));

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