package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIBannerManager {

      public Transporter transport;

      public APIBannerManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Add a new image to an existing bannerset.<br>
     * The fileid is just an identifier and should be generated / stored by the one calling this application<br>
     *
     * @param id The id to save on.
     * @param fileId The given file id for this image, fetched from the filemanager when uploading the image.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.app.bannermanager.data.BannerSet addImage(java.lang.String id, java.lang.String fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "addImage";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.app.bannermanager.data.BannerSet>() {}.getType();
          com.thundashop.app.bannermanager.data.BannerSet object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Initiate / create a new bannerset.
     * @param width The width for the banners
     * @param height The height for the banners.
     * @param id Specify an id if you want to override the id generation, leave empty otherwhise.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.app.bannermanager.data.BannerSet createSet(int width, int height, java.lang.String id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("width",new Gson().toJson(width));
          gs_json_object_data.args.put("height",new Gson().toJson(height));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "createSet";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.app.bannermanager.data.BannerSet>() {}.getType();
          com.thundashop.app.bannermanager.data.BannerSet object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Delete a given banner set.
     *
     * @param id The id for the bannerset to delete.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.app.bannermanager.data.BannerSet deleteSet(java.lang.String id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteSet";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.app.bannermanager.data.BannerSet>() {}.getType();
          com.thundashop.app.bannermanager.data.BannerSet object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch an existing bannerset.
     * @param id The id for the bannerset to fetch, if the id does not exists, it will be created.
     * @return A bannerset.
     * @throws ErrorException
     */
     public com.thundashop.app.bannermanager.data.BannerSet getSet(java.lang.String id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getSet";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.app.bannermanager.data.BannerSet>() {}.getType();
          com.thundashop.app.bannermanager.data.BannerSet object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Add a product to a given image.
     * @param bannerSetId The id of the bannerset which holds the images.
     * @param imageId The image id to link a product to.
     * @param productId The id of the product to be linked.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.app.bannermanager.data.BannerSet linkProductToImage(java.lang.String bannerSetId, java.lang.String imageId, java.lang.String productId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("bannerSetId",new Gson().toJson(bannerSetId));
          gs_json_object_data.args.put("imageId",new Gson().toJson(imageId));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.method = "linkProductToImage";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.app.bannermanager.data.BannerSet>() {}.getType();
          com.thundashop.app.bannermanager.data.BannerSet object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Remove an already existing image from a given bannerset.
     * @param bannerSetId The id for the bannerset.
     * @param fileId The file id to remove.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.app.bannermanager.data.BannerSet removeImage(java.lang.String bannerSetId, java.lang.String fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("bannerSetId",new Gson().toJson(bannerSetId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "removeImage";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.app.bannermanager.data.BannerSet>() {}.getType();
          com.thundashop.app.bannermanager.data.BannerSet object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Update a given bannerset.<br>
     * If the id of the bannerset does not exists, it will automatically create one for you.<br>
     *
     * @param set The bannerset to save.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.app.bannermanager.data.BannerSet saveSet(com.thundashop.app.bannermanager.data.BannerSet set)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("set",new Gson().toJson(set));
          gs_json_object_data.method = "saveSet";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.app.bannermanager.data.BannerSet>() {}.getType();
          com.thundashop.app.bannermanager.data.BannerSet object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

}
