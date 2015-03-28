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
     public java.lang.Integer checkAvailable(long startDate, long endDate, java.lang.String productId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("endDate",new Gson().toJson(endDate));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.method = "checkAvailable";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
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
     public java.lang.Integer checkAvailableParkingSpots(long startDate, long endDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("endDate",new Gson().toJson(endDate));
          gs_json_object_data.method = "checkAvailableParkingSpots";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
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
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "checkForArxTransfer";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void checkForOrdersToGenerate()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "checkForOrdersToGenerate";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void checkForVismaTransfer()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "checkForVismaTransfer";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void checkForWelcomeMessagesToSend()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "checkForWelcomeMessagesToSend";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void clearBookingReservation()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "clearBookingReservation";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException
     */
     public java.lang.String completeOrder()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "completeOrder";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
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
     public void deleteReference(int reference)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("reference",new Gson().toJson(reference));
          gs_json_object_data.method = "deleteReference";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public java.util.List getAllReservations()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllReservations";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.hotelbookingmanager.BookingReference>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public java.util.List getAllReservationsArx()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllReservationsArx";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
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
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllRooms";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.hotelbookingmanager.Room>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Change a room for a reference.
     * @param reference
     * @param oldRoom the old room
     * @param newRoomId
     * @throws ErrorException
     */
     public java.util.List getAllTempAccesses()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllTempAccesses";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.hotelbookingmanager.TempAccess>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public java.util.List getArxLog()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getArxLog";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.hotelbookingmanager.ArxLogEntry>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     *
     * @param roomType
     * @param startDate The first day unix timestamp.
     * @param endDate The last day unix timestamp.
     * @param count The number of rooms to book.
     * @throws ErrorException
     */
     public com.thundashop.core.hotelbookingmanager.GlobalBookingSettings getBookingConfiguration()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getBookingConfiguration";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.hotelbookingmanager.GlobalBookingSettings>() {}.getType();
          com.thundashop.core.hotelbookingmanager.GlobalBookingSettings object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.hotelbookingmanager.UsersBookingData getCurrentUserBookingData()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCurrentUserBookingData";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.hotelbookingmanager.UsersBookingData>() {}.getType();
          com.thundashop.core.hotelbookingmanager.UsersBookingData object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException
     */
     public java.lang.String getEmailMessage(java.lang.String language)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("language",new Gson().toJson(language));
          gs_json_object_data.method = "getEmailMessage";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
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
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("referenceId",new Gson().toJson(referenceId));
          gs_json_object_data.method = "getReservationByReferenceId";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
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
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getRoom";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
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
     public java.util.ArrayList getRoomProductIds()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getRoomProductIds";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.ArrayList>() {}.getType();
          java.util.ArrayList object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public java.lang.String getUserIdForRoom(java.lang.String roomNumber)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("roomNumber",new Gson().toJson(roomNumber));
          gs_json_object_data.method = "getUserIdForRoom";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
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
     public boolean isRoomAvailable(java.lang.String roomId, long startDate, long endDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("endDate",new Gson().toJson(endDate));
          gs_json_object_data.method = "isRoomAvailable";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Boolean>() {}.getType();
          Boolean object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void markRoomAsReady(java.lang.String roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "markRoomAsReady";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Change a room for a reference.
     * @param reference
     * @param oldRoom the old room
     * @param newRoomId
     * @throws ErrorException
     */
     public void moveRoomOnReference(java.lang.Integer reference, java.lang.String oldRoom, java.lang.String newRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("reference",new Gson().toJson(reference));
          gs_json_object_data.args.put("oldRoom",new Gson().toJson(oldRoom));
          gs_json_object_data.args.put("newRoomId",new Gson().toJson(newRoomId));
          gs_json_object_data.method = "moveRoomOnReference";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void notifyUserAboutRoom(com.thundashop.core.hotelbookingmanager.BookingReference reference, com.thundashop.core.hotelbookingmanager.RoomInformation roomInfo, java.lang.Integer code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("reference",new Gson().toJson(reference));
          gs_json_object_data.args.put("roomInfo",new Gson().toJson(roomInfo));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "notifyUserAboutRoom";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException
     */
     public void removeRoom(java.lang.String id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "removeRoom";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @param roomType
     * @param startDate The first day unix timestamp.
     * @param endDate The last day unix timestamp.
     * @param count The number of rooms to book.
     * @throws ErrorException
     */
     public void reserveRoom(long startDate, long endDate, java.lang.Integer count)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("endDate",new Gson().toJson(endDate));
          gs_json_object_data.args.put("count",new Gson().toJson(count));
          gs_json_object_data.method = "reserveRoom";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException
     */
     public void saveRoom(com.thundashop.core.hotelbookingmanager.Room room)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("room",new Gson().toJson(room));
          gs_json_object_data.method = "saveRoom";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void setArxConfiguration(com.thundashop.core.hotelbookingmanager.ArxSettings settings)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("settings",new Gson().toJson(settings));
          gs_json_object_data.method = "setArxConfiguration";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void setBookingConfiguration(com.thundashop.core.hotelbookingmanager.GlobalBookingSettings settings)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("settings",new Gson().toJson(settings));
          gs_json_object_data.method = "setBookingConfiguration";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void setVismaConfiguration(com.thundashop.core.hotelbookingmanager.VismaSettings settings)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("settings",new Gson().toJson(settings));
          gs_json_object_data.method = "setVismaConfiguration";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException
     */
     public void setVistorData(java.util.HashMap visitors)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("visitors",new Gson().toJson(visitors));
          gs_json_object_data.method = "setVistorData";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Change a room for a reference.
     * @param reference
     * @param oldRoom the old room
     * @param newRoomId
     * @throws ErrorException
     */
     public void tempGrantAccess(java.lang.Integer reference, java.lang.String roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("reference",new Gson().toJson(reference));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "tempGrantAccess";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all references
     * @return
     * @throws ErrorException
     */
     public void updateAdditionalInformation(com.thundashop.core.hotelbookingmanager.AdditionalBookingInformation info)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("info",new Gson().toJson(info));
          gs_json_object_data.method = "updateAdditionalInformation";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add new room to the manager.
     * @param room
     * @throws ErrorException
     */
     public void updateCart()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "updateCart";
          gs_json_object_data.interfaceName = "core.hotelbookingmanager.IHotelBookingManager";
          String result = transport.send(gs_json_object_data);
     }

}
