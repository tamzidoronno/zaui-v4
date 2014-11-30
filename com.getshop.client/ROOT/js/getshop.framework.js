GetShop = {};

likebefore = null;
likebeforeStyles = null;

thundashop.framework = {
    operatingCellId : null,
    activeContainerCellId : {},
    lastRotatedCell : {},
    
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
        $(document).on('click', '.gsrotateleft', this.rotateCell);
        $(document).on('click', '.gsrotateright', this.rotateCell);
        $(document).on('click', '.gscarouseldot', this.showCarouselEntry);
        $(document).on('click', '.carouselsettings', this.showCarouselSettings);
        $(document).on('click', '.tabsettings', this.showTabSettings);
        $(document).on('click', '.savecarouselconfig', this.saveCarouselSettings);
        $(document).on('mousedown', '.gscellsettings .fa-cogs', function () {
            thundashop.framework.showCellSettingsPanel($(this));
        });
        $(document).on('mouseup', this.releaseMouse);
        $(document).on('click', '.gs_closecelledit', this.closeCellEdit);
        $(document).on('click', '.gs_closecarouselsettings', this.closeCarouselSettings);
        $(document).on('mouseover', '.gseditrowouter', this.showEditIcon);
        $(document).on('click', '.gseditrowbutton', this.startEditRow);
        $(document).on('click', '.gsdoneeditbutton', this.startEditRow);
        $(document).on('click', '.gs_resizing', this.showCellResizing);
        $(document).on('click', '.gscellsettings .fa-image', this.switchCellResizing);
        $(document).on('click', '.gsresizingpanel .gstabmenu .tabbtn', this.switchtab);
        $(document).on('click', '.gsresizingpanel .closeresizing', this.closeResizing);
        $(document).on('click', '.gsresizingpanel .gssavechanges', this.saveCellChanges);
        $(document).on('change', '.gsresizingpanel .gsbgimageselection', this.loadImage);
        $(document).on('click', '.gsresizingpanel .gsremovebgimage', this.loadImage);
        $(document).on('change', '.gsdisplaygridcheckbox', this.toggleVisualization);
        $(document).on('click', '.gsresizingpanel .tabbtn[target="css"]', this.loadCssEditor);
        $(document).on('keyup', '.gstabname', this.updateTabName);
        $(document).on('click', '.gsdonemodifytab', this.hideTabSettings);
        $(document).on('click', '.gstabbtn', this.changeTab);
        
        /* Cell operations */
        $(document).on('click', '.gsoperatecell', this.operateCell);
        $(document).on('mousedown', '.gscellsettings .gsoperate', this.operateCell);
    },
    releaseMouse : function() {
        thundashop.framework.mousedown = false;
    },
    changeTab : function() {
        var newId = $(this).attr('incrementid');
        var container = $(this).closest('.gscontainercell');
        container.find('.gstabrow').hide();
        $('.gstabrow.gscell_'+newId).show();
        var cellid = $('.gscell.gscell_'+newId).attr('cellid');
        thundashop.framework.setActiveContainerCellId(cellid, container.attr('cellid'));
    },
    switchCellResizing : function() {
        thundashop.framework.saveCellChanges(true);
        thundashop.framework.operatingCellId = $(this).closest('.gscell').attr('cellid');
        thundashop.framework.showCellResizing();
    },
    closeCarouselSettings: function () {
        $('.carouselsettingspanel').fadeOut();
    },
    findCss: function (id) {
        var result = "";
        var cellobject = $('.gscell[cellid="'+id+'"]');
        var incrementid = cellobject.attr('incrementcellid');
        var includeOuter = false;
        
        if( $('.gscell[cellid="'+id+'"]').hasClass('gsdepth_0')) {
            includeOuter = true;
        }
        
        if($('style[cellid="'+id+'"]').length > 0) {
            return $('style[cellid="'+id+'"]').html();
        } else {
            if(cellobject.hasClass('gscontainercell')) {
                result = ".gscell_"+incrementid+".gscell {\n\n}\n";
            } else {
                result = ".gscell_"+incrementid+".gsinner {\n\n}\n";
                if(includeOuter) {
                    result += ".gscell_"+incrementid+".gscell {\n\n}\n";
                }
            }
        }
        
        return result;
    },
    updateTabName : function() {
        var containercellid = $(this).closest('.gscontainercell').attr('cellid');
        var cellid = thundashop.framework.getActiveContainerCellId(containercellid);
        var name = $(this).val();
        $('.gstabbtn[cellid="'+cellid+'"]').text(name);
        var event = thundashop.Ajax.createEvent('','updateCellName',$(this),{"cellid" : cellid, "name" : name});
        thundashop.Ajax.postWithCallBack(event, function() {});
    },
    
    setCss: function (id, value) {
        if($('style[cellid="'+id+'"]').length > 0) {
            console.log('exists');
            console.log(value);
            $('style[cellid="'+id+'"]').html(value);
        } else {
            var style = $("<style></style>");
            style.html(value);
            style.attr('cellid',id);
            $('body').append(style);
        }
        
    },
    loadCssEditor: function () {
        var id = thundashop.framework.operatingCellId;
        thundashop.framework.csseditorid = id;
        var css = thundashop.framework.findCss(id);
        if (!$('#cellcsseditor').hasClass('ace_editor')) {
            $('#cellcsseditor').html(css);
            cssEditorForCell = ace.edit("cellcsseditor");
            cssEditorForCell.setTheme("ace/theme/github");
            cssEditorForCell.getSession().setMode("ace/mode/css");
            cssEditorForCell.on("change", function (event) {
                var value = cssEditorForCell.getSession().getValue();
                thundashop.framework.setCss(thundashop.framework.csseditorid, value);
            });
        } else {
            cssEditorForCell.setValue(css);
        }
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
        var cell = $(this).closest('.gscontainercell');
        var containerId = cell.attr('cellid');

        thundashop.framework.resetCarouselTimer(cell);
        thundashop.framework.rotateCellDirection(cell, offsetcount);
        var rotatecell = $('.gscell[cellid="' + thundashop.framework.getActiveContainerCellId(containerId) + '"]');
        if (rotatecell.hasClass('gseditrowouter')) {
            thundashop.framework.loadResizing(rotatecell, true);
            thundashop.framework.lastRotatedCell[cell.attr('cellid')] = thundashop.framework.getActiveContainerCellId(containerId);
        }

    },
    rotateCell: function () {
        var cell = $(this).closest('.gscontainercell');
        var containerId = cell.attr('cellid');
        thundashop.framework.resetCarouselTimer(cell);
        if ($(this).hasClass('gsrotateright')) {
            thundashop.framework.rotateCellDirection(cell, "right");
        } else {
            thundashop.framework.rotateCellDirection(cell, "left");
        }
        var rotatecell = $('.gscell[cellid="' + thundashop.framework.getActiveContainerCellId(containerId) + '"]');
        if (rotatecell.hasClass('gseditrowouter')) {
            thundashop.framework.loadResizing(rotatecell, true);
            thundashop.framework.lastRotatedCell[cell.attr('cellid')] = thundashop.framework.getActiveContainerCellId(containerId);
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
        var cellid = $(this).closest('.gscontainercell').attr('cellid');
        thundashop.framework.updateOperateOnCellId(thundashop.framework.getActiveContainerCellId(cellid));
        var panel =  $(this).closest('.gscontainercell').find('.carouselsettingspanel');
        panel.css('left', $(this).offset().left);
        panel.css('top', $(this).offset().top + 15);
        panel.attr('cellid', cellid);

        //populate values
        panel.find('.gscarouselheight').val($(this).closest('.gscontainercell').attr('height'))
        panel.find('.gscarouseltimer').val($(this).closest('.gscontainercell').attr('timer'))
        panel.find('.gscarouseltype').val($(this).closest('.gscontainercell').attr('type'))
        $(this).closest('.gscontainercell').attr('timer')
        $(this).closest('.gscontainercell').attr('timertype')

        $('.carouselsettingspanel').fadeIn();
    },
    hideTabSettings : function() {
        $('.tabsettingspanel').fadeOut();
    },
    
    showTabSettings : function() {
        var container = $(this).closest('.gscontainercell');
        var cellid = container.find('.gsactivetab').attr('cellid');
        var tabtext = $('.gstabbtn[cellid="'+cellid+'"]').text();
        var panel = container.find('.tabsettingspanel');
        
        thundashop.framework.setActiveContainerCellId(cellid, container.attr('cellid'));
        thundashop.framework.updateOperateOnCellId(cellid);
        panel.show();
        $('.tabsettingspanel').css('left', $(this).position().left);
        $('.tabsettingspanel').css('top', $(this).position().top + 15);
//        $('.tabsettingspanel').css('top', $(this).offset().top + 15);
//        $('.tabsettingspanel').fadeIn();
        $('.gstabname').val(tabtext);
    },
    
    rotateCellDirection: function (cell, direction) {
        var found = 0;
        var before = null;
        var newcellid = "";
        var count = 0;
        var curoffset = 0;
        var counter = 0;
        cell.find('.gscontainerinner').children('.gsrotatingrow').each(function () {
            if($(this).css('z-index') === "2") {
                curoffset = counter;
            }
            counter++;
        });
        
        if(direction === "right") {
            if(curoffset === cell.find('.gscontainerinner').children('.gsrotatingrow').length-1) {
                curoffset = -1;
            }
        }
        
        cell.find('.gscontainerinner').children('.gsrotatingrow').each(function () {
            $(this).css('opacity', '0');
            $(this).css('z-index', '0');
        });
        
        cell.find('.gscontainerinner').children('.gsrotatingrow').each(function () {
            if (direction === "right") {
                if(count === (curoffset+1)) {
                    $(this).css('opacity', '1');
                    $(this).css('z-index', '2');
                    newcellid = $(this).attr('cellid');
                }
            } else if (direction === "left") {
                if(count === (curoffset-1)) {
                    $(this).css('opacity', '1');
                    $(this).css('z-index', '2');
                    newcellid = $(this).attr('cellid');
                }
           } else if (direction === count) {
                $(this).css('opacity', '1');
                $(this).css('z-index', '2');
                newcellid = $(this).attr('cellid');
            }
            count++;
            before = $(this);
        });

        if ($('.gscell[cellid="' + newcellid + '"]').hasClass('gseditrowouter')) {
            console.log("Active cell: " + newcellid);
            thundashop.framework.setActiveContainerCellId(newcellid, cell.attr('cellid'));
        }
    },
    loadResizing: function (cell, saveonmove) {
        if (cell.find('.range').length > 0) {
            return;
        }
        if (cell.hasClass('gsrotatingrow') && !cell.hasClass('gsdepth_0')) {
            return;
        }

        $('.gsresizetable').remove();
        $('.CRC').remove();
        var children = cell.children('.gsinner').children('.gscolumn');
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
                    cell.children('.gsinner').children('.gscolumn').each(function () {
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
    removeCss : function(attribute, id) {
        var css = thundashop.framework.findCss(id);
        if(css.indexOf(attribute) >= 0) {
        }
    },
    addCss : function(attribute, value, id, level) {
        if(!level) {
            level = ".gscell";
        }
        var css = thundashop.framework.findCss(id);
        var incrementid = $('.gscell[cellid="'+id+'"]').attr('incrementcellid');
        var startPos = css.indexOf(".gscell_" + incrementid + level+ " ");
        var endPos = css.indexOf("}", startPos);
        css = css.substring(0, endPos) + "\t" + attribute + " : " + value + ";\n " + css.substring(endPos);
        console.log(css);
        thundashop.framework.setCss(id, css);
        cssEditorForCell.setValue(css);

    },
    loadImage: function (evt) {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        var cell = $('.gscell[cellid="' + cellid + '"]');

        var level = $(this).closest('.gscolorselectionpanel').attr('level');
        if (level) {
            cell = cell.find(level).first();
        }

        if ($(this).hasClass('gsremovebgimage')) {
            thundashop.framework.removeCss('background-repeat', cellid);
            thundashop.framework.removeCss('background-position', cellid);
            thundashop.framework.removeCss('background-size', cellid);
            thundashop.framework.removeCss('background-image', cellid);
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
                        thundashop.framework.addCss('background-repeat', 'no-repeat', cellid, level);
                        thundashop.framework.addCss('background-position', 'center', cellid, level);
                        thundashop.framework.addCss('background-size', '100%', cellid, level);
                        thundashop.framework.addCss('background-image', 'url("/displayImage.php?id=' + id + '")', cellid, level);
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
    activateCarousel: function (container, timer) {
        if (container.hasClass('editcontainer')) {
            return;
        }
        var timerevent = setInterval(function () {
            thundashop.framework.rotateCellDirection(container, "right");
        }, timer);
        container.attr('timerevent', timerevent);
        container.attr('timer', timer);
    },
    saveCellChanges: function (avoidreprint) {
        var cellid = thundashop.framework.operatingCellId;
        var cell = $('.gscell[cellid="' + cellid + '"]');

        var colsizes = {};
        cell.children('.gsinner').children('.gscell').each(function () {
            colsizes[$(this).attr('cellid')] = $(this).attr('width');
        });
        
        
        
        var styles = cssEditorForCell.getSession().getValue();
        var data = {
            "cellid": cellid,
            "styles": styles,
            "colsizes": colsizes
        };
        var event = thundashop.Ajax.createEvent('', 'saveColChanges', $(this), data);
        if(avoidreprint === true) {
            thundashop.Ajax.postWithCallBack(event, function() {
                
            });
        } else {
            thundashop.Ajax.post(event);
        }
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
        $('.tabsettingspanel').hide();
        var resizingpanel = $('.gsresizingpanel');
        var cellid = thundashop.framework.operatingCellId;
        resizingpanel.attr('cellid', cellid);
        $('.gsoverlay').remove();
        var cell = $('.gscell[cellid="' + cellid + '"]');
        likebefore = cell.html();
        likebeforeStyles = cell.attr('style');

        $('.gscellsettingspanel').fadeOut();
        if($(this).offset() !== undefined) {
            resizingpanel.css('top', 20);
            resizingpanel.css('left', 20);
        }
        resizingpanel.fadeIn();

        cell.find('.gscell.gshorisontal').resizable({grid: [10000, 1]});
        var cellcount = cell.children('.gsinner').first().children('.gscell.gscolumn').length;
        if (cellcount > 1) {
            thundashop.framework.loadResizing(cell, false);
        }
        resizingpanel.find('.tabbtn[target="css"]').click();
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
        if(!cellid) {
            cellid = $(this).closest('.gscontainercell').attr('cellid');
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
        if($('.gsresizingpanel').is(':visible')) {
            target.find('.gscellsettings').first().find('.fa-image').show();
        }
        target.find('.gscellsettings').first().closest('.gscell').addClass('gsvisualizeedit');
    },
    
    getActiveContainerCellId : function(containerid) {
        return thundashop.framework.activeContainerCellId[containerid];
    },
    setActiveContainerCellId : function(id, containerid) {
        console.log("Setting id : " + id + " container: " + containerid);
        thundashop.framework.lastRotatedCell[containerid] = id;
        thundashop.framework.activeContainerCellId[containerid] = id;
        console.log('Set active container: ' + containerid + " cellid: " + id);
    },
    
    operateCell: function () {
        var type = $(this).attr('type');
        if (type === "settings") {
            thundashop.framework.showCellSettingsPanel($(this));
            return;
        }

        var cellid = thundashop.framework.operatingCellId;
        var containerId = $(this).closest('.gscontainercell').attr('cellid');
        
        if($(this).attr('target') && $(this).attr('target') === "selectedcell") {
            cellid = thundashop.framework.getActiveContainerCellId(containerId);
        }
        
        if($(this).attr('target') && $(this).attr('target') === "container") {
            cellid = containerId;
        }

        if (type === "delete" && !confirm("Are you sure you want to delete this cell and all its content?")) {
            return;
        }
        
        var cellobj = $('.gscell[cellid="' + cellid + '"]');

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
    
    updateOperateOnCellId : function(id) {
        thundashop.framework.operatingCellId = id;
    },
    setOperateOnCellId : function(element) {
        thundashop.framework.operatingCellId = element.closest('.gscell').attr('cellid');
        if(!thundashop.framework.operatingCellId) {
            thundashop.framework.operatingCellId = element.closest('.gscontainercell').attr('cellid');
        }
    },
    
    showCellSettingsPanel: function (element) {
        thundashop.framework.setOperateOnCellId(element);
        
        thundashop.framework.mousedown = true;
        setTimeout(function() {
            if(thundashop.framework.mousedown === true) {
                $('.gsrowmenu').show();
                $('.gscolumnmenu').show();
            }
        }, "2000");
        
        var panel = $('.gscellsettingspanel');
        panel.find('.gsrowmenu').hide();
        panel.find('.gscolumnmenu').hide();
        panel.fadeIn();
        var cell = element.closest('.gscell');
        $('.gscellsettingspanel').attr('topmenu', 'false');
       
        if (cell.hasClass('gscolumn')) {
            panel.find('.gscolumnmenu').show();
        } else {
            panel.find('.gsrowmenu').show();
        }

        panel.find('.carouselsettings').hide();
        if (cell.hasClass('gsrotatingrow') || cell.hasClass('gstab')) {
            panel.find('.modesettings').hide();
            panel.find('.carouselsettings').show();
        }

        $('.gsoverlay').remove();
        var overlay = $('<span class="gsoverlay" style="filter: blur(5px);width:100%; height:100%; background-color:#bbb; opacity:0.6; position:absolute; left:0px; top:0px;display:inline-block;"></span>');
        cell.append(overlay);

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