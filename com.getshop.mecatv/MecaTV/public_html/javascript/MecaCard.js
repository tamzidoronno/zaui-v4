/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

move.defaults = {
  duration: 1000
};

MecaCard = {
    currentId : "newSlider_1",
    
    animate: function() {
        move('#'+MecaCard.currentId+' .creditcart')
            .x(900)
            .y(780)
            .end(function() {
            MecaCard.slideImageBackABit()
        });
    },
            
    slideImageBackABit: function() {
        $( "#"+MecaCard.currentId+" .creditcart" ).animate({
            top: "+=-50",
            left: "+=-180"
        }, 200, MecaCard.showHeader
        );
    },
        
    showHeader: function() {
        $( "#"+MecaCard.currentId+" .title" ).delay(200).animate({
            opacity: "1"
        }, 500, function() {
            $('#mecacard .bullets').css('opacity', '1');
            MecaCard.showBullet(1);
        });
    },
    
    
    showBullet: function(bulletnumber) {
        if ($( "#"+MecaCard.currentId+" .bullets .bullet"+bulletnumber).length === 0) {
            MecaCard.showCar();
        } else {
            $( "#"+MecaCard.currentId+" .bullets .bullet"+bulletnumber).delay(400).animate({
                opacity: "1"
            }, 500, function() {
                bulletnumber++;
                MecaCard.showBullet(bulletnumber);
            }
            );
        }
    },
    
    showCar: function() {
        $( "#"+MecaCard.currentId+" .car").animate({
            opacity: "1"
        }, 200, MecaCard.moveCar
        );
    },
    
    moveCar: function() {
        move('#'+MecaCard.currentId+' .car')
                .ease('in-out')
                .duration(2000)
                .x(-1980)
                .end(function() {
                    MecaCard.finised();
        });
    },
    
    finised: function() {
        $( "#"+MecaCard.currentId+" .creditcart" ).removeAttr('style');
        $( "#"+MecaCard.currentId+" .title" ).removeAttr('style');
        $('#'+MecaCard.currentId+' .bullets').removeAttr('style');
        $('#'+MecaCard.currentId+' .bullets div').removeAttr('style');    
        $('#'+MecaCard.currentId+' .car').removeAttr('style');  
        $( "#"+MecaCard.currentId).hide();
        GetShopTV.next();
    },
    
    reset: function(name) {
        MecaCard.currentId = name;
        
        $( "#"+MecaCard.currentId).show();
        setTimeout(function() {  
            setTimeout(function() {
                MecaCard.animate();
            }, 100);
        }, 300);        
    },
    
    createSlide: function(slideData, count) {
        var slide = $('#slide_type_1_template').clone();
        var name = 'slide_type1_'+count;
        slide.attr('id', name);
        slide.find('.title_line1').html(slideData.texts.title_line_1);
        slide.find('.title_line2').html(slideData.texts.title_line_2);
        slide.find('.bullet1').html(slideData.texts.bullet_1);
        slide.find('.bullet2').html(slideData.texts.bullet_2);
        slide.find('.bullet3').html(slideData.texts.bullet_3);
        slide.find('.bullet4').html(slideData.texts.bullet_4);
        slide.find('.bullet5').html(slideData.texts.bullet_5);
        $('.animation_inner').append(slide);
        
        return name;
    }
}

