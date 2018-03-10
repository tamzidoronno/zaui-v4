app.PmsChartOverview = {
    loadview : function(field) {
            var event = thundashop.Ajax.createEvent('','lazyLoadOverviewData',$('.PmsChartOverview'), {
                "view" : field
            });
            if(field === "Arrivals") {
                //first one
            } else if(field === "DEPARTURES" && viewloaded["Arrivals"] !== "loaded") {
                setTimeout(function() {
                    app.PmsChartOverview.loadview(field);
                }, "50");
                return;
            } else if(field === "NEW_BOOKINGS" && viewloaded["DEPARTURES"] !== "loaded") {
                setTimeout(function() {
                    app.PmsChartOverview.loadview(field);
                }, "50");
                return;
            } else if(field === "CLEANING" && viewloaded["NEW_BOOKINGS"] !== "loaded") {
                setTimeout(function() {
                    app.PmsChartOverview.loadview(field);
                }, "50");
                return;
            } else if(field === "GUEST_COMMENTS" && viewloaded["CLEANING"] !== "loaded") {
                setTimeout(function() {
                    app.PmsChartOverview.loadview(field);
                }, "50");
                return;
            } else if(field === "ECONOMY" && viewloaded["GUEST_COMMENTS"] !== "loaded") {
                setTimeout(function() {
                    app.PmsChartOverview.loadview(field);
                }, "50");
                return;
            } else if(field === "Coverage" && viewloaded["ECONOMY"] !== "loaded") {
                setTimeout(function() {
                    app.PmsChartOverview.loadview(field);
                }, "50");
                return;
            } else if(field === "Janitor" && viewloaded["Coverage"] !== "loaded") {
                setTimeout(function() {
                    app.PmsChartOverview.loadview(field);
                }, "50");
                return;
            }

            if(!$('.PmsChartOverview .' + field).is(':visible')) {
                return;
            }

            thundashop.Ajax.postWithCallBack(event, function(res) {
                viewloaded[field] = "loaded";
                var json = JSON.parse(res);

                var timerCounter = 0;
                var countertoday = 1;
                var todayContainer = $('.PmsChartOverview .' + field).find('.counter1 .boldFont');
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
                var tomorrowContainer = $('.PmsChartOverview .'+field).find('.counter2 .boldFont');
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
}