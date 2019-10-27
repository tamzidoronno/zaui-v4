#include <Arduino.h>

#include "Communication.h"
#include "KeypadReader.h"
#include "DataStorage.h"
#include "CodeHandler.h"
#include "Logging.h"
#include "ExternalInputReader.h"

#define disk1 0x50

int cycles = 0;

byte key[16] = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F};
byte ciphertext[16] = {0xE0, 0xC4, 0xE0, 0xD8, 0x6A, 0x7B, 0x04, 0x30, 0xD8, 0xCD, 0xB7, 0x80, 0x70, 0xB4, 0xC5, 0x5A};

Clock clock;

DataStorage dataStorage;

Logging logging(&dataStorage, &clock);
Communication communication(key, ciphertext, &clock);

KeyPadReader keypadReaderObj(&clock);
KeyPadReader& keypadReader = keypadReaderObj;

CodeHandler codeHandler(&dataStorage, &keypadReader, &communication, &logging, &clock);

ExternalInputReader externalReader(&clock, &logging, &codeHandler);

bool started = false;
bool lighstat = LOW;

bool foundData = false;

unsigned char bufferForWiegand[16];
unsigned char bufferForCommunication[16];

unsigned int getDeviceId() {
	unsigned char buf[16];
	dataStorage.getCode(5500, buf);

	if (buf[0] != 0x44) {
		return 65000;
	}

	char retVal[6];
	retVal[0] = buf[1];
	retVal[1] = buf[2];
	retVal[2] = buf[3];
	retVal[3] = buf[4];
	retVal[4] = buf[5];
	retVal[5] = buf[6];

	return atoi(retVal);
}

void setDeviceIdToLoraChip() {
	String address = "AT+ADDRESS=";
	address = address + getDeviceId();
	address = address + "\r\n";
	Serial.print(address);
}

void saveDeviceId(char* msg) {
	unsigned char data[16] = {
			0x44, msg[4], msg[5], msg[6],
			msg[7], msg[8], 0x00, 0xFF,
			0xFF, 0xFF, 0xFF, 0xFF,
			0xFF, 0xFF, 0xFF, 0xFF
	};

	dataStorage.writeCode(5500, data);
	setDeviceIdToLoraChip();
	communication.writeEncrypted("CID", 3);
}

void initLora() {
	// Set reset of LoraChip to be high.

	pinMode(PB5, OUTPUT);

	digitalWrite(PB5, HIGH);
	delay(500);
	digitalWrite(PB5, LOW);
	delay(500);

	digitalWrite(PB5, HIGH);
	delay(500);
	setDeviceIdToLoraChip();
	delay(200);
	Serial.print("AT+NETWORKID=5\r\n");
	delay(200);
	Serial.print("AT+MODE=0\r\n");
	delay(200);
	Serial.print("AT+BAND=868500000\r\n");
	delay(200);
	Serial.print("AT+PARAMETER=10,7,1,7\r\n");
	delay(200);
}

void setup()
{
	Serial.begin(115200);

	dataStorage.setupDataStorageBus();
	initLora();
	keypadReader.setupWiegand();
	logging.init();
	codeHandler.setup();

	logging.addLog("Started", 7, true);

	pinMode(PD6, INPUT);
	digitalWrite(PD6, HIGH);

	pinMode(16, OUTPUT); // Strike
}

void toggleLight() {
	if (lighstat == LOW) {
		lighstat = HIGH;
	} else {
		lighstat = LOW;
	}

	cycles = 0;
	digitalWrite(PD5, lighstat);
}

void aliveDebugLight() {
	if (cycles > 30000) {
		toggleLight();
	}
	cycles++;
}

void loop()
{

//	pinMode(14, OUTPUT);
//	digitalWrite(14, LOW);
//	delay(4000);
//	digitalWrite(14, HIGH);
//	delay(4000);

	delay(1);
	communication.check();
	keypadReader.checkWiegand();
	codeHandler.check();
	externalReader.checkButtons();
	externalReader.checkAlarms();
//
	if (keypadReader.isAvailable()) {
		keypadReader.getBuffer(bufferForWiegand);
		bool usedACode = codeHandler.testCodes(bufferForWiegand);
		if (usedACode) {
			keypadReader.clearBuffer();
		}
	}
//
	if (communication.isDataAvailable()) {
		communication.getData(bufferForCommunication);

		if (bufferForCommunication[0] == 'C' && bufferForCommunication[1] == 'I' && bufferForCommunication[2] == 'D') {
			saveDeviceId(bufferForCommunication);
			return;
		}

		if (bufferForCommunication[0] == 'C') {
			unsigned int slotId = dataStorage.handleCodeMessage(bufferForCommunication);
			char buf[6] = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
			itoa(slotId, buf, 10);

			char msg[10];
			msg[0] = 'C';
			msg[1] = ':';

			// Removed if the code startd with a 0xFF;
			if (bufferForCommunication[3] == 0xFF) {
				msg[2] = 'R';
			} else {
				msg[2] = 'S';
			}

			msg[3] = ':';
			msg[4] = buf[0];
			msg[5] = buf[1];
			msg[6] = buf[2];
			msg[7] = buf[3];
			msg[8] = buf[4];
			msg[9] = buf[5];

			logging.addLog(msg, 10, true);
			return;
		}

		if (bufferForCommunication[0] == 'A' && bufferForCommunication[1] == 'C' && bufferForCommunication[2] == 'K') {
			logging.handleAckMessage(bufferForCommunication);
			return;
		}

		if (bufferForCommunication[0] == 'F' && bufferForCommunication[1] == 'O' && bufferForCommunication[2] == 'R') {
			codeHandler.toggleForceState();
			return;
		}
	}
//
	logging.runSendCheck(&communication);

}
