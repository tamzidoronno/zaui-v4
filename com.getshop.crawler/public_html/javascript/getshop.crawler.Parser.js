getshop = {}
getshop.crawler = {};

getshop.crawler.Parser = {
    init: function() {
        $('#fileloader').on('change', function(e) {
            var reader = new FileReader();

            reader.onload = function(e) {
                var div = $('<div/>');
                div.html(reader.result);
                getshop.crawler.Parser.parse(div);
            }
            
            reader.readAsText(this.files[0]);
        });
        this.startup();
    },
    
    startup: function() {
        this.getshopApi = new GetShopApiWebSocket("20163.getshop.com");
        this.getshopApi.connect();
        this.getshopApi.connectedCallback = $.proxy(this.connected, this);
        
    },
    
    connected: function() {
        this.getshopApi.UserManager.logOn("getshop@tenketing.no", "abcd1234aaa").done(function() {
            console.log('Logged in');
        });
    },
    
    parse: function(page) {
        page.find('.post').each(function() {
            var title = $(this).find('h3').text();
            var images = $(this).find('img').remove();
            var desc = $(this).find('div').html();
            
            desc = $(this).find('div').html();
            var imageDiv = $('<div/>');
            $(images).each(function() {
                imageDiv.append($(this).attr('src')+"<br/>");
            });
            
            var price = "";
            $(this).find('span').each(function() {
                var text = $(this).text();
                if (text.toLowerCase().indexOf("pris") > -1) {
                    price = $(this).text().match(/\d+/)[0];
                }
            });
            
            var product = {
                id: guid(),
                name: title,
                shortDescription: desc,
                price: price
            }
            
            var imgArray = [];
            $(images).each(function() {
                var id = guid();
                console.log("wget -O " + id + " " + $(this).attr('src'));
                imgArray.push(id);
            });
            
            product.imagesAdded = imgArray;
            
            getshop.crawler.Parser.getshopApi.ProductManager.saveProduct(product);
            
            $('.pageresult').append("<div style='border: solid 1px #DDD; padding: 10px; margin-bottom: 10px;'> Title: " + title + "</div>");
            $('.pageresult').append("<div style='border: solid 1px #DDD; padding: 10px; margin-bottom: 10px;'> Pris: " + price + "</div>");
            $('.pageresult').append("<div style='border: solid 1px #DDD; padding: 10px; margin-bottom: 10px;'> "+ imageDiv.html() +" </div>");
            $('.pageresult').append("<div style='border: solid 1px #DDD; padding: 10px; margin-bottom: 10px;'> " + desc + "</div>");
        });
    }
};

var guid = (function() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
               .toString(16)
               .substring(1);
  }
  return function() {
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
           s4() + '-' + s4() + s4() + s4();
  };
})();


$(document).ready(function() {
    getshop.crawler.Parser.init();
});