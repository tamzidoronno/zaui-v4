move.defaults = {
  duration: 1000
};

slider = {};

GetShopTV = {
    tvId : null,
    tvData : null,
    sliders : [],
    newsAnimation : null,
    address: 'tv.2.0.local.getshop.com',
    slidePause: 5000,
    i : 0,
    
    loadImage : function(uri, imageLoaded) {
        var xhr = new XMLHttpRequest();
        xhr.responseType = 'blob';
        xhr.onload = function() {

            var img = document.createElement('img');
            var url = window.URL.createObjectURL(xhr.response);
            img.src = url;
            document.body.appendChild(img);
            imageLoaded(url);
        }
        
        xhr.open('GET', uri, true);
        xhr.send();
    },

    init: function() {
        $(document).on('keyup', 'body', function(res, key) {
            if (res.keyCode === 82) {
                GetShopTV.resetCode = 82;
            } else {
                GetShopTV.resetCode += res.keyCode;
            }
            
            if (GetShopTV.resetCode === 387) {
                GetShopTV.resetSystem();
            }
        });
        
        $(document).on('click', '.savebutton', GetShopTV.saveId);
        GetShopTV.api = new GetShopApiWebSocket(GetShopTV.address);
        GetShopTV.api.setConnectedEvent(GetShopTV.connectionEstablished);
        !GetShopTV.showAskForId(function(res) {
            if (!res) {
                 GetShopTV.connect();
            }
        });
    },
    
    getLocalStorage: function() {
        if (typeof chrome !== 'undefined' && chrome.runtime && chrome.runtime.id) {
            if (chrome.tabs && chrome.tabs.create) {
                return window.localStorage;
            } else {
                return chrome.storage.local;
            }
        }
    },
    
    resetSystem: function() {
        GetShopTV.getLocalStorage().set({ "getshoptv_tv_id" : "" });
        chrome.runtime.reload();
    },
    
    connect: function() {
        $('.getshop_tv_id').fadeOut(function() {
            GetShopTV.getLocalStorage().get("getshoptv_tv_id", function(res) {
                GetShopTV.tvId = res.getshoptv_tv_id;
                if (GetShopTV.api.connectionEstablished) {
                    GetShopTV.connectionEstablished();
                } else {
                    GetShopTV.api.connect();    
                }
            });
            
            
        });
        
    },
    
    saveId: function() {
        GetShopTV.getLocalStorage().set({ "getshoptv_tv_id" : $('#getshop_tv_id_text').val() });
        GetShopTV.connect();
    },
    
    showAskForId: function(callback) {
        GetShopTV.getLocalStorage().get("getshoptv_tv_id", function (data) {
            if (!data.getshoptv_tv_id) {
                $('.getshop_tv_id').show();
                retsult = true;
                callback(true);
                return;
            }
            
            callback(false);
        });
        
    },
    
    connectionEstablished: function() {
        GetShopTV.api.InformationScreenManager.getScreen(GetShopTV.tvId).done(function(result) {
            if (result.length === 0) {
                GetShopTV.resetSystem();
            } else {
                GetShopTV.tvData = result;
                GetShopTV.initTv();
                GetShopTV.buildSliders();
            }
            
        });
    },
    
    initTv: function() {
        var url = "http://"+GetShopTV.address+"/displayImage.php?id=" + GetShopTV.tvData.backgroundImage;
        GetShopTV.loadImage(url, function(url) {
            $('.tvbody').css('background-image', 'url("'+url+'")');            
        });
        
        
        if (GetShopTV.tvData.showNewsFeed) {
            GetShopTV.showNewsFeed();
        }
    },
    
    showNewsFeed: function() {
        $('.newsstripe').fadeIn();
        $('.newsstripe .nyheterinner').html("");
        
        if (GetShopTV.newsAnimation) {
            clearTimeout(GetShopTV.newsAnimation);
        }
        
        GetShopTV.api.InformationScreenManager.getNews().done(function(feed) { 
            console.log(feed);
            var date = feed.pubDate.substring(5);
            date = date.substring(0, date.length - 6);
            $('.newsstripe .newstitle').html("Nyheter - " + date);
            var i = 0;

            $(feed.entries).each(function() {
                if (i > 10) {
                    return;
                }
                
                var prefix = i == 0 ? "" : "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
                $('.newsstripe .nyheterinner').append(prefix + this.title);
                $('.newsstripe .nyheterinner').append(" - " + this.description);
                i++;
            })
        });
        
        GetShopTV.animateNewsFeed();
        var oneHourInMs = 60*60*1000;
        setTimeout(GetShopTV.showNewsFeed, oneHourInMs);
    },
    
    animateNewsFeed: function() {
        var fps = 60;
        var currentLeft = $('.nyheterinner').position().left;
        var newLeft = currentLeft - 2;
        
        if ((newLeft*-1) > $('.nyheterinner').width()) {
            newLeft = 0;
            $('.nyheterinner').css('left', newLeft + "px");
            GetShopTV.newsAnimation = setTimeout(GetShopTV.animateNewsFeed, 4000);
        } else {
            $('.nyheterinner').css('left', newLeft + "px");
            GetShopTV.newsAnimation = setTimeout(GetShopTV.animateNewsFeed, 1000/fps);
        }
        
    },
    
    showWarning: function(warning) {
        alert(warning);
    },
    
    buildSliders: function() {
        var data = GetShopTV.tvData;
        if (!data.sliders) {
            GetShopTV.showWarning('No sliders added, please login and add one or more sliders');
            return;
        } 
        
        var i = 1;
        for (var sliderId in data.sliders) {
            var slider = data.sliders[sliderId];
            GetShopTV.createSlide(slider, i);
            i++;
        }
        
        GetShopTV.next();
    },
    
    next: function() {
        $('.animationslide').hide();
        if (GetShopTV.i == GetShopTV.sliders.length) {
            GetShopTV.i = 0;
        }
        
        var slideData = GetShopTV.sliders[GetShopTV.i];
        if (slideData.sliderType === "type1") 
            slider.type1.reset(slideData.gs_slider_name);
        
        if (slideData.sliderType === "type2") 
            slider.type2.reset(slideData.gs_slider_name);
        
        GetShopTV.i++;
    },
    
    createSlide: function(slideData, i) {
        if (slideData.sliderType === "type1") {
            slideData.gs_slider_name = slider.type1.createSlide(slideData, i);
            GetShopTV.sliders.push(slideData);    
        }
        
        if (slideData.sliderType === "type2") {
            slideData.gs_slider_name = slider.type2.createSlide(slideData, i);
            GetShopTV.sliders.push(slideData);    
        }
    }
};

$(document).ready(function() {
    GetShopTV.init();
});