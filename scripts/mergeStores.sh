# Used for merging two stores together with data.
# How to use: 
# 1. replace storeIds
# 2. copy script to dump folder
# 3. Run script
# 4. Use mongorestore to import data


rm -rf `find . |grep 13442b34-31e5-424c-bb23-a396b7aeb8ca`;
rm -rf `find . |grep all`;

mv ./ArxManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                           ./ArxManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./BookingComRateManagerManager_demo/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson    ./BookingComRateManagerManager_demo/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./ContentManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                       ./ContentManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./PmsManager_default/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                   ./PmsManager_default/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./StoreApplicationInstancePool/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson         ./StoreApplicationInstancePool/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./PmsInvoiceManager_demo/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson               ./PmsInvoiceManager_demo/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./TripleTexExcel/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                       ./TripleTexExcel/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./BookingEngineAbstract_default/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson        ./BookingEngineAbstract_default/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./TrackAndTraceManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                 ./TrackAndTraceManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./PmsInvoiceManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                    ./PmsInvoiceManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./ProductManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                       ./ProductManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./GetShopLockManager_demo/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson              ./GetShopLockManager_demo/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./OrderManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                         ./OrderManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./GetShopAccountingManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson             ./GetShopAccountingManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./WubookManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                        ./WubookManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./DibsManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                          ./DibsManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./PageManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                          ./PageManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./GetShopLockManager_default/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson           ./GetShopLockManager_default/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./BookingEngineAbstract/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                ./BookingEngineAbstract/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./GetShopLockSystemManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson             ./GetShopLockSystemManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./UtilManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                          ./UtilManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./StoreApplicationPool/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                 ./StoreApplicationPool/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./GetShopLockManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                   ./GetShopLockManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./UserManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                          ./UserManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./PmsManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                           ./PmsManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./PmsInvoiceManager_default/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson            ./PmsInvoiceManager_default/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./BookingComRateManagerManager_default/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson ./BookingComRateManagerManager_default/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./ListManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                          ./ListManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./BookingEngineAbstract_demo/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson           ./BookingEngineAbstract_demo/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./PmsManager_demo/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                      ./PmsManager_demo/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./TicketManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                        ./TicketManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./WubookManager_default/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                ./WubookManager_default/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./MessageManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                       ./MessageManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./StoreManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                         ./StoreManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./FikenAccountingSystem/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                ./FikenAccountingSystem/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./AccountingManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                    ./AccountingManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./MecaManager/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                          ./MecaManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
mv ./WubookManager_demo/col_b703b793-c7f4-4803-83bb-106cab891d6c.bson                   ./WubookManager_demo/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;

rm -rf ./PageManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
rm -rf ./ListManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
rm -rf ./StoreManager/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
rm -rf ./StoreApplicationInstancePool/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;
rm -rf ./StoreApplicationPool/col_13442b34-31e5-424c-bb23-a396b7aeb8ca.bson;

