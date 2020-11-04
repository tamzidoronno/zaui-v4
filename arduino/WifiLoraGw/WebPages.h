#ifndef WebPages_h
#define WebPages_h

#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>


String wifi = "<br/><b> Available WIFI: </b>";

void scanWifi() {
    WiFi.mode(WIFI_STA);
    WiFi.disconnect();
    delay(100);  

  int n = WiFi.scanNetworks();
  if (n == 0) {
    wifi = wifi + "<br/> No wifi found";
  } else {
    for (int i = 0; i < n; ++i) {
      char channelbyte[10];
      itoa(WiFi.channel(i), channelbyte, 10);
      
      wifi = wifi + "<br/> " + String(i+1) + ". " + WiFi.SSID(i) + " ( " + WiFi.RSSI(i) + ") | ch: " + channelbyte + " | bssid: " + WiFi.BSSIDstr(i);
      delay(10);
    }
  }

  wifi = wifi + "<br/><br/>";
}

void webPage_home(ESP8266WebServer *webServer) {
  String homePage_start = "<html><head><style>body {    background: #23d5ab; } form {max-width: 500px; margin: 0 auto; padding: 50px; text-align: center; } input {padding: 10px; font-size: 20px; width: 100%; display: block; margin: 5px auto; } </style></head><body><form id='form' action='/' method='post'><h1>Seros Gateway Configuration</h1>";
  String homePage_end = "<input name='wifissid' type='text' placeholder='Wifi SSID'><input name='wifipass' id='password1' type='text' placeholder='Wifi Password'><input name='token' type='text' placeholder='Token'><input name='address' type='text' placeholder='Address to your seros system'><input type='submit' value='Update'></form></body></html>";

  
  
  String totalPage = homePage_start + wifi + homePage_end;

  int n = WiFi.scanNetworks();
  
  
  if (webServer->args() == 0) {
    webServer->sendHeader("Content-Length", String(totalPage.length()));
    webServer->send(200, "text/html", totalPage);
  } else {
    config.configured = 0x01;
    webServer->arg("wifissid").toCharArray(config.essid, sizeof(config.essid));
    webServer->arg("wifipass").toCharArray(config.password, sizeof(config.password));
    webServer->arg("token").toCharArray(config.token, sizeof(config.token));
    webServer->arg("address").toCharArray(config.address, sizeof(config.address));
    saveConfig();

    webServer->send(200, "text/plain", "Restarting device");
    ESP.restart();
  }

}

#endif
