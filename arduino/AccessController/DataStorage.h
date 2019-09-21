#ifndef DataStorage_h
#define DataStorage_h

class DataStorage {
   public:
	  DataStorage();
	  void DataStorage::writeCode(unsigned int slot, unsigned char* code);
	  void DataStorage::setupDataStorageBus();
	  void DataStorage::getCode(unsigned int slot, unsigned char* buffer);
	  unsigned int DataStorage::handleCodeMessage(unsigned char* msgFromGateWay);

   private:
	  void DataStorage::writeEEPROMPage(unsigned int eeAddress, unsigned char* data);
	  void DataStorage::readEEPROM(int deviceaddress, unsigned int eeaddress, unsigned char* data, unsigned int num_chars);
};

#endif
