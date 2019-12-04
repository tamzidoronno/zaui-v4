/*
 * CodeHandler.cpp
 *
 *  Created on: Sep 8, 2019
 *      Author: ktonder
 */


#include "CodeHandler.h";
#include "Communication.h";
#include "Arduino.h"

#define strikeRelay 14
#define engineRelay 15

static char unsigned resetCode[16] = {
		0x00, 0x00, 0x00, 0x00,
		0x01, 0x02, 0x03, 0x01,
		0x01, 0x08, 0x02, 0x04,
		0x03, 0xFF, 0xFF, 0xFF
};

CodeHandler::CodeHandler(DataStorage* dataStorage, KeyPadReader* keypadReader, Communication* commu, Logging *logging, Clock* clock) {
	this->dataStorage = dataStorage;
	this->keypadReader = keypadReader;
	this->communication = commu;
	this->logging = logging;
	this->clock = clock;
	this->_forceOpen = false;
	this->resetCloseTimeStamp();
	this->resetOpenTimeStamp();
	this->_isOpen = false;
}

void CodeHandler::setup() {
	pinMode(engineRelay, OUTPUT); // Engine
	pinMode(strikeRelay, OUTPUT); // Strike
	pinMode(PD5, OUTPUT); // CP LIGHT

	digitalWrite(PD5, HIGH);
	digitalWrite(engineRelay, LOW);
	digitalWrite(strikeRelay, LOW);
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

	for (unsigned int i=0; i<=2000; i++) {

		dataStorage->getCode(i, buffer);

		if (this->compareCodes(buffer, codeFromPanel, i)) {

			if (this->isLocked()) {
				this->unlock(i);
			} else {
				this->triggerDoorAutomation();
			}

			return true;
		}

		if (keypadReader->checkWiegand()) {
			break;
		}
	}

	if (this->compareCodes(resetCode, codeFromPanel, 0)) {
		dataStorage->resetAll();
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

	digitalWrite(strikeRelay, LOW);
	digitalWrite(PD5, HIGH);

	this->logging->addLog("D:LOCKED", 8, true);
	this->_isOpen = false;
}

/**
 * triggeredBySlot = user slot code used for opening.
 	 	 	 	 	 if triggeredBySlot = 0, then its triggered by system.
 	 	 	 	 	 if triggeredBySlot = 32767, then its triggered by exit button (inside)
 */
void CodeHandler::unlock(unsigned int triggeredBySlot) {
	this->setCloseTimeStamp(millis() + 5000);

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
	this->_isOpen = true;
	this->resetOpenTimeStamp();
	digitalWrite(strikeRelay, HIGH);
	digitalWrite(PD5, LOW);
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

	delay(200);
	digitalWrite(engineRelay, HIGH);
	delay(100);
	digitalWrite(engineRelay, LOW);
}

void CodeHandler::toggleForceState() {

	if (this->_forceOpen) {
		this->lock(0);
	} else {
		this->internalUnlock();
		this->logging->addLog("FORCEDOPEN", 10, true);
	}

	this->_forceOpen = !this->_forceOpen;
}
