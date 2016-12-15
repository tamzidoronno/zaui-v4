
angular.module('ProMeisterAcademy').config([ '$translateProvider', function($translateProvider) {
  $translateProvider.translations('se', {
    'Username' : 'Användarnamn',
    'LOGIN_HOME_SCREEN_HEADER' : 'Du behöver logga in för att se din översikt. Fyll i fälten och tryck på login.',
    'Password' : 'Lösenord',
    'Log in' : 'Login',
    'HOME_SCREEN_HEADER_TEXT' : 'Nedan ser du dina tillgängliga val, tryck på en ikon för mer info.',
    'Hi' : 'Hej',
    'Signup to event' : 'Boka kurs',
    'Your overview' : 'Din översikt',
    'Our course list' : 'Vårt kursutbud',
    'Contact ProMeister Academy' : 'Kontakta ProMeister Academy',
    'News' : 'Nyheter',
    'SIGN OUT' : 'Logga ut',
    'Loading data, please wait' : 'Laddar, vänta..',
    'To sign up for an event you can select the event below, to find your events easier you can tap the filter at the bottom' : 'Här kan du anmäla dig på kurs. Du kan också genom att trycka på "Filter" filtrera på de orter du är intresserad av.',
    'Please select locations' : 'Välj ort/orter',
    'Done' : 'Klar',
    'Available spots' : 'Lediga platser',
    'Waitinglist available' : 'Väntlista tillgänglig',
    'No available spots' : 'Inga lediga platser',
    'Location' : 'Ort',
    'Date' : 'Datum',
    'Back' : 'Tillbaka',
    'Filter' : 'Filter',
    'Upcoming events' : 'Kurs du är anmäld på',
    'Old events' : 'Kurs du har deltagit på',
    'No events available' : 'Ingen kurs tillgänglig',
    'Below you see a list of all the events we held.' : 'För mer info om respektive kurs, klicka på kursen.',
    'You can contact us by filling out the form below, we will get back to you as soon as possible' : 'Du kan kontakta oss genom att fylla i formuläret nedan, så svarar vi så fort vi kan.',
    'Send message' : 'Skicka',
    'You are signed up for this event' : 'Du är nu anmäld på kursen',
    'Sign up' : 'Boka',
    'Sign up waitinglist' : 'Boka på väntlista',
    'Who are we?' : 'Om ProMeister Academy',
    'No events available, please check back later' : 'Ingen kurs tillgänglig, kom gärna tillbaka lite senare, vi lägger kontinuerligt ut nya utbildningar.',
    'goToOverview' : 'Mer information',
    'Thank you, your message has been sent' : 'Tack, ditt meddelande är skickat'
  });

  $translateProvider.preferredLanguage('se');
  
} ]);
