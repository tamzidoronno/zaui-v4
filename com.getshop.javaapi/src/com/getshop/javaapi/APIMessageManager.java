package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIMessageManager {

      public Transporter transport;

      public APIMessageManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Get how many messages a user has sent.
     *
     * @param year
     * @param month
     * @return
     */
     public int getSmsCount(int year, int month)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("year",new Gson().toJson(year));
          data.args.put("month",new Gson().toJson(month));
          data.method = "getSmsCount";
          data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Integer>() {}.getType();
          Integer object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
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
     public void sendMail(java.lang.String to, java.lang.String toName, java.lang.String subject, java.lang.String content, java.lang.String from, java.lang.String fromName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("to",new Gson().toJson(to));
          data.args.put("toName",new Gson().toJson(toName));
          data.args.put("subject",new Gson().toJson(subject));
          data.args.put("content",new Gson().toJson(content));
          data.args.put("from",new Gson().toJson(from));
          data.args.put("fromName",new Gson().toJson(fromName));
          data.method = "sendMail";
          data.interfaceName = "core.messagemanager.IMessageManager";
          String result = transport.send(data);
     }

}
