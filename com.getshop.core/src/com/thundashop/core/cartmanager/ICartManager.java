package com.thundashop.core.cartmanager;

import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.cartmanager.data.CartTax;
import com.thundashop.core.cartmanager.data.Coupon;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.usermanager.data.Address;
import java.util.List;

/**
 * The cartmanager is a manager helping you keep track of your shopping cart.<br>
 * All orders are first being pushed to this shopping cart, then you are able to create an order based on the cart object.<br>
 * The cart object is being processed by the OrderManager.<br>
 */
@GetShopApi
public interface ICartManager {
    /**
     * Add a new product to the cart.
     * @param productId The product id generated by the productmanager.
     * @param int Number instances of the product ordered.
     * @return
     * @throws ErrorException 
     */
    public Cart addProduct(String productId, int count, List<String> variations) throws ErrorException;
    
    public CartItem addProductItem(String productId, int count) throws ErrorException;
    
    /**
     * Fetch the total amount of price to use when calculating shipping price.
     * @return
     * @throws ErrorException 
     */
    public Double getShippingPriceBasis() throws ErrorException;
    
    /**
     * Change the number of instances added to the product.
     * @param productId The product id generated by the productmanager.
     * @param count The number of instances (has to be a positive integer)
     * @return
     * @throws ErrorException 
     */
    public Cart updateProductCount(String cartItemId, int count) throws ErrorException;
    
    /**
     * Remove an added product from the cart.
     * @param productId The product id generated by the productmanager, that has been added to the cart.
     * @return
     * @throws ErrorException 
     */
    public Cart removeProduct(String cartItemId) throws ErrorException;
    
    /**
     * Fetch the current cart.
     * @return
     * @throws ErrorException 
     */
    public Cart getCart() throws ErrorException;
    
    /**
     * Returns the current total amount
     * note, this does not include shipping.
     * 
     * @return
     * @throws ErrorException 
     */
    public Double getCartTotalAmount() throws ErrorException;
    
    /**
     * Clear the current shopping cart.
     */
    public void clear() throws ErrorException;
    
    /**
     * Send in a cart and you shall have the total price for all products.
     */
    public Double calculateTotalCost(Cart cart) throws ErrorException;
    
    
    /**
     * Set a new address to the current cart.
     */
    public void setAddress(Address address) throws ErrorException;
    
    /**
     * Returns the shipping cost
     * @return 
     */
    public Double getShippingCost() throws ErrorException;
    
    /**
     * Sets the shipping cost.
     * Should be in base currency.
     */
    public void setShippingCost(double shippingCost) throws ErrorException;
    
    /**
     * Add coupons to the system.
     */
    @Administrator
    public void addCoupon(Coupon coupon) throws ErrorException;
    
    /**
     * Apply the coupon to the cart.
     */
    public void applyCouponToCurrentCart(String code) throws ErrorException;
    
    /**
     * Returns a list of all coupons.
     * @return 
     */
    @Administrator
    public List<Coupon> getCoupons();
    
    /**
     * Remove all coupons from the system.
     * @throws ErrorException 
     */
    @Administrator
    public void removeAllCoupons() throws ErrorException;
    
    /**
     * Remove a coupon from the system.
     * 
     * @param coupon
     * @throws ErrorException 
     */
    @Administrator
    public void removeCoupon(String code) throws ErrorException;
    
    /**
     * Returns the current calculation of taxes.
     * @return 
     */
    public List<CartTax> getTaxes() throws ErrorException;
    
    /**
     * Need to attach a reference number manually to the cart?
     * @throws ErrorException 
     */
    public void setReference(String reference) throws ErrorException;
    
    public void addMetaDataToProduct(String cartItemId, String metaData);
}
