#include <Arduino.h>

#include "Communication.h"
#include "KeypadReader.h"
#include "DataStorage.h"
#include "CodeHandler.h"


#define disk1 0x50

int cycles = 0;

byte key[16] = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F};
byte ciphertext[16] = {0xE0, 0xC4, 0xE0, 0xD8, 0x6A, 0x7B, 0x04, 0x30, 0xD8, 0xCD, 0xB7, 0x80, 0x70, 0xB4, 0xC5, 0x5A};

DataStorage storage;
Communication communication(key, ciphertext);

KeyPadReader keypadReader;
CodeHandler codeHandler(storage, keypadReader, communication);



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
//	  Serial.print("AT+RESET\r\n");
//	  delay(200);
//	  Serial.print("AT+IPR=115200\r\n");
//	  delay(200);
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
	storage.setupDataStorageBus();
	keypadReader.setupWiegand();



	pinMode(14, OUTPUT); // Engine
	pinMode(15, OUTPUT); // Strike
	pinMode(PD5, OUTPUT); // CP LIGHT
	pinMode(PB5, OUTPUT);

	digitalWrite(PB5, HIGH);
	digitalWrite(PD5, HIGH);
	digitalWrite(14, HIGH);

	communication.writeEncrypted("Started", 7);
	delay(1000);
}

void toggleLight() {
	if (lighstat == LOW) {
		lighstat = HIGH;
	} else {
		lighstat = LOW;
	}

	cycles = 0;
	digitalWrite(PD5, lighstat);
//	communication.writeEncrypted("test", 4);
}

void aliveDebugLight() {
	if (cycles > 30000) {
		toggleLight();
	}
	cycles++;
}

void loop()
{

// Check for data at the different inputs.

	communication.check();
	keypadReader.checkWiegand();

	if (keypadReader.isAvailable()) {
		keypadReader.getBuffer(bufferForWiegand);
		codeHandler.testCodes(bufferForWiegand);
	}

	if (communication.isDataAvailable()) {
		communication.getData(bufferForCommunication);

		if (storage.handleMessage(bufferForCommunication)) {
			communication.writeEncrypted("Code stored", 11);
		}
	}

//	communication.writeEncrypted("C", 1);
//	aliveDebugLight();
}
