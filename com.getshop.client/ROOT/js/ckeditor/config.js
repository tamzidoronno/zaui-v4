/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */
CKEDITOR.editorConfig = function(config) {
    config.protectedSource.push(/<i[^>]*><\/i>/g);
    
    config.resize_minWidth = 100;
    config.width = 100;
    config.toolbar = [
        {name: 'document', items: ['Save', 'Source']},
        {name: 'clipboard', items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo']},
        {name: 'editing', items: ['Find', 'Replace', '-', 'SelectAll', '-', 'Scayt', 'JustifyLeft', 'JustifyCenter', 'JustifyRight']},
        {name: 'insert', items: ['Image', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar']},
        {name: 'links', items: ['Link', 'Unlink', 'Anchor']},
        '/',
        {name: 'styles', items: ['Styles', 'Format', 'FontSize']},
        {name: 'colors', items: ['TextColor', 'BGColor', 'Center']},
        {name: 'basicstyles', items: ['Bold', 'Italic', 'Strike', '-', 'RemoveFormat']},
        {name: 'paragraph', items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote','FontAwesome']}
    ];
    config.contentsCss = '/skin/default/fontawesome/css/font-awesome.css';
    config.allowedContent = true; 

};
CKEDITOR.plugins.registered['save'] = {
    init: function(editor)
    {
        var command = editor.addCommand('save',
                {
                    modes: {wysiwyg: 1, source: 1},
                    exec: function(editor) {
                       $('#'+editor.name).attr('contenteditable',false);
                       CKEDITOR.instances[editor.name].fire('save');
                    }
                }
        );
        editor.ui.addButton('Save', {label: 'My Save', command: 'save'});
    }
}
