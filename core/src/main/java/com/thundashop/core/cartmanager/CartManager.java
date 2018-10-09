package com.thundashop.core.cartmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.cartmanager.data.Coupon;
import com.thundashop.core.cartmanager.data.CouponType;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pmsmanager.PmsRepeatingData;
import com.thundashop.core.pmsmanager.TimeRepeater;
import com.thundashop.core.pmsmanager.TimeRepeaterDateRange;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.usermanager.data.Address;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    private Product getProduct(String productId, Map<String, String> variations) throws ErrorException {
        ArrayList<String> productIds = new ArrayList<String>();
        productIds.add(productId);
        Product prod = productManager.getProduct(productId);
        Product product = prod.clone();
        product.price = productManager.getPrice(product.id, variations);
        return product;
    }

    @Override
    public Cart addProduct(String productId, int count, Map<String, String> variations) throws ErrorException {
        Product product = getProduct(productId, variations);

        if (product != null) {
            Cart cart = getCart(getSession().id);
            for(int i = 0; i < count; i++) {
                CartItem item = cart.addProduct(product, variations);
                item.addedBy = "cartmanager";
            }
            return cart;
        } else {
            throw new ErrorException(1011);
        }
    }
    
    @Override
    public CartItem addProductWithSource(String productId, int count, String source) throws ErrorException {
        Product product = getProduct(productId, null);

        if (product != null) {
            Cart cart = getCart(getSession().id);
            CartItem item = cart.addProduct(product, null);
            item.setCount(count);
            item.addedBy = source;
            return item;
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
        saveObject(coupon);
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
            deleteObject(coupon);
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
                saveObject(inMemoryCoupon);
            } else {
                coupons.remove(coupon.code);
                deleteObject(inMemoryCoupon);
            }
        }
    }

    @Override
    public void removeCoupon(String code) throws ErrorException {
        Coupon coupon = coupons.remove(code);
        if (coupon != null) {
            deleteObject(coupon);
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

    public double getSumOfAllProductsPrices(boolean isPartner) {
        Cart cart = getCart();
        double price = 0;
        
        if (!isPartner) {
            for (CartItem cartItem : cart.getItems()) {
                price += cartItem.getProduct().price;
            }
        } else {
            for (CartItem cartItem : cart.getItems()) {
                double rowPrice = cartItem.getProduct().price * cartItem.getCount();
                rowPrice = Math.round(rowPrice * 100.0) / 100.0;
                price += rowPrice;
            }

            int i = 0;
            for (CartItem cartItem : cart.getItems()) {
                i += cartItem.getCount();
            }

            return price/i;
        }
        
        return price;
    }
    
    @Override
    public Integer calculateTotalCount(Cart cart) throws ErrorException {
        int count = 0;
        for(CartItem item : cart.getItems()) {
            count += item.getCount();
        }
        
        return count;
    }

    
    @Administrator
    public Coupon getCoupon(String couponCode) {
        if(couponCode == null) {
            couponCode = "";
        }
        for(String key : coupons.keySet()) {
            if(key == null) {
                continue;
            }
            if(couponCode.toLowerCase().equals(key.toLowerCase())) {
                return coupons.get(key);
            }
        }
        
        return null;
    }

    @Administrator
    public Coupon getCouponById(String couponId) {
        for(Coupon cop :getCoupons()) {
            if(cop.id.equals(couponId)) {
                return cop;
            }
        }

        return null;
    }

    public void summarizeItems() {
        List<String> iterated = new ArrayList();
        List<CartItem> removeItem = new ArrayList();
        for(CartItem item : getCart().getItems()) {
            iterated.add(item.getCartItemId());
            for(CartItem item2 : getCart().getItems()) {
                if(iterated.contains(item2.getCartItemId())) {
                    continue;
                }
                int itemcount2 = item2.getCount();
                int itemcount = item.getCount();
                
                if(itemcount2 > 0 && itemcount < 0) {
                    continue;
                }
                if(itemcount2 < 0 && itemcount > 0) {
                    continue;
                }
                
                if(item.getProduct().externalReferenceId == null || item2.getProduct().externalReferenceId == null) {
                    continue;
                }
                if(!item2.getProduct().externalReferenceId.equals(item.getProduct().externalReferenceId)) {
                    continue;
                }
                
                 if(item.getProduct().id.equals(item2.getProduct().id)) {
                     if(item.getProduct().price == item2.getProduct().price) {
                         item.setCount(item.getCount()+item2.getCount());
                         removeItem.add(item2);
                     }
                 }
            }
        }
        for(CartItem remove : removeItem) {
            getCart().removeItem(remove.getCartItemId());
        }
    }
    
    public boolean couponIsValid(Date registrationDate, String couponCode, Date start, Date end, String productId, int days) {
        if(start == null || end == null) {
            return true;
        }
        Coupon coupon = getCoupon(couponCode);
        if(coupon == null) {
            return false;
        }
        if(coupon.whenAvailable == null) {
            return true;
        }
        
        if(productId != null && !coupon.productsToSupport.isEmpty()) {
            if(!coupon.productsToSupport.contains(productId)) {
                return false;
            }
        }
        
        PmsRepeatingData when = coupon.whenAvailable;
        if(coupon.minDays > 0 && coupon.minDays > days) {
            return false;
        }
        if(coupon.maxDays > 0 && coupon.maxDays < days) {
            return false;
        }
        
        if(coupon.pmsWhenAvailable != null && !coupon.pmsWhenAvailable.isEmpty() && coupon.pmsWhenAvailable.equals("REGISTERED")) {
            if(registrationDate != null) {
                start = registrationDate;
                end = registrationDate;
            } else {
                start = new Date();
                end = new Date();
            }
        }
        
        TimeRepeater repeater = new TimeRepeater();
        LinkedList<TimeRepeaterDateRange> res = repeater.generateRange(when.data);
        for(TimeRepeaterDateRange range : res) {
            if((range.start.before(start) || range.start.equals(start)) && (range.end.after(end) || end.equals(range.end))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getPartnershipCoupons() {
        List<String> res = new ArrayList();
        for(Coupon cup : coupons.values()) {
            if(cup == null || cup.code == null) {
                continue;
            }
            if(cup.code.contains("partnership:")) {
                res.add(cup.code.replace("partnership:", ""));
            }
        }
        return res;
    }

    @Override
    public boolean hasCoupons() {
        for(Coupon cup : coupons.values()) {
            if(cup == null || cup.code == null) {
                continue;
            }
            if(!cup.code.contains("partnership:")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a cart by it ids.
     * 
     * If the cart with the ID does not exists, it will simply 
     * create a new one.
     * 
     * @param cart
     * @return 
     */
    public Cart getCartById(String cartId) {
        Cart cart = carts.get(cartId);
        if (cart == null) {
            cart = new Cart();
            cart.id = cartId;
        }
        
        cart.finalizeCart();
        return cart;
    }
    
    public void storeCart(String cartId) {
        
    }

    public void subtractTimesLeft(String couponCode) {
        if(couponCode == null || couponCode.isEmpty()) {
            return;
        }
        Coupon coupon = getCoupon(couponCode);
        if(coupon.timesLeft > 0) {
            coupon.timesLeft--;
            saveObject(coupon);
        }
    }

    public double calculatePriceForCouponWithoutSubstract(String couponCode, double price, int days, Integer guestCount, String bookingTypeId) {
        Coupon coupon = getCoupon(couponCode); 
        Double newPrice = price;
        if(coupon != null) {
            if(coupon.timesLeft > 0) {
                if(coupon.type == CouponType.FIXED) {
                    newPrice = price - coupon.amount;
                }
                if(coupon.type == CouponType.FIXEDPRICE) {
                    if(guestCount != null && bookingTypeId != null && coupon.dailyPriceAmountByType.containsKey(bookingTypeId+"_" +guestCount)) {
                        newPrice = coupon.dailyPriceAmountByType.get(bookingTypeId+"_" +guestCount);
                    } else {
                        newPrice = price;
                    }
                }
                if(coupon.type == CouponType.PERCENTAGE) {
                    double multiplier = (double)(100-coupon.amount)/(double)100;
                    newPrice = price * multiplier;
                }
                if(coupon.type == CouponType.FIXEDDISCOUNTSTAY) {
                    newPrice = price - ((double)coupon.amount / days);
                }
            }
        }
        return newPrice;
    }

    @Override
    public void updateCartItem(CartItem item) {
        Cart cart = getCart();
        cart.updateCartItem(item);
    }

    @Override
    public void removeCartItem(String cartItemId) throws ErrorException {
        Cart cart = getCart();
        cart.removeItem(cartItemId);
    }

    @Override
    public void recalculateMetaData() {
        Cart cart = getCart();
        if (cart != null) {
            cart.getItems().stream().forEach(i -> i.recalculateMetaData());
        }
    }

    public void setCreateByGetShopModule(String getshopModuleName) {
        Cart cart = this.getCart();
        cart.createByGetShopModule = getshopModuleName;
    }

    @Override
    public void setCart(Cart cart) {
        Session session = getSession();
        if (session != null) {
            carts.put(session.id, cart);
        }
    }

    @Override
    public Cart recalculateMetaDataCart(Cart cart) {
        cart.getItems().stream().forEach(i -> i.recalculateMetaData());
        return cart;
    }
    
    @Override
    public Double getCartTotal(Cart cart) {
        orderManager.finalizeCart(cart);
        
        Double total = cart.getTotal(false);
        if(total == null || total.isNaN()) {
            total = 0.0;
        }
        return total;
    }    

    @Override
    public void filterByDate(Date start, Date end) {
        Cart cart = getCart();
        for(CartItem item : cart.getItems()) {
            item.keepOnlyDateRange(start, end);
        }
    }
    
    public void checkIfNeedsToConvertToNewCouponSystem(List<BookingItemType> types) {
        for(Coupon coupon : coupons.values()) {
            if(coupon.type == CouponType.FIXEDPRICE && coupon.dailyPriceAmountByType.isEmpty()) {
                coupon.convertToNewSystem(types);
            }
        }
    }
}
