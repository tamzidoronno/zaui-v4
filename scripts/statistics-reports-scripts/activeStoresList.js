mongoServerAddress = 'localhost';
conn = new Mongo(mongoServerAddress + ":27018");
userDb = conn.getDB("UserManager");
storeDb = conn.getDB("StoreManager");
orderDb = conn.getDB("OrderManager");
messageDb = conn.getDB("MessageManager");
pmsManager = conn.getDB("PmsManager_default");

customerUser = 10;
now = new Date();

activeStoreIds = [];
activeStores = [];

findOutActiveStoresBasedOnLastSixMonthLogin();
getEHFAndSMSDataForActiveStores();
calculateRevenueForEveryStore();

function findOutActiveStoresBasedOnLastSixMonthLogin(){
    print('======== fetching active stores based on last 6 months login =========')
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

    storeDb.getCollection('col_all')
        .find({"className" : "com.thundashop.core.storemanager.data.Store"}, {
            _id: 1,
            "configuration.shopName": 1,
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
                    name: store.configuration.shopName || '',
                    address: address,
                    creationDate: store.rowCreatedDate.toISOString(),
                    defaultPrefix: store.configuration.defaultPrefix
                })
            }
        })

    print('No of total active store: ' + activeStoreIds.length)
}

function getEHFAndSMSDataForActiveStores(){
    print('======== started getting EHF + SMS data of active stores =========')
    activeStoreIds.forEach(storeId => {
        store = activeStores.find( x => x.id === storeId);
        ehfLog = {};
        smsSent = {};

        lastSixMonthAgoFirstDayOfMonth = new Date(now);
        lastSixMonthAgoFirstDayOfMonth.setMonth(now.getMonth() - 1);
        lastSixMonthAgoFirstDayOfMonth.setDate(1);
        lastSixMonthAgoFirstDayOfMonth.setUTCHours(0,0,0,0);

        lastSixMonthAgoLastDayOfMonth = new Date(now);
        lastSixMonthAgoLastDayOfMonth.setMonth(now.getMonth() - 0);
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
        // printjson(store);
    })
    print('======== finished EHF + SMS data fetching =========')
}

function calculateRevenueForEveryStore(){
    print('======== started calculating revenue of active stores =========')
    activeStoreIds.forEach(storeId => {
        // if(storeId !== '668ae391-3ef4-4cae-a0ef-f9ed25515d73') return;
        store = activeStores.find( x => x.id === storeId);
        n = 1;
        lastNMonthAgoFirstDayOfMonth = new Date(now);
        lastNMonthAgoFirstDayOfMonth.setMonth(now.getMonth() - n);
        lastNMonthAgoFirstDayOfMonth.setDate(1);
        lastNMonthAgoFirstDayOfMonth.setUTCHours(0,0,0,0);

        lastNMonthAgoLastDayOfMonth = new Date(now);
        lastNMonthAgoLastDayOfMonth.setMonth(now.getMonth() - (n -1));
        lastNMonthAgoLastDayOfMonth.setDate(0);
        lastNMonthAgoLastDayOfMonth.setUTCHours(23,59,59);
        revenue = {};

        while(lastNMonthAgoFirstDayOfMonth.getMonth() <= now.getMonth()){
            revenueForThisMonth = 0;
            key = lastNMonthAgoFirstDayOfMonth.getFullYear() + '.' + (lastNMonthAgoFirstDayOfMonth.getMonth() + 1);

            dayOfMonth = new Date(lastNMonthAgoFirstDayOfMonth);
            dayWiseRevenue = {};
            while(lastNMonthAgoLastDayOfMonth > dayOfMonth){
                dayKey = dayOfMonth.getDate().toString().padStart(2, '0') + '-' + (dayOfMonth.getMonth() + 1).toString().padStart(2, '0') + '-' + dayOfMonth.getFullYear()
                dayWiseRevenue[dayKey] = [];
                dayOfMonth.setDate(dayOfMonth.getDate() + 1);
            }

            pmsManager.getCollection('col_' + storeId)
            .find(
            {
                'className': 'com.thundashop.core.pmsmanager.PmsBooking',
                'rooms.date.start': {
                    '$lt': ISODate(lastNMonthAgoLastDayOfMonth.toISOString())
                },
                'rooms.date.end': {
                    '$gt': ISODate(lastNMonthAgoFirstDayOfMonth.toISOString())
                },
                confirmed: true,
                isDeleted: false,
                testReservation: false
            },
            {
                "rooms.priceMatrix": 1,
                "rooms.pmsBookingRoomId": 1,
                "rooms.deleted": 1,
                "rooms.addons.date": 1,
                "rooms.addons.price": 1,
                "rooms.addons.count": 1,
                "rooms.addons.isIncludedInRoomPrice": 1,
                incrementBookingId: 1
            }).forEach(booking => {
                Object.keys(dayWiseRevenue)
                    .forEach(key => {
                        booking.rooms.forEach(room => {
                        if(room.deleted) return;
                            if(room.priceMatrix && room.priceMatrix[key]){
                                roomPrice = Math.round(room.priceMatrix[key]);
                                includedAddonPrice = 0;
                                if(room.addons && room.addons.length > 0){
                                    includedAddonPrice = room.addons
                                        .filter(x=> x.isIncludedInRoomPrice && x.date.toISOString().substr(0,10).split('-').reverse().join('-') === key)
                                        .reduce((a,b) => a + b.price * b.count, 0);
                                }
                                roomPriceExcludingAddon = roomPrice - includedAddonPrice;
                                revenueForThisMonth += roomPriceExcludingAddon;
                                dayWiseRevenue[key].push(
                                    {   bookingId: booking.incrementBookingId,
                                        pmsBookingRoomId: room.pmsBookingRoomId,
                                        roomPriceExcludingAddon: roomPriceExcludingAddon
                                    });
                            }
                        })
                    })
            });

            Object.keys(dayWiseRevenue).forEach(key => {
                dayWiseRevenue[key].push({
                dayTotal: dayWiseRevenue[key].reduce((a,b) => a + b.roomPriceExcludingAddon, 0)
                })
            })

//            revenue[key] = {
//                total: revenueForThisMonth,
//                dayWiseRevenue: dayWiseRevenue
//            };

            revenue[key] = revenueForThisMonth;

            lastNMonthAgoFirstDayOfMonth.setMonth(lastNMonthAgoFirstDayOfMonth.getMonth() + 1);
            lastNMonthAgoLastDayOfMonth.setMonth(lastNMonthAgoLastDayOfMonth.getMonth() + 1);
        }

        store.revenue = revenue;
        printjson(store);
    });
    print('======== finished revenue calculation =========')
}

print('======== end of processing =========')
print('Total time elapsed: ' + Math.round(Math.abs(new Date() - now)/1000) + ' S')