allDbs = db.getMongo().getDBNames();
var i = 0;
print("Getting going; trying to find " + storeId);
if(storeId)
{
    allDbs.forEach( function(dbName ){
        i++;

        db2 = db.getMongo().getDB( dbName );
        db2.getCollectionNames().forEach(function(collectionName) {
            if(collectionName.indexOf(storeId) > -1)
            {
                print( "We delete this " + dbName + " :: " + collectionName);
                db2[collectionName].drop();
            }
        });
    });
    print("Done here");
}
