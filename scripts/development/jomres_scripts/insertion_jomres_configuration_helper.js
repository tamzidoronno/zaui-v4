const jomresDatabase = "JomresManager_default";
let jomresDb = db.getMongo().getDB(jomresDatabase);
let collection = jomresDb.getCollection('col_' + storeId);
let className = "com.thundashop.core.jomres.JomresConfiguration";
let timeNow = Date.now();

let jomresConfiguration = {
    "className": className,
    "cmfClientTokenUrl": cmfClientTokenUrl,
    "clientBaseUrl": clientBaseUrl,
    "cmfRestApiClientId": cmfRestApiClientId,
    "cmfRestApiClientSecret": cmfRestApiClientSecret,
    "channelName": channelName,
    "storeId": storeId,
    "rowCreatedDate": ISODate(timeNow.toISOString),
    "lastModified": ISODate(timeNow.toISOString),
    "gs_manager": jomresDatabase,
    "lastModifiedByUserId": "",
    "getshopModule": "",
    "deepFreeze": false,
    "translationId": ""
}

collection.deleteOne({'className': className});
collection.insertOne(jomresConfiguration);
