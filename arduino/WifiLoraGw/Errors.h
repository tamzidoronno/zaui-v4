#ifndef Errors_H
#define Errors_H

#define red 16
#define green 14
#define blue 12

boolean blinkMode = false;
unsigned long outOfBlinkMode = 0;

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
  if (digitalRead(red)) {
    off();
    digitalWrite(red, LOW);
  } else {
    off();
    digitalWrite(red, HIGH);
  }
}



void showNotInitializedLight() {
  off();
  digitalWrite(blue, HIGH);
}

void blinkGreen() {
  if (blinkMode && outOfBlinkMode > millis()) {
     return;
  }

  if (blinkMode) {
    blinkMode = false;
    outOfBlinkMode = 0;
    return;
  }

  off();
  outOfBlinkMode = millis() + 50;
  blinkMode = true;
}

void showConnected() {
  if (blinkMode) {
    blinkGreen();
    return;
  }
  
  digitalWrite(green, HIGH);
}
#endif
