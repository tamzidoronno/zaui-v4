#ifndef Clock_h
#define Clock_h

/**
 * Clock implementation based on the last received message timestamp.
 *
 * Needs adjusting each time receieving a message from the server.
 *
 */

class Clock {
	public:
		Clock();

		void adjustClock(unsigned long timestamp);
		long getTime();
		void Clock::getTimeChar(char* charBuf);

	private:
		volatile unsigned long diffSinceStartup;
};

#endif
