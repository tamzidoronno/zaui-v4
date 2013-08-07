package com.thundashop.core.cartmanager;

import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class CartManager extends ManagerBase implements ICartManager {
    private HashMap<String, Cart> carts = new HashMap();

    @Autowired
    public CartManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
    }

    private Cart getCart(String sessionId) {
        if (carts.get(sessionId) == null) {
            carts.put(sessionId, new Cart());
        }

        return carts.get(sessionId);
    }

    private Product getProduct(String productId) throws ErrorException {
        ArrayList<String> productIds = new ArrayList<String>();
        productIds.add(productId);
        ProductManager man = getManager(ProductManager.class);
        return man.getProduct(productId);
    }

    @Override
    public Cart addProduct(String productId, int count, List<String> variations) throws ErrorException {
        Product product = getProduct(productId);
        if (product != null) {
            Cart cart = getCart(getSession().id);
            cart.addProduct(product, variations);
            return cart;
        } else {
            throw new ErrorException(1011);
        }
    }

    @Override
    public Cart updateProductCount(String cartItemId, int count) throws ErrorException {
        Cart cart = getCart(getSession().id);
        cart.setProductCount(cartItemId, count);
        return cart;
    }

    @Override
    public Cart removeProduct(String cartItemId) throws ErrorException {
        Cart cart = getCart(getSession().id);
        cart.removeProduct(cartItemId);
        return cart;
    }

    @Override
    public Cart getCart() throws ErrorException {
        Cart cart = getCart(getSession().id);
        return cart;
    }

    @Override
    public Double getCartTotalAmount() throws ErrorException {
        double conversionRate = 1.0;
        if (getSession().currentUser == null || !getSession().currentUser.isAdministrator()) {
            conversionRate = ExchangeConvert.getExchangeRate(getSettings("Settings"));
        }
        return getCart(getSession().id).getTotal(conversionRate);
    }

    @Override
    public void clear() throws ErrorException {
        Cart cart = getCart(getSession().id);
        cart.clear();
    }

    @Override
    public Double calculateTotalCost(Cart cart) throws ErrorException {
        double conversionRate = 1.0;
        if (getSession().currentUser == null || !getSession().currentUser.isAdministrator()) {
            conversionRate = ExchangeConvert.getExchangeRate(getSettings("Settings"));
        }
        return cart.getTotal(conversionRate);
    }

    @Override
    public void setAddress(Address address) throws ErrorException {
        Cart cart = this.getCart();        
        cart.address = address;
    }

    @Override
    public Double getShippingCost() throws ErrorException {
        Cart cart = this.getCart();
        return cart.getShippingCost();
    }

    @Override
        public void setShippingCost(double shippingCost) throws ErrorException {
        if (getSession().currentUser == null || !getSession().currentUser.isAdministrator()) {
            shippingCost = ExchangeConvert.calculateExchangeRate(getSettings("Settings"), shippingCost);
        } 
        Cart cart = this.getCart();
        cart.setShippingCost(shippingCost);
    }
}
