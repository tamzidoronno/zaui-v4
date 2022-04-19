
// TRONDERGOLF
colname ="col_1ed4ab1f-c726-4364-bf04-8dcddb2fb2b1";
db = db.getSiblingDB("admin");
dbs = db.runCommand({ "listDatabases": 1 }).databases;

// Iterate through each database and get its collections.
dbs.forEach(function(database) {
    db = db.getSiblingDB(database.name);
    print ("searcing in " + database.name + " collection " + colname);

    db.getCollection(colname).find().forEach(function(doc) {
        var json = JSON.stringify( doc );
        if ( json.match(/TRONDERGOLF/) != null ){
            print("Found:");
            printjson(doc);
        }
    });

});
