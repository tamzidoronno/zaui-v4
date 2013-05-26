package com.thundashop.core.cartmanager;

import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
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

    private HashMap<String, Cart> carts = new HashMap<String, Cart>();

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
    public Cart updateProductCount(String productId, int count, List<String> variations) throws ErrorException {
        Product product = getProduct(productId);
        if (product != null) {
            Cart cart = getCart(getSession().id);
            cart.setProductCount(productId, variations, count);
            return cart;
        } else {
            throw new ErrorException(1011);
        }
    }

    @Override
    public Cart removeProduct(String productId, List<String> variations) throws ErrorException {
        Product product = getProduct(productId);
        if (product != null) {
            Cart cart = getCart(getSession().id);
            cart.removeProduct(productId, variations);
            return cart;
        } else {
            throw new ErrorException(1011);
        }
    }

    @Override
    public Cart getCart() throws ErrorException {
        return getCart(getSession().id);
    }

    @Override
    public Double getCartTotalAmount() throws ErrorException {
        return getCart(getSession().id).getTotal();
    }

    @Override
    public void clear() throws ErrorException {
        Cart cart = getCart(getSession().id);
        cart.clear();
    }
}
