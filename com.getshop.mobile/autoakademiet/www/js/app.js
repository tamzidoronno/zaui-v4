/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

App = {
    showingYear: 0,
    showingMonth: 0,
    startMonth: 0,
    startYear: 0,
    reloadData: null,
    monthIndex: 1,
    token: "",
    firstConnected: false,
    numberOfMonthToShowInCalendar: 6,
    lang: 'se',
    start: function () {
        this.setLanguageMode();
        this.getshopApi = new GetShopApiWebSocket(this.address);
        this.getshopApi.connect();
        this.getshopApi.transferStarted = $.proxy(this.transferStarted, this);
        this.getshopApi.transferCompleted = $.proxy(this.transferCompleted, this);
        this.getshopApi.disconnectedCallback = $.proxy(this.isDisconnected, this);
        this.getshopApi.connectedCallback = $.proxy(this.onData, this);
        this.setupListeners();
        this.startTranslation();   
    },
    setLanguageMode: function () {
        if (App.lang === 'se') {
            App.address = "promeisterse.local.getshop.com";
            App.appName = "ProMeisterAcademeySe";
        } else {
            App.address = "mecademo.getshop.com";
            App.appName = "ProMeisterAcademey";
        }
    },
    programResumed: function () {
        App.loadNews(false);
    },
    getLocalStorageReadMessages: function () {
        if (!localStorage["readMessages"]) {
            return [];
        }

        return JSON.parse(localStorage["readMessages"]);
    },
    saveLocalStorageReadMessages: function (msg) {
        localStorage["readMessages"] = JSON.stringify(msg);
    },
    validateEmail: function (email) {
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    },
    isDisconnected: function () {
        $('.disconnected').show();
    },
    onData: function () {
        $('.disconnected').hide();
        this.reset();
        this.loadCourses();
        this.loadInformationPage();
        this.loadFilters();
        this.loadCoursePages();
        this.createCalendars();
        this.setupSignupPage();
        this.bindRefreshEvent();
        this.sendToken();
        this.loadNews(false);
        this.loadCourseList();
        this.firstConnected = true;

    },
    startTranslation: function () {
        App.doTranslationForDom($('#dataloader'));
        $('[data-role="page"]').on('pageshow', function (event, page) {
            App.doTranslationForDom(event.delegateTarget)
        });
        
    },
    doTranslationForDom: function (dom) {
        $(dom).find('[translate="true"]').each(function () {
            var text = $(this).html();
            var newText = App.translateText(text);
            $(this).html(newText);
        });
    },
    translateText: function (text) {
        var se = {
            'Venter på at tilkoblingen skal gjennopprettes': 'Venter på at tilkoblingen skal gjennopprettes!',
            'Kobler til server': 'Kobler til server!',
            'Laster data': 'Laster data!',
            'Kalenderfilter': 'Kalenderfilter!',
            'Det finnes ikke noe kurstilbud lengre tilbake i tid.': 'Det finnes ikke noe kurstilbud lengre tilbake i tid.!',
            'For å se kalenderen lengre fram i tid, vennligst benytt websiden.': 'For å se kalenderen lengre fram i tid, vennligst benytt websiden.!',
            'Velg sted for filtrering på kalenderen.': 'Velg sted for filtrering på kalenderen.!',
            'Ledige plasser på valgt kurs': 'Ledige plasser på valgt kurs!',
            'Sett meg på venteliste': 'Sett meg på venteliste!',
            'Meld på': 'Meld på!',
            'Ledige plasser': 'Ledige plasser!',
            'Mer informasjon': 'Mer informasjon!',
            'Påmelding venteliste': 'Påmelding venteliste!',
            'Fjern filter': 'Fjern filter!',
            'Du har ikke oppgitt et gyldig org nr, det må være 9 tegn, du har oppgitt' : 'Du har ikke oppgitt et gyldig org nr, det må være 10 tegn, du har oppgitt!',
            'Dag' : 'Dag!',
            'NB! Dette er et {dager} dagers kurs.' : 'NB! Dette er et {dager} dagers kurs.!',
            'Tidspunkt' : 'Tidspunkt!',
            'Det finnes ingen nyheter akkurat nå, kom tilbake senere.' : 'Det finnes ingen nyheter akkurat nå, kom tilbake senere.!',
            'Trykk for å velge' : 'Trykk for å velge!',
            'Epost addressen er ikke gyldig.' : 'Epost addressen er ikke gyldig.!',
            'Epost teknisk leder er ikke gyldig.' : 'Epost teknisk leder er ikke gyldig.!',
            'Du har ikke oppgitt riktig telefonnr, det må være 8 siffer.' : 'Du har ikke oppgitt riktig telefonnr, det må være 8 siffer.!',
            'Vennligst rett feltene i rødt' : 'Vennligst rett feltene i rødt!',
            'klarte ikke å finne firma med oppgitt org.nr, vennligst sjekk' : 'klarte ikke å finne firma med oppgitt org.nr, vennligst sjekk!',
            'Du er nå meldt på ventelisten' : 'Du er nå meldt på ventelisten!',
            'Du er nå påmeldt kurset' : 'Du er nå påmeldt kurset!',
            'Laster data, vennligst vent' : 'Laster data, vennligst vent!',
            'Fant ingen treff på oppgitt org.nr, vennligst sjekk og prøv på nytt.' : 'Fant ingen treff på oppgitt org.nr, vennligst sjekk og prøv på nytt.',
            
            'Kurs': 'Kurs!',
            'Sted': 'Plats!',
            'Skriv inn org.nr, så vil informasjonen fylles ut automatisk.': 'Hitta dit företag genom att fylla i hela org.nr i fältet ovan.!',
            'Navn': 'Deltagarens namn!',
            'Deltakers e-post': 'Deltagarens e-post!',
            'E-post Teknisk Leder': 'Verkstadens e-post!',
            'Mob.Tlf.': 'Deltagarens mobiltelefon!',
            'Org.nr.': 'Orgnr!',
            'Firma': 'Företag!',
            'Nyheter': 'Nyheter!',
            'Velg kurs': 'Kurs!',
            'Kurs liste': 'Kurslista!',
            'Velg grossist': 'Välj avtalspartner!',
            'Kurs kalender': 'Kurskalender!',
            'Kurs oversikt': 'Kursöversikt!',
            'Påmelding': 'Anmälan!',
            'Informasjon': 'Information!',
            'Tilbake': 'Tillbaka!',
            'Velg sted': 'Välj plats!',
            'Fjern filter': 'Ta bort filter!',
            'Forrige': 'Föregående!',
            'Neste': 'Nästa!',
            'Januar': 'Januari!',
            'Februar': 'Februari!',
            'Mars': 'Mars!',
            'April': 'April!',
            'Mai': 'Maj!',
            'Juni': 'Juni!',
            'Juli': 'Juli!',
            'August': 'Augusti!',
            'September': 'September!',
            'Oktober': 'Oktober!',
            'November': 'November!',
            'Desember': 'December!',
            'Man': 'Mån!',
            'Tir': 'Tis!',
            'Ons': 'Ons!',
            'Tor': 'Tor!',
            'Fre': 'Fre!',
            'Lør': 'Lör!',
            'Søn': 'Sön!',
        }

        // No is default language
        var no = {}

        var matrix = no;

        if (App.lang === 'se') {
            matrix = se;
        }
        if (!matrix[text]) {
            return text;
        }


        return matrix[text];
    },
    loadCourseList: function () {
        var me = this;

        this.getshopApi.CalendarManager.getMonths().done(function (months) {
            var container = $('#courselistoverview');
            container.html("");
            for (var i in months) {
                var monthContainer = $('<div/>');
                monthContainer.addClass('monthcontainer');
                var month = months[i];
                var header = $("<div class='header'>" + me.getNameForMonth(month.month) + " - " + month.year + "</div>");
                var entries = me.getEntriesForListView(month);
                monthContainer.html(header);
                monthContainer.append(entries);
                container.append(monthContainer);
            }
        });
    },
    getEntriesForListView: function (month) {
        var entiresContainer = $('<div/>');
        entiresContainer.addClass('month_list_entries');
        var outerMe = this;

        for (var j in month.days) {
            var day = month.days[j];
            for (var i in day.entries) {
                var entry = day.entries[i];

                var entryContainer = $('<div/>');
                entryContainer.addClass('list_view_entry_entry');
                entryContainer.html(entry.title);
                entiresContainer.append(entryContainer);
                entryContainer.attr('location', entry.location);

                var extraInfoContainer = $('<div/>');
                extraInfoContainer.addClass('extrainfo');
                var date = entry.day + " / " + entry.month;
                if (entry.otherDays.length > 0) {
                    date += " ( " + (entry.otherDays.length + 1) + " dager)";
                }
                extraInfoContainer.html(entry.location + " - " + date);
                entryContainer.append(extraInfoContainer);

                entryContainer.attr('year', entry.year);
                entryContainer.attr('day', entry.day);
                entryContainer.attr('month', entry.month);
                entryContainer.attr('entry', entry.entryId);

                entryContainer.tap(function () {
                    var pageId = 'daypage_' + $(this).attr('year') + "_" + $(this).attr('month') + "_" + $(this).attr('day');
                    var entryId = $(this).attr('entry');
                    $.mobile.changePage("#" + pageId, {transition: 'slide' });
                    outerMe.hideOtherEvents(entryId, pageId);
                });
            }
        }

        return entiresContainer;
    },
    hideOtherEvents: function (entry, pageId) {
        var page = $("#" + pageId);
        this.showOtherEvents();
        page.find('.kursentry').each(function () {
            if ($(this).attr('id') !== entry) {
                $(this).hide();
            }
        })
    },
    showOtherEvents: function () {
        $(document).find('.kursentry').each(function () {
            $(this).show();
        });
    },
    getNameForMonth: function (month) {
        if (month === 1) {
            return App.translateText("Januar");
        }
        if (month === 2) {
            return App.translateText("Februar");
        }
        if (month === 3) {
            return App.translateText("Mars");
        }
        if (month === 4) {
            return App.translateText("April");
        }
        if (month === 5) {
            return App.translateText("Mai");
        }
        if (month === 6) {
            return App.translateText("Juni");
        }
        if (month === 7) {
            return App.translateText("Juli");
        }
        if (month === 8) {
            return App.translateText("August");
        }
        if (month === 9) {
            return App.translateText("September");
        }
        if (month === 10) {
            return App.translateText("Oktober");
        }
        if (month === 11) {
            return App.translateText("November");
        }
        if (month === 12) {
            return App.translateText("Desember");
        }
    },
    isRead: function (entryId) {
        var readMessages = this.getLocalStorageReadMessages();
        for (var i in readMessages) {
            var readMessageEntryId = readMessages[i];
            if (readMessageEntryId === entryId) {
                return true;
            }
        }

        return false;
    },
    updateCounter: function () {
        var counter = $('.notRead').length;
        if (counter === 0) {
            this.getshopApi.MobileManager.clearBadged(this.token, true);
            $('.newsbutton .counter').hide();
        } else {
            $('.newsbutton .counter').show();
            $('.newsbutton .counter').html(counter);
        }
    },
    markAllAsRead: function () {
        var readMessages = this.getLocalStorageReadMessages();
        $('.notRead').each(function () {
            var entryId = $(this).attr('entryId');
            readMessages.push(entryId);
            $(this).removeClass('notRead');
        });
        this.saveLocalStorageReadMessages(readMessages);
        this.updateCounter();
    },
    loadNews: function (silent) {
        var me = this;
        this.getshopApi.NewsManager.getAllNews(true, silent).done(function (news) {
            var holder = $("#news .newsentries");
            holder.html("");

            if (news) {
                for (var i in news) {
                    var newsEntry = news[i];
                    var container = $('<div/>');
                    var content = me.replaceAll(newsEntry.content, '/displayImage', "http://" + me.address + "/displayImage");
                    var contentHtml = $("<div>" + content + "</div>");
                    contentHtml.find('img').css('height', 'auto');
                    contentHtml.find('img').css('width', '100%');
                    contentHtml.find('td span').css('font-size', '8px');

                    container.attr("entryId", newsEntry.id);
                    if (!me.isRead(newsEntry.id)) {
                        container.addClass('notRead');
                    }

                    container.addClass("newsentry");
                    container.append("<div class='header'>" + newsEntry.subject + "</div>");
                    container.append("<div class='content'>" + contentHtml.html() + "</div>");
                    container.append("<div class='footer'>" + newsEntry.date + "</div>");
                    container.append("<div class='right'></div>");
                    container.click(function () {
                        var pageId = "#getshoppage_" + $(this).attr('entryId');
                        if ($('body').find(pageId).length < 1) {
                            var page = new GetShop.Page(me.getshopApi, $(this).attr('entryId'));
                            page.ready = function () {
                                $.mobile.changePage(pageId);
                            }
                            page.load();
                        } else {
                            $.mobile.changePage(pageId);
                        }


                    });
                    holder.append(container);
                }
            }

            if (news.length === 0) {
                holder.html(App.translateText("Det finnes ingen nyheter akkurat nå, kom tilbake senere."));
            }

            me.updateCounter();
        });
    },
    sendToken: function () {
        if (App.token === "") {
            return;
        }

        var tokenObject = {
            tokenId: App.token,
            type: "IOS",
            appName: App.appName,
            testMode: true
        };

        App.getshopApi.MobileManager.registerToken(tokenObject);
    },
    replaceAll: function (o, t, r, c) {
        if (c == 1) {
            cs = "g"
        } else {
            cs = "gi"
        }
        var mp = new RegExp(t, cs);
        ns = o.replace(mp, r);
        return ns
    },
    loadInformationPage: function () {
        var me = this;
        this.getshopApi.PageManager.getPage("f1d04c8a-222c-4e3d-a5cb-32e7f5f9d6f1").done(function (page) {
            var contentManagerId = page['pageAreas']['main_1']['applicationsList'][0];
            me.getshopApi.ContentManager.getContent(contentManagerId).done(function (content) {
                var page = $('#infopage');
                if (page.length === 0) {
                    page = $('#daycoursetemplate').clone();
                    page.attr('id', 'infopage');
                    $('html .ui-mobile-viewport').append(page);
                }

                content = me.replaceAll(content, '/displayImage', "http://www.getshop.com/displayImage");
                var contentHtml = $(content);
                contentHtml.find('img').css('height', 'auto');
                contentHtml.find('img').css('width', '100%');
                contentHtml.find('td span').css('font-size', '8px');
                
                App.doTranslationForDom(page);
                page.find('.innercontent').html(contentHtml);
            });
        });
    },
    refresh: function () {
        this.getshopApi.socket.close();
    },
    bindRefreshEvent: function () {
        $('.refresh').click($.proxy(this.refresh, this));
    },
    reset: function () {
        $('.connecting').hide();
        this.monthIndex = 1;
        this.activateFilter("");
        $.mobile.changePage('#home');
    },
    transferCompleted: function () {
        $('.loading').hide();
    },
    transferStarted: function () {
        $('.loading').show();
    },
    pad: function (num, size) {
        var s = "000000000" + num;
        return s.substr(s.length - size);
    },
    shuffle: function (o) {
        for (var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x)
            ;
        return o;
    },
    showFreePositions: function (positions) {
        $('.availablepositions_course_selected').hide();
        if (typeof (positions) !== "undefined") {
            $('.availablepositions_course_selected').show();
            $('.availablepositions_course_selected').html(App.translateText("Ledige plasser på valgt kurs") + ": <b>" + (positions > 0 ? positions : 0) + "</b>");
        }
        if (typeof (positions) !== "undefined" && positions <= 0) {
            $('#signup a#signon span').html(App.translateText("Sett meg på venteliste"));
        } else {
            $('#signup a#signon span').html(App.translateText("Meld på"));
        }
    },
    setupSignupPage: function () {
        var me = this;
        var day = parseInt(Date.today().toString('dd'));
        var month = parseInt(Date.today().toString('MM'));
        var year = parseInt(Date.today().toString('yyyy'));
        var course = $('<option></option>');
        var empty = course.clone();
        empty.html(App.translateText("Trykk for å velge"));
        empty.attr('value', '');
        empty.attr('selected', 'true');

        $('#select-native-1').append(empty.clone());
        $('#select-native-2').append(empty.clone());

        this.getshopApi.CalendarManager.getEntries(year, month, day, null).done(function (entries) {
            $('#select-native-1').html("");
            $(entries).each(function () {
                var availablePositions = this.maxAttendees - this.attendees.length;
                course = course.clone();
                course.attr('value', this.entryId);
                course.attr('location', this.location);
                course.attr('availablepositions', availablePositions);
                course.html(me.pad(this.day, 2) + " / " + me.pad(this.month, 2) + " - " + this.year + " : " + this.location + " - " + this.title);
                $('#select-native-1').append(course);
            });
            $('#select-native-1').selectmenu();
            $('#select-native-1').selectmenu('refresh', true);
            $('#select-native-1').on('change', function () {
                var positions = $(this).find(':selected').attr('availablepositions');
                App.showFreePositions(positions);
            });


        });
        this.getshopApi.UserManager.getAllGroups().done(function (groups) {
            $('#select-native-2').html("");
            groups = me.shuffle(groups);
            $(groups).each(function () {
                var group = course.clone();
                group.attr('value', this.id);
                group.html(this.groupName);
                $('#select-native-2').append(group);
            });
            $('#select-native-2').selectmenu();
            $('#select-native-2').selectmenu('refresh', true);
        });

        $('#signup').on('pageshow', function () {
            if (App.detached) {
                $('#select-native-1').find('option').detach();
                $('#select-native-1').append(App.detached);
                App.detached = null;
            }

            if (App.filterToEntryId) {
                var entryId = App.filterToEntryId;
                App.detached = $('#select-native-1').find('option').detach();

                var entryFound = false;
                $(App.detached).each(function () {
                    if ($(this).attr('value') === entryId) {
                        entryFound = this;
                    }
                });

                $('#select-native-1').append(entryFound);
            }

            $('#select-native-1').selectmenu('refresh', true);

            var signOnButton = $('#signup a#signon span');

            if ($('#select-native-1').find(':selected') && $('#select-native-1').find(':selected').length > 0) {
                var positions = $('#select-native-1').find(':selected').attr('availablepositions')
                App.showFreePositions(positions);
            }
        });
    },
    nextClicked: function () {
        setTimeout(function () {
            $('#next').removeClass('ui-btn-active');
        }, 50);
        if (this.monthIndex === App.numberOfMonthToShowInCalendar) {
            $("#noCalenderEventForward").popup("open");
            return;
        }
        $('.calendar[year=' + this.showingYear + '][month=' + this.showingMonth + ']').hide()
        if (this.showingMonth === 12) {
            this.showingMonth = 0;
            this.showingYear++;
        }

        this.showingMonth++;
        $('.calendar[year=' + this.showingYear + '][month=' + this.showingMonth + ']').fadeIn(300);
        this.monthIndex++;
    },
    prevClicked: function () {
        setTimeout(function () {
            $('#prev').removeClass('ui-btn-active');
        }, 50);

        if (this.showingMonth === this.startMonth && this.showingYear === this.startYear) {
            $("#noCalenderEventBack").popup("open")
            return;
        }

        $('.calendar[year=' + this.showingYear + '][month=' + this.showingMonth + ']').hide()
        if (this.showingMonth === 1) {
            this.showingMonth = 13;
            this.showingYear--;
        }

        this.showingMonth--;
        this.monthIndex--;
        var me = this;
        $('.calendar[year=' + this.showingYear + '][month=' + this.showingMonth + ']').fadeIn(300);
    },
    signOnClicked: function () {
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

        if (email === "" || !this.validateEmail(email))
            $('label[for=email]').addClass('error');

        if (email === "" || !this.validateEmail(invoiceemail))
            $('label[for=emailinvoice]').addClass('error');

        if (phone === "" || phone.length !== 8)
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

        if (!this.validateEmail(email)) {
            alert(App.translateText('Epost addressen er ikke gyldig.'));
            return;
        }

        if (!this.validateEmail(invoiceemail)) {
            alert(App.translateText('Epost teknisk leder er ikke gyldig.'));
            return;
        }

        if (phone.length !== 8) {
            alert(App.translateText("Du har ikke oppgitt riktig telefonnr, det må være 8 siffer."));
            return;
        }

        if ($('label.error').length > 0) {
            alert(App.translateText('Vennligst rett feltene i rødt'));
        } else {
            this.getshopApi.UtilManager.getCompanyFromBrReg(vatnr).done(function (company) {
                if (!company.name) {
                    alert(App.translateText('klarte ikke å finne firma med oppgitt org.nr, vennligst sjekk'));
                    return;
                }

                var password = Math.floor(Math.random() * 90000) + 10000;
                var user = {
                    fullName: name,
                    emailAddress: email,
                    type: 10,
                    password: password,
                    birthDay: vatnr,
                    cellPhone: phone,
                    groups: [groupId],
                    company: company,
                    emailAddressToInvoice: invoiceemail
                };

                me.getshopApi.UserManager.createUser(user).done(function (createUser) {
                    me.getshopApi.CalendarManager.addUserToEvent(createUser.id, courseId, password, createUser.username, 'mobile').done(function () {
                        if (positions <= 0) {
                            alert(App.translateText('Du er nå meldt på ventelisten'));
                        } else {
                            alert(App.translateText('Du er nå påmeldt kurset'));
                        }
                        document.location.href = document.URL.substring(0, document.URL.indexOf("#"));
                    });
                });
            });
        }
    },
    setupListeners: function () {
        var me = this;
        $('#next').click($.proxy(this.nextClicked, this));
        $('#prev').click($.proxy(this.prevClicked, this));

        $(document).on('tap', '.infobutton', function (e) {
            $.mobile.changePage('#infopage');
            e.preventDefault();
            e.stopPropagation();
            $(this).off('click');
        });

        $(document).on('tap', '.newsbutton', function (e) {
            $.mobile.changePage('#news');
            me.markAllAsRead();
            e.preventDefault();
            e.stopPropagation();
            $(this).off('click');
        });

        $(document).on('tap', '.gotolistview', function (e) {
            $.mobile.changePage('#courselist');
            e.preventDefault();
            e.stopPropagation();
            $(this).off('click');
        });

        $(document).on('tap', '.gotosignup', function (e) {
            App.filterToEntryId = false;
            $.mobile.changePage('#signup');
            e.preventDefault();
            e.stopPropagation();
            $(this).off('click');
        });

        $(document).on('tap', '.gotocourses', function (e) {
            $.mobile.changePage('#courses');
            e.preventDefault();
            e.stopPropagation();
            $(this).off('click');
        });

        $(document).on('tap', '.gotocalendar', function (e) {
            $.mobile.changePage('#calendar');
            e.preventDefault();
            e.stopPropagation();
            $(this).off('click');
        });

        $('#signon').click($.proxy(this.signOnClicked, this));
        $('#vatnr').keyup($.proxy(this.vatnumberupdated, this));
    },
    vatnumberupdated: function () {
        var value = $('#vatnr').val();
        var informationPage = $('.companyinformation');
        var vatLength = 9;
        if (App.lang === 'se') {
            vatLength = 10;
        }
        if (value.length === vatLength) {
            var outer = $('<div/>');
            var loader = $('<img src="css/images/ajaxloader.gif"/><br>');
            outer.append(loader);
            outer.append(App.translateText('Laster data, vennligst vent'));
            outer.css('text-align', 'center');
            informationPage.html(outer);
            var emptyRow = $('<div><div class="description"/><div class="value"/></div>');
            this.getshopApi.UtilManager.getCompanyFromBrReg(value).done(function (company) {
                informationPage.html("");

                if (!company.name) {
                    informationPage.html(App.translateText("Fant ingen treff på oppgitt org.nr, vennligst sjekk og prøv på nytt."));
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
            var invalidText = App.translateText("Du har ikke oppgitt et gyldig org nr, det må være 9 tegn, du har oppgitt")+": " + value.length;
            informationPage.html(invalidText);
        }
    },
    createCalendars: function () {
        $('#calender').html("");
        var year = parseInt(Date.today().toString('yyyy'));
        var originalYear = year;
        var currentMonth = Date.today().getMonth() + 1;
        var j = currentMonth;
        for (var i = currentMonth; i < currentMonth + App.numberOfMonthToShowInCalendar; i++) {

            if (j > 12) {
                j = 1;
                year++;
            }
            this.calendar = new App.Calendar(this.getshopApi, year, j);
            j++;
        }

        $('.calendar[year=' + originalYear + '][month=' + currentMonth + ']').show();
        this.showingYear = originalYear;
        this.showingMonth = currentMonth;
        this.startYear = originalYear;
        this.startMonth = currentMonth;
    },
    loadCoursePages: function () {
        var me = this;

        this.getshopApi.ListManager.getList("71544ed8-406d-4496-baf2-b569975ebb20").done(function (entries) {
            $(entries).each(function () {
                $(this.subentries).each(function () {
                    var page = $('#' + this.pageId);
                    
                    if (page.length === 0) {
                        var getShopPage = new GetShop.Page(me.getshopApi, this.pageId);
                        getShopPage.ready = function () {
                            var newPage = this.page;
                            newPage.attr('id', this.pageId);
                            App.doTranslationForDom(this.page);
                            $('html .ui-mobile-viewport').append(this.page);
                        }
                        
                        getShopPage.load();
                    }
                });
            });
        });
    },
    loadFilters: function () {
        var me = this;
        this.getshopApi.CalendarManager.getFilters().done(function (filters) {
            var filterHolder = $('#filterholdergroup');
            filterHolder.html("");

            var filter = $("<a href='#' data-rel='back' data-role='button'>"+App.translateText("Fjern filter")+"</a>");
            filter.click(function () {
                me.activateFilter("")
            });
            filterHolder.append(filter);
            $(filters).each(function () {
                var filter = $("<a href='#' data-rel='back' data-role='button'>" + this + "</a>");
                var filterName = this;
                filter.click(function () {
                    me.activateFilter(filterName)
                });
                filterHolder.append(filter);
            });

            filterHolder.controlgroup();
            filterHolder.trigger('create');
        });
    },
    activateFilter: function (filter) {
        filter = filter.replace(/'/g, "\\'");

        if (filter === "" && !this.filterIsSet) {
            return;
        }

        $('td.date_has_event').removeClass('disabled');
        $('td.date_has_event').each(function () {
            if ($(this).attr('locations').toUpperCase().indexOf(filter.toUpperCase()) < 0) {
                $(this).addClass('disabled');
            }
        });

        $('.list_view_entry_entry').removeClass('hidden');
        $('.monthcontainer').show();
        $('.list_view_entry_entry').each(function () {
            if ($(this).attr('location').toUpperCase().indexOf(filter.toUpperCase()) < 0) {
                $(this).addClass('hidden');
            }
        });

        $('.monthcontainer').each(function () {
            var subs = $(this).find('.list_view_entry_entry:not(.hidden)');
            if (subs.length === 0) {
                $(this).hide();
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
        $(this.options).each(function () {

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
    loadCourses: function () {
        var topEntry = $('<div data-role="collapsible" data-theme="a" data-content-theme="a"  data-inset="false"/>');
        var subEntryContainer = $('<ul data-role="listview"/>');
        var courselist = $('#courselist .contentcourselist');

        courselist.html("");
        this.getshopApi.ListManager.getList("71544ed8-406d-4496-baf2-b569975ebb20").done(function (list) {
            $(list).each(function () {
                var topEntryCloned = topEntry.clone();
                topEntryCloned.html("<h4>" + this.name + "</h4>");
                courselist.append(topEntryCloned);
                var subentriesClonedTop = subEntryContainer.clone();
                $(this.subentries).each(function () {
                    var subEntry = $('<li><a href="#' + this.pageId + '">' + this.name + '</a></li>');
                    subentriesClonedTop.append(subEntry);
                });
                topEntryCloned.append(subentriesClonedTop);
            });

            $('#courses').page();
            $('#courses').trigger('create');
        });
    },
    registerAndroidToken: function (tokenId) {
        var tokenObject = {
            tokenId: tokenId,
            type: "ANDROID",
            appName: App.appName,
            testMode: true
        };

        App.getshopApi.MobileManager.registerToken(tokenObject);
    },
    pushNotificationSuccess: function (result) {
        // OK ?
    },
    pushNotificationError: function (error) {
        alert(error);
    },
    tokenHandler: function (token) {
        App.token = token;
        if (App.firstConnected) {
            App.sendToken();
        }
    },
    onNotificationApple: function (e) {
        if (e.foreground === "1") {
            App.loadNews(true);
        }
    },
    onNotificationGCM: function (e) {
        switch (e.event)
        {
            case 'registered':
                if (e.regid.length > 0)
                {
                    App.registerAndroidToken(e.regid);
                }
                break;

            case 'message':
                console.log(e);
                // this is the actual push notification. its format depends on the data model from the push server
                if (e.foreground) {
                    App.loadNews(true);
                }
                break;

            case 'error':
                alert('GCM error = ' + e.msg);
                break;

            default:
                alert('An unknown GCM event has occurred');
                break;
        }
    }
};

App.Calendar = function (getshopApi, year, month) {
    this.getshopApi = getshopApi;
    this.year = year;
    this.month = month;
    if (this.month > 12) {
        this.month = this.month - 12;
        this.year++;
    }
    this.init();
};

App.Calendar.prototype = {
    prePend: function (date, tr, emptytd) {
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
    postPend: function (date, tr, emptytd) {
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
    getMonthName: function () {
        return App.getNameForMonth(this.month);
    },
    createTable: function () {
        var outer = $("<div class='calendar'>" + this.getMonthName() + " - " + this.year + "</div>");
        outer.attr('year', this.year);
        outer.attr('month', this.month);
        outer.hide();
        var table = $('<table cellspacing="0"><thead><tr><th>' + App.translateText("Man") + '</th><th>' + App.translateText("Tir") + '</th><th>' + App.translateText("Ons") + '</th><th>' + App.translateText("Tor") + '</th><th>' + App.translateText("Fre") + '</th><th>' + App.translateText("Lør") + '</th><th>' + App.translateText("Søn") + '</th></tr></thead></table>');
        outer.append(table);

        var date = new Date(this.year, this.month - 1);
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
            tableCell.attr('month', this.month - 1);
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

        table.append('<tfoot><tr><th>' + App.translateText("Man") + '</th><th>' + App.translateText("Tir") + '</th><th>' + App.translateText("Ons") + '</th><th>' + App.translateText("Tor") + '</th><th>' + App.translateText("Fre") + '</th><th>' + App.translateText("Lør") + '</th><th>' + App.translateText("Søn") + '</th></tr></tfoot>');

        $('#calender').append(outer);
        this.outer = outer;

    },
    dataLoaded: function (data) {
        for (day in data.days) {
            if (data.days[day].entries && data.days[day].entries.length > 0) {
                var dayCell = $('.calendar[year=' + this.year + '][month=' + this.month + '] [day=' + day + ']');

                var entries = data.days[day].entries;
                var locations = "";
                var original = false;
                for (entryId in entries) {
                    var entry = entries[entryId];
                    if (entry.isOriginal) {
                        original = true;
                    }
                    locations += " " + entry.location;
                }

                if (!original) {
                    continue;
                }

                dayCell.addClass('date_has_event');
                dayCell.attr('locations', locations);
                var me = this;
                dayCell.tap(function () {
                    var pageId = 'daypage_' + me.year + "_" + me.month + "_" + $(this).attr('day');
                    $.mobile.changePage("#" + pageId, {transition: 'slide' });
                    App.showOtherEvents();
                });
            }
        }

        this.createDayPages(data);
    },
    loadData: function () {
        this.getshopApi.CalendarManager.getMonth(this.year, this.month, true).done($.proxy(this.dataLoaded, this));
    },
    nl2br: function (str, is_xhtml) {
        var breakTag = (is_xhtml || typeof is_xhtml === 'undefined') ? '<br />' : '<br>';
        return (str + '').replace(/([^>\r\n]?)(\r\n|\n\r|\r|\n)/g, '$1' + breakTag + '$2');
    },
    createDayPages: function (data) {

        for (day in data.days) {
            if (data.days[day].entries && data.days[day].entries.length > 0) {
                var entries = data.days[day].entries;
                var pageId = 'daypage_' + this.year + '_' + this.month + '_' + day;

                var page = $('#' + pageId);
                if (page.length === 0) {
                    page = $('#daycoursetemplate').clone();
                    page.attr('id', pageId);
                }

                var pageContent = page.find('.innercontent');

                pageContent.html("");
                var currentDate = new Date();

                for (entryId in entries) {
                    var entry = entries[entryId];
                    if (!entry.isOriginal) {
                        continue;
                    }
                    var entryDetails = $('<div/>');
                    entryDetails.attr('id', entry.entryId);
                    entryDetails.addClass('kursentry');
                    entryDetails.append("<h4>"+App.translateText("Kurs")+": " + entry.title + "</h4>");
                    var endTime = "";
                    if (entry.stoptime) {
                        endTime = " - " + entry.stoptime;
                    }

                    if (entry.otherDays.length > 0) {
                        var courseDays = entry.otherDays.length;
                        courseDays++;
                        var nbText =  App.translateText("NB! Dette er et {dager} dagers kurs.");
                        nbText = nbText.replace("{dager}", courseDays);
                        
                        entryDetails.append("<b>"+nbText+"</b>");
                        entryDetails.append("<br> "+App.translateText("Dag")+" 1: " + entry.day + " / " + entry.month + " - " + entry.year + " : " + entry.starttime + endTime);
                        for (var i in entry.otherDays) {
                            var otherDay = entry.otherDays[i];
                            var j = parseInt(i) + 2;
                            entryDetails.append("<br> "+App.translateText("Dag")+" " + j + ": " + otherDay.day + " / " + otherDay.month + " - " + otherDay.year + " : " + otherDay.starttime + " - " + otherDay.stoptime);
                        }
                    } else {
                        entryDetails.append("<b>"+App.translateText("Tidspunkt")+":</b> " + entry.starttime + endTime);
                    }
                    var availablePositions = entry.maxAttendees - entry.attendees.length;

                    var javascriptDate = new Date(entry.year, entry.month - 1, entry.day, 23, 59, 55)
                    var inThePast = javascriptDate < currentDate;
                    if (inThePast) {
                        availablePositions = 0;
                    }

                    var clazz = "notavailable";

                    if (availablePositions > 0) {
                        clazz = "available";
                    }

                    entryDetails.append("<div class='freespots " + clazz + "'><div class='label'>"+App.translateText('Ledige plasser')+"</div><div class='number'>" + (availablePositions > 0 ? availablePositions : 0) + "</div></div>");
                    entryDetails.append("<br>");
                    entryDetails.append("<br><b>"+App.translateText("Sted")+": </b>" + entry.location);

                    if (entry.locationExtended) {
                        entryDetails.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;- " + entry.locationExtended);
                    }

                    var buttons = $('<div/>');
                    buttons.html("<br/>");

                    if (entry.linkToPage && entry.linkToPage != "") {
                        var linkToPage = $("<a data-theme='a' data-role='button' data-inline='true' href='#" + entry.linkToPage + "'>"+App.translateText('Mer informasjon')+"</a>");
                        buttons.append(linkToPage);
                        linkToPage.button();
                    }

                    if (availablePositions > 0) {
                        var linkToSignup = $("<a data-theme='a' entry='" + entry.entryId + "' data-role='button' data-inline='true' href='#signup'>"+App.translateText("Påmelding")+"</a>");
                        buttons.append(linkToSignup);
                        linkToSignup.click(function () {
                            App.filterToEntryId = $(this).attr('entry');
                            App.filterWaitingList = false;
                        });
                        linkToSignup.button();
                    }

                    if (!inThePast && availablePositions <= 0) {
                        var linkToSignup = $("<a data-theme='a' entry='" + entry.entryId + "' data-role='button' data-inline='true' href='#signup'>"+App.translateText('Påmelding venteliste')+"</a>");
                        buttons.append(linkToSignup);
                        linkToSignup.click(function () {
                            App.filterToEntryId = $(this).attr('entry');
                            App.filterWaitingList = true;
                        });
                        linkToSignup.button();
                    }
                    entryDetails.append(buttons);


                    pageContent.append(entryDetails);
                }
                page.append(pageContent);

                App.doTranslationForDom(page);
                $('html .ui-mobile-viewport').append(page);
            }
        }
    },
    init: function () {
        this.createTable();
        this.loadData();
    }
};

TextValidator = {
    validate: function (evt) {
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