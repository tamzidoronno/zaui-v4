mongoServerAddress = 'localhost';
conn = new Mongo(mongoServerAddress + ":27018");
userDb = conn.getDB("UserManager");
storeDb = conn.getDB("StoreManager");
customerUser = 10

activeStoreIds = [];
activeStores = [];

sixMonthsAgoDate = new Date();
sixMonthsAgoDate.setMonth(sixMonthsAgoDate.getMonth() - 6);

userDb.getCollectionNames().forEach(collectionName => {
    // print('checking for collection: ' + collectionName);
    userDb[collectionName].find({"className":"com.thundashop.core.usermanager.data.User", "type":{$ne: customerUser}})
    .forEach(user => {
        if(user.lastLoggedIn && new Date(user.lastLoggedIn) > sixMonthsAgoDate){
            if(!activeStoreIds.includes(user.storeId)){
                activeStoreIds.push(user.storeId);
            }
        }
    })
})

print('No of total active store: ' + activeStoreIds.length)

storeDb.getCollection('col_all')
    .find({"className" : "com.thundashop.core.storemanager.data.Store"})
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
                address: [...new Set(address.split(',').filter(x=> x.length > 0))],
                creationDate: store.rowCreatedDate.toISOString()
            })
        }
    })

printjson(activeStores)