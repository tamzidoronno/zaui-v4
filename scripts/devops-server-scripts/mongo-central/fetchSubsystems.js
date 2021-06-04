conn = new Mongo("localhost:27018");
var dbSystem = conn.getDB("SystemManager");
var cursor2 = dbSystem["col_"+storeId].find();
while ( cursor2.hasNext() ) {
     	var system = cursor2.next();
	if (system["deleted"])
		continue

	var ip = "10.0." + system["cluster"];
	var type = system["type"];

	if (type == "pos") {
		ip += ".43";
	} else if(type == "seros") {
		ip += ".63";
	} else if(type == "pms") {
		ip += ".33";
	}
	print( ip + " " + system["store"]['_id'] + " " + type + " " + system["name"]);

}