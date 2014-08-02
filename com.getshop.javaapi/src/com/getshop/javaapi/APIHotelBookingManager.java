package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIHotelBookingManager {

      public Transporter transport;

      public APIHotelBookingManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Check if a room is available in the given time periode.
     * @param startDate The first day unix timestamp.
     * @param endDate The last day unix timestamp.
     * @param type The type of room to search for-
     * @return Number of avilable rooms. -1, the date is set before todays date, -2 end date is before the start date.
     * @throws ErrorException
     */
     public java.lang.Integer checkAvailable(long startDate, long endDate, java.lang.String type)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("startDate",new Gson().toJson(startDate));
          data.args.put("endDate",new Gson().toJson(endDate));
          data.args.put("type",new Gson().toJson(type));
          data.method = "checkAvailable";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.Integer>() {}.getType();
          java.lang.Integer object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void deleteReference(int reference)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("reference",new Gson().toJson(reference));
          data.method = "deleteReference";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public java.util.List getAllReservations()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAllReservations";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.hotelbookingmanager.BookingReference>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     *
     * @return
     * @throws ErrorException
     */
     public java.util.List getAllRooms()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAllRooms";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.hotelbookingmanager.Room>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.hotelbookingmanager.BookingReference getReservationByReferenceId(java.lang.Integer referenceId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("referenceId",new Gson().toJson(referenceId));
          data.method = "getReservationByReferenceId";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.hotelbookingmanager.BookingReference>() {}.getType();
          com.thundashop.core.hotelbookingmanager.BookingReference object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException
     */
     public com.thundashop.core.hotelbookingmanager.Room getRoom(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getRoom";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.hotelbookingmanager.Room>() {}.getType();
          com.thundashop.core.hotelbookingmanager.Room object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public java.util.List getRoomTypes()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getRoomTypes";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.hotelbookingmanager.RoomType>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException
     */
     public void removeRoom(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "removeRoom";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void removeRoomType(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "removeRoomType";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     *
     * @param roomType
     * @param startDate The first day unix timestamp.
     * @param endDate The last day unix timestamp.
     * @param count The number of rooms to book.
     * @throws ErrorException
     */
     public java.lang.String reserveRoom(java.lang.String roomType, long startDate, long endDate, int count, com.thundashop.core.hotelbookingmanager.ContactData contact)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("roomType",new Gson().toJson(roomType));
          data.args.put("startDate",new Gson().toJson(startDate));
          data.args.put("endDate",new Gson().toJson(endDate));
          data.args.put("count",new Gson().toJson(count));
          data.args.put("contact",new Gson().toJson(contact));
          data.method = "reserveRoom";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException
     */
     public void saveRoom(com.thundashop.core.hotelbookingmanager.Room room)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("room",new Gson().toJson(room));
          data.method = "saveRoom";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void saveRoomType(com.thundashop.core.hotelbookingmanager.RoomType type)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("type",new Gson().toJson(type));
          data.method = "saveRoomType";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Set a new code to a given room.
     * @param code The code to set
     * @param roomId The id of the room to update.
     * @throws ErrorException
     */
     public void setCode(java.lang.String code, java.lang.String roomId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("code",new Gson().toJson(code));
          data.args.put("roomId",new Gson().toJson(roomId));
          data.method = "setCode";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

}
