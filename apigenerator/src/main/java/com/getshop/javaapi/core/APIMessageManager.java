package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIMessageManager {

      public Communicator transport;

      public APIMessageManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public void collectEmail(Object email)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.method = "collectEmail";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public JsonElement getAllSmsMessages(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getAllSmsMessages";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public JsonElement getCollectedEmails()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCollectedEmails";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public JsonElement getIncomingMessages(Object pageNumber)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageNumber",new Gson().toJson(pageNumber));
          gs_json_object_data.method = "getIncomingMessages";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public JsonElement getMailMessage(Object mailMessageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("mailMessageId",new Gson().toJson(mailMessageId));
          gs_json_object_data.method = "getMailMessage";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public JsonElement getMailSent(Object from, Object to, Object toEmailAddress)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.args.put("toEmailAddress",new Gson().toJson(toEmailAddress));
          gs_json_object_data.method = "getMailSent";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public JsonElement getSmsCount(Object year, Object month)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.args.put("month",new Gson().toJson(month));
          gs_json_object_data.method = "getSmsCount";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public JsonElement getSmsMessage(Object smsMessageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smsMessageId",new Gson().toJson(smsMessageId));
          gs_json_object_data.method = "getSmsMessage";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public JsonElement getSmsMessagesSentTo(Object prefix, Object phoneNumber, Object fromDate, Object toDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("prefix",new Gson().toJson(prefix));
          gs_json_object_data.args.put("phoneNumber",new Gson().toJson(phoneNumber));
          gs_json_object_data.args.put("fromDate",new Gson().toJson(fromDate));
          gs_json_object_data.args.put("toDate",new Gson().toJson(toDate));
          gs_json_object_data.method = "getSmsMessagesSentTo";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public void saveIncomingMessage(Object message, Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "saveIncomingMessage";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public void sendErrorNotify(Object inText)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("inText",new Gson().toJson(inText));
          gs_json_object_data.method = "sendErrorNotify";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Send a mail.
     * @param to The address to send to
     * @param toName The name of the one receiving it.
     * @param subject The subject of the mail.
     * @param content The content to send
     * @param from The email sent from.
     * @param fromName The name of the sender.
     */
     public JsonElement sendMail(Object to, Object toName, Object subject, Object content, Object from, Object fromName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.args.put("toName",new Gson().toJson(toName));
          gs_json_object_data.args.put("subject",new Gson().toJson(subject));
          gs_json_object_data.args.put("content",new Gson().toJson(content));
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("fromName",new Gson().toJson(fromName));
          gs_json_object_data.method = "sendMail";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Sending a mail with attachments,
     *
     * Map<Key, Value> - Key = FileName in attchments, Value = Base64 encoded stuff
     *
     * @param to
     * @param toName
     * @param subject
     * @param content
     * @param from
     * @param fromName
     * @param attachments
     */
     public JsonElement sendMailWithAttachments(Object to, Object toName, Object subject, Object content, Object from, Object fromName, Object attachments)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.args.put("toName",new Gson().toJson(toName));
          gs_json_object_data.args.put("subject",new Gson().toJson(subject));
          gs_json_object_data.args.put("content",new Gson().toJson(content));
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("fromName",new Gson().toJson(fromName));
          gs_json_object_data.args.put("attachments",new Gson().toJson(attachments));
          gs_json_object_data.method = "sendMailWithAttachments";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public void sendMessageToStoreOwner(Object message, Object subject)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.args.put("subject",new Gson().toJson(subject));
          gs_json_object_data.method = "sendMessageToStoreOwner";
          gs_json_object_data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(gs_json_object_data);
     }

}
