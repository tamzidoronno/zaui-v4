/*
 * CodeHandler.cpp
 *
 *  Created on: Sep 8, 2019
 *      Author: ktonder
 */


#include "CodeHandler.h";
#include "Communication.h";


CodeHandler::CodeHandler(DataStorage &dataStorage, KeyPadReader &keypadReader, Communication &com) {
	this->dataStorage = dataStorage;
	this->keypadReader = keypadReader;
	this->communication = communication;
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

void CodeHandler::testCodes(unsigned char* codeFromPanel) {
	unsigned char buffer[16];

	for (int i=0; i<=10; i++) {

		this->dataStorage.getCode(i, buffer);

		if (this->compareCodes(buffer, codeFromPanel, i)) {
			char openBuff[10];
			sprintf(openBuff, "O:%5d", i);
			communication.writeEncrypted(openBuff, 7);

			digitalWrite(14, LOW);
			digitalWrite(PD5, LOW);

			delay(3000);
			digitalWrite(14, HIGH);
			digitalWrite(PD5, HIGH);

			break;
		}

		if (keypadReader.checkWiegand()) {
			break;
		}
	}
}
