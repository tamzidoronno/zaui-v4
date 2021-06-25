#!/bin/bash
mongo --port=27017 --eval "var storeId='23442b31-31e5-424c-bb23-a396b7aeb8ca'" ./development_db_scripts/onestoresync_clear_existing_data.js
mongo --port=27017 --eval "var storeId='fd2fecef-1ca1-4231-86a6-0ec445fbac83'" ./development_db_scripts/onestoresync_clear_existing_data.js