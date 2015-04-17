package com.thundashop.core.cartmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.cartmanager.data.Coupon;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.usermanager.data.Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class CartManager extends ManagerBase implements ICartManager {
    public HashMap<String, Coupon> coupons = new HashMap();
    private HashMap<String, Cart> carts = new HashMap();

    @Autowired
    private ProductManager productManager;
    
    @Autowired
    private OrderManager orderManager;
    
    @Autowired
    private PageManager pageManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataObject : data.data) {
            if (dataObject instanceof Coupon) {
                Coupon coupon = (Coupon)dataObject;
                coupons.put(coupon.code, coupon);
            }
        }
    }


    @Override
    public CartItem addProductItem(String productId, int count) throws ErrorException {
        Product product = getProduct(productId, null);
        updateTranslation(product, true);

        if (product != null) {
            Cart cart = getCart(getSession().id);
            return cart.createCartItem(product, count);
        } else {
            throw new ErrorException(1011);
        }
    }
        
    private Cart getCart(String sessionId) {
        if (!carts.containsKey(sessionId)) {
            carts.put(sessionId, new Cart());
        }
        Cart cart = carts.get(sessionId);
        removeDeletedProducts(cart);
        cart.finalizeCart();
        return cart;
    }

    private Product getProduct(String productId, List<String> variations) throws ErrorException {
        ArrayList<String> productIds = new ArrayList<String>();
        productIds.add(productId);
        Product product = productManager.getProduct(productId).clone();
        product.price = productManager.getPrice(product.id, variations);
        return product;
    }

    @Override
    public Cart addProduct(String productId, int count, List<String> variations) throws ErrorException {
        Product product = getProduct(productId, variations);
        updateTranslation(product, true);

        if (product != null) {
            Cart cart = getCart(getSession().id);
            for(int i = 0; i < count; i++) {
                cart.addProduct(product, variations);
            }
            return cart;
        } else {
            throw new ErrorException(1011);
        }
    }

    @Override
    public Cart updateProductCount(String cartItemId, int count) throws ErrorException {
        Cart cart = getCart(getSession().id);
        cart.setProductCount(cartItemId, count);
        orderManager.finalizeCart(cart);
        return cart;
    }

    @Override
    public Cart removeProduct(String cartItemId) throws ErrorException {
        Cart cart = getCart(getSession().id);
        cart.removeItem(cartItemId);
        orderManager.finalizeCart(cart);
        return cart;
    }

    @Override
    public Cart getCart() throws ErrorException {
        Cart cart = getCart(getSession().id);
        orderManager.finalizeCart(cart);
        return cart;
    }

    @Override
    public Double getCartTotalAmount() throws ErrorException {
        Cart cart  = getCart(getSession().id).clone();
        orderManager.finalizeCart(cart);
        Double total = cart.getTotal(false);
        if(total == null || total.isNaN()) {
            total = 0.0;
        }
        return total;
    }

    @Override
    public void clear() throws ErrorException {
        Cart cart = getCart(getSession().id);
        cart.clear();
    }

    @Override
    public Double calculateTotalCost(Cart cart) throws ErrorException {
        if(cart == null) {
            return 0.0;
        }
        return cart.getTotal(false);
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
        Cart cart = this.getCart();
        
        TaxGroup shippingTaxGroup = productManager.getTaxGroup(0);
        cart.setShippingCost(shippingCost, shippingTaxGroup);
    }

    @Override
    public Double getShippingPriceBasis() throws ErrorException {
        Cart cart  = getCart(getSession().id).clone();
        orderManager.finalizeCart(cart);
        return cart.getTotal(true);
    }

    @Override
    public void addCoupon(Coupon coupon) throws ErrorException {
        coupon.storeId = storeId;
        databaseSaver.saveObject(coupon, credentials);
        coupons.put(coupon.code, coupon);
    }

    @Override
    public void applyCouponToCurrentCart(String code) throws ErrorException {
        Coupon coupon = coupons.get(code);
        if (coupon == null) {
            throw new ErrorException(99);
        }
        
        Cart cart = getCart(getSession().id);
        cart.coupon = coupon;
    }

    @Override
    public List<Coupon> getCoupons() {
        return new ArrayList(coupons.values());
    }

    @Override
    public void removeAllCoupons() throws ErrorException {
        for (Coupon coupon : coupons.values()) {
            databaseSaver.deleteObject(coupon, credentials);
        }
        coupons.clear();
    }

    public void updateCoupons(Coupon coupon) throws ErrorException {
        if (coupon != null) {
            Coupon inMemoryCoupon = coupons.get(coupon.code);
            if (inMemoryCoupon == null) {
                return;
            }
            if (inMemoryCoupon.timesLeft > 1) {
                inMemoryCoupon.timesLeft--;
                databaseSaver.saveObject(inMemoryCoupon, credentials);
            } else {
                coupons.remove(coupon.code);
                databaseSaver.deleteObject(inMemoryCoupon, credentials);
            }
        }
    }

    @Override
    public void removeCoupon(String code) throws ErrorException {
        Coupon coupon = coupons.remove(code);
        if (coupon != null) {
            databaseSaver.deleteObject(coupon, credentials);
        }
    }

    private void removeDeletedProducts(Cart cart) {
        
        //Look for removed products.
        List<CartItem> toRemove = new ArrayList();
        for(CartItem item : cart.getItems()) {
            if(!productManager.exists(item.getProduct().id)) {
                toRemove.add(item);
            }
        }
        
        for(CartItem remove : toRemove) {
            cart.removeItem(remove.getCartItemId());
        }
    }

    @Override
    public List<CartTax> getTaxes() throws ErrorException {
        Cart cart = getCart();
        return cart.getCartTaxes();
    }

    @Override
    public void setReference(String reference) throws ErrorException {
        Cart cart = this.getCart();
        cart.reference = reference;
    }

    @Override
    public void addMetaDataToProduct(String cartItemId, String metaData) {
        for (CartItem cartItem : getCart().getItems()) {
            if (cartItem != null && cartItem.getCartItemId().equals(cartItemId)) {
                cartItem.getProduct().metaData = metaData;
            }
        }
    }

    public void saveCartItem(CartItem cartItem) {
        getCart().saveCartItem(cartItem);
    }

}
