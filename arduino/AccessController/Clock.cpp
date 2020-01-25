/*
 * Clock.cpp
 *
 *  Created on: Sep 15, 2019
 *      Author: ktonder
 */


#include "Clock.h"
#include "Arduino.h"

static unsigned long timerOverFlowCounter = 0;
static unsigned long lastMillis = 0;

Clock::Clock() {
	diffSinceStartup = 0;
}

void Clock::adjustClock(unsigned long tsamp) {
	if (diffSinceStartup == 0) {
		diffSinceStartup = tsamp;
	}
}

unsigned long Clock::getTime() {
	if (millis() < lastMillis && millis() != 0) {
		timerOverFlowCounter++;
	}

	lastMillis = millis();

	return diffSinceStartup + (millis()/1000) + (timerOverFlowCounter * 4294967);
}


void Clock::getTimeChar(char* charBuf) {
	unsigned long value = getTime();

	charBuf[0] = value & 0xFF; // 0x78
	charBuf[1] = (value >> 8) & 0xFF; // 0x56
	charBuf[2] = (value >> 16) & 0xFF; // 0x34
	charBuf[3] = (value >> 24) & 0xFF; // 0x12

}
