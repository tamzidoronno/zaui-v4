/*
 * Clock.cpp
 *
 *  Created on: Sep 15, 2019
 *      Author: ktonder
 */


#include "Clock.h"
#include "Arduino.h"

Clock::Clock() {
	diffSinceStartup = 0;
}

void Clock::adjustClock(long tsamp) {
	diffSinceStartup = tsamp - millis();
}

long Clock::getTime() {
	return (diffSinceStartup + millis());
}


void Clock::getTimeChar(char* charBuf) {
	long value = getTime();

//	sprintf(charBuf, "%04lX", value);

	charBuf[0] = value & 0xFF; // 0x78
	charBuf[1] = (value >> 8) & 0xFF; // 0x56
	charBuf[2] = (value >> 16) & 0xFF; // 0x34
	charBuf[3] = (value >> 24) & 0xFF; // 0x12

}
