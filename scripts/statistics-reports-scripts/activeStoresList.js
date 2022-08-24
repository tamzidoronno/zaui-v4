mongoServerAddress = 'localhost';
conn = new Mongo(mongoServerAddress + ":27018");
userDb = conn.getDB("UserManager");
storeDb = conn.getDB("StoreManager");
orderDb = conn.getDB("OrderManager");
messageDb = conn.getDB("MessageManager");

customerUser = 10

activeStoreIds = [];
activeStores = [];

sixMonthsAgoDate = new Date();
sixMonthsAgoDate.setMonth(sixMonthsAgoDate.getMonth() - 6);

userDb.getCollectionNames().forEach(collectionName => {
    userDb[collectionName]
        .find({
                 "className":"com.thundashop.core.usermanager.data.User",
                 "type":{$ne: customerUser},
                 "lastLoggedIn": {
                    "$gte": ISODate(sixMonthsAgoDate.toISOString())
                 }
               },
               {
                    _id: 1,
                    emailAddress: 1,
                    storeId: 1,
                    lastLoggedIn: 1
               })
        .forEach(user => {
                if(!activeStoreIds.includes(user.storeId)){
                        activeStoreIds.push(user.storeId);
                    }
                });
})

print('No of total active store: ' + activeStoreIds.length)

storeDb.getCollection('col_all')
    .find({"className" : "com.thundashop.core.storemanager.data.Store"}, {
        _id: 1,
        webAddress: 1,
        webAddressPrimary: 1,
        additionalDomainNames: 1,
        rowCreatedDate: 1,
        "configuration.defaultPrefix": 1
    })
    .forEach(store => {
        if(activeStoreIds.includes(store._id)){
            address = store.webAddress ? store.webAddress : '';
            if(store.webAddressPrimary && store.webAddressPrimary !== store.webAddress){
                address = address + ', ' + store.webAddressPrimary;
            }
            if(!address && store.additionalDomainNames && store.additionalDomainNames.length > 0){
                address = address + ', ' + store.additionalDomainNames.join(', ')
            }
            activeStores.push({
                id: store._id,
                address: address,
                creationDate: store.rowCreatedDate.toISOString(),
                defaultPrefix: store.configuration.defaultPrefix
            })
        }
    })

now = new Date();

activeStoreIds.forEach(storeId => {
    store = activeStores.find( x => x.id === storeId);
    ehfLog = {};
    smsSent = {};

    lastSixMonthAgoFirstDayOfMonth = new Date(now);
    lastSixMonthAgoFirstDayOfMonth.setMonth(now.getMonth() - 3);
    lastSixMonthAgoFirstDayOfMonth.setDate(1);
    lastSixMonthAgoFirstDayOfMonth.setUTCHours(0,0,0,0);

    lastSixMonthAgoLastDayOfMonth = new Date(now);
    lastSixMonthAgoLastDayOfMonth.setMonth(now.getMonth() - 2);
    lastSixMonthAgoLastDayOfMonth.setDate(0);
    lastSixMonthAgoLastDayOfMonth.setUTCHours(23,59,59);

    while(lastSixMonthAgoFirstDayOfMonth.getMonth() <= now.getMonth()){
        key = lastSixMonthAgoFirstDayOfMonth.getFullYear() + '.' + (lastSixMonthAgoFirstDayOfMonth.getMonth() + 1);
        ehfLogCount = orderDb.getCollection('col_' + storeId)
                              .count({
                                  'className': 'com.thundashop.core.ordermanager.data.EhfSentLog',
                                  'rowCreatedDate': {
                                      '$gte': ISODate(lastSixMonthAgoFirstDayOfMonth.toISOString()),
                                      '$lte': ISODate(lastSixMonthAgoLastDayOfMonth.toISOString())
                                  }
                              })
        domesticSmsCount = 0;
        internationalSmsCount = 0;
        messageDb.getCollection('col_' + storeId + '_log')
             .find({
                 'className': 'com.thundashop.core.messagemanager.SmsMessage',
                 'rowCreatedDate': {
                     '$gte': ISODate(lastSixMonthAgoFirstDayOfMonth.toISOString()),
                     '$lte': ISODate(lastSixMonthAgoLastDayOfMonth.toISOString())
                 }
             },
             {
             message: 1,
             prefix: 1
             }).forEach(sms => {
                if(sms.prefix.trim() == store.defaultPrefix){
                    domesticSmsCount = domesticSmsCount + Math.ceil(sms.message.length/130);
                }
                else{
                    internationalSmsCount = internationalSmsCount + Math.ceil(sms.message.length/130);
                }

             })

        ehfLog[key] = ehfLogCount;
        smsSent[key] = {
            domestic: domesticSmsCount,
            international: internationalSmsCount,
            total: domesticSmsCount + internationalSmsCount
        };

        lastSixMonthAgoFirstDayOfMonth.setMonth(lastSixMonthAgoFirstDayOfMonth.getMonth() + 1);
        lastSixMonthAgoLastDayOfMonth.setMonth(lastSixMonthAgoLastDayOfMonth.getMonth() + 1);
    }

    store.EHF = ehfLog;
    store.SMS = smsSent;
    delete store.defaultPrefix;
    printjson(store);
})

print('======== end of processing =========')