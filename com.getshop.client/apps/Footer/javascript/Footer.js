GetShop.Footer = {
    formPosted: function(result, event) {
        if (event.event == "saveConfiguration" && event.data.text) {
            var target = $('.Footer').find('.contentText');
            var event = thundashop.Ajax.createEvent(null, "render", target, null);
            var data = thundashop.Ajax.postSynchron(event);
            $('.footer .Footer .applicationinner').html(data);
            thundashop.common.Alert(__f("Text saved"), __f("The text has been saved"));
        }
    },
    changeNumberOfColumns: function(number, target) {
        $('.Footer span[gsname="changetab"]').hide();
        var count = 0;
        var width = 100 / number;
        $('.Footer .footer_col').each(function() {
            if (count >= number) {
                $(this).addClass('hidden');
            } else {
                $(this).removeClass('hidden');
                $(this).attr('width',width+"%");
                var id = $(this).find('.footer_content').attr('id');
                CKEDITOR.instances[id].destroy();
                $(this).find('.footer_content').attr('contenteditable','true');
            }
            count++;
        });
        var data = {
            "count" : number
        }
        var event = thundashop.Ajax.createEvent(null, "updateColCount", target, data);
        thundashop.Ajax.postSynchron(event);
    },
    initialize: function() {
        GetShop.Footer.changeNumberOfColumns($('.Footer .col_number').val());
        $('.Footer .configuration_area').hide();
        $('.Footer .config_area_' + 0).slideDown();

        $('.Footer span[gsname="changetab"]').live('click', function(e) {
            GetShop.Footer.changeTab($(this).attr('tab'));
        });
    },
    changeTab: function(tab) {
        $('.Footer span[gsname="changetab"]').removeClass('tab_selected');
        $('.Footer [tab="' + tab + '"]').addClass('tab_selected');
        $('.Footer .configuration_area').hide();
        $('.Footer .config_area_' + tab).show();
    }
}

$('.Footer .col_number').live('change', function(e) {
    var number = $(this).val();
    GetShop.Footer.changeNumberOfColumns(number, $(this));
});

$('.Footer #saveconfiguration').live('click', function(e) {
    var editors = thundashop.common.destroyCKEditors();
    var data = {
        "content_0": editors.contentText_0,
        "content_1": editors.contentText_1,
        "content_2": editors.contentText_2,
        "content_3": editors.contentText_3,
        "num_cols": $('.Footer .col_number').val()
    }

    var event = thundashop.Ajax.createEvent("", "saveConfiguration", $(this), data);
    thundashop.Ajax.post(event);
    thundashop.common.Alert(__f("Text saved"), __f("The text has been saved"));
    thundashop.common.hideInformationBox();
});