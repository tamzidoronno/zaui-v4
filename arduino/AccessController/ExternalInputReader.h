#ifndef ExternalInputReader_h
#define ExternalInputReader_h

#include "Clock.h"
#include "CodeHandler.h"
#include "Logging.h"

class ExternalInputReader {
	public:
		ExternalInputReader(Clock* clock, Logging* logging, CodeHandler* codeHandler);
		void checkButtons();
		void checkAlarms();
		void setup();
		void reset();

	private:
		volatile bool _alarmActivated1;
		volatile bool _alarmActivated2;

		volatile bool _exitButtonInsideTriggered;
		volatile bool _exitButtonOutsideTriggered;

		volatile int _timePushButtonPressMs = 0;

		Clock* _clock;
		Logging* _logging;
		CodeHandler* _codeHandler;

};

#endif
