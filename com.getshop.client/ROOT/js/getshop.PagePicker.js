/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

if (typeof(getshop) === "undefined") {
    getshop = {};
}

getshop.PagePicker = {
    currentFields : [],
            
    currentFieldAlreadyCreated: function(field) {
        for (var i in getshop.PagePicker.currentFields) {
            var data = getshop.PagePicker.currentFields[i];
            if (data.field === field) {
                return true;
            }
        }
        
        return false;
    },

    init: function() {
        $(document).on("click", ".pagepicker", function() {
            
            if (getshop.PagePicker.currentFieldAlreadyCreated(this)) {
                return;
            }
            
            getshop.PagePicker.currentField = this;
            // TODO, check if the same textfield already has opened one PagePickerInstance.
            var instance = new getshop.PagePickerInstance(this);
            var data = {
                instance: instance,
                field : this
            };
            
            getshop.PagePicker.currentFields.push(data);
        });
    },
    
    instanceDestroyed: function(instance) {
         for (var i in getshop.PagePicker.currentFields) {
            var data = getshop.PagePicker.currentFields[i];
            if (data.field === instance.inputfieldHtml) {
                getshop.PagePicker.currentFields.splice(i, 1);
            }
        }
    }
};


getshop.PagePickerInstance = function(inputfield) {
    this.inputfieldHtml = inputfield;
    this.inputfield = $(inputfield);
    this.create();
    this.listenEvents();
};

getshop.PagePickerInstance.prototype = {
    create: function() {
        this.listenEvents();
        this.dom = $('<div/>');
        this.dom.addClass('pagepickerwindow');
        
        this.inner = $('<div/>');
        this.inner.addClass('inner');
        this.dom.html(this.inner);
        
        this.searchArea = $('<div/>');
        this.searchArea.html("");
        this.searchArea.addClass('searching');
        this.clear();
        this.inner.append(this.searchArea);
        
        this.clearInputField();
        this.inputfield.parent().append(this.dom);
        this.setPosition();
        this.addCloseButton();
    },
            
    clearInputField: function() {
        this.inputfield.val("");
        this.inputfield.removeAttr("pageId");
    },
            
    addCloseButton: function() {
        var closeButton = $("<div/>");
        closeButton.addClass('closebutton');
        closeButton.click($.proxy(this.destroy, this));
        this.inner.append(closeButton);
    },
            
    setPosition: function() {
        var top = this.inputfield.position().top;
        top = top + this.inputfield.outerHeight() + 10;
        var left = this.inputfield.position().left + 5;
        this.dom.css('top', top + "px");
        this.dom.css('left', left + "px");
    },
            
    clear: function() {
        this.searchArea.html(__w("Please start typing to search for page, must be more then 2 chars"));
    },
          
    listenEvents: function() {
        this.inputfield.keyup($.proxy(this.textChanged, this));
    },
            
    textChanged: function() {
        var text = this.inputfield.val();
        if (text.length > 2) {
            this.searchArea.html(__w("Searching...."));
            me = this;
            
            if (this.searchTimer) {
                window.clearInterval(this.searchTimer);
            }
            
            this.searchTimer = setTimeout(function() {
                me.search(text);
            }, 200);
            
        } else {
            this.clear();
        }
    },
            
    search: function(text) {
        var data = {
            searchText : text
        };
        
        var event = thundashop.Ajax.createEvent("PagePicker", "searchForPages", null, data);
        event['synchron'] = true;
        me = this;
        thundashop.Ajax.post(event, function(result) {
            me.searchResult(result);
        });
    },
            
    resultClicked: function(name, pageId) {
        this.inputfield.attr('pageId', pageId);
        this.inputfield.val(name);
        this.inputfield.keyup()
        this.destroy();
    },
            
    searchResult: function(result) {    
        var me = this;
        this.searchArea.html(result);
        $(this.searchArea).find('.pageresult').click(function() {
            var name = $(this).find('div').html();
            var pageId = $(this).attr('pageId');
            me.resultClicked(name, pageId)
        });
    },

    destroy: function() {
        var me = this;
        getshop.PagePicker.instanceDestroyed(me);
        this.inputfield.unbind('keyup', this.textChanged);
        this.dom.remove();
    }
};

getshop.PagePicker.init();