
getshop.Model = {};

getshop.Models = {
    watching: {},
    init: function () {
        $(document).on('change', 'input[gs_model]', this.inputChanged);
        $(document).on('change', 'select[gs_model]', this.selectChanged);
        $(document).on('click', '[gs_model].gss_onoff', this.toggleOnOff);
    },
    toggleOnOff: function (silent) {
        var fontAwesomeIcon = $(this).find('i');
        if (fontAwesomeIcon.hasClass("fa-toggle-off")) {
            fontAwesomeIcon.removeClass("fa-toggle-off")
            fontAwesomeIcon.addClass("fa-toggle-on")
        } else {
            fontAwesomeIcon.removeClass("fa-toggle-on")
            fontAwesomeIcon.addClass("fa-toggle-off")
        }

        if (silent !== true) {
            getshop.Models.onOffChanged(this);
        }
    },
    onOffChanged: function (field) {
        var model = $(field).attr('gs_model');
        var attr = $(field).attr('gs_model_attr');
        var val = $(field).find('i').hasClass('fa-toggle-on');
        this.setAttr(model, attr, val);
    },
    selectChanged: function (field) {
        if (!$(field).is('select')) {
            field = this;
        }

        var model = $(field).attr('gs_model');
        var attr = $(field).attr('gs_model_attr');
        var val = $(field).find('option:selected').val();

        getshop.Models.setAttr(model, attr, val);
    },
    inputChanged: function () {
        if (getshop.Models.isTextfield(this)) {
            getshop.Models.textfieldChanged(this);
        }
    },
    isOnOffSwitch: function (field) {
        return $(field).hasClass('gss_onoff');
    },
    textfieldChanged: function (field) {

        var model = $(field).attr('gs_model');
        var attr = $(field).attr('gs_model_attr');
        var val = $(field).val();

        this.setAttr(model, attr, val);
    },
    setAttr: function (model, attr, val) {
        getshop.Model[model][attr] = val;
    },
    isTextfield: function (field) {
        return ($(field).is("[type=text]") || $(field).is("[type=textfield]") || $(field).is("[type=hidden]") || $(field).is("[type=password]"));
    },
    modelChanged: function (prop, action, difference, oldvalue, model) {
        var fields = $('[gs_model=' + model + "][gs_model_attr=" + prop + "]");
        $(fields).each(function () {
            if (getshop.Models.isTextfield(this)) {
                $(this).val(difference);
            }

            if (getshop.Models.isOnOffSwitch(this)) {
                var fontAwesomeIcon = $(this).find('i');
                if (difference) {
                    fontAwesomeIcon.removeClass("fa-toggle-off")
                    fontAwesomeIcon.addClass("fa-toggle-on")
                } else {
                    fontAwesomeIcon.removeClass("fa-toggle-on")
                    fontAwesomeIcon.addClass("fa-toggle-off")
                }
            }

            if (getshop.Models.isSelectBox(this)) {
                $(this).val(difference);
            }
        });
    },
    addWatchers: function (html) {
        $(html).find('[gs_model]').each(function () {
            var model = $(this).attr('gs_model');
            var attr = $(this).attr('gs_model_attr');

            if (typeof (getshop.Model[model]) === "undefined") {
                getshop.Model[model] = {};
            }

            if (!getshop.Models.isWatching(model, attr)) {
                getshop.Models.setWatching(model, attr);
                watch(getshop.Model[model], attr, getshop.Models.modelChanged, 0, 0, model);
            }

            getshop.Models.allChanged(this);
        });
    },
    setWatching: function (model, attr) {
        if (typeof (getshop.Models.watching[model]) === "undefined") {
            getshop.Models.watching[model] = {};
        }

        getshop.Models.watching[model][attr] = true;
    },
    isWatching: function (model, attr) {
        if ((typeof (getshop.Models.watching[model]) === "undefined") || (typeof (getshop.Models.watching[model][attr]) === "undefined")) {
            return false;
        }

        return true;
    },
    isSelectBox: function (field) {
        return $(field).is("select");
    },
    allChanged: function (field) {
        if (getshop.Models.isTextfield(field)) {
            getshop.Models.textfieldChanged(field);
        }

        if (getshop.Models.isOnOffSwitch(field)) {
            getshop.Models.onOffChanged(field);
        }

        if (getshop.Models.isSelectBox(field)) {
            getshop.Models.selectChanged(field);
        }
    }
}

getshop.Models.init();