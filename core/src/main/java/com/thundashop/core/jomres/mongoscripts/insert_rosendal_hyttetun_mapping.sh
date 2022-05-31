storeId='8fb61ca0-3889-4963-be21-0e0daa0ca0fa';
host='127.0.0.1:27018'
mappingFileName='rosendal_hyttetun_room_mapping.json'
mongo --port=27018 --eval "const storeId='$storeId', host='$host', mappingFileName='$mappingFileName'" \
 insertion_room_mapping_helper.js