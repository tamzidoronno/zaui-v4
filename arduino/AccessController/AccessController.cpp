#include <Arduino.h>

#include "Communication.h"
#include "KeypadReader.h"
#include "GS8015KeyReader.h"
#include "DataStorage.h"
#include "ApacActionHandler.h"
#include "GS8015ActionHandler.h"
#include "CodeHandler.h"
#include "Logging.h"
#include "ExternalInputReader.h"
#include <avr/wdt.h>


#include <util/atomic.h>

void(* resetAfterDeviceIdSet) (void) = 0;//declare reset function at address 0

#define disk1 0x50

/**
 * The different type of supported lock types are as following
 * 1 : Wiegand Reader
 * 2 : GS8015 Numeric Input Reader.
 */
#define LOCKTYPE 1

int cycles = 0;

Clock clock;

DataStorage dataStorage;

Logging logging(&dataStorage, &clock);
Communication communication(&clock, &dataStorage);

// Readers
#if(LOCKTYPE == 1)
	int resetLoraChipPin = PB5;
	KeyPadReader readerObj(&clock);
	KeyPadReader& keypadReader = readerObj;
	ApacActionHandler actionHandlerObj;
#else
	int resetLoraChipPin = -1;
	GS8015KeyReader readerObj(&clock);
	GS8015KeyReader& keypadReader = readerObj;
	GS8015ActionHandler actionHandlerObj;
#endif


CodeHandler codeHandler(&dataStorage, &keypadReader, &communication, &logging, &clock, &actionHandlerObj);

ExternalInputReader externalReader(&clock, &logging, &codeHandler);

bool started = false;
bool lighstat = LOW;

bool foundData = false;

unsigned char bufferForWiegand[16];
unsigned char bufferForCommunication[16];

const char signature [] = "GetShop";
char * p = (char *) malloc (sizeof (signature));

unsigned int getDeviceId() {
	unsigned char buf[16];
	dataStorage.getCode(902, buf);

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

bool checkIfColdStart() {
  if (strcmp (p, signature) == 0) { // signature is in RAM this was reset
    return false;
  }
  else {  // signature not in RAM this was a power on
    // add the signature to be retained in memory during reset
    memcpy (p, signature, sizeof signature);  // copy signature into RAM
    return true;
  }
}

void setDeviceIdToLoraChip() {
	String address = "AT+ADDRESS=";
	address = address + getDeviceId();
	address = address + "\r\n";
	Serial.print(address);
}

void saveDeviceId(unsigned char* msg) {
	unsigned char data[16] = {
			0x44, msg[4], msg[5], msg[6],
			msg[7], msg[8], 0x00, 0xFF,
			0xFF, 0xFF, 0xFF, 0xFF,
			0xFF, 0xFF, 0xFF, 0xFF
	};

	dataStorage.writeCode(902, data);
//	setDeviceIdToLoraChip();
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

void initLora() {
	// Set reset of LoraChip to be high.

	if (resetLoraChipPin > 0) {
		pinMode(resetLoraChipPin, OUTPUT);
		delay(500);
		digitalWrite(resetLoraChipPin, LOW);
		delay(500);
		digitalWrite(resetLoraChipPin, HIGH);
	}


	delay(500);
	setDeviceIdToLoraChip();
	delay(500);
	Serial.print("AT+NETWORKID=5\r\n");
	delay(500);
	Serial.print("AT+MODE=0\r\n");
	delay(500);
	Serial.print("AT+BAND=868500000\r\n");
	delay(500);
	Serial.print("AT+PARAMETER=10,7,1,7\r\n");
	delay(500);

	wdt_reset();
}

void setMillis(unsigned long ms)
{
    extern unsigned long timer0_millis;
    ATOMIC_BLOCK (ATOMIC_RESTORESTATE) {
        timer0_millis = ms;
    }
}

void setup()
{
	keypadReader.setCodeHandler(&codeHandler);

	wdt_enable(WDTO_4S);

	Serial.begin(115200);

	dataStorage.setupDataStorageBus();
	initLora();
	communication.setup();
	keypadReader.setup();
	logging.init();
	codeHandler.setup();
	actionHandlerObj.setup();

	wdt_reset();
  
	if (checkIfColdStart()) {
		logging.addLog("Started:N", 9, true);
	} else {
		logging.addLog("Started:W", 9, true);
	}
}


void aliveDebugLight() {
	if (cycles > 200) {
		toggleLight();
	}
	cycles++;
}

void loop()
{

//	aliveDebugLight();

	delay(1);

	wdt_reset();

	communication.check();
	keypadReader.check();
	codeHandler.check();
	externalReader.checkButtons();
	externalReader.checkAlarms();

	if (keypadReader.isAvailable()) {
		keypadReader.getBuffer(bufferForWiegand);
		bool usedACode = codeHandler.testCodes(bufferForWiegand);
		if (usedACode) {
			keypadReader.clearBuffer();
		}
	}

	if (communication.isDataAvailable()) {
		communication.getData(bufferForCommunication);

		if (bufferForCommunication[0] == 'P' && bufferForCommunication[1] == 'I' && bufferForCommunication[2] == 'N' && bufferForCommunication[3] == 'G') {
			logging.addLog("PONG", 4, true);
			return;
		}

		if (bufferForCommunication[0] == 'C' && bufferForCommunication[1] == 'I' && bufferForCommunication[2] == 'D') {
			saveDeviceId(bufferForCommunication);
			setDeviceIdToLoraChip();
			communication.initializeEncryption();
			resetAfterDeviceIdSet();
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

		// CHANGES THE STATE OF THE LOCK
		if (bufferForCommunication[0] == 'S' && bufferForCommunication[1] == ':' && bufferForCommunication[2] == 'S') {
			codeHandler.changeState(bufferForCommunication[4]);
			return;
		}

		// SETS THE AUTO CLOSE TIME
		if (bufferForCommunication[0] == 'S' && bufferForCommunication[1] == ':' && bufferForCommunication[2] == 'A') {
			codeHandler.changeOpeningTime(bufferForCommunication);
			return;
		}

		// SETS THE AUTO CLOSE TIME
		if (bufferForCommunication[0] == 'S' && bufferForCommunication[1] == ':' && bufferForCommunication[2] == 'D') {
			dataStorage.deleteAllCodes();
			logging.addLog("ACODEDEL", 8, true);
			return;
		}

		// SETS THE GID
		if (bufferForCommunication[0] == 'S' && bufferForCommunication[1] == ':' && bufferForCommunication[2] == 'G') {
			dataStorage.writeCode(904, bufferForCommunication);
			logging.addLog("GIDCHANGED", 10, true);
			return;
		}

		// SETS ENCRYPTIONKEY
		if (bufferForCommunication[0] == 'S' && bufferForCommunication[1] == 'C') {
			bool codeUpdated = communication.setEncryptionKey(bufferForCommunication);
			if (!codeUpdated) {
				logging.addLog("ENC:SET:2", 9, true);
			} else {
				if (bufferForCommunication[2] == 0x01) {
					logging.addLog("ENC:SET:1", 9, true);
				}
				if (bufferForCommunication[2] == 0x02) {
					logging.addLog("ENC:SET:2", 9, true);
				}
			}
			return;
		}
	}

	logging.runSendCheck(&communication);
}
