package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIBannerManager {

      public Communicator transport;

      public APIBannerManager(Communicator transport){
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
     public JsonElement addImage(Object id, Object fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "addImage";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement addSlide()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "addSlide";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement createSet(Object width, Object height, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("width",new Gson().toJson(width));
          gs_json_object_data.args.put("height",new Gson().toJson(height));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "createSet";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Delete a given banner set.
     *
     * @param id The id for the bannerset to delete.
     * @return
     * @throws ErrorException
     */
     public JsonElement deleteSet(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteSet";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public void deleteSlide(Object slideId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("slideId",new Gson().toJson(slideId));
          gs_json_object_data.method = "deleteSlide";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Fetch an existing bannerset.
     * @param id The id for the bannerset to fetch, if the id does not exists, it will be created.
     * @return A bannerset.
     * @throws ErrorException
     */
     public JsonElement getSet(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getSet";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getSlideById(Object slideId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("slideId",new Gson().toJson(slideId));
          gs_json_object_data.method = "getSlideById";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement linkProductToImage(Object bannerSetId, Object imageId, Object productId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("bannerSetId",new Gson().toJson(bannerSetId));
          gs_json_object_data.args.put("imageId",new Gson().toJson(imageId));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.method = "linkProductToImage";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Remove an already existing image from a given bannerset.
     * @param bannerSetId The id for the bannerset.
     * @param fileId The file id to remove.
     * @return
     * @throws ErrorException
     */
     public JsonElement removeImage(Object bannerSetId, Object fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("bannerSetId",new Gson().toJson(bannerSetId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "removeImage";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement saveSet(Object set)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("set",new Gson().toJson(set));
          gs_json_object_data.method = "saveSet";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public void setImageForSlide(Object slideId, Object fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("slideId",new Gson().toJson(slideId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "setImageForSlide";
          gs_json_object_data.interfaceName = "app.banner.IBannerManager";
          String result = transport.send(gs_json_object_data);
     }

}
