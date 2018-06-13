app.Publications = {
    init : function(){
        $(document).on('click', '.Publications .publicationShellSubmit', $.proxy(this.addEvent, app.Publications));
        $(document).on('click', '.Publications .gs_button.small.delete', this.deleteEvent );
        $(document).on('click', '.Publications .dateforpublication', this.toggleChangeDate );
    },
    toggleChangeDate: function() {
        var form = $(this).parent().find('.chagnedate');
        
        if ($(form).is(':visible')) {
            $(form).hide();
        } else {
            $(form).show();
        }
    },
    addEvent: function() {
        var authors = $('.Publications #authors').val();
        var articleName = $('.Publications #articleName').val();
        var articleLink = $('.Publications #articleLink').val();
        var publisher = $('.Publications #publisher').val();
        var ISSN = $('.Publications #ISSN').val();
        var PMID = $('.Publications #PMID').val();
        
        var data = {
            authors : authors,
            articleName : articleName,
            articleLink : articleLink,
            publisher : publisher,
            ISSN : ISSN,
            PMID : PMID
        };
        if(authors || articleName || articleLink || publisher || ISSN || PMID){
            var event = thundashop.Ajax.createEvent('', 'addEntry', $('.Publications'), data);
            thundashop.Ajax.post(event);
        }
    },
    deleteEvent: function(event) {
        var confirmed = confirm('Are you sure you want to delete this publication?');
        if(confirmed){
            event.stopPropagation();
            var id = $(this).closest('.publicationlistShell').attr('id');
            var data = {
                id : id
            }
            var event = thundashop.Ajax.createEvent('', 'removeEntry', $('.Publications'), data);
            thundashop.Ajax.post(event);
        }
    },
    loadSettings: function(element, application) {
        var config = { showSettings : true, draggable: true, title: "Settings", items: [] }
        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
}
app.Publications.init();