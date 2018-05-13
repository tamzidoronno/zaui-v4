app.PmsNewBooking = {
    init : function() {
        $(document).on('change', '.PmsNewBooking select[gsname="type"]', function() {
            $('.PmsNewBooking [gsname="numberOfRooms"]').val(0);
        });
        
        $(document).on('keyup', '.PmsNewBooking [gsname="numberOfRooms"]', function() {
            $('.moreThenAvailableWarning').hide();
            var number = $(this).val();
            var type = $('select[gsname="type"]').val();
            var available = parseInt($('.availableroomscounter[roomtype="'+type+'"]').text());
            var typeName = $('.availabletypename[roomtype="'+type+'"]').text();
            if(number > available) {
                $(this).addClass('moreThenAvailable');
                $('.moreThenAvailableWarning').find('.maxrooms').html(available);
                $('.moreThenAvailableWarning').find('.numberofrooms').html(number);
                $('.moreThenAvailableWarning').find('.typename').html(typeName);
                $('.moreThenAvailableWarning').find('.roomsdiff').html((number-available));
                $('.moreThenAvailableWarning').fadeIn();
            }
        });
        $(document).on('keyup','.PmsNewBooking [gsname="nameofholder"]',app.PmsNewBooking.checkForExisting);
        $(document).on('click','.PmsNewBooking .selectuser',app.PmsNewBooking.selectUser);
        $(document).on('click','.PmsNewBooking .selecteduser .fa-trash-o',app.PmsNewBooking.removeSelectedUser);
        $(document).on('keyup','.PmsNewBooking .roomtypecount',app.PmsNewBooking.countRoomsToAdd);
        $(document).on('keyup','.PmsNewBooking .discountcode',app.PmsNewBooking.setDiscountCode);
        $(document).on('click','.PmsNewBooking .totalcount',app.PmsNewBooking.selectAllRooms);
        $(document).on('click','.PmsNewBooking .newcustomertypebutton', app.PmsNewBooking.loadNewCustomerType);
        $(document).on('click','.PmsNewBooking .newcustomerbutton', app.PmsNewBooking.loadNewCustomerField);
        $(document).on('click','.PmsNewBooking .searchbrregbutton', app.PmsNewBooking.showBrregSearch);
        $(document).on('click','.PmsNewBooking .brregsearchresultrow', app.PmsNewBooking.selectBrregResult);
    },
    setDiscountCode : function() {
        if(typeof(waitToInsertCode) !== "undefined") {
            console.log('clearing');
            clearTimeout(waitToInsertCode);
        }
        
        
        var input = $(this);
        waitToInsertCode = setTimeout(function() {
            var event = thundashop.Ajax.createEvent('','setDiscountCode', input, {
                "code" : input.val()
            });
            thundashop.Ajax.postWithCallBack(event, function(res) {
                console.log(res);
                if(res == "yes") {
                    input.css('border-color','green');
                } else {
                    input.css('border-color','none');
                }
            });
        }, "500");
    },
    existingSearchResult : function(res) {
        $('.existingsearchcustomerresult').html(res);
    },
    selectBrregResult : function() {
        var name = $(this).attr('name');
        var vatnumber = $(this).attr('vatnumber');
        $('[gsname="vatnumber"]').val(vatnumber);
        $('[gsname="name"]').val(name);
        $('.searchbrregarea').hide();
    },
    searchResult : function(res) {
        if(res) {
            $('.brregsearchresult').html('');
            for(var k in res) {
                var company = res[k];
                var row = $('<div class="brregsearchresultrow" vatnumber="'+company.vatNumber+'" name="'+company.name+'"><span>' + company.vatNumber + "</span> - <span>" + company.name + "</span></div>");
                $('.brregsearchresult').append(row);
            }
        }
    },
    showBrregSearch : function() {
        $('.searchbrregarea').slideDown();
    },    
    loadNewCustomerField : function() {
        $('.nextstep').hide();
        $('.companytypeselection').slideDown();
    },
    loadNewCustomerType : function() {
        $('.selectedbutton').removeClass('selectedbutton');
        $(this).addClass('selectedbutton');
        $('.nextstep').hide();
        var type = $(this).attr('type');
        $('.'+type).show();
    },
    
    selectAllRooms : function() {
        $('.roomstoaddrow').each(function() {
            var count = $(this).find('.roomsleftfield').attr('original');
            $(this).find('.roomtypecount').val(count);
        });
        app.PmsNewBooking.countRoomsToAdd();
    },
    countRoomsToAdd : function() {
        var total = 0;
        $('.roomstoaddrow').each(function() {
            var count = 0;
            var row = $(this);
            var left = parseInt(row.find('.roomsleftfield').attr('original'));
            if(row.find('.roomtypecount').val()) {
                count = parseInt(row.find('.roomtypecount').val());
                total += count;
            }
            left = left - count;
            row.find('.roomsleftfield').text(left);
        });
        $('.totalcount').html(total);
    },
    removeSelectedUser : function() {
        $('.PmsNewBooking [gsname="nameofholder"]').val("");
        $('.PmsNewBooking [gsname="nameofholder"]').show();
        $('.PmsNewBooking .selecteduser').hide();
    },
    
    selectUser : function() {
        var userid = $(this).closest('tr').attr('userid');
        $('.PmsNewBooking [gsname="nameofholder"]').val(userid);
        $('.PmsNewBooking [gsname="nameofholder"]').hide();
        $('.PmsNewBooking .selecteduser').show();
        $('.PmsNewBooking .selecteduser').find('.name').html($(this).closest('tr').attr('name'));
    },
    
    checkForExisting : function() {
        var text = $(this).val();
        if(text.length < 3) {
            return;
        }
        
        var event = thundashop.Ajax.createEvent('','checkForExisiting',$(this), {
            "text" : text
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.PmsNewBooking .alreadyexists').html(res);
        });
    },
    reloadAvailability : function() {
        var event = thundashop.Ajax.createEvent('','reloadAvailability',$('.PmsNewBooking'), {
            "start" : $('#startdate').val(),
            "end" : $('#enddate').val()
        });
        thundashop.Ajax.postWithCallBack(event, function(res) {
            $('.availablerooms').html(res);
            $('.availableroomscounter').each(function() {
                var roomtype = $(this).attr('roomtype');
                var count = $(this).text();
                $('.roomstoaddrow[roomtype="'+roomtype+'"]').find('.roomsleftfield').attr('original', count);
                $('.roomstoaddrow[roomtype="'+roomtype+'"]').find('.roomsleftfield').text(count);
            })
        });
    }
};

app.PmsNewBooking.init();