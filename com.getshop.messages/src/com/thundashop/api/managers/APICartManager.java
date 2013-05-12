package com.thundashop.api.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.cartmanager.data.Cart;

public class APICartManager {

      private Transporter transport;

      public APICartManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Add a new product to the cart.
     * @param productId The product id generated by the productmanager.
     * @param int Number instances of the product ordered.
     * @return Cart
     * @throws ErrorException 
     */

     public Cart addProduct(String productId, int count)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("count",new Gson().toJson(count));
          data.method = "addProduct";
          data.interfaceName = "core.cartmanager.ICartManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Cart>() {}.getType();
          Cart object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Clear the current shopping cart.
     */

     public void clear()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "clear";
          data.interfaceName = "core.cartmanager.ICartManager";
          String result = transport.send(data);
     }

     /**
     * Fetch the current cart.
     * @return Cart
     * @throws ErrorException 
     */

     public Cart getCart()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getCart";
          data.interfaceName = "core.cartmanager.ICartManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Cart>() {}.getType();
          Cart object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns the current total amount
     * note, this does not include shipping.
     * 
     * @return Double
     * @throws ErrorException 
     */

     public Double getCartTotalAmount()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getCartTotalAmount";
          data.interfaceName = "core.cartmanager.ICartManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Double>() {}.getType();
          Double object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Remove an added product from the cart.
     * @param productId The product id generated by the productmanager, that has been added to the cart.
     * @return Cart
     * @throws ErrorException 
     */

     public Cart removeProduct(String productId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.method = "removeProduct";
          data.interfaceName = "core.cartmanager.ICartManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Cart>() {}.getType();
          Cart object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Change the number of instances added to the product.
     * @param productId The product id generated by the productmanager.
     * @param count The number of instances (has to be a positive integer)
     * @return Cart
     * @throws ErrorException 
     */

     public Cart updateProductCount(String productId, int count)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("count",new Gson().toJson(count));
          data.method = "updateProductCount";
          data.interfaceName = "core.cartmanager.ICartManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Cart>() {}.getType();
          Cart object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

}
