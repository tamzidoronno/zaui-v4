GetShop = {};

likebefore = null;
likebeforeStyles = null;

thundashop.framework = {
    originObject: null,
    activeContainerCellId: {},
    lastRotatedCell: {},
    activeBoxTimeout: {},
    cellRotatingWait: {},
    advancedMode : false,
    cssEditorCount : 0,
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
        $(document).on('mousedown', '.gscellsettings .fa-cogs', thundashop.framework.showCellSettingsPanel);
        $(document).on('mouseup', this.releaseMouse);
        $(document).on('click', '.gs_closecelledit', this.closeCellEdit);
        $(document).on('click', '.gs_closecarouselsettings', this.closeCarouselSettings);
        $(document).on('click', '.gs_start_template_button', this.startTemplateClicked);
        $(document).on('click', '.gs_toggle_advanced_mode', this.toggleAdvancedMode);
        $(document).on('click', '.gsclosetabsettings', this.closeTabSettings);
        $(document).on('click', '.gsclosecsseditor', this.closeCssEditor);
        $(document).on('click', '.gsresizecolumn', this.activateResizeColumn);
        $(document).on('mouseover', '.gseditrowouter', this.showEditIcon);
        $(document).on('mouseenter', '.gscell', this.showCellPanel);
        $(document).on('mouseleave', '.gscell', this.mouseLeftPanel);
        $(document).on('mouseover', '.gsrow', this.showEditRowIcons);
        $(document).on('mouseover', '.gscontainercell', this.showEditRowIcons);
        $(document).on('click', '.gscellheadermin', this.showCellBoxHeader);
        $(document).on('mouseout', '.gscell', this.hideEditRowIcons);
        $(document).on('mouseout', '.gscontainercell', this.hideEditRowIcons);
        $(document).on('click', '.gseditrowbutton', this.startEditRow);
        $(document).on('click', '.gsdoneeditbutton', this.startEditRow);
        $(document).on('click', '.gs_resizing', this.showCellResizing);
        $(document).on('click', '.gscellsettings .fa-image', this.switchCellResizing);
        $(document).on('click', '.gsresizingpanel .gstabmenu .tabbtn', this.switchtab);
        $(document).on('click', '.gsresizingpanel .closeresizing', this.closeResizing);
        $(document).on('click', '.gsresizingpanel .gssavechanges', this.saveCellChanges);
        $(document).on('change', '.gsresizingpanel .gsbgimageselection', this.loadImage);
        $(document).on('change', '.gsmobileupload', this.loadImage);
        $(document).on('click', '.gsresizingpanel .gsremovebgimage', this.loadImage);
        $(document).on('change', '.gsdisplaygridcheckbox', this.toggleVisualization);
        $(document).on('click', '.gsresizingpanel .tabbtn[target="css"]', this.loadCssEditor);
        $(document).on('keyup', '.gstabname', this.updateTabName);
        $(document).on('click', '.gsdonemodifytab', this.hideTabSettings);
        $(document).on('click', '.gstabbtn', this.changeTab);
        $(document).on('click', '.gsdoneresizing', this.deleteResizing);
        $(document).on('click', '.store_mobile_view_button', this.displayMobileView);
        $(document).on('click', '.store_design_button', this.loadGlobalCssEditor);
        $(document).on('click', '.gsmobileeditor', this.hideMobileView);
        $(document).on('click', '.gsmobilemenu .gsslideleft', this.slideMobileMenu);
        $(document).on('click', '.gsmobilemenu .gsslideright', this.slideMobileMenu);
        $(document).on('click', '.gsmobilemenu .gsmobiletopmenu', this.showMobileTopMenu);
        $(document).on('click', '.gssetslidemodemobile', this.setSlideViewMode);
        $(document).on('click', '.gsmobilemenu .gsmobilesearch', this.showMobileSearch);
        $(document).on('keyup', '.gsmobilsearchfield', this.doMobileSearch);
        $(document).on('click', '.gsmobilesearchbox .fa-search', this.doMobileSearch);
        $(document).on('keyup', '.gscssattributes', this.setCssAttributes);
        $(document).on('mouseenter', '.gs_templatehaeaderfunctions .selectcolors', this.showColors);
        $(document).on('click', '.gs_templatehaeaderfunctions .selectcolors', this.showColors);
        $(document).on('click', '.gs_templatehaeaderfunctions .color_select.close', this.hideColors);

        /* Cell operations */
        $(document).on('click', '.gsoperatecell', this.operateCell);
        $(document).on('click', '.simpleaddrow', this.simpleaddrow);
        $(document).on('click', '.gsemptyarea .shop_button', this.simpleaddrow);
        $(document).on('mousedown', '.gscellsettings .gsoperate', this.operateCell);
    },
    
    showColors: function() {
        $('.gs_templatehaeaderfunctions .colors_menu').slideDown('fast');
    },
    
    hideColors: function() {
        $('.gs_templatehaeaderfunctions .colors_menu').slideUp('fast');
    },

    
    showAdvancedOptions : function() {
        if(thundashop.framework.advancedMode) {
            $('.gsadvancedlayoutmode').show();
        } else {
            $('.gsadvancedlayoutmode').hide();
        }
    },
    setSlideViewMode : function() {
        var event = thundashop.Ajax.createEvent('','setSlideMode',$(this),{});
        thundashop.Ajax.post(event);
    },
    
    toggleAdvancedMode: function() {
        var isActive = $(this).hasClass('gs_advanced_mode_activated');
        
        if (isActive) {
            $(this).removeClass('gs_advanced_mode_activated');
            thundashop.framework.advancedMode = false;
        } else {
            $(this).addClass('gs_advanced_mode_activated');
            thundashop.framework.advancedMode = true;
        }
    },
    
    loadGlobalCssEditor : function() {
        var pageId = $('#gspageid').val();
        var box = window.open("cssbox.php?page="+pageId,'popUpWindow','height=500,width=800,left=100,addressbar=no,top=100,resizable=no,scrollbars=yes,toolbar=no,menubar=no,location=no,directories=no, status=yes');
        thundashop.framework.checkCss(box);
    },
    
    checkCss : function(box) {
        if(!box) {
            return;
        }
        if(box.top === null) {
            return;
        }
        var oDom = box.document;
        var elem = oDom.getElementById("styles");
        var pageElem = oDom.getElementById("styles_colors");
        var pageInput = oDom.getElementById("current_pageid");
        if (elem) {
            var curpage =  $('.skelholder').attr('page');
            pageInput.setAttribute("pageid",curpage);
            var globalstyles = elem.value;
            var colorcss = pageElem.value;
            $('#gs_customcss').html("<style>" + globalstyles + "</style>");
            $('#gs_color_css').html("<style>" + colorcss + "</style>");
        }
        setTimeout(function() {
            thundashop.framework.checkCss(box);
        }, "300");
    },
    
    showMobileSearch : function() {
        $('.gsmobilemenuinstance').hide();
        $('.gsmobilesearchbox').fadeIn();
        $('.gsmobilsearchfield').focus();
    },
    doMobileSearch : function(event) {
        var code = event.keyCode;
        if(code === 13 || $(this).hasClass('fa-search')) {
            window.location.href='page=productsearch&searchWord=' + $(this).val();
        }
    },
    slideMobileMenu: function () {
        var target = $(this);
        $('.gsmobilemenuinstance').hide();
        if (!$(this).hasClass('gsmobilemenuentry')) {
            target = $(this).closest('.gsmobilemenuentry');
        }

        var width = $(window).width() - 70;
        var options = {
            right: "+=" + width
        };

        var slideRight = true;
        if (!target.hasClass('gsslideleft')) {
            options['right'] = "-=" + width;
            slideRight = false;
        }
        if (slideRight) {
            $('.gsmobilemenubox').hide();
        }
        $('.gsmobilemenu').animate(options, 200, function () {
            $('.gsslideleft').hide();
            if (slideRight) {
                $('.gsmobilemenubox').fadeIn();
            } else {
                $('.gsmobilemenubox').hide();
                $('.gsslideleft').fadeIn();
            }
        });
    },
    showMobileTopMenu : function() {
        $(window).scrollTop(0);
        if($('.gsmobilemenuinstance').is(':visible')) {
            $('.gsbody').show();
            $('.gsmobilemenuinstance').hide();
            return;
        }
        $('.gsmobilemenuinstance').fadeIn(function() {
            $('.gsbody').hide();
        });
        var options = {
            bottom: "+=" + height
        };
         $('.gsmobilemenuinstance').animate(options, 200, function () {
             
         });
    },
    
    hideMobileView: function (event) {
        if($(event.target).closest('.gsmobileconfiguration').length > 0 || $(this).hasClass('gsmobileconfiguration')) {
            return;
        }
        $('.gsmobileeditor').hide();
    },
    displayMobileView: function () {
        $('.gsmobileeditor').fadeIn();

        var sid = document.cookie.match('PHPSESSID=([^;]*)')[1];
        var location = window.location.protocol + "//mobile." + window.location.host + "/?page=" + $('#gspageid').val() + "&PHPSESSID=" + sid;
        $('#gscontentframe').attr('src', location);
        $('#gscontentframelandscape').attr('src', location);
    },
    setCssAttributes: function (event) {
        var target = $(event.target);
        var cellid = target.closest('.gsresizingpanel').attr('cellid');
        
        var val = target.val();
        var attr = target.attr('data-attr');
        var level = target.attr('data-level');
        var prefix = target.attr('data-prefix');
        thundashop.framework.removeCss(attr, cellid);
        
        if(attr === "background-color") {
            thundashop.framework.removeCss('background-repeat', cellid);
            thundashop.framework.removeCss('background-position', cellid);
            thundashop.framework.removeCss('background-size', cellid);
            thundashop.framework.removeCss('background-image', cellid);
        }
        
        if(!val) {
            return;
        }
        if (prefix) {
            val += prefix;
        }
        thundashop.framework.addCss(attr, val, cellid, level);
    },
    loadCssAttributes: function () {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        $('.gscssattributes input').each(function () {
            var target = $(this);
            var attr = target.attr('data-attr');
            var prefix = target.attr('data-prefix');
            var val = thundashop.framework.getCssAttr(attr, cellid);
            val = val.replace(prefix, "");
            val = val.trim();
            target.val(val);
        });
    },
    showCellBoxHeader: function (event) {
        if(!thundashop.framework.advancedMode) {
            return;
        }
        
        var target = $(event.target);
        $('.gsactiveboxheader').removeClass('gsactiveboxheader');
        $('.gscellheadermin').show();

        target.parent().parent().find('.gscellboxheader').addClass('gsactiveboxheader');
        $(this).hide();
    },
    showEditRowIcons: function () {
        if ($(this).closest('.gsarea').attr('area') !== "body") {
            if (!thundashop.framework.isAdvancedMode()) {
                return;
            }
        }
        thundashop.framework.showAdvancedOptions();
        var buttons = $(this).find('.gseditrowbuttons');
        buttons.show();
        
    },
    isAdvancedMode: function () {
        if (!localStorage.getItem('gsadvancedcombo')) {
            return false;
        }
        if (localStorage.getItem('gsadvancedcombo') === "false") {
            return false;
        }

        return true;
    },
    hideEditRowIcons: function (event) {
        if($(event.relatedTarget).hasClass('gs_row_selection_box')) {
            return;
        }
        if($(event.relatedTarget).closest('.gs_row_selection_box').length > 0) {
            return;
        }
        $(this).find('.gseditrowbuttons').hide();
    },
    activateResizeColumn: function () {
        var cellid = $(this).closest('.gsrow').attr('cellid');
        thundashop.framework.loadResizing($('.gscell[cellid="' + cellid + '"]'), true);
    },
    startFromCurrentStore: function () {
        
        var data = {
            'storeId': $('input[name="storeid"]').val(),
            'gs_start_store_name': $('#gs_start_store_name').val(),
            'gs_start_store_email': $('#gs_start_store_email').val(),
            'gs_start_store_phonenumber': $('#gs_start_store_phonenumber').val(),
            'gs_start_store_shopname': $('#gs_start_store_shopname').val(),
            'gs_start_store_password1': $('#gs_start_store_password1').val(),
            'gs_start_store_password2': $('#gs_start_store_password2').val()
        }

        if (!data.gs_start_store_name) {
            alert(__f("Your name can not be empty"));
            return;
        }
        if (!data.gs_start_store_email) {
            alert(__f("Your email can not be empty"));
            return;
        }
        if (!data.gs_start_store_phonenumber) {
            alert(__f("Your phonenumber can not be empty"));
            return;
        }
        if (!data.gs_start_store_shopname) {
            alert(__f("Your shopname can not be empty"));
            return;
        }
        if (!data.gs_start_store_password1 || !data.gs_start_store_password2) {
            alert(__f("Your shopname can not be empty"));
            return;
        }
        
        if (data.gs_start_store_password2 != data.gs_start_store_password1) {
            alert(__f("Please check your password"));
            return;
        }
        
        var event = thundashop.Ajax.createEvent(null, 'startStore', null, data);
        event['synchron'] = 'true';
        thundashop.Ajax.postWithCallBack(event, function (response) {
            var form = $('<form/>');
            form.attr('action', response);
            form.attr('method', 'post');
            form.append('<input type="hidden" name="loginbutton" value="submit"/>');
            form.append('<input type="hidden" name="username" value="' + data.gs_start_store_email + '"/>');
            form.append('<input type="hidden" name="password" value="' + data.gs_start_store_password1 + '"/>');
            form.submit();
//            alert(response);
        });

    },
    startTemplateClicked: function () {
        if ($(this).html() === __w("Start")) {
            thundashop.framework.startFromCurrentStore();
            return;
        }
        $('.gs_start_template_text_form').slideDown();
        $(this).html(__w("Start"));
    },
    deleteResizing: function () {
        var event = thundashop.Ajax.createEvent('', 'ping', $(document), {});
        thundashop.Ajax.post(event);
    },
    closeTabSettings: function () {
        $(this).closest('.tabsettingspanel').fadeOut();
    },
    closeCssEditor: function () {
        $(this).closest('.gsresizingpanel').fadeOut();
    },
    releaseMouse: function () {
        thundashop.framework.mousedown = false;
    },
    saveFloating: function (cell) {
        var data = {
            cellid: cell.children('.gscell').attr('cellid'),
            left: cell.position().left,
            top: cell.position().top,
            width: cell.width(),
            height: cell.height()
        };
        var event = thundashop.Ajax.createEvent('', 'saveFloatingPosition', $(this), data);
        thundashop.Ajax.postWithCallBack(event, function () {

        });
    },
    changeTab: function () {
        var newId = $(this).attr('incrementid');
        var container = $(this).closest('.gscontainercell');
        container.find('.gstabrow').hide();
        $('.gstabrow.gscell_' + newId).show();
        var cellid = $('.gscell.gscell_' + newId).attr('cellid');
        thundashop.framework.setActiveContainerCellId(cellid, container.attr('cellid'));
        PubSub.publish("GS_TAB_NAVIGATED", {"rowid": cellid});
    },
    switchCellResizing: function () {
        thundashop.framework.saveCellChanges(true);
        thundashop.framework.operatingCellId = $(this).closest('.gscell').attr('cellid');
        thundashop.framework.showCellResizing();
    },
    closeCarouselSettings: function () {
        $('.carouselsettingspanel').fadeOut();
    },
    findCss: function (id) {
        var result = "";
        var cellobject = $('.gsucell[cellid="' + id + '"]');
        var incrementid = cellobject.attr('incrementcellid');
        var includeOuter = false;

        if ($('.gsucell[cellid="' + id + '"]').hasClass('gsdepth_0')) {
            includeOuter = true;
        }

        if ($('style[cellid="' + id + '"]').length > 0) {
            return $('style[cellid="' + id + '"]').html();
        } else {
            if (cellobject.hasClass('gscontainercell')) {
                result = ".gscell_" + incrementid + ".gsucell {\n\n}\n";
            } else {
                result = ".gscell_" + incrementid + ".gsuicell {\n\n}\n";
                if (includeOuter) {
                    result += ".gscell_" + incrementid + ".gsucell {\n\n}\n";
                }
            }
        }

        return result;
    },
    updateTabName: function () {
        var containercellid = $(this).closest('.gscontainercell');
        var cellid = containercellid.find('.gsactivetab:visible').attr('cellid');
        var name = $(this).val();
        $('.gstabbtn[cellid="' + cellid + '"]').text(name);
        var event = thundashop.Ajax.createEvent('', 'updateCellName', $(this), {"cellid": cellid, "name": name});
        thundashop.Ajax.postWithCallBack(event, function () {
        });
    },
    setCss: function (id, value) {
        if ($('style[cellid="' + id + '"]').length > 0) {
            $('style[cellid="' + id + '"]').html(value);
        } else {
            var style = $("<style></style>");
            style.html(value);
            style.attr('cellid', id);
            $('body').append(style);
        }

    },
    loadCssEditor: function () {
        var target = "";
        if (thundashop.framework.originObject.attr('target')) {
            target = thundashop.framework.originObject.attr('target');
        }
        var id = thundashop.framework.findCellId(target);
        var css = thundashop.framework.findCss(id);
        
        if(typeof(cssEditorForCell) === "undefined" || cssEditorForCell === null) {
            var tmpeditor = $('<div id="innercsseditor_'+thundashop.framework.cssEditorCount+'" style="width:500px; height: 400px"></div>');
            tmpeditor.html(css);
            $('#cellcsseditor').html(tmpeditor);
            cssEditorForCell = ace.edit("innercsseditor_"+thundashop.framework.cssEditorCount);
            cssEditorForCell.setTheme("ace/theme/github");
            cssEditorForCell.getSession().setMode("ace/mode/css");
            cssEditorForCell.on("change", function (event) {
                var value = cssEditorForCell.getSession().getValue();
                thundashop.framework.setCss(id, value);
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
        thundashop.framework.cellRotatingWait[container.attr('cellid')] = new Date().getTime() + 10000;
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
            height: $(this).closest('.carouselsettingspanel').find('.gscarouselheight').val(),
            heightMobile: $(this).closest('.carouselsettingspanel').find('.gscarouselheightmobile').val(),
            timer: $(this).closest('.carouselsettingspanel').find('.gscarouseltimer').val(),
            type: $(this).closest('.carouselsettingspanel').find('.gscarouseltype').val(),
            cellid: $(this).closest('.carouselsettingspanel').attr('cellid'),
        }

        data['outerWidth'] = $('.gscontainercell[cellid="'+data['cellid']+'"] .gsinner').outerWidth();
        data['outerWidthWithMargins'] = $('.gscontainercell[cellid="'+data['cellid']+'"] .gsinner').outerWidth(true);
        
        var event = thundashop.Ajax.createEvent('', 'updateCarouselConfig', $(this), data);
        thundashop.Ajax.post(event);
    },
    showCarouselSettings: function () {
        var cellid = $(this).closest('.gscontainercell').attr('cellid');
        var panel = $(this).closest('.gscontainercell').find('.carouselsettingspanel');
        panel.css('left', $(this).offset().left);
        panel.css('top', $(this).parent().position().top);
        panel.attr('cellid', cellid);
        panel.fadeIn();
    },
    hideTabSettings: function () {
        $('.tabsettingspanel').fadeOut();
    },
    showTabSettings: function () {
        thundashop.framework.originObject = $(this);

        var container = $(this).closest('.gscontainercell');
        var cellid = container.find('.gsactivetab:visible').attr('cellid');
        var tabtext = container.find('.gsactivetab:visible').text();
        var panel = container.find('.tabsettingspanel');

        thundashop.framework.setActiveContainerCellId(cellid, container.attr('cellid'));
        panel.show();
        $('.tabsettingspanel').css('left', $(this).offset().left);
        $('.tabsettingspanel').css('top', $(this).position().top + 15);
        $('.gstabname').val(tabtext);
    },
    displayCarouselEntry: function (cell) {
        cell.closest('.gscontainerinner').children('.gsrotatingrow').each(function () {
            $(this).css('opacity', '0');
            $(this).css('z-index', '0');
        });


        cell.closest('.gscontainerinner').find('.gscarouseldots').hide();
        cell.css('opacity', '1');
        cell.css('z-index', '1');
        cell.find('.gscarouseldots').show();
        return cell.attr('cellid');
    },
    rotateCellDirection: function (cell, direction) {
        var found = 0;
        var before = null;
        var newcellid = "";
        var count = 0;
        var curoffset = 0;
        var counter = 0;
        cell.find('.gscontainerinner').children('.gsrotatingrow').each(function () {
            if ($(this).css('z-index') === "1") {
                curoffset = counter;
            }
            counter++;
        });

        if (direction === "right") {
            if (curoffset === cell.find('.gscontainerinner').children('.gsrotatingrow').length - 1) {
                curoffset = -1;
            }
        }

        cell.find('.gscontainerinner').children('.gsrotatingrow').each(function () {
            if (direction === "right") {
                if (count === (curoffset + 1)) {
                    newcellid = thundashop.framework.displayCarouselEntry($(this));
                }
            } else if (direction === "left") {
                if (count === (curoffset - 1)) {
                    newcellid = thundashop.framework.displayCarouselEntry($(this));
                }
            } else if (direction === count) {
                newcellid = thundashop.framework.displayCarouselEntry($(this));
            }
            count++;
            before = $(this);
        });

//        if ($('.gscell[cellid="' + newcellid + '"]').hasClass('gseditrowouter')) {
        thundashop.framework.setActiveContainerCellId(newcellid, cell.attr('cellid'));
//        }
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
            cell.children(".gsinner").append("<span class='gsdoneresizing'>Done resizing</span>");


            table.colResizable({
                liveDrag: true,
                dragCursor: 'auto',
                hoverCursor: 'auto',
                draggingClass: "rangeDrag",
                gripInnerHtml: "<div class='rangeGrip'></div>",
                onResize: function (e) {
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
    removeCss: function (attribute, id) {
        var css = cssEditorForCell.getSession().getValue();
        if (css.indexOf(attribute) >= 0) {
        }
        var csslines = css.split("\n");
        var newcss = "";
        for (var key in csslines) {
            var attr = csslines[key].split(":");
            if (attr[0].trim() === attribute) {
                continue;
            }
            newcss += csslines[key] + "\n";
        }
        cssEditorForCell.setValue(newcss);
    },
    getCssAttr: function (attribute, id) {
        var css = cssEditorForCell.getSession().getValue();
        if (css.indexOf(attribute) >= 0) {
        }
        var csslines = css.split("\n");
        var newcss = "";
        var found = false;
        for (var key in csslines) {
            var attr = csslines[key].split(":");
            console.log(attribute + " " + attr[0]);
            if (attr[0].trim() === attribute) {
                found = csslines[key];
                continue;
            }
            newcss += csslines[key] + "\n";
        }

        if (!found) {
            return "";
        }

        found = found.split(":");
        return found[1].replace(";", "");
    },
    addCss: function (attribute, value, id, level) {
        if (!level) {
            level = ".gsucell";
        }
        var css = thundashop.framework.findCss(id);
        var incrementid = $('.gscell[cellid="' + id + '"]').attr('incrementcellid');
        var startPos = css.indexOf(".gscell_" + incrementid + level + " ");
        var endPos = css.indexOf("}", startPos);
        css = css.substring(0, endPos) + "\t" + attribute + " : " + value + ";\n " + css.substring(endPos);
        thundashop.framework.setCss(id, css);
        css = css.trim();
        cssEditorForCell.setValue(css);

    },
    loadImage: function (evt) {
        var cellid = $(this).closest('.gsresizingpanel').attr('cellid');
        var cell = $('.gscell[cellid="' + cellid + '"]');

        var level = $(this).closest('.gscolorselectionpanel').attr('level');
        if (level) {
            cell = cell.find(level).first();
        }

        thundashop.framework.removeCss('background-repeat', cellid);
        thundashop.framework.removeCss('background-position', cellid);
        thundashop.framework.removeCss('background-size', cellid);
        thundashop.framework.removeCss('background-image', cellid);
        thundashop.framework.removeCss('background-color', cellid);
        if ($(this).hasClass('gsremovebgimage')) {
            return;
        }

        if (window.File && window.FileReader && window.FileList && window.Blob) {
            cell.css('background-color', "");

            var target = $(this);
            var type = "";
            if(target.attr('data-type')) {
                type = target.attr('data-type');
            }
            target.parent().find('.gschoosebgimagebutton').hide();
            target.parent().find('.gsuploadimage').show();
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
                        "type" : type,
                        "data": e.target.result
                    }
                    var event = thundashop.Ajax.createEvent('', 'saveBackgroundImage', target, data);
                    thundashop.Ajax.postWithCallBack(event, function (id) {
                        target.parent().find('.gschoosebgimagebutton').show();
                        target.parent().find('.gsuploadimage').hide();
                        target.val('');
                        if(!type || type === "") {
                            thundashop.framework.addCss('background-repeat', 'no-repeat', cellid, level);
                            thundashop.framework.addCss('background-position', 'center', cellid, level);
                            thundashop.framework.addCss('background-size', '100%', cellid, level);
                            thundashop.framework.addCss('background-image', 'url("/displayImage.php?id=' + id + '")', cellid, level);
                            target.closest('.gscolorselectionpanel').find('.gschoosebgimagebutton').show();
                            target.closest('.gscolorselectionpanel').find('.gsuploadimage').hide();
                        } else {
                            var sid = document.cookie.match('PHPSESSID=([^;]*)')[1];
                            var location = window.location.protocol + "//mobile." + window.location.host + "/?page=" + $('#gspageid').val() + "&PHPSESSID=" + sid;
                            $('#gscontentframe').attr('src', location);
                            $('#gscontentframelandscape').attr('src', location);
                        }
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
            if (thundashop.framework.cellRotatingWait[container.attr('cellid')]) {
                var time = thundashop.framework.cellRotatingWait[container.attr('cellid')];
                if (time > new Date().getTime()) {
                    return;
                }
            }


            thundashop.framework.rotateCellDirection(container, "right");
        }, timer);
        container.attr('timerevent', timerevent);
        container.attr('timer', timer);
    },
    saveCellChanges: function (avoidreprint) {
        var target = "";
        if (thundashop.framework.originObject.attr('target')) {
            target = thundashop.framework.originObject.attr('target');
        }

        var cellid = thundashop.framework.findCellId(target);
        var cell = thundashop.framework.findCell(cellid);

        var colsizes = {};
        cell.children('.gsinner').children('.gscell').each(function () {
            colsizes[$(this).attr('cellid')] = $(this).attr('width');
        });


        var incrementcellid = cell.attr('incrementcellid');
        var styles = cssEditorForCell.getSession().getValue();

        styles = styles.replace(".gscell_" + incrementcellid + ".gscell", ".gscell_{incrementcellid}.gscell");
        styles = styles.replace(".gscell_" + incrementcellid + ".gsinner", ".gscell_{incrementcellid}.gsinner");

        var data = {
            "cellid": cellid,
            "styles": styles,
            "colsizes": colsizes
        };
        var event = thundashop.Ajax.createEvent('', 'saveColChanges', $(this), data);
        if (avoidreprint === true) {
            $('.gsresizingpanel').hide();
            thundashop.Ajax.postWithCallBack(event, function () {

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
        $('.gsresizingpanel').fadeOut();
    },
    switchtab: function () {
        var target = $(this).attr('target');
        var type = $(this).attr('type');
        $('.gsresizingpanel .gstabselected').removeClass('gstabselected');
        $(this).addClass('gstabselected');
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
        if(typeof(cssEditorForCell) !== "undefined") {
            cssEditorForCell.destroy();
            cssEditorForCell = null;
        }
        
        $('.tabsettingspanel').hide();
        var resizingpanel = $('.gsresizingpanel');
        var target = $(this).attr('target');

        if ($(this).closest('.gscellsettingspanel').length === 0) {
            thundashop.framework.originObject = $(this);
        }

        $('.gsoutercolorselectionbg').show();
        
        if($(this).attr('data-hideouterbg')) {
            $('.gsoutercolorselectionbg').hide();
        }

        var cellid = thundashop.framework.findCellId(target);
        var cell = thundashop.framework.findCell(cellid);

        resizingpanel.attr('cellid', cellid);
        $('.gscellsettingspanel').fadeOut();
        var offset = thundashop.framework.originObject.offset();
        if (offset !== undefined) {
            var left = offset.left;
            if (left + 500 > $(document).width()) {
                left = $(document).width() - 550;
            }
            resizingpanel.css('top', offset.top);
            resizingpanel.css('left', left);
        }
        resizingpanel.fadeIn();

        cell.find('.gscell.gshorisontal').resizable({grid: [10000, 1]});
        var cellcount = cell.children('.gsinner').first().children('.gscell.gscolumn').length;
        if (cellcount > 1) {
            thundashop.framework.loadResizing(cell, false);
        }
        resizingpanel.find('.tabbtn[target="background"]').click();
        thundashop.framework.loadCssEditor();
        thundashop.framework.loadCssAttributes();
    },
    closeCellEdit: function () {
        $('.gscellsettingspanel').hide();
    },
    startEditRow: function () {
        if ($(this).attr('done') === "true") {
            cellid = "";
        } else {
            cellid = $(this).closest('.gscell').attr('cellid');
        }
        if (!cellid  || $(this).attr('target') === "container") {
            cellid = $(this).closest('.gscontainercell').attr('cellid');
        }

        var event = thundashop.Ajax.createEvent('', 'startEditRow', $(this), {"cellid": cellid});
        thundashop.Ajax.post(event);
    },
    showCellPanel: function (event) {
        if(!thundashop.framework.advancedMode) {
            return;
        }
        
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
        if (target.find('.gscellbox').length > 1) {
            return;
        }

        var cellid = $(this).attr('cellid');
        if (thundashop.framework.activeBoxTimeout[cellid]) {
            clearTimeout(thundashop.framework.activeBoxTimeout[cellid]);
        }

        target.find('.gscellbox').first().addClass('gsactivebox');
        target.find('.gsactivebox').attr('entered', 'true');
    },
    mouseLeftPanel: function (event) {
        var cell = $(this);
        var cellid = cell.attr('cellid');

        if (thundashop.framework.activeBoxTimeout[cellid]) {
            clearTimeout(thundashop.framework.activeBoxTimeout[cellid]);
        }

        var timer = 0;
        if (cell.find('.gsactiveboxheader').length > 0) {
            timer = 2000;
        }
        cell.find('.gsactivebox').attr('entered', 'false');
        thundashop.framework.activeBoxTimeout[cellid] = setTimeout(function () {
            if (cell.find('.gsactivebox').attr('entered') === 'false') {
                cell.find('.gsactiveboxheader').first().removeClass('gsactiveboxheader');
                cell.find('.gscellheadermin').first().show();
                cell.find('.gsactivebox').removeClass('gsactivebox');
            }
        }, timer);
    },
    showEditIcon: function (event) {
        if (!thundashop.framework.isAdvancedMode()) {
            return;
        }


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
        if ($('.gsresizingpanel').is(':visible')) {
            target.find('.gscellsettings').first().find('.fa-image').show();
        }
        target.find('.gscellsettings').first().closest('.gscell').addClass('gsvisualizeedit');
    },
    getActiveContainerCellId: function (containerid) {
        return thundashop.framework.activeContainerCellId[containerid];
    },
    setActiveContainerCellId: function (id, containerid) {
        thundashop.framework.lastRotatedCell[containerid] = id;
        thundashop.framework.activeContainerCellId[containerid] = id;
    },
    findActiveCell: function (container) {
        return container.find('.gsactivetab:visible').attr('cellid');
    },
    findCellId: function (target) {
        var originObject = thundashop.framework.originObject;

        if (originObject.closest('.gseasyrowmode').length > 0) {
            var tmpcellid = originObject.closest('.gseasyrowmode').attr('cellid');
            originObject = $('.gscell[cellid="' + tmpcellid + '"] .gsinner');
            if (originObject.length === 0) {
                originObject = $('.gscontainercell[cellid="' + tmpcellid + '"] .gsinner').first();
            }
        }

        var cellid = originObject.closest('.gscell').attr('cellid');

        if (originObject.parent().hasClass('gsfloatingheader')) {
            cellid = originObject.closest('.gsfloatingbox').attr('cellid');
        }

        if (target === "selectedcell") {
            cellid = originObject.closest('.gscontainercell').find('.gsactivecell:visible').attr('cellid');
        }

        if (target === "container") {
            cellid = originObject.closest('.gscontainercell').attr('cellid');
        }
        if (target === "gseasymode") {
            cellid = originObject.closest('.gseasymode').attr('cellid');
        }

        return cellid;
    },
    findCell: function (cellid) {
        var element = $('.gscell[cellid="' + cellid + '"]');
        if (element.length === 0) {
            return $('.gscontainercell[cellid="' + cellid + '"]');
        }
        return element;
    },
    simpleaddrow: function() {
        
        var button = $(this);
        var metaData = {
            rowId : "",
            name : "",
            someThingElse : ""
        };
        
        var cellobj = $(this).closest('.gscell');
        
        if($(this).attr('target') === "container") {
            cellobj = $(this).closest('.gscontainercell');
        }
        

        
        var selected = function(result) {
            var before = cellobj.attr('cellid');
            if(result.direction === "below") {
                before = cellobj.next().attr('cellid');
                if (cellobj.next().hasClass("gseditinfo")) {
                    before = cellobj.next().next().attr('cellid');
                }
            }
            
            var data = {
                "area" : button.closest('.gsarea').attr('area'),
                "cellid" : before,
                "metaData" : metaData
            }
            var event = thundashop.Ajax.createEvent('','simpleAddRow',$(this),data);
            thundashop.Ajax.post(event, function() {
                thundashop.framework.rowPicker.close();
            });
            
        }
        if(cellobj.attr('cellid')) {
            thundashop.framework.rowPicker.toggleRowPicker('left', this, selected, metaData);
        } else {
            thundashop.framework.rowPicker.toggleRowPicker('down', this, selected, metaData, false);
        }
    },
    operateCell: function () {
        if ($(this).closest('.gscellsettingspanel').length === 0) {
            thundashop.framework.originObject = $(this);
        }

        var type = $(this).attr('type');
        var target = "";

        if ($(this).attr('target')) {
            target = $(this).attr('target');
        }

        if (target === "this") {
            cellid = $(this).attr('cellid');
        } else {
            var cellid = thundashop.framework.findCellId(target);
        }
        var cellobj = thundashop.framework.findCell(cellid);

        if (type === "delete" && $(cellobj).hasClass('gsdepth_0')) {
            thundashop.framework.highlightRow(cellid);
        }
        
        if (type === "delete" && !confirm("Are you sure you want to delete this cell and all its content?")) {
            thundashop.framework.stopHighlightRow();
            return;
        } else {
            thundashop.framework.stopHighlightRow();
        }
        
        
        if (cellobj.length === 0) {
            cellobj = $('.gscontainercell[cellid="' + cellid + '"]');
        }
        if (cellobj.hasClass('gstab') && target === "gseasyrowmode" && type === "addcolumn") {
            cellid = thundashop.framework.findActiveCell(cellobj);
            cellobj = $('.gscell[cellid="' + cellid + '"]');
        }

        var data = {
            "cellid": cellid,
            "type": type,
            "mode": $(this).attr('mode'),
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

        if (type === "addcolbefore") {
            data['type'] = "addcolumn";
            data['before'] = cellid;
            data['cellid'] = cellid;
        }

        if (type === "addafter" || type === "initafter") {
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
            if (!data['before']) {
                if (cellobj.hasClass('gscolumn')) {
                    data['type'] = "addcolumn";
                } else {
                    data['type'] = "addrow";
                }
            }

            if (type === "initbefore" || type === "initafter") {
                data['type'] = type;
            }
        }


        var event = thundashop.Ajax.createEvent('', 'operateCell', $(this), data);
        thundashop.Ajax.post(event);
    },
    showCellSettingsPanel: function () {
        thundashop.framework.originObject = $(this);
        thundashop.framework.mousedown = true;
        setTimeout(function () {
            if (thundashop.framework.mousedown === true) {
                $('.gsrowmenu').show();
                $('.gscolumnmenu').show();
            }
        }, "2000");

        var cell = $(this).closest('.gscell');
        var panel = $('.gscellsettingspanel');
        panel.find('.gsrowmenu').hide();
        panel.find('.gscolumnmenu').hide();
        panel.attr('cellid', cell.attr('cellid'));
        panel.fadeIn();

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

        var offset = $(this).offset();
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
    highlightRow: function(cellid) {
        var cell = $('div[cellid="'+cellid+'"]');
        
        var top = cell.offset().top;
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_top').css('height', top+'px');
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_top').css('top', '0px');
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_top').css('left', '0px');
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_top').css('right', '0px');
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_top').show();
        
        var rowHeight = cell.outerHeight(true);
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_bottom').css('top', (top+rowHeight)+'px');
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_bottom').css('left', '0px');
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_bottom').css('right', '0px');
        var bottomHeight = $(document).outerHeight(true) - (top+rowHeight);
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_bottom').css('height', bottomHeight+'px');
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_bottom').show();
        
        
    },
    stopHighlightRow: function() {
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_bottom').hide();
        $('.gs_overlay_row_highlighter.gs_overlay_row_highlighter_top').hide();
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


PubSub.subscribe('NAVIGATION_COMPLETED', function (a, b) {
    if(isAdministrator) {
        for (var containerid in thundashop.framework.lastRotatedCell) {
            var lastRotatedCell = thundashop.framework.getActiveContainerCellId(containerid);
            var cell = $('.gscell[cellid="' + lastRotatedCell + '"]');
            var container = cell.closest('.gscontainercell');
            if (container.hasClass('gsrotating')) {
                thundashop.framework.displayCarouselEntry(cell);
            }
            if (container.hasClass('gstab')) {
                container.find('.gstabrow').hide();
                cell.show();
            }
        }
    }
});

var gssstepp = 0;
$(document).on('keyup', function (event) {
    if (event.keyCode === 71) {
        gssstepp = 1;
    } else if (event.keyCode === 83) {
        gssstepp++;
    } else {
        gssstepp = 0;
    }
    if (gssstepp === 3) {
        if (thundashop.framework.isAdvancedMode()) {
            localStorage.setItem('gsadvancedcombo', false);
        } else {
            localStorage.setItem('gsadvancedcombo', true);
        }
    }
});

thundashop.framework.bindEvents();


thundashop.framework.rowPicker = {
    lastx : null,
    lasty : null,
    callBackFunction: null,
    lastCallBackData : {},
    slidedirection: null,
    
    init: function() {
        $(document).on('click', '.gs_rowpicker_box .gs_row_box_to_select', thundashop.framework.rowPicker.showDirection);
        $(document).on('click', '.gs_rowpicker_box .gs_row_mode_select', thundashop.framework.rowPicker.modeChange);
        $(document).on('click', '.gs_rowpicker_box .close_gs_row_picker', thundashop.framework.rowPicker.close);
        $(document).on('click', '.gs_rowpicker_box .select_row_picker_direction_button', thundashop.framework.rowPicker.selected);
    },
    
    close: function() {
        thundashop.framework.stopHighlightRow();
        var pickerDom = $('.gs_rowpicker_box');
        if (thundashop.framework.rowPicker.slidedirection) {
            pickerDom.hide("slide", { direction: thundashop.framework.rowPicker.slidedirection }, 250);
        } else {
            pickerDom.hide();
        }
    },
    
    showDirection: function() {
        if (typeof(thundashop.framework.rowPicker.callBackFunction) === "function") {
            if (!thundashop.framework.rowPicker.lastCallBackData) {
                thundashop.framework.rowPicker.lastCallBackData = {};
            }
            
            thundashop.framework.rowPicker.lastCallBackData.mode = $(this).attr('rowmode');
            thundashop.framework.rowPicker.lastCallBackData.rowconfig = JSON.parse($(this).attr('rowconfig'));
            
        }
        
        if (!thundashop.framework.rowPicker.showDirections) {
            thundashop.framework.rowPicker.selected();
            return;
        }
        $('.gs_rowpicker_box .select_row_picker_direction').show();
        $('.gs_rowpicker_box .gs_row_picker_modes').hide();
    },
    
    modeChange: function() {
        $('.gs_rowpicker_box .gs_row_mode_select_active').removeClass('gs_row_mode_select_active');
        $('.gs_rowpicker_box .gs_row_selection_box').hide();
        $(this).addClass('gs_row_mode_select_active');
        var mode = $(this).attr('rowmode');
        $('.gs_rowpicker_box .gs_row_selection_box[type="'+mode+'"]').show();
    },
    
    selected: function() {
        thundashop.framework.stopHighlightRow();
        thundashop.framework.rowPicker.lastCallBackData.direction = $(this).attr('direction');
        thundashop.framework.rowPicker.callBackFunction(thundashop.framework.rowPicker.lastCallBackData);
    },
    
    toggleRowPicker: function(direction, target, callback, callbackData, showDirections) {
        if (typeof(showDirections) === "undefined") {
            showDirections = true;
        }
        
        $('.gs_rowpicker_box .select_row_picker_direction').hide();
        $('.gs_rowpicker_box .gs_row_picker_modes').show();
        thundashop.framework.rowPicker.callBackFunction = callback;
        thundashop.framework.rowPicker.lastCallBackData = callbackData;
        
        var rowCellId = $(target).closest('.gsrow').attr('cellid');
        if(!rowCellId) {
            rowCellId = $(target).closest('.gscontainercell').attr('cellid');
        }
        var pickerDom = $('.gs_rowpicker_box');
        
        pickerDom.removeClass('shadowsadded');
        var target = $(target);
        var startx = 250;
        var starty = 250;
        var padding = 20;
        var slidedirection = "";
        
        var addShadow = function() {
            $(this).addClass('shadowsadded');
        }
        
        if (direction === "up") {
            slidedirection = "down";
            startx = target.offset().left + ( target.outerWidth(true)/2) - (pickerDom.outerWidth(true)/2);
            starty = target.offset().top - (pickerDom.outerHeight(true)) - padding;
        }
        
        if (direction === "down") {
            slidedirection = "up";
            startx = target.offset().left + ( target.outerWidth(true)/2) - (pickerDom.outerWidth(true)/2);
            starty = target.offset().top + target.outerHeight(true) + padding;
        }
        
        if (direction === "left") {
            slidedirection = "right";
            startx = target.offset().left - padding - pickerDom.outerWidth(true);
            starty = target.offset().top + target.outerHeight(true)/2 - (pickerDom.outerHeight(true)/2);
        }
        
        if (direction === "right") {
            slidedirection = "left";
            startx = target.offset().left + padding + target.outerWidth(true);
            starty = target.offset().top + target.outerHeight(true)/2 - (pickerDom.outerHeight(true)/2);
        }
        
        if (pickerDom.is(':visible') &&  thundashop.framework.rowPicker.lastx === startx && thundashop.framework.rowPicker.lasty === starty) {
            thundashop.framework.rowPicker.close();
            return;
        }
        if(rowCellId) {
            thundashop.framework.highlightRow(rowCellId);
        }
        $(pickerDom).hide();
        thundashop.framework.rowPicker.lastx = startx;
        thundashop.framework.rowPicker.lasty = starty;
        
        thundashop.framework.rowPicker.showDirections = showDirections;
        thundashop.framework.rowPicker.slidedirection = slidedirection;
        pickerDom.css('top', starty);
        pickerDom.css('left', startx);
        pickerDom.show("slide", { direction: slidedirection, complete: addShadow }, 250);
    }
};

thundashop.framework.rowPicker.init();
