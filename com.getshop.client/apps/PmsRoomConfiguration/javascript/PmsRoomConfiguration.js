app.PmsRoomConfiguration = {
    init : function() {
        $(document).on('click', '.PmsRoomConfiguration .createnewaccessory', app.PmsRoomConfiguration.createAccessory);
        $(document).on('click', '.PmsRoomConfiguration .deleteimage', app.PmsRoomConfiguration.deleteimage);
        $(document).on('click', '.PmsRoomConfiguration .uploadTypeImage', app.PmsRoomConfiguration.uploadBoxClick);
        $(document).on('click', '.PmsRoomConfiguration .setdefaultimg', app.PmsRoomConfiguration.selectDefaultImage);
        $(document).on('click', '.PmsRoomConfiguration .changeicon', app.PmsRoomConfiguration.changeIcon);
        $(document).on('click', '.PmsRoomConfiguration .opentypesorting', app.PmsRoomConfiguration.opentypesorting);
        $(document).on('click', '.PmsRoomConfiguration .opentypesortingavailability', app.PmsRoomConfiguration.opentypesortingavailability);
        $(document).on('click', '.PmsRoomConfiguration .openitemsorting', app.PmsRoomConfiguration.openitemsorting);
        $(document).on('click', '.PmsRoomConfiguration .savetypesorting', app.PmsRoomConfiguration.savetypesorting);
        $(document).on('click', '.PmsRoomConfiguration .saveitemsorting', app.PmsRoomConfiguration.saveitemsorting);
        $(document).on('click', '.PmsRoomConfiguration .createType', app.PmsRoomConfiguration.createType);
        $(document).on('click', '.PmsRoomConfiguration .createRoom', app.PmsRoomConfiguration.createRoom);
     },
    opentypesorting : function() {
        var event = thundashop.Ajax.createEvent('','loadSortingTypes', $(this), {});
        getshop.pms.showInformationBox(event, "Sorting");
    },
    opentypesortingavailability : function() {
        var event = thundashop.Ajax.createEvent('','loadSortingTypesAvailability', $(this), {});
        getshop.pms.showInformationBox(event, "Sorting");
    },
    openitemsorting : function() {
        var event = thundashop.Ajax.createEvent('','loadSortingItems', $(this), {});
        getshop.pms.showInformationBox(event, "Sorting");
    },
    saveitemsorting: function() {
        var counter = 0;
        var result = [];
        $('#sortable li').each(function() {
            result.push($(this).attr('itemid'));
            counter++;
        });
        var data = {
            "sortlist" : result
        }
        thundashop.Ajax.simplePost($(this), 'setNewSorting', data);
        getshop.pms.hideInformationBox();
    },
    
    savetypesorting: function() {
        var counter = 0;
        var result = [];
        $('#sortable li').each(function() {
            result.push($(this).attr('typeid'));
            counter++;
        });
        var data = {
            "sortlist" : result
        }
        
        data.sortingtype = "booking";
        if($(this).attr('sortingtype') && $(this).attr('sortingtype') == "availability") {
            data.sortingtype = "availability";
        }
        
        thundashop.Ajax.simplePost($(this), 'setNewTypeSorting', data);
        getshop.pms.hideInformationBox();
    },
    
    
    changeIcon : function() {
        var newIcon = window.prompt("Enter name on icon (example: bed for fa-bed)");
        if(!newIcon) {
            return;
        }
        var event = thundashop.Ajax.createEvent(null, 'changeIcon', $(this), {
            "id" : $(this).attr('data-id'),
            "icon" : newIcon
        });
        var icon = $(this);
        thundashop.Ajax.postWithCallBack(event, function() {
            icon.addClass('fa-'+newIcon);
        });
    },
    uploadBoxClick: function () {
        $('#getshop_select_files_link').remove();
        $('#your-files').remove();
        var button = $(this);
        var curText = button.html();
        app.PmsRoomConfiguration.currentTypeId = "";
        var selectDialogueLink = $('<a href="" id="getshop_select_files_link">Select files</a>');
        var fileSelector = $('<input type="file" id="your-files" multiple/>');

        selectDialogueLink.click(function () {
            fileSelector.click();
        });
        $('body').append(fileSelector);
        $('body').append(selectDialogueLink);

        var control = document.getElementById("your-files");
        var me = this;
        typeIdToUploadTo = $(this).attr('typeid');
        control.addEventListener("change", function () {
            button.html('<i class="fa fa-spin fa-spinner"></i>');
            fileSelector.remove();
            app.PmsRoomConfiguration.imageSelected(control.files, button.closest('.app'));
        });

        selectDialogueLink.click();
        selectDialogueLink.remove();
    },
    imageSelected: function (files, application) {
        var file = files[0];
        var fileName = file.name;

        var reader = new FileReader();

        reader.onload = function (event) {
            var dataUri = event.target.result;
            var data = {
                typeId: typeIdToUploadTo,
                fileBase64: dataUri,
                fileName: fileName
            };

            var event = thundashop.Ajax.createEvent('','saveTypeImage', application, data);

            thundashop.Ajax.postWithCallBack(event, function(res) {
                $('.imagearea').html(res);
            });
        };

        reader.onerror = function (event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
    },
    deleteimage : function() {
        var event = thundashop.Ajax.createEvent('','deleteImage',$(this), {
            "id" : $(this).attr('data-id'),
            "typeid" : $(this).attr('data-typeid')
        });
        var imgContainer = $(this).closest('.imagecontainer');
        thundashop.Ajax.postWithCallBack(event, function() {
            imgContainer.fadeOut();
        });
    },
    selectDefaultImage : function() {
        var event = thundashop.Ajax.createEvent('','setDefaultImage',$(this), {
            "fileid" : $(this).attr('data-fileid'),
            "typeId" : $(this).attr('data-typeid')
        });
        var imgContainer = $(this).closest('.imagecontainer');
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.imagearea').html(res);
        });
    },
    createAccessory : function() {
        var accessoryName = window.prompt("Name of accessory");
        if(!accessoryName) {
            return;
        }
        
        var event = thundashop.Ajax.createEvent('', 'createAccesory', $(this), {
            "name" : accessoryName
        });
        
        thundashop.Ajax.post(event);
    },
    createType : function() {
        var name = $('[gsname="name"]').val();
        if(name===''){
            alert('Please input a room type');
        }
        else {
            var event = thundashop.Ajax.createEvent('','createType',$(this), {
                "name" : $('[gsname="name"]').val()
            });
            thundashop.Ajax.post(event);
        }
        
    },
    createRoom : function() {
        var name = $('[gsname="roomname"]').val();
        var type = $('[gsname="type"]').val();
        console.log('name',name);
        console.log('type',type);
        if(name===''){
            alert('Please input a room name/number');
            return;
        }
        else {
            var event = thundashop.Ajax.createEvent('','createRoom',$(this), {
                "name" : $('[gsname="roomname"]').val(),
                "type" : $('[gsname="type"]').val()
            });
            thundashop.Ajax.post(event);
        }
        
    },
};
app.PmsRoomConfiguration.init();