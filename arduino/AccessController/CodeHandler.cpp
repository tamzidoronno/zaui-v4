/*
 * CodeHandler.cpp
 *
 *  Created on: Sep 8, 2019
 *      Author: ktonder
 */


#include "CodeHandler.h";
#include "Communication.h";


CodeHandler::CodeHandler(DataStorage* dataStorage, KeyPadReader* keypadReader, Communication* commu) {
	this->dataStorage = dataStorage;
	this->keypadReader = keypadReader;
	this->communication = commu;
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

	for (int i=0; i<=2000; i++) {

		dataStorage->getCode(i, buffer);

		if (this->compareCodes(buffer, codeFromPanel, i)) {
			digitalWrite(14, LOW);
			digitalWrite(PD5, LOW);

			delay(3000);
			digitalWrite(14, HIGH);
			digitalWrite(PD5, HIGH);

			return true;
		}

		if (keypadReader->checkWiegand()) {
			break;
		}
	}

	return false;
}
