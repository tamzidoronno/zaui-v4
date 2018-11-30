app.SupportDashBoard = {
    init : function() {
        $(document).on('click','.SupportDashBoard .requestbutton', function() {
            $('#requestform').fadeIn();
            $('#requesttitle').focus();
            $('.contentdescription').hide();
            $('.contentdescription[titletype="'+$(this).attr('requesttype')+'"]').show();
            $('#requesttype').val($(this).attr('requesttype'));
        });
    },
    
    loadview : function(field) {
            var event = thundashop.Ajax.createEvent('','lazyLoadOverviewData',$('.SupportDashBoard'), {
                "view" : field
            });
            if(field === "Bugs") {
                //first one
            } else if(field === "Questions" && viewloaded["Bugs"] !== "loaded") {
                setTimeout(function() {
                    app.SupportDashBoard.loadview(field);
                }, "50");
                return;
            } else if(field === "Features" && viewloaded["Questions"] !== "loaded") {
                setTimeout(function() {
                    app.SupportDashBoard.loadview(field);
                }, "50");
                return;
            }

            if(!$('.SupportDashBoard .' + field).is(':visible')) {
                return;
            }

            thundashop.Ajax.postWithCallBack(event, function(res) {
                viewloaded[field] = "loaded";
                if(res) {
                    var json = JSON.parse(res);
                } else {
                    var json = {};
                }

                var timerCounter = 0;
                var countertoday = 1;
                var todayContainer = $('.SupportDashBoard .' + field).find('.counter1 .boldFont');
                for(var i = 0; i <= json.today;) {
                    setTimeout(function() {
                        if(json.today > 1000) {
                            countertoday += 199;
                        } else if(json.today > 10000) {
                            countertoday += 1999;
                        } else if(json.today > 100000) {
                            countertoday += 19999;
                        } else {
                            countertoday++
                        }

                        if(countertoday > json.today) {
                            countertoday = json.today;
                        }

                        todayContainer.html(countertoday);

                    }, (20*timerCounter));
                    timerCounter++;
                    if(json.today > 1000) {
                        i += 199;
                    } else if(json.today > 10000) {
                        i += 1999;
                    } else if(json.today > 100000) {
                        i += 19999;
                    } else {
                        i++
                    }
                }

                var timerCounter = 0;
                var counterTomorrow = 1;
                var tomorrowContainer = $('.SupportDashBoard .'+field).find('.counter2 .boldFont');
                for(var i = 0; i <= json.tomorrow;) {
                    setTimeout(function() {
                        if(json.tomorrow > 1000) {
                            counterTomorrow += 199;
                        } else if(json.tomorrow > 10000) {
                            counterTomorrow += 1999;
                        } else if(json.tomorrow > 100000) {
                            counterTomorrow += 19999;
                        } else {
                            counterTomorrow++
                        }

                        if(counterTomorrow > json.tomorrow) {
                            counterTomorrow = json.tomorrow;
                        }

                        tomorrowContainer.html(counterTomorrow);


                    }, (20*timerCounter));
                    timerCounter++;
                    if(json.tomorrow > 1000) {
                        i += 199;
                    } else if(json.tomorrow > 10000) {
                        i += 1999;
                    } else if(json.tomorrow > 100000) {
                        i += 19999;
                    } else {
                        i++
                    }
                }
            });
        }
};
app.SupportDashBoard.init();