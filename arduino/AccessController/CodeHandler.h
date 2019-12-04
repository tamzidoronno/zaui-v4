/**
 *  Listening for the following commands:
 *  CXXXXYYYYYYYYYOO 						Sets Code
 *   - X = Slotnumber						Example: 0001
 *   - Y = Code, use 0xFF to stop code.		Example: 08020100FFFFFFFFFF (sets the code 8210)
 *   - O = Signature bytes					Example: 0x2222 ( random byte )
 *
 */

#ifndef CodeHandler_h
#define CodeHandler_h

#include "DataStorage.h";
#include "KeypadReader.h";
#include "Communication.h";
#include "Logging.h";
#include "Clock.h";

class CodeHandler {
	public:
		CodeHandler(DataStorage* dataStorage, KeyPadReader* keypadReader, Communication* com, Logging* logging, Clock* clock);
		bool CodeHandler::testCodes(unsigned char* codeFromPanel);
		void setCloseTimeStamp(unsigned long tstamp);
		void setOpenTimeStamp(unsigned long tstamp);
		void unlock(unsigned int triggeredBySlot);
		void triggerDoorAutomation();
		void lock(unsigned int triggeredBySlot);
		bool isLocked();
		void setup();
		void check();
		void toggleForceState();

	private:
		DataStorage* dataStorage;
		KeyPadReader* keypadReader;
		Communication* communication;
		Logging* logging;
		Clock* clock;
		bool CodeHandler::compareCodes(unsigned char* savedCode, unsigned char* typedCode, int codeSlot);

		unsigned long closeTimeStamp;
		unsigned long openTimeStamp;
		bool _forceOpen;
		bool _isOpen = false;
		void CodeHandler::resetOpenTimeStamp();
		void CodeHandler::resetCloseTimeStamp();
		void internalUnlock();
};

#endif
