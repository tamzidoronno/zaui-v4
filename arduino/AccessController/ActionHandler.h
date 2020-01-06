/**
 * And actionhandler is an abstract class.
 *
 * The a few main functions
 * 1. Unlock the door
 * 2. Lock the door.
 * 3. Start sliding door automation if the hardwarew supports it.
 */

#ifndef ActionHandler_h
#define ActionHandler_h

class ActionHandler {
	public:
		virtual void unlock() = 0;
		virtual void lock() = 0;
		virtual void setup() = 0;
		virtual void triggerDoorAutomation() = 0;
};

#endif
