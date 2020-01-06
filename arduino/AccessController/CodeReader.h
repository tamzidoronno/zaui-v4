#include "CodeHandler.h"

#ifndef CodeReader_h
#define CodeReader_h

class CodeReader {

	public:
		virtual void setup() = 0;
		virtual bool isAvailable() = 0;
		virtual void getBuffer(unsigned char* retBuffer) = 0;
		virtual void clearBuffer() = 0;
		virtual bool check() = 0;

};


#endif
