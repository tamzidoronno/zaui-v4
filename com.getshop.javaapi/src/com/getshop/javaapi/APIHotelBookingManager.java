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
     *
     * @return
     * @throws ErrorException
     */
     public java.lang.Integer checkAvailableParkingSpots(long startDate, long endDate)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("startDate",new Gson().toJson(startDate));
          data.args.put("endDate",new Gson().toJson(endDate));
          data.method = "checkAvailableParkingSpots";
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
     public void checkForArxTransfer()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "checkForArxTransfer";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void checkForVismaTransfer()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "checkForVismaTransfer";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void checkForWelcomeMessagesToSend()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "checkForWelcomeMessagesToSend";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
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
     public java.util.List getArxLog()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getArxLog";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.hotelbookingmanager.ArxLogEntry>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.hotelbookingmanager.GlobalBookingSettings getBookingConfiguration()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getBookingConfiguration";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.hotelbookingmanager.GlobalBookingSettings>() {}.getType();
          com.thundashop.core.hotelbookingmanager.GlobalBookingSettings object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException
     */
     public java.lang.String getEmailMessage(java.lang.String language)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("language",new Gson().toJson(language));
          data.method = "getEmailMessage";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
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
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void markReferenceAsStopped(int referenceId, java.util.Date stoppedDate)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("referenceId",new Gson().toJson(referenceId));
          data.args.put("stoppedDate",new Gson().toJson(stoppedDate));
          data.method = "markReferenceAsStopped";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void markRoomAsReady(java.lang.String roomId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("roomId",new Gson().toJson(roomId));
          data.method = "markRoomAsReady";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Change a room for a reference.
     * @param reference
     * @param oldRoom the old room
     * @param newRoomId
     * @throws ErrorException
     */
     public void moveRoomOnReference(java.lang.Integer reference, java.lang.String oldRoom, java.lang.String newRoomId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("reference",new Gson().toJson(reference));
          data.args.put("oldRoom",new Gson().toJson(oldRoom));
          data.args.put("newRoomId",new Gson().toJson(newRoomId));
          data.method = "moveRoomOnReference";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
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
     public java.lang.String reserveRoom(java.lang.String roomType, long startDate, long endDate, int count, com.thundashop.core.hotelbookingmanager.ContactData contact, boolean markAsInctive, java.lang.String language)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("roomType",new Gson().toJson(roomType));
          data.args.put("startDate",new Gson().toJson(startDate));
          data.args.put("endDate",new Gson().toJson(endDate));
          data.args.put("count",new Gson().toJson(count));
          data.args.put("contact",new Gson().toJson(contact));
          data.args.put("markAsInctive",new Gson().toJson(markAsInctive));
          data.args.put("language",new Gson().toJson(language));
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
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void setArxConfiguration(com.thundashop.core.hotelbookingmanager.ArxSettings settings)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("settings",new Gson().toJson(settings));
          data.method = "setArxConfiguration";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void setBookingConfiguration(com.thundashop.core.hotelbookingmanager.GlobalBookingSettings settings)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("settings",new Gson().toJson(settings));
          data.method = "setBookingConfiguration";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void setVismaConfiguration(com.thundashop.core.hotelbookingmanager.VismaSettings settings)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("settings",new Gson().toJson(settings));
          data.method = "setVismaConfiguration";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void updateReservation(com.thundashop.core.hotelbookingmanager.BookingReference reference)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("reference",new Gson().toJson(reference));
          data.method = "updateReservation";
          data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(data);
     }

}
