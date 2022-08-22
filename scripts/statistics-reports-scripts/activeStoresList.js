mongoServerAddress = '10.0.6.33';
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
        rowCreatedDate: 1
    })
    .forEach(store => {
        if(activeStoreIds.includes(store._id)){
            address = store.webAddress ? store.webAddress + ',' : '';
            if(store.webAddressPrimary){
                address = address + store.webAddressPrimary + ',';
            }
            if(store.additionalDomainNames && store.additionalDomainNames.length > 0){
                address = address + store.additionalDomainNames.join(',')
            }
            activeStores.push({
                id: store._id,
                address: address.split(',').filter(x=> x.length > 0).join(", "),
                creationDate: store.rowCreatedDate.toISOString()
            })
        }
    })

now = new Date();

activeStoreIds.forEach(storeId => {
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
        sentSmsCount = messageDb.getCollection('col_' + storeId + '_log')
                             .count({
                                 'className': 'com.thundashop.core.messagemanager.SmsMessage',
                                 'rowCreatedDate': {
                                     '$gte': ISODate(lastSixMonthAgoFirstDayOfMonth.toISOString()),
                                     '$lte': ISODate(lastSixMonthAgoLastDayOfMonth.toISOString())
                                 }
                             })
        ehfLog[key] = ehfLogCount;
        smsSent[key] = sentSmsCount;

        lastSixMonthAgoFirstDayOfMonth.setMonth(lastSixMonthAgoFirstDayOfMonth.getMonth() + 1);
        lastSixMonthAgoLastDayOfMonth.setMonth(lastSixMonthAgoLastDayOfMonth.getMonth() + 1);
    }

    store = activeStores.find( x => x.id === storeId);
    store.EHF = ehfLog;
    store.SMS = smsSent;
    printjson(store);
})

print('======== end of processing =========')