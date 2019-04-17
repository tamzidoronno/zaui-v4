/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


ContentManager = {
    init: function() {
        $(document).on('click', '.top_translation .areaentry', this.loadEditor)
        $(document).on('click', '.editcontentbutton', this.showEditor)
        $(document).on('click', '.top_translation .closebutton', this.closeTranslation)
        $(document).on('click', '.top_translation .closebutton', this.closeTranslation)
        
        $(document).on('mouseover', '.menu .entry', function() {
            $(this).find('.subentries').first().show();
        });
        $(document).on('mouseout', '.menu .entry', function() {
            $(this).find('.subentries').first().hide();
        });
    },
    
    closeTranslation: function() {
        location.reload();
    },
    
    showEditor: function() {
        var keyId = $(this).attr('keyid');
        $('.top_translation').show();
        $('.areaentry[keyid="'+keyId+'"]').click();
    },
    
    loadEditor: function() {
        var id = $(this).attr('keyid');
        var pageId = getCurrentPageId();
        
        $.get('plugins/contentmanager.php?id='+id+"&event=load&page="+pageId,  // url
            function (data, textStatus, jqXHR) {  // success callback
                $('.translation_leftmenu_mainarea').html(data);
        });
    },
    
    saveCurrentEditor: function() {
        var id = $(this).attr('keyid');
        var pageId = getCurrentPageId();
        
        $.post('plugins/contentmanager.php?id='+id+"&event=save&page="+pageId, { data : editor.getData() },
            function (data, textStatus, jqXHR) {  // success callback
                $('.translation_leftmenu_mainarea').html(data);
        });
    }
}

ContentManager.init();