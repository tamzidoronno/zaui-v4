const jomresDatabase = "JomresManager_default_2";

let db = connect(host+'/'+jomresDatabase);
let collection = db.getCollection('col_'+storeId);
let className = "com.thundashop.core.jomres.JomresRoomData";

let rawdata = cat(mappingFileName);
let jomresPropertyId = JSON.parse(rawdata);

collection.deleteMany({'className':'com.thundashop.core.jomres.JomresRoomData'});

let listOfFinalMapping = [];
let roomIdMapping;
for (let roomId in jomresPropertyId) {
    let timeNow = Date.now();
    roomIdMapping = {
        "className": className,
        "bookingItemId": roomId,
        "jomresPropertyId": jomresPropertyId[roomId],
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