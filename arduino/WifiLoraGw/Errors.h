#ifndef Errors_H
#define Errors_H

#define red 16
#define green 14
#define blue 12

boolean blinkMode = false;
boolean aliveModeActive = false;
unsigned long outOfBlinkMode = 0;
unsigned long lastAliveMessage = 0;

void off() {
  digitalWrite(red, LOW);
  digitalWrite(green, LOW);
  digitalWrite(blue, LOW);
}

void setupErrorLights() {
  pinMode(red, OUTPUT);
  pinMode(green, OUTPUT);
  pinMode(blue, OUTPUT);
}

void showScanningWifiLight() {
   off();  
   digitalWrite(red, HIGH);
   digitalWrite(green, HIGH);
   digitalWrite(blue, HIGH);
}


void showWifiNotConnected() {
  if (digitalRead(blue)) {
    off();
    digitalWrite(blue, LOW);
  } else {
    off();
    digitalWrite(blue, HIGH);
  }
}

void showNotAbleToConnectToServer() {
  off();
  digitalWrite(red, HIGH);
}

void showWrongToken() {
  if (!digitalRead(red)) {
    digitalWrite(red, HIGH);
  } else {
    digitalWrite(red, LOW);
  }
}


void showNotInitializedLight() {
  off();
  digitalWrite(blue, HIGH);
}

void blinkGreen() {
  if (blinkMode && outOfBlinkMode > millis()) {
     off();
     return;
  }

  digitalWrite(green, HIGH);
  outOfBlinkMode = millis() + 100;
  blinkMode = true;
}

void showDebug() {
  for (int j=0; j<10; j++) {
    if (digitalRead(red)) {
      off();
      digitalWrite(red, LOW);
    } else {
      off();
      digitalWrite(red, HIGH);
    }
    delay(100);
  }
}

void blinkAlive() {
  unsigned long milliSecondsSinceLastAliveMessage = millis() - lastAliveMessage;
  
  if (milliSecondsSinceLastAliveMessage > 10000 && !aliveModeActive) {
      off();
      digitalWrite(green, HIGH);
  }

  if (milliSecondsSinceLastAliveMessage > 10100) {
      off();
      aliveModeActive = false;
      lastAliveMessage = millis();
  }
}

void showConnected() {
  off();
}
#endif
