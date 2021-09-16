#!/bin/bash
# cluster 5 collections dump
mongodump --forceTableScan --db $2 --collection  "col_23442b31-31e5-424c-bb23-a396b7aeb8ca" --port $1 --host localhost &> /dev/null
mongodump --forceTableScan --db $2 --collection  "col_fd2fecef-1ca1-4231-86a6-0ec445fbac83" --port $1 --host localhost &> /dev/null
mongodump --forceTableScan --db $2 --collection "col_47522cce-8fda-4538-9b61-84eb72faf03f"  --port $1 --host localhost &> /dev/null
mongodump --forceTableScan --db $2 --collection "col_13442b34-31e5-424c-bb23-a396b7aeb8ca"  --port $1 --host localhost &> /dev/null
mongodump --forceTableScan --db $2 --collection "col_all"  --port  $1 --host localhost &> /dev/null
mongodump --forceTableScan --db $2 --collection "dbscripts"  --port  $1 --host localhost &> /dev/null
