chrome.app.runtime.onLaunched.addListener(function() {
 var options = {
   'id': 'MECA TV Kiosk app',
   'bounds': {
     'width': 1920,
     'height': 1080
   }
 };
 chrome.app.window.create('index.html', (options));
});