#ifndef SleepHandler_h
#define SleepHandler_h

#include "DataStorage.h"


class SleepHandler {
  public:
    void wakeupLora();
    void delaySleepWithMs(unsigned long tstamp);
    void initalize(bool powerSaveMode, DataStorage *dataStorage);
    bool checkSleep();
    void setDeviceIdToLoraChip();

  private:
    DataStorage *_dataStorage;
    unsigned int getDeviceId();
    int getNetworkId();
    bool loraAwake = false;
};

#endif
