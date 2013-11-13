/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

App = {
    showingYear : 0,
    showingMonth : 0,
    startMonth : 0,
    startYear : 0,
    reloadData : null,
    monthIndex : 1,
    
    start: function() {
        this.getshopApi = new GetShopApiWebSocket("www.autoakademiet.no");
        this.getshopApi.connect();
        this.getshopApi.transferStarted = $.proxy(this.transferStarted, this);
        this.getshopApi.transferCompleted = $.proxy(this.transferCompleted, this);
        this.getshopApi.disconnectedCallback = $.proxy(this.isDisconnected, this);
        this.getshopApi.connectedCallback = $.proxy(this.onData, this);
        this.setupListeners();
    },   
            
    isDisconnected: function() {
        $('.disconnected').show();
    },

    onData: function() {
        $('.disconnected').hide();
        this.reset();
        this.loadCourses();
        this.loadInformationPage();
        this.loadFilters();
        this.loadCoursePages();
        this.createCalendars();
        this.setupSignupPage();
        this.bindRefreshEvent();
    },
            
    replaceAll: function (o,t,r,c){if(c==1){cs="g"}else{cs="gi"}var mp=new RegExp(t,cs);ns=o.replace(mp,r);return ns},
            
    loadInformationPage: function() {
        var me = this;
        this.getshopApi.PageManager.getPage("f1d04c8a-222c-4e3d-a5cb-32e7f5f9d6f1").done(function(page) {
            var contentManagerId = page['pageAreas']['main_1']['applicationsList'][0];
            me.getshopApi.ContentManager.getContent(contentManagerId).done(function(content) {
                var page = $('#infopage');
                if (page.length === 0) {
                    page = $('<div class="infopage" data-role="page" data-theme="a" id="infopage"/>');
                    page.html("<div class='header'/>");
                    page.find(".header").load("header.html", function() {
                        page.find('.header').trigger('create');
                    });
                    page.append('<div data-role="content" data-theme="a" class="ContentManager">');
                    $('html .ui-mobile-viewport').append(page);    
                }
                
                content = me.replaceAll(content, '/displayImage', "http://www.getshop.com/displayImage");
                var contentHtml = $(content);
                contentHtml.find('img').css('height','auto');
                contentHtml.find('img').css('width','100%');
                contentHtml.find('td span').css('font-size','8px');
                page.find('.ContentManager').html(contentHtml);
                
            });
        });
    },
            
    refresh: function() {
//        this.isLoaded = false;
        this.getshopApi.socket.close();
    },
            
    bindRefreshEvent: function() {
        $('.refresh').click($.proxy(this.refresh, this));
    },
            
    reset : function() {
        $('.connecting').hide();
        this.monthIndex = 1;
        this.activateFilter("");
        $.mobile.changePage('#home');
    },
            
    transferCompleted: function() {
        $('.loading').hide();
    },
    
    transferStarted: function() {
        $('.loading').show();
    },
            
    pad: function (num, size) {
        var s = "000000000" + num;
        return s.substr(s.length-size);
    },
    shuffle: function (o){ 
        for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
        return o;
    },
    setupSignupPage: function() {
        var me = this;
        var day = parseInt(Date.today().toString('dd'));
        var month = parseInt(Date.today().toString('MM'));
        var year = parseInt(Date.today().toString('yyyy'));
        var course = $('<option></option>');
        var empty = course.clone();
        empty.html("Trykk for å velge");
        empty.attr('value', '');
        empty.attr('selected', 'true');
        
        $('#select-native-1').append(empty.clone());
        $('#select-native-2').append(empty.clone());
        
        this.getshopApi.CalendarManager.getEntries(year, month, day, null).done(function(entries) {
            $('#select-native-1').html("");
            $(entries).each(function() {
                var availablePositions = this.maxAttendees - this.attendees.length;
                course = course.clone();
                course.attr('value', this.entryId);
                course.attr('location', this.location);
                course.attr('availablepositions', availablePositions);
                course.html(me.pad(this.day, 2) + " / " + me.pad(this.month,2) + " - " + this.year + " : " +this.location + " - " + this.title);
                $('#select-native-1').append(course);
            });
            $('#select-native-1').selectmenu();
            $('#select-native-1').selectmenu('refresh', true);
            $('#select-native-1').on('change', function() {
                var positions = $(this).find(':selected').attr('availablepositions');
                $('.availablepositions_course_selected').hide();
                if (typeof(positions) !== "undefined") {
                    $('.availablepositions_course_selected').show();
                    $('.availablepositions_course_selected').html("Ledige plasser på valgt kurs: <b>" + positions + "</b>");
                }
            });
        });
        this.getshopApi.UserManager.getAllGroups().done(function(groups) {
            $('#select-native-2').html("");
            groups = me.shuffle(groups);
            $(groups).each(function() {
                var group = course.clone();
                group.attr('value', this.id);
                group.html(this.groupName);
                $('#select-native-2').append(group);
            });
            $('#select-native-2').selectmenu();
            $('#select-native-2').selectmenu('refresh', true);
        });
    },
            
    nextClicked: function() {
        setTimeout(function () { $('#next').removeClass('ui-btn-active'); }, 50);
        if (this.monthIndex === 3) {
            $( "#noCalenderEventForward" ).popup("open");
            return;
        }
        $('.calendar[year='+this.showingYear+'][month='+this.showingMonth+']').hide()
        if (this.showingMonth === 12) {
            this.showingMonth = 0;
            this.showingYear++;
        }

        this.showingMonth++;
        $('.calendar[year='+this.showingYear+'][month='+this.showingMonth+']').fadeIn(300);
        this.monthIndex++;
    },
            
    prevClicked: function() {
        setTimeout(function () { $('#prev').removeClass('ui-btn-active'); }, 50);
            
        if (this.showingMonth === this.startMonth && this.showingYear === this.startYear) {
            $( "#noCalenderEventBack" ).popup("open")
            return;
        }

        $('.calendar[year='+this.showingYear+'][month='+this.showingMonth+']').hide()
        if (this.showingMonth === 1) {
            this.showingMonth = 13;
            this.showingYear--;
        }

        this.showingMonth--;
        this.monthIndex--;
        var me = this;
        $('.calendar[year='+this.showingYear+'][month='+this.showingMonth+']').fadeIn(300);
    },
            
    signOnClicked: function() {
        var me = this;
        var name = $('#name').val();
        var email = $('#email').val();
        var invoiceemail = $('#invoiceemail').val();
        var phone = $('#phone').val();
        var vatnr = $('#vatnr').val();
        var company = $('#company').val();
        var courseId = $('#select-native-1').find(':selected').val();
        var groupId = $('#select-native-2').find(':selected').val()
        
        $('label').removeClass('error');
        
        if (name === "") 
            $('label[for=name]').addClass('error');
        
        if (email === "") 
            $('label[for=email]').addClass('error');
        
        if (phone === "") 
            $('label[for=phone]').addClass('error');
        
        if (vatnr === "") 
            $('label[for=vatnr]').addClass('error');
        
        if (courseId === "") 
            $('label[for=courseId]').addClass('error');
        
        if (groupId === "") 
            $('label[for=groupId]').addClass('error');
        
        if (courseId === "") 
            $('label[for=select-native-1]').addClass('error');
        
        if (groupId === "") 
            $('label[for=select-native-2]').addClass('error');
        
        var positions = $('#select-native-1').find(':selected').attr('availablepositions');
        
        if (positions === "0") {
            alert('Beklager, det er ingen ledige plasser på valgt kurs.');
            return;
        }
        
        if ($('label.error').length > 0) {
            alert('Vennligst rett feltene i rødt');
        } else {
            this.getshopApi.UtilManager.getCompanyFromBrReg(vatnr).done(function(company) {
                if(!company.name) {
                    alert('klarte ikke å finne firma med oppgitt org.nr, vennligst sjekk');
                    return;
                }
                
                var password = Math.floor(Math.random()*90000) + 10000;
                var user = {
                    fullName : name,
                    emailAddress : email,
                    type : 10,
                    password : password,
                    birthDay : vatnr,
                    cellPhone : phone, 
                    groups : [groupId],
                    company: company,
                    emailAddressToInvoice: invoiceemail
                };

                me.getshopApi.UserManager.createUser(user).done(function(createUser) {
                    me.getshopApi.CalendarManager.addUserToEvent(createUser.id, courseId, password, createUser.username).done(function() {
                        alert('Du er nå påmeldt kurset');
                        document.location.href = document.URL.substring(0, document.URL.indexOf("#"));
                    });
                });
            });
            
        }
    },
            
    setupListeners: function() {  
        $('#next').click($.proxy(this.nextClicked,this));
        $('#prev').click($.proxy(this.prevClicked,this));
        $(document).on('click', '.infobutton', function() {
            $.mobile.changePage('#infopage');
        });
        $('#signon').click($.proxy(this.signOnClicked,this));
        $('#vatnr').keyup($.proxy(this.vatnumberupdated,this))
    },
            
    vatnumberupdated: function() {
        var value = $('#vatnr').val();
        var informationPage = $('.companyinformation');
        if (value.length === 9) {
            var outer = $('<div/>');
            var loader = $('<img src="css/images/ajaxloader.gif"/><br>');
            outer.append(loader);
            outer.append('Laster data, vennligst vent');
            outer.css('text-align', 'center');
            informationPage.html(outer);
            var emptyRow = $('<div><div class="description"/><div class="value"/></div>');
            this.getshopApi.UtilManager.getCompanyFromBrReg(value).done(function(company) {
                informationPage.html("");
                
                if (!company.name) {
                    informationPage.html("Fant ingen treff på oppgitt org.nr, vennligst sjekk og prøv på nytt.");
                    return;
                }
                
                var row = emptyRow.clone();
                row.find('.description').html("Navn");
                row.find('.value').html(company.name);
                informationPage.append(row);
                
                var row = emptyRow.clone();
                row.find('.description').html("Addresse");
                row.find('.value').html(company.streetAddress);
                informationPage.append(row);
                
                var row = emptyRow.clone();
                row.find('.description').html("Post.nr");
                row.find('.value').html(company.postnumber);
                informationPage.append(row);
                
                var row = emptyRow.clone();
                row.find('.description').html("City");
                row.find('.value').html(company.city);
                informationPage.append(row);
            });
        } else {
            var invalidText = "Du har ikke oppgitt et gyldig org nr, det må være 9 tegn, du har oppgitt: " + value.length;
            informationPage.html(invalidText);
        }
    },
            
    createCalendars: function() {
        $('#calender').html("");
        var year = parseInt(Date.today().toString('yyyy'));
        var currentMonth = Date.today().getMonth()+1;
        for (var i=currentMonth; i<currentMonth+3; i++) {
            this.calendar = new App.Calendar(this.getshopApi, year, i);
        }

        $('.calendar[year='+year+'][month='+currentMonth+']').show();
        this.showingYear = year;
        this.showingMonth = currentMonth;
        this.startYear = year;
        this.startMonth = currentMonth;
    },
            
    loadCoursePages: function() {
        var me = this;

        this.getshopApi.ListManager.getList("71544ed8-406d-4496-baf2-b569975ebb20").done(function(entries) {
            $(entries).each(function() {
                $(this.subentries).each(function() {
                    var page = $('#'+this.pageId);
                    if (page.length === 0) {
                        page = $('<div class="coursepages_23459123948" data-role="page" data-theme="a" id="' + this.pageId + '"/>');
                        page.html("<div class='header'/>");
                        page.find(".header").load("header.html", function() {
                            page.find('.header').trigger('create');
                        });
                        $('html .ui-mobile-viewport').append(page);    
                    }
                    
                    

                    me.getshopApi.PageManager.getPage(this.pageId).done(function(dataPage) {

                        var contentHolder = page.find('.ContentManager');
                        if (contentHolder.length === 0 ) {
                            contentHolder = $('<div data-role="content" data-theme="a" class="ContentManager">');
                        }

                        for (id in dataPage.pageAreas.main_1.applications) {

                            var application = dataPage.pageAreas.main_1.applications[id];

                            if (application.appName === "ContentManager") {
                                me.getshopApi.ContentManager.getContent(application.id).done(function(content) {
                                    content = content.replace("/displayImage", "http://www.getshop.com/displayImage")
                                    var contentHtml = $(content);
                                    contentHtml.find('img').css('height','auto');
                                    contentHtml.find('img').css('width','100%');
                                    contentHolder.html(contentHtml);
                                });
                            }
                        }

                        page.append(contentHolder);
                    });
                });
            });
        });
    },
    
    loadFilters: function() {
        var me = this;
        this.getshopApi.CalendarManager.getFilters().done(function(filters) {
            var filterHolder = $('#filterholdergroup');
            filterHolder.html("");
            
            var filter = $("<a href='#' data-rel='back' data-role='button'>Fjern filter</a>");
            filter.click(function() { me.activateFilter("") });
            filterHolder.append(filter);
            $(filters).each(function() {
                var filter = $("<a href='#' data-rel='back' data-role='button'>" + this + "</a>");
                var filterName = this;
                filter.click(function() { me.activateFilter(filterName) });
                filterHolder.append(filter);
            });
            
            filterHolder.controlgroup();
            filterHolder.trigger('create');
        });
    },
    
    activateFilter: function(filter) {
        filter = filter.replace(/'/g, "\\'");
        
        if (filter === "" && !this.filterIsSet) {
            return;
        }
        
        $('td.date_has_event').removeClass('disabled');
        $('td.date_has_event').each(function() {
            if ($(this).attr('locations').toUpperCase().indexOf(filter.toUpperCase()) < 0) {
                $(this).addClass('disabled');
            }
        });
        if (filter === "") {
            $('.displayActiveFilter').hide();
        } else {
            $('.displayActiveFilter').fadeIn(500);
            $('#displayActiveFilter').html(filter);
        }

        //Hide select options
        if (!this.options && filter !== "") {
            this.options = $('#select-native-1').find('option');
        }
        
        var selectValue = $('<div/>').html(this.options).find('option[value=]');
        $('#select-native-1').html("");
        $('#select-native-1').append(selectValue.clone());
        $(this.options).each(function() {
            
            if ($(this).attr('location') && $(this).attr('location').toUpperCase() === filter.toUpperCase() || filter === "") {
                $(this).removeAttr('selected');
                $('#select-native-1').append(this);
            }
        });
        
        $('#select-native-1').selectmenu();
        $('#select-native-1').selectmenu('refresh', true);
        if (filter !== "") {
            this.filterIsSet = true;
        } else {
            this.filterIsSet = false;
        }
    },
    loadCourses: function() {
        var topEntry = $('<div data-role="collapsible" data-theme="a" data-content-theme="a"  data-inset="false"/>');
        var subEntryContainer = $('<ul data-role="listview"/>');
        var courselist = $('#courselist .contentcourselist');
        
        courselist.html("");
        this.getshopApi.ListManager.getList("71544ed8-406d-4496-baf2-b569975ebb20").done(function(list) {
            $(list).each(function() {
                var topEntryCloned = topEntry.clone();
                topEntryCloned.html("<h4>" + this.name + "</h4>");
                courselist.append(topEntryCloned);
                var subentriesClonedTop = subEntryContainer.clone();
                $(this.subentries).each(function() {
                    var subEntry = $('<li><a href="#' + this.pageId + '">' + this.name + '</a></li>');
                    subentriesClonedTop.append(subEntry);
                });
                topEntryCloned.append(subentriesClonedTop);  
            });
            
            $('#courses').page();
            $('#courses').trigger('create');
        });
    }
};

App.Calendar = function(getshopApi, year, month) {
    this.getshopApi = getshopApi;
    this.year = year;
    this.month = month;
    if (this.month > 12) {
        this.month = this.month-12;
        this.year++;
    }
    this.init();
};

App.Calendar.prototype = {
    prePend: function(date, tr, emptytd) {
        var prependDays = 0;
        prependDays = date.getDayName() === "Saturday" ? 5 : prependDays;
        prependDays = date.getDayName() === "Sunday" ? 6 : prependDays;
        prependDays = date.getDayName() === "Monday" ? 0 : prependDays;
        prependDays = date.getDayName() === "Tuesday" ? 1 : prependDays;
        prependDays = date.getDayName() === "Wednesday" ? 2 : prependDays;
        prependDays = date.getDayName() === "Thursday" ? 3 : prependDays;
        prependDays = date.getDayName() === "Friday" ? 4 : prependDays;
        for (var j = 0; j < prependDays; j++) {
            tr.append(emptytd.clone());
        }
    },
    postPend: function(date, tr, emptytd) {
        var prependDays = 0;
        prependDays = date.getDayName() === "Monday" ? 6 : prependDays;
        prependDays = date.getDayName() === "Tuesday" ? 5 : prependDays;
        prependDays = date.getDayName() === "Wednesday" ? 4 : prependDays;
        prependDays = date.getDayName() === "Thursday" ? 3 : prependDays;
        prependDays = date.getDayName() === "Friday" ? 2 : prependDays;
        prependDays = date.getDayName() === "Saturday" ? 1 : prependDays;
        prependDays = date.getDayName() === "Sunday" ? 0 : prependDays;
        for (var j = 0; j < prependDays; j++) {
            tr.append(emptytd.clone());

        }
    },
            
    getMonthName: function() {
        var name = "";
        name = this.month === 1 ? "Januar" : name;
        name = this.month === 2 ? "Februar" : name;
        name = this.month === 3 ? "Mars" : name;
        name = this.month === 4 ? "April" : name;
        name = this.month === 5 ? "Mai" : name;
        name = this.month === 6 ? "Juni" : name;
        name = this.month === 7 ? "Juli" : name;
        name = this.month === 8 ? "August" : name;
        name = this.month === 9 ? "September" : name;
        name = this.month === 10 ? "Oktober" : name;
        name = this.month === 11 ? "November" : name;
        name = this.month === 12 ? "Desember" : name;
        return name;
    },
            
    createTable: function() {
        var outer = $("<div class='calendar'>"+this.getMonthName()+" - " + this.year +"</div>");
        outer.attr('year', this.year);
        outer.attr('month', this.month);
        outer.hide();
        var table = $('<table cellspacing="0"><thead><tr><th>Man</th><th>Tir</th><th>Ons</th><th>Tor</th><th>Fre</th><th>Lør</th><th>Søn</th></tr></thead></table>');
        outer.append(table);
        
        var date = new Date(this.year, this.month-1);
        var daysInMonth = date.getDaysInMonth();
        date.moveToFirstDayOfMonth();
        var tr = $('<tr/>');
        table.append(tr);
        var emptytd = $('<td class="calendar-day-np">&nbsp;</td>');
        for (var i = 1; i <= daysInMonth; i++) {
            var tableCell = $('<td >' + i + '</td>');
            
            if (i === 1) {
                this.prePend(date, tr, emptytd);
            }
            
            tableCell.attr('year', this.year);
            tableCell.attr('month', this.month-1);
            tableCell.attr('day', i);
            
            tr.append(tableCell);

            if (date.getDayName() === "Sunday") {
                tr = tr.clone();
                tr.html("");
                table.append(tr);
            }

            if (daysInMonth === i) {
                this.postPend(date, tr, emptytd);
            }

            date.addDays(1);
        }

        table.append('<tfoot><tr><th>Man</th><th>Tir</th><th>Ons</th><th>Tor</th><th>Fre</th><th>Lør</th><th>Søn</th></tr></tfoot>');

        $('#calender').append(outer);
        this.outer = outer;

    },
    dataLoaded: function(data) {
        for (day in data.days) {
            if (data.days[day].entries && data.days[day].entries.length > 0) {
                var dayCell = $('.calendar[year='+this.year+'][month='+this.month+'] [day='+day+']');
                dayCell.addClass('date_has_event');
                var entries = data.days[day].entries;
                var locations = "";
                for (entryId in entries) {
                    var entry = entries[entryId];
                    locations += " "+entry.location;
                }
                dayCell.attr('locations', locations);
                var me = this;
                dayCell.click(function() {  
                    $.mobile.changePage('#daypage_'+me.year+"_"+me.month+"_"+$(this).attr('day'), { transition: 'slide' });
                });
            }
        }
        
        this.createDayPages(data);
    },
    loadData: function() {
        this.getshopApi.CalendarManager.getMonth(this.year, this.month, true).done($.proxy(this.dataLoaded, this));
    },
    nl2br: function(str, is_xhtml) {
        var breakTag = (is_xhtml || typeof is_xhtml === 'undefined') ? '<br />' : '<br>';
        return (str + '').replace(/([^>\r\n]?)(\r\n|\n\r|\r|\n)/g, '$1' + breakTag + '$2');
    },
    createDayPages: function(data) {

        for (day in data.days) {
            if (data.days[day].entries && data.days[day].entries.length > 0) {
                var entries = data.days[day].entries;
                var pageId = 'daypage_'+this.year+'_'+this.month+'_'+day;
                var page = $('#'+pageId);
                if (page.length === 0) {
                    page = $('<div data-role="page" class="daypage" id="'+pageId+'"><div class="header"></div></div>');
                    page.find(".header").load("header.html", function() {
                        page.find('.header').trigger('create');
                    });
                }
                    
                var pageContent = page.find('.CourseDayEntry');
                if (pageContent.length === 0)
                    pageContent = $('<div class="CourseDayEntry" data-role="content"/>');
                
                pageContent.html("");
                
                for (entryId in entries) {
                    var entry = entries[entryId];
                    var entryDetails = $('<div/>');
                    entryDetails.addClass('kursentry');
                    entryDetails.append("<h4>Kurs: "+entry.title+"</h4>");
                    var endTime = "";
                    if (entry.stoptime) {
                        endTime = " - " + entry.stoptime;
                    }
                    entryDetails.append("<b>Tidspunkt:</b> " + entry.starttime + endTime);
                    var availablePositions = entry.maxAttendees - entry.attendees.length;
                    var clazz = "notavailable";
                    
                    if (availablePositions > 0) {
                        clazz = "available";
                    } 
                        
                    entryDetails.append("<div class='freespots "+clazz+"'><div class='label'>Ledige plasser</div><div class='number'>" + availablePositions + "</div></div>");
                    entryDetails.append("<br>");
                    entryDetails.append("<br><b>Sted</b>");
                    entryDetails.append("<br>"+entry.location);
                    entryDetails.append("<br>");
                    entryDetails.append("<br><b> Beskrivelse </b>");
                    entryDetails.append("<br>"+this.nl2br(entry.description));
                    pageContent.append(entryDetails);
                }
                page.append(pageContent);
                
                $('html .ui-mobile-viewport').append(page);
            }
        }
    },
    init: function() {
        this.createTable();
        this.loadData();
    }
};

TextValidator = {
    validate: function(evt) {
        var theEvent = evt || window.event;
        var key = theEvent.keyCode || theEvent.which;
        key = String.fromCharCode(key);
        var regex = /[0-9]|\./;
        if (!regex.test(key)) {
            theEvent.returnValue = false;
            if (theEvent.preventDefault)
                theEvent.preventDefault();
        }
    }
}