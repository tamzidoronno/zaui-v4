package com.thundashop.api.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.app.gallerymanager.data.ImageEntry;
import java.util.List;

public class APIGalleryManager {

      public Transporter transport;

      public APIGalleryManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Add a image to a given gallery.
     * @param galleryId The id for the gallery, if this does not exists, it creates a gallery related to this id.
     * @param imageId The image id generated by the filemanager.
     * @param description A description to the image.
     * @param title A title to the image.
     * @throws ErrorException 
     */

     public ImageEntry addImageToGallery(String galleryId, String imageId, String description, String title)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("galleryId",new Gson().toJson(galleryId));
          data.args.put("imageId",new Gson().toJson(imageId));
          data.args.put("description",new Gson().toJson(description));
          data.args.put("title",new Gson().toJson(title));
          data.method = "addImageToGallery";
          data.interfaceName = "core.gallerymanager.IGalleryManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<ImageEntry>() {}.getType();
          ImageEntry object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Create a new gallery.
     * @return String
     * @throws ErrorException 
     */

     public String createImageGallery()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "createImageGallery";
          data.interfaceName = "core.gallerymanager.IGalleryManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<String>() {}.getType();
          String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Remove an already existing image.
     * @param entryId The id of the image to remove.
     * @throws ErrorException 
     */

     public void deleteImage(String entryId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("entryId",new Gson().toJson(entryId));
          data.method = "deleteImage";
          data.interfaceName = "core.gallerymanager.IGalleryManager";
          String result = transport.send(data);
     }

     /**
     * Fetch all images binded to a given gallery id.
     * @param id The id to fetch the entries from.
     * @return List<ImageEntry>
     * @throws ErrorException 
     */

     public List<ImageEntry> getAllImages(String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getAllImages";
          data.interfaceName = "core.gallerymanager.IGalleryManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<ImageEntry>>() {}.getType();
          List<ImageEntry> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Find an existing entry.
     * @param id The id for search for (found in the ImageEntry object)
     * @return ImageEntry
     * @throws ErrorException 
     */

     public ImageEntry getEntry(String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getEntry";
          data.interfaceName = "core.gallerymanager.IGalleryManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<ImageEntry>() {}.getType();
          ImageEntry object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Update an already existing image.
     * @param entry The entry to update.
     * @throws ErrorException 
     */

     public void saveEntry(ImageEntry entry)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("entry",new Gson().toJson(entry));
          data.method = "saveEntry";
          data.interfaceName = "core.gallerymanager.IGalleryManager";
          String result = transport.send(data);
     }

}
