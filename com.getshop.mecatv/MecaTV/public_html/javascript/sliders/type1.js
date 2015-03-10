/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



slider.type1 = {
    currentId : "newSlider_1",
    
    animate: function() {
        console.log("animate type1");
        move('#'+slider.type1.currentId+' .creditcart')
            .x(900)
            .y(880)
            .end(function() {
            slider.type1.slideImageBackABit()
        });
    },
            
    slideImageBackABit: function() {
        console.log("slidebackabit type1");
        move('#'+slider.type1.currentId+' .creditcart')
            .x(800)
            .y(800)
            .end(function() {
            slider.type1.showHeader()
        });
    },
        
    showHeader: function() {
        console.log("show header type1");
        $( "#"+slider.type1.currentId+" .title" ).delay(200).animate({
            opacity: "1"
        }, 500, function() {
            $('#mecacard .bullets').css('opacity', '1');
            slider.type1.showBullet(1);
        });
    },
    
    
    showBullet: function(bulletnumber) {
        console.log("show bullet type1");
        if ($( "#"+slider.type1.currentId+" .bullets .bullet"+bulletnumber).length === 0) {
            slider.type1.finised();
        } else {
            $( "#"+slider.type1.currentId+" .bullets .bullet"+bulletnumber).delay(400).animate({
                opacity: "1"
            }, 500, function() {
                bulletnumber++;
                slider.type1.showBullet(bulletnumber);
            }
            );
        }
    },

    finised: function() {
        console.log("finished type1");
        setTimeout(function() {
            $( "#"+slider.type1.currentId+" .creditcart" ).removeAttr('style');
        
            $( "#"+slider.type1.currentId+" .title" ).removeAttr('style');
            $('#'+slider.type1.currentId+' .bullets').removeAttr('style');
            $('#'+slider.type1.currentId+' .bullets div').removeAttr('style');    
            $('#'+slider.type1.currentId+' .car').removeAttr('style');  
            $( "#"+slider.type1.currentId).hide();
            GetShopTV.next();
        }, GetShopTV.slidePause);
        
    },
    
    reset: function(name) {
        console.log("reset type1");
        slider.type1.currentId = name;
        
        $( "#"+slider.type1.currentId).show();
        setTimeout(function() {  
            setTimeout(function() {
                slider.type1.animate();
            }, 100);
        }, 300);        
    },
    
    createSlide: function(slideData, count) {
        console.log("create slider type1");
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
        var addressToImage = "http://"+GetShopTV.address+"/displayImage.php?id=" + slideData.images.picture_1;
        
        GetShopTV.loadImage(addressToImage, function(addressToImage) {
            slide.find('.creditcart .inner_image').attr('style', "background-image: url('"+addressToImage+"');");
        });
        
        $('.animation_inner').append(slide);
        
        
        if (!slideData.texts.bullet_1) 
            $('#'+name).find('.bullet1').remove();
        
        if (!slideData.texts.bullet_2) 
            $('#'+name).find('.bullet2').remove();
        
        if (!slideData.texts.bullet_3) 
            $('#'+name).find('.bullet3').remove();
        
        if (!slideData.texts.bullet_4) 
            $('#'+name).find('.bullet4').remove();
        
        if (!slideData.texts.bullet_5) 
            $('#'+name).find('.bullet5').remove();
        
        return name;
    }
}

