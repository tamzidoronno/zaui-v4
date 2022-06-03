storeId='687cd85e-4812-405a-9532-7748acc29d13';
host='localhost:27018'
hotelName="development_hotel"
mongo --port=27018 --eval "const storeId='$storeId', host='$host', hotelName='$hotelName'" \
 insertion_room_mapping_helper.js