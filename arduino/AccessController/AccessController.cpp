#include <Arduino.h>

#include "Communication.h"
#include "KeypadReader.h"
#include "DataStorage.h"
#include "CodeHandler.h"
#include "Logging.h"

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

CodeHandler codeHandler(&dataStorage, &keypadReader, &communication);

bool started = false;
bool lighstat = LOW;

bool foundData = false;

unsigned char bufferForWiegand[16];
unsigned char bufferForCommunication[16];

void initLora() {
	  Serial.begin(115200);

	  digitalWrite(PB5, LOW);
	  delay(20);
	  digitalWrite(PB5, HIGH);
	  delay(500);
	  Serial.print("AT+ADDRESS=250\r\n");
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

	initLora();
	dataStorage.setupDataStorageBus();
	keypadReader.setupWiegand();
	logging.init();

	pinMode(14, OUTPUT); // Engine
	pinMode(15, OUTPUT); // Strike
	pinMode(PD5, OUTPUT); // CP LIGHT
	pinMode(PB5, OUTPUT);

	digitalWrite(PB5, HIGH);
	digitalWrite(PD5, HIGH);
	digitalWrite(14, HIGH);


	logging.addLog("Started1", 8, true);
	logging.addLog("Started2", 8, true);
	logging.addLog("Started3", 8, true);
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
	communication.check();
	keypadReader.checkWiegand();

	if (keypadReader.isAvailable()) {
		keypadReader.getBuffer(bufferForWiegand);
		bool usedACode = codeHandler.testCodes(bufferForWiegand);
		if (usedACode) {
			keypadReader.clearBuffer();
		}
	}

	if (communication.isDataAvailable()) {
		communication.getData(bufferForCommunication);

		if (bufferForCommunication[0] == 'C') {
			unsigned int slotId = dataStorage.handleCodeMessage(bufferForCommunication);
			char buf[5];
			itoa(slotId, buf, 10);
			logging.addLog("C:S:" + slotId, 9, true);
			return;
		}

		if (bufferForCommunication[0] == 'A' && bufferForCommunication[1] == 'C' && bufferForCommunication[2] == 'K') {
			logging.handleAckMessage(bufferForCommunication);
			return;
		}
	}

	logging.runSendCheck(&communication);
}
