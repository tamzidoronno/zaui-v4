#ifndef CodeHandler_h
#define CodeHandler_h

#include "DataStorage.h";
#include "KeypadReader.h";
#include "Communication.h";

class CodeHandler {
	public:
		CodeHandler(DataStorage &dataStorage, KeyPadReader &keypadReader, Communication &com);
		void CodeHandler::testCodes(unsigned char* codeFromPanel);

	private:
		DataStorage &dataStorage;
		KeyPadReader &keypadReader;
		Communication &communication;
		bool CodeHandler::compareCodes(unsigned char* savedCode, unsigned char* typedCode, int codeSlot);

};

#endif
