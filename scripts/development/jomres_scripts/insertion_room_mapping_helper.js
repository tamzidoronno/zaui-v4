const jomresDatabase = "JomresManager_default";
let jomresDb = db.getMongo().getDB(jomresDatabase);
let collection = jomresDb.getCollection('col_' + storeId);
let className = "com.thundashop.core.jomres.JomresRoomData";

let fileName = './roommapping/' + hotelName + '.json';
let rawdata = cat(fileName);
let jomresPropertyId = JSON.parse(rawdata);

collection.deleteMany({'className': className});

let listOfFinalMapping = [];
let roomIdMapping;
for (let roomId in jomresPropertyId) {
    let timeNow = Date.now();
    roomIdMapping = {
        "className": className,
        "bookingItemId": roomId,
        "jomresPropertyId": parseInt(jomresPropertyId[roomId]),
        "storeId": storeId,
        "rowCreatedDate": ISODate(timeNow.toISOString),
        "lastModified": ISODate(timeNow.toISOString),
        "gs_manager": jomresDatabase,
        "lastModifiedByUserId": "",
        "getshopModule": "",
        "deepFreeze": false,
        "translationId": ""
    }
    listOfFinalMapping.push(roomIdMapping);
}

collection.insertMany(listOfFinalMapping);
