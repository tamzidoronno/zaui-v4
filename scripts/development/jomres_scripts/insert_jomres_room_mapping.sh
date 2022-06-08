storeId='687cd85e-4812-405a-9532-7748acc29d13'
hotelName="development_hotel"
mongo --port=27018 --eval "const storeId='$storeId', hotelName='$hotelName'" \
 insertion_room_mapping_helper.js
