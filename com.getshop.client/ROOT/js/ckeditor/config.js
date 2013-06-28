/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function(config) {

    config.resize_minWidth = 100;
    config.width = 100;
    config.toolbar = [
        {name: 'document', items: ['Save']},
        {name: 'clipboard', items: ['MediaEmbed','Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo']},
        {name: 'editing', items: ['Find', 'Replace', '-', 'SelectAll', '-', 'Scayt', 'JustifyLeft', 'JustifyCenter', 'JustifyRight']},
        {name: 'insert', items: ['Image', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar']},
        {name: 'links', items: ['Link', 'Unlink', 'Anchor']},
        '/',
        {name: 'styles', items: ['Styles', 'Format', 'FontSize']},
        {name: 'colors', items: ['TextColor', 'BGColor', 'Center']},
        {name: 'basicstyles', items: ['Bold', 'Italic', 'Strike', '-', 'RemoveFormat']},
        {name: 'paragraph', items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote']}
    ];
    config.extraPlugins = 'mediaembed';
};
CKEDITOR.plugins.registered['save'] = {
    init: function(editor)
    {
        var command = editor.addCommand('save',
                {
                    modes: {wysiwyg: 1, source: 1},
                    exec: function(editor) {
                        thundashop.common.addNotificationProgress('contentmanager', "Saving content");
                        console.log(editor);
                        thundashop.common.saveCKEditor(editor.getData(), $('#'+editor.name));
                        thundashop.common.Alert('Saved', 'content has been saved');
                    }
                }
        );
        editor.ui.addButton('Save', {label: 'My Save', command: 'save'});
    }
}