package com.thundashop.core.databasemanager;

import com.mongodb.*;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.ExtendedDatabase3;
import com.thundashop.core.storemanager.StoreIdRepository;
import com.thundashop.core.storemanager.StoreIdRepositoryTestContext;
import com.thundashop.core.storemanager.data.Store;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StoreIdRepositoryTestContext.class})
public class DatabaseTest {
    // To run this tests, a mongo needs to be run on host: localhost and port: 27019

    @Autowired
    ExtendedDatabase3 extendedDatabase3;

    static String testStoreId = "all";

    @Before
    public void insert() {
        Order order = new Order();
        CartItem cartItem1 = generateCartItem(100);
        CartItem cartItem2 = generateCartItem(200);
        CartItem cartItem3 = generateCartItem(300);
        CartItem cartItem4 = generateCartItem(450);
        CartItem cartItem5 = generateCartItem(2000);
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem1);
        items.add(cartItem2);
        items.add(cartItem3);
        items.add(cartItem4);
        items.add(cartItem5);
        order.cart = new Cart();
        order.cart.addCartItems(items);

        Store store = new Store();
        store.id = testStoreId;
        store.incrementalStoreId = 1;
        extendedDatabase3.save("OrderManager", "all", order);
    }

    public CartItem generateCartItem(double price){
        Product p = new Product();
        p.name = "Name " + String.valueOf((int)price);
        p.price = price;
        CartItem cartItem = new CartItem();
        cartItem.setProduct(p);
        return cartItem;
    }

    @After
    public void after() {
        extendedDatabase3.dropDatabase("StoreManager");
    }

    private static Order getOrderData(DBCollection collection) {
        BasicDBObject query = new BasicDBObject();
        Morphia morphia = new Morphia();
        morphia.map(DataCommon.class);

        query.put("className", Order.class.getCanonicalName());

        DBObject dbObject = collection.findOne(query);
        List<DataCommon> all = new ArrayList<DataCommon>();

        DataCommon dataCommon = morphia.fromDBObject(DataCommon.class, dbObject);//LUCija
        Order o = (Order) dataCommon;


        return o;
    }

    /* When changing morphia library sometimes loaded date does not reflect data from database.*/
    @Test
    public void numbersLoadedCorectlyFromDb() throws UnknownHostException {
        Mongo mongo = new Mongo("localhost", StoreIdRepositoryTestContext.TEST_DB_PORT);
        DB mongoDb = mongo.getDB("OrderManager");
        DBCollection collection = mongoDb.getCollection("col_" + testStoreId);

        Order order = getOrderData(collection);
        System.out.println("Product price: " +order.cart.getItems().get(0).getProductPrice());
        System.out.println("Product price: " +order.cart.getItems().get(1).getProductPrice());
        System.out.println("Product price: " +order.cart.getItems().get(2).getProductPrice());
        System.out.println("Product price: " +order.cart.getItems().get(3).getProductPrice());
        System.out.println("Product price: " +order.cart.getItems().get(4).getProductPrice());
        Assert.assertEquals(100, order.cart.getItems().get(0).getProductPrice(), 0);
        Assert.assertEquals(200, order.cart.getItems().get(1).getProductPrice(), 0);
        Assert.assertEquals(300, order.cart.getItems().get(2).getProductPrice(), 0);
        Assert.assertEquals(450, order.cart.getItems().get(3).getProductPrice(), 0);
        Assert.assertEquals(2000, order.cart.getItems().get(4).getProductPrice(), 0);
    }

}