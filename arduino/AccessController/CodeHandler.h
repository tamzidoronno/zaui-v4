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

class CodeHandler {
	public:
		CodeHandler(DataStorage* dataStorage, KeyPadReader* keypadReader, Communication* com);
		bool CodeHandler::testCodes(unsigned char* codeFromPanel);

	private:
		DataStorage* dataStorage;
		KeyPadReader* keypadReader;
		Communication* communication;
		bool CodeHandler::compareCodes(unsigned char* savedCode, unsigned char* typedCode, int codeSlot);
};

#endif
