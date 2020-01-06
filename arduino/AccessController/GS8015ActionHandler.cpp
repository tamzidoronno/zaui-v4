/*
 * GS8015ActionHandler.cpp
 *
 *  Created on: Dec 31, 2019
 *      Author: root
 */

#include "GS8015ActionHandler.h"
#include "Arduino.h"
#include <avr/wdt.h>

 #define  C     2100
 #define  D     1870
 #define  E     1670
 #define  f     1580    // Does not seem to like capital F
 #define  G     1400
 #define  R     0

int melody[] = {E, E, E,R,
 E, E, E,R,
 E, G, C, D, E, R,
 f, f, f,f, f, E, E,E, E, D ,D,E, D, R, G ,R,
 E, E, E,R,
 E, E, E,R,
 E, G, C, D, E, R,
 f, f, f,f, f, E, E, E,  G,G, f, D, C,R };

GS8015ActionHandler::GS8015ActionHandler() {

}

void GS8015ActionHandler::lock() {
	digitalWrite(7, HIGH);
	delay(100);
	digitalWrite(7, LOW);
}

void GS8015ActionHandler::playTone() {
   long elapsed_time = 0;
   if (tone_ > 0) { // if this isn't a Rest beat, while the tone has
     //  played less long than 'duration', pulse speaker HIGH and LOW
     while (elapsed_time < duration) {
       digitalWrite(buzzer,HIGH);
       delayMicroseconds(tone_ / 2);
       // DOWN
       digitalWrite(buzzer, LOW);
       delayMicroseconds(tone_ / 2);
       // Keep track of how long we pulsed
       elapsed_time += (tone_);
       wdt_reset();
     }
   }
   else { // Rest beat; loop times delay
     for (int j = 0; j < rest_count; j++) { // See NOTE on rest_count
       delayMicroseconds(duration);
     }
   }
 }

void GS8015ActionHandler::setup() {
	pinMode(6, OUTPUT);
	pinMode(7, OUTPUT);
}

void GS8015ActionHandler::unlock() {
	bool high = 0;

	int MAX_COUNT = sizeof(melody) / 2; // Melody length, for looping.

	for (int i=0; i<MAX_COUNT; i++) {
	    tone_ = melody[i];
	    beat = 50;

	    duration = beat * tempo; // Set up timing

	    playTone();
	    // A pause between notes...
	    delayMicroseconds(pause);
	}

}

void GS8015ActionHandler::triggerDoorAutomation() {

}
