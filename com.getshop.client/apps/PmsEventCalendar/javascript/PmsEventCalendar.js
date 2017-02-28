app.PmsEventCalendar = {
    init: function () {
        $(document).on('keyup', '.PmsEventCalendar .eventeditform input', app.PmsEventCalendar.updateFields);
        $(document).on('keyup', '.PmsEventCalendar .eventeditform select', app.PmsEventCalendar.updateFields);
        $(document).on('keyup', '.PmsEventCalendar .eventeditform textarea', app.PmsEventCalendar.updateFields);
        
        $(document).on('change', '.PmsEventCalendar .eventeditform input', app.PmsEventCalendar.updateFields);
        $(document).on('change', '.PmsEventCalendar .eventeditform select', app.PmsEventCalendar.updateFields);
        $(document).on('change', '.PmsEventCalendar .eventeditform textarea', app.PmsEventCalendar.updateFields);
        $(document).on('change', '.PmsManagement .addToEventList', app.PmsEventCalendar.addToEventList);
        $(document).on('change', '.PmsEventCalendar #file', app.PmsEventCalendar.imagePreview);
        $(document).on('change', '.PmsEventCalendar #logo', app.PmsEventCalendar.logoPreview);
        $(document).on('click', '.PmsEventCalendar .deleteevent', app.PmsEventCalendar.deleteevent);        
    },
    
    imagePreview : function(){
        
        $('#message').empty();
        var file = this.files[0];
        var imagefile = file.type;
        var match =["image/jpeg","image/png","image/jpg"];
        if(!((imagefile == match[0]) || (imagefile == match[1]) || (imagefile == match[2]))){
            $('#previewing').attr('src', 'noimage.png');
            $('#message').html("<p id='error'>Please Select A valid Image File!"+"<br><b>Note: </b> Only jpeg, jpg and png Images type allowed</p>");
            $('#labelFile').css("color", "red");
            return false;
        }
        else{
            var reader = new FileReader();
            reader.onload = app.PmsEventCalendar.imageIsLoaded;
            reader.readAsDataURL(this.files[0]);
        }
    
    },
    imageIsLoaded : function(e){
        var event = thundashop.Ajax.createEvent('','savePrimaryImage', $('#file'), {
            "data" : e.target.result,
        });
        
        thundashop.Ajax.postWithCallBack(event, function() {
            $('#labelFile').css("color", "green");
            $('#imagePreview').css("display","block");
            $('#previewing').attr('src', e.target.result);
            $('#previewing').attr('width', '100%');
            $('#previewing').attr('height', '175px');
        });
    },
    logoPreview : function(){
        $('#message').empty();
        var file = this.files[0];
        var logofile = file.type;
        var match =["image/jpeg","image/png","image/jpg"];
        if(!((logofile == match[0]) || (logofile == match[1]) || (logofile == match[2]))){
            $('#previewingLogo').attr('src', 'noimage.png');
            $('#message').html("<p id='error'>Please Select A valid Image File!"+"<br><b>Note: </b> Only jpeg, jpg and png Images type allowed</p>");
            $('#labelLogo').css("color", "red");
            return false;
        }
        else{
            var reader = new FileReader();
            reader.onload = app.PmsEventCalendar.logoIsLoaded;
            reader.readAsDataURL(this.files[0]);
        }
    },
    logoIsLoaded : function(e){
      var event = thundashop.Ajax.createEvent('','savePrimaryLogo', $('#logo'), {
          "data" : e.target.result,
      });
      
      thundashop.Ajax.postWithCallBack(event, function(){
          $('#labelLogo').css("color", "green");
          $('#logoPreview').css("display", "block");
          $('#previewingLogo').attr('src', e.target.result);
          $('#previewingLogo').attr('width', '100%');
          $('#previewingLogo').attr('height', '175px');
      });
    },
    deleteevent : function() {
        var dodelete = confirm("Are you sure you want to delete this event?");
        if(dodelete) {
            thundashop.Ajax.simplePost($(this), 'removeEntry', {
                "id" : $(this).attr('eventid')
            });
        }
    },
    addToEventList : function() {
        var instanceid = $(this).attr('instanceid');
        var data = {
            id : $(this).attr('id'),
            checked : $(this).is(":checked")
        }
        var event = thundashop.Ajax.createEvent('','checkEntry',instanceid, data);
        thundashop.Ajax.postWithCallBack(event, function() {});
    },
    updateFields : function() {
        var form = $('.PmsEventCalendar .eventeditform');

        if(typeof(saveEventBookingData) === "number") {
            clearTimeout(saveEventBookingData);
        }
        
        saveEventBookingData = setTimeout(function() {
            var data = thundashop.framework.createGsArgs(form);
            var event = thundashop.Ajax.createEvent('','savePostedForm',form, data);
            thundashop.Ajax.postWithCallBack(event, function() {});
        }, "1000");
    },
    showSettings : function() {
        var event = thundashop.Ajax.createEvent('','showSettings',$(this), {});
        thundashop.common.showInformationBoxNew(event,'Pms form settings');
    },
    loadSettings : function(element, application) {
         var config = {
            draggable: true,
            app : true,
            application: application,
            title: "Settings",
            items: [
                {
                    icontype: "awesome",
                    icon: "fa-edit",
                    iconsize : "30",
                    title: __f("Add / Remove products to this list"),
                    click: app.PmsEventCalendar.showSettings
                }
            ]
        };

        var toolbox = new GetShopToolbox(config, application);
        toolbox.show();
        toolbox.attachToElement(application, 2);
    }
};
app.PmsEventCalendar.init();