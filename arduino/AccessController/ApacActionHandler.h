#include "ActionHandler.h"

#ifndef ApacActionHandler_h
#define ApacActionHandler_h

class ApacActionHandler : public ActionHandler {
	public:
		ApacActionHandler();
		void unlock();
		void lock();
		void setup();
		void triggerDoorAutomation();
	private:
		int strikeRelay = 14;
		int engineRelay = 15;
		int signalLight = 5;

		int red = 17;
		int green = 9;
		int blue = 8;

		void lightOff();
};
#endif
