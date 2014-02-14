/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

$('.checkout .payment .sisowoptions .entry').live('click', function(e) {
    $('.checkout .payment .sisowoptions .entry').removeClass('selected');
    $(this).addClass('selected');
});
