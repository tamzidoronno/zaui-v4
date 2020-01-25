/*
 * CodeHandler.cpp
 *
 *  Created on: Sep 8, 2019
 *      Author: ktonder
 */


#include "CodeHandler.h";
#include "Communication.h";
#include "Arduino.h"

static char unsigned resetCode[16] = {
		0x00, 0x00, 0x00, 0x00,
		0x01, 0x02, 0x03, 0x01,
		0x01, 0x08, 0x02, 0x04,
		0x03, 0xFF, 0xFF, 0xFF
};

void(* resetFunc) (void) = 0;//declare reset function at address 0


CodeHandler::CodeHandler(DataStorage* dataStorage, CodeReader* keypadReader, Communication* commu, Logging *logging, Clock* clock, ActionHandler* actionHandler) {
	this->dataStorage = dataStorage;
	this->keypadReader = keypadReader;
	this->communication = commu;
	this->logging = logging;
	this->clock = clock;
	this->_forceClosed = false;
	this->actionHandler = actionHandler;
	this->resetCloseTimeStamp();
	this->resetOpenTimeStamp();
	this->_isOpen = false;
}

void CodeHandler::_initAutoCloseAfterMillis() {
	unsigned char data[16];

	dataStorage->getCode(903, data);

	if (data[0] != 0x44) {
		autoCloseAfterMs = 5000;
		return;
	}

	autoCloseAfterMs = data[1];
	autoCloseAfterMs = autoCloseAfterMs * 256 + data[2];
	autoCloseAfterMs = autoCloseAfterMs * 256 + data[3];
	autoCloseAfterMs = autoCloseAfterMs * 256 + data[4];
}

void CodeHandler::setup() {
	this->actionHandler->setup();
	this->_initAutoCloseAfterMillis();
}

void CodeHandler::resetOpenTimeStamp() {
	this->openTimeStamp = 4294967290;
}

void CodeHandler::resetCloseTimeStamp() {
	this->closeTimeStamp = 4294967290;
}

bool CodeHandler::compareCodes(unsigned char* savedCode, unsigned char* typedCode, int codeSlot) {
	int codeLengthSaved = 0;

	for (int i=0; i<16; i++) {
		if (savedCode[i] == 0xFF) {
			break;
		}

		codeLengthSaved++;
	}

	if (codeLengthSaved < 4) {
		return false;
	}

	bool allEqual = false;
    int j = 0;

	for (int i=16-codeLengthSaved; i < 16; i++) {
		bool equal = savedCode[j] == typedCode[i];
	    if (!equal) {
	    	return false;
	    }

	    j++;
	}
	return true;
}

bool CodeHandler::testCodes(unsigned char* codeFromPanel) {
	unsigned char buffer[16];

	Serial.print(codeFromPanel[15], HEX);
	Serial.print("\r\n");

	if (!this->isLocked() && codeFromPanel[15] == 0x0A && codeFromPanel[14] == 0x01 && codeFromPanel[13] == 0x0A) {
		this->resetCloseTimeStamp();
    this->changeState('U');
    return false;
	}

  if (!this->isLocked() && codeFromPanel[15] == 0x0A && codeFromPanel[14] == 0x02 && codeFromPanel[13] == 0x0A) {
   this->changeState('N');
    return false;
  }

	for (unsigned int i=1; i<=800; i++) {

		dataStorage->getCode(i, buffer);

		if (this->compareCodes(buffer, codeFromPanel, i)) {

			if (this->isLocked()) {
				this->unlock(i);
			} else {
				this->triggerDoorAutomation();
			}

			return true;
		}

		if (keypadReader->check()) {
			break;
		}
	}

	if (this->compareCodes(resetCode, codeFromPanel, 0)) {
		dataStorage->resetAll();
		resetFunc();
	}

	return false;
}

void CodeHandler::check() {
	unsigned long time = this->clock->getTime();

	if (time > openTimeStamp) {
		this->unlock(0);
	}

	if (millis() > closeTimeStamp) {
		this->lock(0);
	}
}

/**
 * triggeredBySlot = user slot code used for opening.
 	 	 	 	 	 if triggeredBySlot = 0, then its triggered by system.
 */
void CodeHandler::lock(unsigned int triggeredBySlot) {
	this->resetCloseTimeStamp();
	this->actionHandler->lock();
	this->logging->addLog("D:LOCKED", 8, true);
	this->_isOpen = false;
}

/**
 * This function is also failsafe for retriggering the unlock functionallity.
 *
 * If the door is asked to unlock while the door is open it will just trigger the door automation logic.
 *
 * triggeredBySlot = user slot code used for opening.
 	 	 	 	 	 if triggeredBySlot = 0, then its triggered by system.
 	 	 	 	 	 if triggeredBySlot = 32767, then its triggered by exit button (inside)
 */
void CodeHandler::unlock(unsigned int triggeredBySlot) {
	if (this->_forceClosed) {
		return;
	}

	if (!this->isLocked()) {
		this->triggerDoorAutomation();
		return;
	}

	this->setCloseTimeStamp(millis() + autoCloseAfterMs);

	this->internalUnlock();

	this->triggerDoorAutomation();

	unsigned char buf[10];
	char buf2[6] = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	itoa(triggeredBySlot, buf2, 10);

	buf[0] = 'D';
	buf[1] = ':';
	buf[2] = 'U';
	buf[3] = ':';
	buf[4] = buf2[0];
	buf[5] = buf2[1];
	buf[6] = buf2[2];
	buf[7] = buf2[3];
	buf[8] = buf2[4];
	buf[9] = buf2[5];

	this->logging->addLog(buf, 10, true);
}

void CodeHandler::internalUnlock() {
	if (_forceClosed) {
		return;
	}

	this->_isOpen = true;
	this->resetOpenTimeStamp();
	this->actionHandler->unlock();
}

void CodeHandler::setCloseTimeStamp(unsigned long tstamp) {
	this->closeTimeStamp = tstamp;
}

void CodeHandler::setOpenTimeStamp(unsigned long tstamp) {
	this->openTimeStamp = tstamp;
}

bool CodeHandler::isLocked() {
	return !this->_isOpen;
}

void CodeHandler::triggerDoorAutomation() {
	if (this->isLocked()) {
		return;
	}

	bool retriggerCheck = (millis() - this->lastTriggeredDoorAutomation) < 500;

	if (retriggerCheck) {
		return;
	}

	this->lastTriggeredDoorAutomation = millis();
	this->actionHandler->triggerDoorAutomation();
}

void CodeHandler::changeState(char state) {

	if (state == 'N') {
		this->_forceClosed = false;
		this->lock(0);
		this->logging->addLog("STATE:N", 7, true);
	}

	if (state == 'U') {
		this->_forceClosed = false;
		this->internalUnlock();
		this->logging->addLog("STATE:U", 7, true);
	}

	if (state == 'L') {
		this->_forceClosed = true;
		this->lock(0);
		this->logging->addLog("STATE:L", 7, true);
	}
}

void CodeHandler::changeOpeningTime(unsigned char* data) {

	unsigned char buf[5];
	buf[0] = 0x44;
	buf[1] = data[4];
	buf[2] = data[5];
	buf[3] = data[6];
	buf[4] = data[7];

	dataStorage->writeCode(903, buf);
	delay(100);

	autoCloseAfterMs = data[4];
	autoCloseAfterMs = autoCloseAfterMs * 256 + data[5];
	autoCloseAfterMs = autoCloseAfterMs * 256 + data[6];
	autoCloseAfterMs = autoCloseAfterMs * 256 + data[7];

	this->logging->addLog("SET:AUTIME", 10, true);
}
