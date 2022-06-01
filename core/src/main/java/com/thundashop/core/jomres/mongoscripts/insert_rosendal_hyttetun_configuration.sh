storeId='8fb61ca0-3889-4963-be21-0e0daa0ca0fa';
host='127.0.0.1:27018'
hotelName="rosendal_hyttetun"
#mappingFileName='_room_mapping.json'
mongo --port=27018 --eval "const storeId='$storeId', host='$host', hotelName='$hotelName'" \
 insertion_jomres_configuration_helper.js