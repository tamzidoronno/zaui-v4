const jomresDatabase = "JomresManager_default";

let db = connect(host+'/'+jomresDatabase);
let collection = db.getCollection('col_'+storeId);
let className = "com.thundashop.core.jomres.JomresRoomData";

let fileName = './configuration/'+hotelName+'.json';
let rawdata = cat(fileName);
let configurationFile = JSON.parse(rawdata);
let timeNow = Date.now();

let jomresConfiguration = {
    "className": className,
    "storeId": storeId,
    "rowCreatedDate": ISODate(timeNow.toISOString),
    "lastModified": ISODate(timeNow.toISOString),
    "gs_manager": jomresDatabase,
    "lastModifiedByUserId": "",
    "getshopModule": "",
    "deepFreeze": false,
    "translationId": ""
}
for (let key in configurationFile) {
    jomresConfiguration[key] = configurationFile[key]
}

collection.insertOne(jomresConfiguration);