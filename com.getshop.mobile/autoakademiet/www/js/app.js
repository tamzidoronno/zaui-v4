/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

App = {
    loggedInUser: false,
    showingYear: 0,
    showingMonth: 0,
    startMonth: 0,
    startYear: 0,
    reloadData: null,
    monthIndex: 1,
    token: "",
    firstConnected: false,
    numberOfMonthToShowInCalendar: 12,
    lang: 'no',
    version: 1,
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
            App.address = "promeisterse.getshop.com";
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
        this.loadAgreementText();
        this.firstConnected = true;
    },
    loadAgreementText: function() {
        App.getshopApi.CalendarManager.getAgreementText().done(function(result) {
            $('.useragreement').html(result);
        })
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
            'Velg din avtalepartner' : 'Välj din avtalspartner',
            'Venter på at tilkoblingen skal gjennopprettes': 'Väntar på uppkoppling',
            'Kobler til server': 'Ansluter till server',
            'Laster data': 'Laddar',
            'Kalenderfilter': 'Kalenderfilter',
            'Vi holder dette kurset på følgende datoer': 'Följande tillfällen är tillgängliga',
            'Det finnes ikke noe kurstilbud lengre tilbake i tid.': 'Det finns inga tidigare kurstillfällen',
            'For å se kalenderen lengre fram i tid, vennligst benytt websiden.': 'För att se kurskalendern längre fram i  tiden, använd hemsidan.',
            'Velg sted for filtrering på kalenderen.': 'Välj ort för att filtrera kalendern.',
            'Ledige plasser på valgt kurs': 'Lediga platser på vald kurs',
            'Sett meg på venteliste': 'Sätt mig på väntelista',
            'Meld på': 'Anmäl',
            'Deltakers navn': 'Deltagarens namn',
            'Ingen tilgjengelige kurs': 'Det finns inga tillgängliga tillfällen ',
            'Ledige plasser': 'Lediga platser',
            'Finn ditt selskap' : 'Hitta ditt företag/verkstad',
            'Velg ditt firma fra listen under' : 'Välj ditt företag/verkstad',
            'Dato' : 'Datum',
            'Mer informasjon': 'Mer info',
            'Påmelding venteliste': 'Anmälan väntelista',
            'E-post deltaker': 'Deltagarens e-post',
            'E-post teknisk leder': 'Verkstadens e-post',
            'Fjern filter': 'Rensa filter',
            'Du har ikke oppgitt et gyldig org nr, det må være 9 tegn, du har oppgitt': 'Du har inte fyllt ett giltigt org.nr, 10 tecken krävs. Du har angett',
            'Dag': 'Dag',
            'NB! Dette er et {dager} dagers kurs.': 'Detta är en {dager} dagars kurs',
            'Tidspunkt': 'Tidpunkt',
            'Det finnes ingen nyheter akkurat nå, kom tilbake senere.': 'Det finns inga nyheter just nu, välkommen tillbaka senare.',
            'Trykk for å velge': 'Tryck för att välja',
            'Epost addressen er ikke gyldig.': 'Epostadressen är inte giltigt.',
            'Epost teknisk leder er ikke gyldig.': 'Epost Verkstaden är inte giltig.',
            'Du har ikke oppgitt riktig telefonnr, det må være 8 siffer.': 'Du har inte angett ett giltigt telefonnr, det måste innehålla 10 siffror.',
            'Vennligst rett feltene i rødt': 'Vänligen korrigera rödmarkerade fält',
            'klarte ikke å finne firma med oppgitt org.nr, vennligst sjekk': 'Hittade inget företag med angivet org.nr. Kontrollera numret',
            'Du er nå meldt på ventelisten': 'Du står nu på väntelista',
            'Du er nå påmeldt kurset': 'Du är nu anmäld till kursen',
            'Laster data, vennligst vent': 'Laddar, vänligen vänta',
            'Fant ingen treff på oppgitt org.nr, vennligst sjekk og prøv på nytt.': 'Fick ingen träff på angivet org.nr, kontrollera uppgifterna och prova igen.',
            'Kurs': 'Kurs',
            'Sted': 'Plats',
            'Skriv inn org.nr, så vil informasjonen fylles ut automatisk.': 'Hitta dit företag genom att fylla i hela org.nr i fältet ovan.',
            'Navn': 'Deltagarens namn',
            'Deltakers e-post': 'Deltagarens e-post',
            'E-post Teknisk Leder': 'Verkstadens e-post',
            'Mob.Tlf.': 'Deltagarens mobiltelefon',
            'Org.nr.': 'Orgnr',
            'Firma': 'Företag',
            'Nyheter': 'Nyheter',
            'Velg kurs': 'Kurs',
            'Kurs liste': 'Kurslista',
            'Velg grossist': 'Välj avtalspartner',
            'Kurs kalender': 'Kurskalender',
            'Kurs oversikt': 'Kursöversikt',
            'Påmelding': 'Anmälan',
            'Informasjon': 'Information',
            'Tilbake': 'Tillbaka',
            'Kurs i din region': 'Kurslista efter avstånd',
            'Velg sted': 'Välj plats',
            'Forrige': 'Föregående',
            'Neste': 'Nästa',
            'Januar': 'Januari',
            'Februar': 'Februari',
            'Mars': 'Mars',
            'April': 'April',
            'Mai': 'Maj',
            'Juni': 'Juni',
            'Juli': 'Juli',
            'August': 'Augusti',
            'September': 'September',
            'Oktober': 'Oktober',
            'November': 'November',
            'Desember': 'December',
            'Man': 'Mån',
            'Tir': 'Tis',
            'Ons': 'Ons',
            'Tor': 'Tor',
            'Fre': 'Fre',
            'Lør': 'Lör',
            'Søn': 'Sön',
            'Søk' : 'Sök',
            'Firmanavn': 'Orgnr/Företag',
            'Kursliste etter avstand' : 'Kurslista sorterad utifrån avstånd till din position',
            
            'Leter etter gps signaler, vennligst vent.': 'Vänligen vänta, söker GPS-position',
            'Klarte ikke å finne et GPS signal, kontroller at appen har tilgang til GPSen.': 'Kan inte finna GPS-position, kontrollera att appen har tillgång till GPS-position i Inställningar',
            'Fant ikke din region, trykk her for å prøve igjen': 'Hittar inget i ditt område, klicka här för att göra ny sökning',
            'Trykk på selskapet under for å velge det': 'Klicka på rätt företag i listan nedan',
            'Skriv inn ditt firmanavn og trykk på søk, da vil resultatet vises her og du kan velge selskapet du tilhører.': 'Hitta dit företag genom att fylla i hela org.nr i fältet ovan. I vissa fall går det även bra att söka på företagsnamn',
            'Trykk på kursstedene for å velge, når du har valgt stedene du ønsker å se trykk så ferdig' : 'Välj önskade kursorter och tryck sedan Färdig',
            
            'Ny bruker? - Opprett konto her' : 'Ny användare? Registrera dig här',
            'Logg inn' : 'Logga in',
            'Brukernavn' : 'Användarnamn',
            'Passord' : 'Lösenord',
            'Velkommen til ProMeister Academy, appen som gir deg kunnskapspåfylling med noen enkle tastetrykk.' : 'Välkommen till ProMeister Academy, appen som ger dig kompetensutveckling med några enkla klick.',
            'Logger inn... Vennligst vent' : 'Loggar in… Vänligen vänta',
            'Feil brukernavn eller passord' : 'Fel användarnamn eller lösen',
            'Logg ut' : 'Logga ut',
            'Du er i ferd med å melde deg på dette kurset, ved å trykke OK har du bekreftet ProMeister sine brukervilkår og du har blitt meldt på kurset' : 'Då är nästan klar med din anmälan, genom att klicka på OK godkänner du ProMeister\'s användarvillkor och blir sedan anmäld på kursen',
            'Dine kurs' : 'Dina kurser',
            'Du har ikke deltatt på noen kurs enda' : 'Du har ännu inte deltagit på någon kurs',
            'Kurs du skal delta på' : 'Kurs/er som du är anmäld till',
            'Du har gjennomført følgende kurs' : 'Du har genomfört följande kurs/er',
            'En epost har blitt sendt til deg med nye kode.' : 'Ett epostmeddelande har skickats till dig med ditt nya lösen.',
            'Fant ikke brukeren ved oppgitt brukernavn.' : 'Kunde inte hitta användaren med angivet användarnamn.',
            'Ditt navn' : 'Ditt namn',
            'Din epostaddresse' : 'Din epostadress',
            'Teknisk leders epostaddresse' : 'Verkstadens epostadress',
            'Ditt telefonnr' : 'Ditt telefonnr',
            'Du har deltatt på' : 'Du har deltagit på',
            'kurs' : 'kurs/er',
            'Reset password' : 'Nollställ lösenord',
            'Det finnes allerede en konto registrert på epost' : 'Det finns redan ett konto registrerat på',
            ', prøv å gjenopprett passordet istedenfor å lage en ny' : ', prova att återställa lösenordet istället för att skapa ett nytt konto',
            'Navnet ditt kan ikke være blank' : 'Namnfältet får inte vara tomt',
            'Epost kan ikke være blank' : 'Epostfältet får inte vara tomt',
            'Ugyldig telefonnr' : 'Ogiltigt telefonnr',
            'Din epostaddresse er ugyldig, sjekk at du har oppgitt riktig epostaddresse' : 'Ogiltig epostadress, kontrollera att du uppgett korrekt epostadress',
            'Epost til teknisk leder er ugyldig, sjekk at du har oppgitt riktig epostaddresse' : 'Epost till verkstaden är ogiltig, kontrollera att uppgett korrekt epostadress',
            'Fant ikke firmaet du søkte etter, prøv på nytt' : 'Kunde inte hitta firman du sökte efter, prova igen',
            'Takk, du har nå opprettet en konto og er klar til å bruke appen. Du har blitt logget inn automatisk og en epost har blitt sendt til deg med ditt tildelte passord' : 'Tack, du har nu skapat ett konto och färdig att använda appen. Du är nu automatiskt inloggad och ett epostmeddelande har skickats med ditt lösenord.',
            'Du har en gammel versjon av appen, last ned og installer appen på nytt for å sikre deg om at alt av funksjonalitet fungerer slik som det skal' : 'Du har en gammal version av appen, ladda ner senaste versionen för att säkerställa funktionen.'
        }

        // No is default language
        var no = {
            'Reset password' : 'Gjenopprett passord',
            'Velkommen til ProMeister Academy, appen som gir deg kunnskapspåfylling med noen enkle tastetrykk.' : 'Velkommen til ProMeister Academy, her kan du få kunnskapsløft med enkle tastetrykk.',
            'Ny bruker? - Opprett konto her' : 'Ny bruker? Opprett konto',
            'Du har deltatt på' : 'er påmeldt',
            'Kurs i din region' : 'Kurs i min region',
            'Dine kurs' : 'Mine kurs',
            'Kurs liste' : 'Kursliste',
            'Kurs oversikt' : 'Kursoversikt',
            'Kurs du skal delta på' : 'Du er påmeldt på følgende:',
            'Du har gjennomført følgende kurs' : 'Følgende kurs er gjennomført:',
            'Du er nå påmeldt kurset' : 'Du er nå påmeldt kurset og bekreftelse er sendt på registrert epost.',
            'Du er i ferd med å melde deg på dette kurset, ved å trykke OK har du bekreftet ProMeister sine brukervilkår og du har blitt meldt på kurset' : 'Ved å trykke OK har du bekreftet ProMeister sine vilkår og meldt deg på kurset.'
        }

        var matrix = no;

        if (App.lang === 'se') {
            matrix = se;
        }
        
        if (text)
            text = text.trim();
        
        if (!matrix[text]) {
            return text;
        }


        return matrix[text];
    },
    loadMyCourses: function() {
        this.getshopApi.CalendarManager.getMyEvents().done($.proxy(this.myCoursesFetched, this));
    },
    
    signup: function(entryId, waitinglist) {
        var userId = App.loggedInUser.id;
        var emailAddress = App.loggedInUser.emailAddress;
        var password = localStorage.getItem("password");
        
        this.getshopApi.CalendarManager.addUserToEvent(userId, entryId, password, emailAddress, 'mobile').done(function () {
            if (waitinglist) {
                alert(App.translateText('Du er nå meldt på ventelisten'));
            } else {
                alert(App.translateText('Du er nå påmeldt kurset'));
            }
            document.location.href = document.URL.substring(0, document.URL.indexOf("#"));
        });        
    },
    
    myCoursesFetched: function(result) {
        var me = this;
        var container = $('#yourcourses .mycourses_page_result');
        container.html(""); 
        
        var count = 0;
        for (var i in result) {
            var event = result[i];
            if (App.lang === "se" && event.isInPast) {
                count++;
            } 
            
            if (App.lang === "no" && !event.isInPast) {
                count++;
            }
        }
        
        $('.number_of_events').html(App.translateText("Du har deltatt på") + " " + count + " " + App.translateText("kurs"))
        if (result.length === 0) {
            container.html(App.translateText('Du har ikke deltatt på noen kurs enda'));
        } else {
            container.html("<div class='my_course_header'><center>" + App.translateText("Kurs du skal delta på") + "</center></div>");
            for (var i in result) {
                var event = result[i];
                if (event.isInPast)
                    continue;
                
                var div = me.createInfoBox(event);
                container.append(div);
            }
            
            
            container.append("<div class='my_course_header'><center>" + App.translateText("Du har gjennomført følgende kurs") + "</center></div>");
            for (var i in result) {
                var event = result[i];
                if (!event.isInPast)
                    continue;
                
                var div = me.createInfoBox(event);
                container.append(div);
            }
        }
    },
    
    createInfoBox: function(event) {
        var div = $('<div/>');
        div.addClass('your_course_entry');
        div.append("<div>"+App.translateText("Kurs")+": " + event.title + "</div>");
        div.append("<div class='course_my_page_date'>"+event.day + "/" + event.month + "-" + event.year +"</div>");

        div.append("<div>"+App.translateText('Sted')+": " +event.location+"</div>");
        return div;
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
                var entryContainer = this.getEntryHtml(entry);
                entiresContainer.append(entryContainer);

            }
        }

        return entiresContainer;
    },
    getEntryHtml: function (entry) {
        var freePositions = entry.maxAttendees - entry.attendees.length;
        if (freePositions < 0) {
            freePositions = 0;
        }
        
        var availableClass = "available";
        var unavilable = !entry.availableForBooking || entry.lockedForSignup;
        
        if (unavilable) {
            availableClass = "notAvailable";
        }
        
        if (!unavilable && !freePositions) {
            availableClass = "waitingList";
        }
       
        
        var availableMark = $('<div/>');
        availableMark.addClass('availablemark '+availableClass);
        
        var entryContainer = $('<div/>');
        entryContainer.addClass('list_view_entry_entry');
        entryContainer.html(entry.title);
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
            var entryId = $(this).attr('entry');
            var pageId = 'daypage_' + $(this).attr('year') + "_" + $(this).attr('month') + "_" + $(this).attr('day')+"_"+entryId;
            
            $.mobile.changePage("#" + pageId, {transition: 'slide' });
        });
        
        entryContainer.append(availableMark);
        
        return entryContainer;
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
        App.showFirstPage();
        
    },
    showFirstPage: function() {
        App.getshopApi.UtilManager.getAppVersion().done(function(result) {
            if (App.version < result) {
                alert(App.translateText("Du har en gammel versjon av appen, last ned og installer appen på nytt for å sikre deg om at alt av funksjonalitet fungerer slik som det skal"));
            }
        });
        if (App.loggedInUser) {
            $.mobile.changePage('#home');
        } else {
            $.mobile.changePage('#login');
            App.doLogin(true);
        }
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
    
    checkAllSignonFields: function() {
        var name = $('#name').val();
        var email = $('#email').val();
        var invoiceemail = $('#invoiceemail').val();
        var phone = $('#phone').val();
        var courseId = $('#select-native-1').find(':selected').val();
        var groupId = $('#select-native-2').find(':selected').val()
        var groupInformation = $('#extrainformationforgroup').val();

        $('label').removeClass('error');

        if (name === "")
            $('label[for=name]').addClass('error');

        if (email === "" || !this.validateEmail(email))
            $('label[for=email]').addClass('error');

        if (email === "" || !this.validateEmail(invoiceemail))
            $('label[for=emailinvoice]').addClass('error');

        if (App.lang == "no" && (phone === "" || phone.length !== 8))
            $('label[for=phone]').addClass('error');

        if (App.lang == "se" && (phone === "" || phone.length !== 10))
            $('label[for=phone]').addClass('error');

  
        if (courseId === "")
            $('label[for=courseId]').addClass('error');

        if (groupId === "")
            $('label[for=groupId]').addClass('error');

        if (courseId === "")
            $('label[for=select-native-1]').addClass('error');

        if (groupId === "")
            $('label[for=select-native-2]').addClass('error');

        if (!this.validateEmail(email)) {
            alert(App.translateText('Epost addressen er ikke gyldig.'));
            return false;
        }

        if (!this.validateEmail(invoiceemail)) {
            alert(App.translateText('Epost teknisk leder er ikke gyldig.'));
            return false;
        }
        
        if (App.lang === "se" && $('.extrainformationforgroup').is(':visible')) {
            var groupId = $('#select-native-2').find(':selected').val();

            if (!groupInformation) {
                $('label[for=extrainformationforgroup]').addClass('error');
                alert($('label[for=extrainformationforgroup]').html() + ' är inte giltigt.');
                return  false;
            }
        }

        if ((App.lang == "no" && phone.length !== 8) || (App.lang == "se" && phone.length !== 10)) {
            alert(App.translateText("Du har ikke oppgitt riktig telefonnr, det må være 8 siffer."));
            return false;
        }
        
        if ($('label.error').length > 0) {
            alert(App.translateText('Vennligst rett feltene i rødt'));
            return false;
        }
        
        return true;
    },
    doLogin: function(fromStorage) {
        var loginButton = $('#login #app_login_button');
        var oldButtonTextLogin = loginButton.html();
        
        var username = $('#login_username').val();
        var password = $('#login_password').val();
        
        $('#login_username').val("");
        $('#login_password').val("");
        
        if (typeof(fromStorage) !== "undefined" && fromStorage === true) {
            username = localStorage.getItem("username");
            password = localStorage.getItem("password");
            
            if (typeof(username) === "undefined" || typeof(password) === "undefined" || !username || !password) {
                return;
            }
        }
        
        loginButton.html('<i class="fa-li fa fa-spinner fa-spin"></i> ' + App.translateText("Logger inn... Vennligst vent"));
        loginButton.addClass('disabled');
        
        
        this.getshopApi.UserManager.logOn(username, password).done(function(result) {
            if (result != null && typeof(result.errorCode) !== "undefined" && result.errorCode === 13) {
                loginButton.html(App.translateText("Feil brukernavn eller passord"));
                loginButton.removeClass("disabled");
                setTimeout(function() {
                    loginButton.html(oldButtonTextLogin);
                }, 2000);
                
                return;
            };
            
            $('.customer_name').html(result.fullName);
            localStorage.setItem("username", username);
            localStorage.setItem("password", password);
            App.loggedInUser = result;
            loginButton.html(oldButtonTextLogin);
            loginButton.removeClass("disabled");
            App.reset();
            
            App.getshopApi.CalendarManager.registerToken(App.token).done(function() { } );
            App.getshopApi.MobileManager.registerTokenToUserId(App.token).done(function() { } );
            App.loadMyCourses();
        });
    },
    doLogout: function() {
        App.loggedInUser = false;
        localStorage.setItem("username", "");
        localStorage.setItem("password", "");
        App.reset();
    },
    resetPassword: function() {
        var email = $('#reset_username').val();
        
        App.getshopApi.UserManager.createAndSendNewPassword(email).done(function(result) {
            if (result) {
                alert(App.translateText('En epost har blitt sendt til deg med nye kode.'));
                $.mobile.changePage("#login");
            } else {
                alert(App.translateText('Fant ikke brukeren ved oppgitt brukernavn.'));
            }
        });
    },
    setupListeners: function () {
        var me = this;
        $(document).on('click', '#signup .company_search_row_result', function() {
            var vatNumber = $(this).attr('vatnumber');
                var exec = function(){
                App.signOnClicked(vatNumber);
            }

            var run = $.proxy(exec, this);
            run();
        });
        
        $('#login #app_login_button').click($.proxy(this.doLogin, this));
        $('.logoutbutton').click($.proxy(this.doLogout, this));
        $('#next').click($.proxy(this.nextClicked, this));
        $('#prev').click($.proxy(this.prevClicked, this));
        $('#regions .region_failed').click($.proxy(this.checkGps, this));

        $(document).on('tap', '#reset_password_button', App.resetPassword);
        
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
        
        $(document).on('tap', '.yourcourses', function (e) {
            $.mobile.changePage('#yourcourses');
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

        $(document).on('pageshow', '#create_new_user', App.createCreateUserPage);
        $(document).on('tap', '.gotocalendar', function (e) {
            App.checkGps();
            $.mobile.changePage('#regions');
            e.preventDefault();
            e.stopPropagation();
            $(this).off('click');
            
        });

        $('#vatnr').keyup($.proxy(this.vatnumberupdated, this));
        $(document).on('change', '#select-native-2', App.groupChanged);
        
        $('#signup_confirmation .pro_button').tap(function() {
            var waitinglist = $(this).attr('waitinglist') === "true";
            var entryId = $(this).attr('eventid');
            App.signup(entryId, waitinglist);
        });
    },
    
    createCreateUserPage: function() {
        var container = $('#create_new_user .content');
        container.html("");
        container.html("<center><h1>"+App.translateText("Velg din avtalepartner")+"</h1></center>")
        App.getshopApi.UserManager.getAllGroups().done(function(result) {
            for (var i in result) {
                var groupData = result[i];
                var div = $('<div/>');
                if (groupData.imageId) {
                    div.append('<img src="http://'+App.address+'/displayImage.php?id='+groupData.imageId+'"/>');
                } else {
                    div.append(groupData.groupName);
                }
                div.attr('groupId', groupData.id);
                div.addClass('select_group');
                
                div.tap(function() {
                    App.groupSelected($(this).attr('groupId'));
                })
                container.append(div);
            }
        })
    },
    
    groupSelected: function(groupId) {
        App.createUser = {
            group : groupId
        };
        
        var container = $('#create_new_user .content');
        container.html("");
        
        var schema = $('#registerschema').clone();
        schema.attr('id', 'realregisterschema');
        schema.find('#go_to_company_search').tap(App.searchForCompany)
          
        if (App.lang === "se" && groupId === "1cdd1d93-6d1b-4db3-8e91-3c30cfe38a4a") {
            $(schema).find('.meknomenid').show();
        }
        
        if (App.lang === "se" && (groupId === "ddcdcab9-dedf-42e1-a093-667f1f091311" || groupId === "608c2f52-8d1a-4708-84bb-f6ecba67c2fb" )) {
            $(schema).find('.mecaid').show();
        }
        
        container.html(schema);
        $('#create_new_user').trigger('create');
        
    },
    
    searchForCompany: function() {
        App.createUser.emailAddress = $('#realregisterschema #create_user_email').val();
        App.createUser.emailAddressToInvoice =  $('#realregisterschema #create_user_invoiceemail').val();
        App.createUser.cellPhone =  $('#realregisterschema #create_user_phonenumber').val();
        App.createUser.fullName =  $('#realregisterschema #create_user_name').val();
        
        if (App.createUser.fullName == "") {
            alert(App.translateText("Navnet ditt kan ikke være blank"));
            return;
        }
        
        if (App.createUser.emailAddress === "" || App.createUser.emailAddressToInvoice === "") {
            alert(App.translateText("Epost kan ikke være blank"));
            return;
        }
        
        if (App.lang === "no" && (App.createUser.cellPhone === "" || App.createUser.cellPhone.length !== 8)) {
            alert(App.translateText("Ugyldig telefonnr"));
            return;
        }
        
        var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
        if (!re.test(App.createUser.emailAddress)) {
            alert(App.translateText("Din epostaddresse er ugyldig, sjekk at du har oppgitt riktig epostaddresse"));
            return;
        }
        
        if (!re.test(App.createUser.emailAddressToInvoice)) {
            alert(App.translateText("Epost til teknisk leder er ugyldig, sjekk at du har oppgitt riktig epostaddresse"));
            return;
        }
       
        if (App.lang === "se" && (App.createUser.group === "ddcdcab9-dedf-42e1-a093-667f1f091311" || App.createUser.group === "608c2f52-8d1a-4708-84bb-f6ecba67c2fb" )) {
            var val = $('#realregisterschema #create_user_kundnummer').val();
            
            if (val.length < 7 || val.length > 13) {
                alert(App.translateText('Your customerid can not be less then 7 digits or more then 13 digits.'));
                return;
            }

            if (isNaN(val)) {
                alert(App.translateText('Your customerid can only be numbers'));
                return;
            }
        }
        
        
        App.getshopApi.UserManager.doEmailExists(App.createUser.emailAddress).done(function(result) {
            if (result) {
                alert(App.translateText("Det finnes allerede en konto registrert på epost") + " " + App.createUser.emailAddress + App.translateText(", prøv å gjenopprett passordet istedenfor å lage en ny"));
            } else {
                App.goToCompanySearch();
            }
        });
    },
    
    goToCompanySearch: function() {
        var container = $('#create_new_user .content');
        container.html("");
        var schema = $('#searchcompanyschema').clone();
        schema.attr('id', 'realsearchcompanyschema');
        schema.find('#search_for_company').tap(App.doSearchForCompany);
        container.html(schema);
        $('#create_new_user').trigger('create');        
    },
    
    doSearchForCompany: function() {
        var searchRestultContainer = $('#realsearchcompanyschema .companyresult');
        
        App.getshopApi.UtilManager.getCompaniesFromBrReg($('#realsearchcompanyschema #create_user_serach_company').val()).done(function(result) {
            if (!result.length) {
                searchRestultContainer.html("<br/><center>"+App.translateText("Fant ikke firmaet du søkte etter, prøv på nytt")+"</center>");
            } else {
                searchRestultContainer.html("<br/><center>"+App.translateText("Velg ditt firma fra listen under")+"</center>");
                searchRestultContainer.append("<br/>");
                for (var i in result) {
                    var company = result[i];
                    var companyDiv = $("<div/>");
                    companyDiv.addClass('companyselector');
                    companyDiv.attr('vatnumber', company.vatNumber);
                    companyDiv.append(company.name);
                    companyDiv.append("<br/>"+company.vatNumber);
                    companyDiv.tap(function() {
                        App.companySelected($(this).attr('vatnumber'));
                    });
                    searchRestultContainer.append(companyDiv);
                }
            }
         });
    },
    
    companySelected: function(data) {
        var password = Math.floor(Math.random() * 98440) + 11820;
        App.createUser.birthDay = data;
        App.createUser.company = App.getshopApi.UtilManager.getCompanyFromBrReg(data);
        App.createUser.password = password;
        
        App.getshopApi.UserManager.createUser(App.createUser).done(function(user) {
            alert(App.translateText("Takk, du har nå opprettet en konto og er klar til å bruke appen. Du har blitt logget inn automatisk og en epost har blitt sendt til deg med ditt tildelte passord"))
            localStorage.setItem("username", user.emailAddress);
            localStorage.setItem("password", password);    
            App.doLogin(true);
        });
        
        
    },
    
    checkGps: function() {
        $('#regions').find('.waiting_for_gps').show();
        $('#regions').find('.region_failed').hide();
        $('#regions').find('.gps_failed').hide();
        $('#regions').find('.regionas_page_content').hide();
                
        var onSuccess = function(position) {
            $('#regions').find('.waiting_for_gps').hide();
            var lat = position.coords.latitude.toFixed(5);
            var alt = position.coords.longitude.toFixed(5);
            App.findRegions(lat,alt);
        };
        
        var onError = function(error) {
            $('#regions').find('.waiting_for_gps').hide();
            $('#regions').find('.gps_failed').show();
        };
        
        navigator.geolocation.getCurrentPosition(onSuccess, onError);

    },
    
    getDistanceFromLatLonInKm: function(lat1,lon1,lat2,lon2) {
        var R = 6371;
        var dLat = App.deg2rad(lat2-lat1); 
        var dLon = App.deg2rad(lon2-lon1); 
        var a = 
            Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(App.deg2rad(lat1)) * Math.cos(App.deg2rad(lat2)) * 
            Math.sin(dLon/2) * Math.sin(dLon/2); 
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        var d = R * c; 
        return d;
    },

    deg2rad: function(deg) {
        return deg * (Math.PI/180);
    },

    findRegions: function (lat, alt) {
        var data = {
            x: (lat*100000),
            y: (alt*100000)
        }
        

        App.getshopApi.CalendarManager.getEntriesByPosition(data).done(function (location) {
            if (!location || typeof(location.name) === "undefined") {
                $('#regions').find('.region_failed').show();
                return;
            }

            $('#regions').find('.region_failed').hide();
            $('#regions').find('.regionas_page_content').show();
            $('#regions').find('.regionname').html(location.name);
            if (App.lang == "se") {
                $('#regions').find('.kurslocationheader').html(App.translateText("Kursliste etter avstand"));
            }
            $('#regiondata').html("");
            var monthName = "";
            var distance = "";
            
            if (App.lang == "se") 
                location = App.sortLocationByDistance(location, lat, alt);
            
            for ( var i in location.entries) {
                var entry = location.entries[i];
                
                if (App.lang == "no") {
                    var currentMonthName = App.getNameForMonth(entry.month);
                    if (currentMonthName !== monthName) {
                        $('#regiondata').append("<div class='gps_month_header' style='monthName'>"+currentMonthName+" - " + entry.year + " </div>");
                        monthName = currentMonthName;
                    }
                }
                
                if (App.lang == "se") {
                    var currentDistance = App.getDistanceFromLatLonInKm(lat, alt, entry.locationObject.lat, entry.locationObject.lon);
                    if (currentDistance !== distance) {
                        distance = currentDistance;
                        
                        var printDistance = "";
                        if (!isNaN(distance)) {
                            printDistance =  " - " + distance.toFixed(2)+ " km";
                        }
                        $('#regiondata').append("<div class='gps_month_header' style='monthName'>"+ entry.locationObject.location + printDistance + " </div>");
                    }
                }
                
                var html = App.getEntryHtml(entry);
                $('#regiondata').append(html);
            }

        });
    },
    
    sortLocationByDistance: function(location, lat, alt) {
        location.entries.sort(function(a, b) {
            var distanceA = App.getDistanceFromLatLonInKm(lat, alt, a.locationObject.lat, a.locationObject.lon);
            var distanceB = App.getDistanceFromLatLonInKm(lat, alt, b.locationObject.lat, b.locationObject.lon);
            
            if (isNaN(distanceA)) {
                distanceA = 999999;
            }
            
            if (isNaN(distanceB)) {
                distanceB = 999999;
            }
            
            if (distanceA === distanceB) {
                return 0;
            }
            
            if (distanceA > distanceB) {
                return 1;
            }
            
            if (distanceA < distanceB) {
                return -1;
            }
        });
        
        
        return location;
    },
    
    groupChanged: function () {
        var groupId = $('#select-native-2').find(':selected').val();

        $('.extrainformationforgroup').hide();

        // Meca
        if (groupId === "ddcdcab9-dedf-42e1-a093-667f1f091311" && App.lang === "se") {
            $('.extrainformationforgroup').find('label').html("Kundnummer");
            $('.extrainformationforgroup').show();
        }

        // Mekonomen
        if (groupId === "1cdd1d93-6d1b-4db3-8e91-3c30cfe38a4a" && App.lang === "se") {
            $('.extrainformationforgroup').find('label').html("Meko-Id");
            $('.extrainformationforgroup').show();
        }
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
            this.getshopApi.UtilManager.getCompanyFree(value).done(function (company) {
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
            var invalidText = App.translateText("Du har ikke oppgitt et gyldig org nr, det må være 9 tegn, du har oppgitt") + ": " + value.length;
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

            var filter = $("<a href='#' data-rel='back'  data-role='button'>" + App.translateText("Ferdig") + "</a>");
            filter.click(function () {
                me.activateFilter();
            });
            filterHolder.append(filter);
            
            
            filterHolder.append(filter);
            $(filters).each(function () {
                var filter = $("<div href='#' class='filter_selection' data-rel='back' data-role='button'>" + this + "</div>");
                var filterName = this;
                
                filter.click(function () {
                    if ($(this).hasClass('filter_checked')) {
                        $(this).removeClass('filter_checked');
                    } else {
                        $(this).addClass('filter_checked');
                    }
                });
                filterHolder.append(filter);
            });

            filterHolder.controlgroup();
            filterHolder.trigger('create');
        });
    },
    activateFilter: function (filter) {
        if (filter === "") {
            $('#filter').find('.filter_checked').removeClass('.filter_checked');
        }
        
        var filters = [];
        
        $('#filter').find('.filter_checked').each(function() {
            filters.push($(this).html().toUpperCase());
        });
        
        $('.list_view_entry_entry').removeClass('hidden');
        $('.monthcontainer').show();
        if (filters.length > 0) {
            $('.list_view_entry_entry').each(function () {
                if (filters.indexOf($(this).attr('location').toUpperCase()) < 0) {
                    $(this).addClass('hidden');
                } 
            });
        }

        $('.monthcontainer').each(function () {
            var subs = $(this).find('.list_view_entry_entry:not(.hidden)');
            if (subs.length === 0) {
                $(this).hide();
            } else {
                $(this).show();
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

//        var selectValue = $('<div/>').html(this.options).find('option[value=]');
//        $('#select-native-1').html("");
//        $('#select-native-1').append(selectValue.clone());
//  
//
//        $('#select-native-1').selectmenu();
//        $('#select-native-1').selectmenu('refresh', true);
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
    signUpForEvent: function(entryId, waitinglist) {
        $.mobile.changePage("#signup_confirmation" , {transition: 'slide' });
        $('#signup_confirmation .pro_button').attr('eventId', entryId);
        $('#signup_confirmation .pro_button').attr('waitinglist', waitinglist);
    },
   
    createDayPages: function (data) {
        var meCalendar = this;
        for (day in data.days) {
            if (data.days[day].entries && data.days[day].entries.length > 0) {
                var entries = data.days[day].entries;
                
                for (entryId in entries) {
                    var entry = entries[entryId];
                    var pageId = 'daypage_' + this.year + '_' + this.month + '_' + day+"_"+entry.entryId;
                    
                    var page = $('#' + pageId);
                    if (page.length === 0) {
                        page = $('#daycoursetemplate').clone();
                        page.attr('id', pageId);
                    }

                    var pageContent = page.find('.innercontent');

                    pageContent.html("");
                    var currentDate = new Date();

                    
                    if (!entry.isOriginal) {
                        continue;
                    }
                    var entryDetails = $('<div/>');
                    entryDetails.attr('id', entry.entryId);
                    entryDetails.addClass('kursentry');
                    entryDetails.append("<h4>" + App.translateText("Kurs") + ": " + entry.title + "</h4>");
                    var endTime = "";
                    if (entry.stoptime) {
                        endTime = " - " + entry.stoptime;
                    }

                    if (entry.otherDays.length > 0) {
                        var courseDays = entry.otherDays.length;
                        courseDays++;
                        var nbText = App.translateText("NB! Dette er et {dager} dagers kurs.");
                        nbText = nbText.replace("{dager}", courseDays);

                        entryDetails.append("<b>" + nbText + "</b>");
                        entryDetails.append("<br> " + App.translateText("Dag") + " 1: " + entry.day + " / " + entry.month + " - " + entry.year + " : " + entry.starttime + endTime);
                        for (var i in entry.otherDays) {
                            var otherDay = entry.otherDays[i];
                            var j = parseInt(i) + 2;
                            entryDetails.append("<br> " + App.translateText("Dag") + " " + j + ": " + otherDay.day + " / " + otherDay.month + " - " + otherDay.year + " : " + otherDay.starttime + " - " + otherDay.stoptime);
                        }
                    } else {
                        entryDetails.append("<b>" + App.translateText("Dato") + ":</b> " + entry.day + " / " + entry.month + " - " + entry.year + " : " + entry.starttime + endTime);
                        entryDetails.append("<br/><b>" + App.translateText("Tidspunkt") + ":</b> " + entry.starttime + endTime);
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

                    entryDetails.append("<div class='freespots " + clazz + "'><div class='label'>" + App.translateText('Ledige plasser') + "</div><div class='number'>" + (availablePositions > 0 ? availablePositions : 0) + "</div></div>");
                    entryDetails.append("<br>");
                    entryDetails.append("<br><b>" + App.translateText("Sted") + ": </b>" + entry.location);

                    if (entry.locationExtended) {
                        entryDetails.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;- " + entry.locationExtended);
                    }

                    var buttons = $('<div/>');
                    buttons.html("<br/>");

                    if (entry.linkToPage && entry.linkToPage != "") {
                        var linkToPage = $("<a data-theme='a' data-role='button' data-inline='true' href='#" + entry.linkToPage + "'>" + App.translateText('Mer informasjon') + "</a>");
                        buttons.append(linkToPage);
                        linkToPage.button();
                    }

                    if (availablePositions > 0 && !entry.lockedForSignup) {
                        var linkToSignup = $("<a data-theme='a' entry='" + entry.entryId + "' data-role='button' data-inline='true'>" + App.translateText("Påmelding") + "</a>");
                        buttons.append(linkToSignup);
                        linkToSignup.click(function () {
                            App.calendar.signUpForEvent($(this).attr('entry'), false);
                        });
                        linkToSignup.button();
                    }

                    if (!inThePast && availablePositions <= 0 && !entry.lockedForSignup) {
                        var linkToSignup = $("<a data-theme='a' entry='" + entry.entryId + "' data-role='button' data-inline='true'>" + App.translateText('Påmelding venteliste') + "</a>");
                        buttons.append(linkToSignup);
                        linkToSignup.click(function () {
                            App.calendar.signUpForEvent($(this).attr('entry'), true);
                        });
                        linkToSignup.button();
                    }
                    entryDetails.append(buttons);


                    pageContent.append(entryDetails);
                
                    page.append(pageContent);

                    App.doTranslationForDom(page);
                    $('html .ui-mobile-viewport').append(page);
                }
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