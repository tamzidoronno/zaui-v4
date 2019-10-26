/*
 * Clock.cpp
 *
 *  Created on: Sep 15, 2019
 *      Author: ktonder
 */


#include "Clock.h"
#include "Arduino.h"
#include <util/atomic.h>


Clock::Clock() {
	diffSinceStartup = 0;
}

void Clock::adjustClock(unsigned long tsamp) {
	diffSinceStartup = tsamp;

	extern unsigned long timer0_millis;
	ATOMIC_BLOCK (ATOMIC_RESTORESTATE) {
		timer0_millis = 0;
	}
}

long Clock::getTime() {
	return (diffSinceStartup + (millis()/1000));
}


void Clock::getTimeChar(char* charBuf) {
	unsigned long value = getTime();

//	sprintf(charBuf, "%04lX", value);

	charBuf[0] = value & 0xFF; // 0x78
	charBuf[1] = (value >> 8) & 0xFF; // 0x56
	charBuf[2] = (value >> 16) & 0xFF; // 0x34
	charBuf[3] = (value >> 24) & 0xFF; // 0x12

}
