/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* Used for CKEDITOR */
let editor;

$(document).on('click', '.button.savetranslation', ContentManager.saveCurrentEditor);
$(document).on('click', '#menuToggle', toggleMobileMenu);

function getCurrentPageId() {
    return $('body').attr('pageid');
}

function toggleMobileMenu() {
    var entries = $('.mobilemenu');
    
    if (entries.is(':visible')) {
        entries.slideUp();
    } else {
        entries.slideDown();
        window.scroll(0,0);
    }
    
}