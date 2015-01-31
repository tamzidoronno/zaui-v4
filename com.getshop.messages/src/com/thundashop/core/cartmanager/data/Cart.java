/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.ordermanager.data.Shipping;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.usermanager.data.Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class Cart extends DataCommon {
    private List<CartItem> items = new ArrayList();
    private double shippingCost = 0;
    public boolean isShippingFree = false;
    public TaxGroup shippingTax = null;
    public String reference = "";
    
    public Address address;
    public Coupon coupon;
    public Double couponCost;
    
    private CartItem getCartItem(String productId, List<String> variations) {
        for (CartItem cartItem : items) {
            if (cartItem.isSame(productId, variations)) {
                return cartItem;
            }
        }
        return null;
    }
    
    public CartItem createCartItem(Product product, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setCount(count);
        items.add(cartItem);
        return cartItem;
    }

    public CartItem getCartItem(String cartItemId) {
        for (CartItem cartItem : items) {
            if (cartItem.getCartItemId().equals(cartItemId)) {
                return cartItem;
            }
        }
        
        return null;
    }
    
    public void addProduct(Product product, List<String> variations) {
        CartItem cartItem = getCartItem(product.id, variations);
        
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setVariations(variations);
            cartItem.setCount(0);
            items.add(cartItem);
        }
        
        cartItem.increseCounter();
    }
    
    public void removeItem(String cartItemId) {
        CartItem cartItem = getCartItem(cartItemId);
        items.remove(cartItem);
    }

    public int getProductCount(String cartItemId) {
        CartItem cartItem = getCartItem(cartItemId);
        if (cartItem != null) {
            return cartItem.getCount();
        }
        
        return 0;
    }

    public void setProductCount(String cartItemId, int count) {
        CartItem cartItem = getCartItem(cartItemId);
        cartItem.setCount(count);
    }
   
    public void clear() {
        shippingCost = 0;
        coupon = null;
        couponCost = null;
        items.clear();
    }

    private Double getProductTotal(boolean excludeFreeShipping) {
        Double total = 0D;
        for (CartItem cartItem : items) {
            if(excludeFreeShipping && cartItem.getProduct().freeShipping) {
                continue;
            }
            total += getCartItemTotal(cartItem);
        }
        return total;
    }
    
    private Double getCartItemTotal(CartItem cartItem) {
        return cartItem.getProduct().price * cartItem.getCount();
    }
    
    public Double getTotal(boolean excludeFreeShipping) {
        Double total = getProductTotal(excludeFreeShipping);
        total -= getCouponCost(total);
        return total;
    }
    
    public Double getCouponCost(Double total) {
        Double retValue = 0D;
        if (coupon != null) {    
            if (coupon.type == CouponType.FIXED) {
                retValue = Double.valueOf(coupon.amount);
            }
            if (coupon.type == CouponType.PERCENTAGE) {
                if (coupon.amount > 0 && coupon.amount < 100) {
                    Double percentage = Double.valueOf(coupon.amount);
                    retValue = percentage/100*total;
                }
            }
        }
        
        if (retValue > total) {
            retValue = total;
        }
        
        return retValue;
    }
    
    public List<CartItem> getItems() {
        return items;
    }

    @Override
    public Cart clone() {
        Gson gson = new GsonBuilder().serializeNulls().disableInnerClassSerialization().create();
        String json = gson.toJson(this);
        Cart copied = gson.fromJson(json, Cart.class);
        return copied;
    }

    public void setShippingCost(double shippingCost, TaxGroup shippingTax) throws ErrorException {
        this.shippingCost = shippingCost;
        this.shippingTax = shippingTax;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    private void setCouponCost() {
        Double total = getProductTotal(false);
        couponCost = getCouponCost(total);
    }
    
    public void finalizeCart() {
        List<CartItem> allItems = items;
        setCouponCost();
        
        if(allItems == null || allItems.isEmpty()) {
            isShippingFree = false;
            return;
        }
        
        for(CartItem item : allItems) {
            if(!item.getProduct().freeShipping) {
                isShippingFree = false;
                return;
            }
        }
        
        isShippingFree = true;
    }     

    public List<CartTax> getCartTaxes() {
        Map<Integer, CartTax> taxes = new HashMap<Integer, CartTax>();
        
        Double remainingCouponCost = couponCost;
        if (remainingCouponCost == null) {
            remainingCouponCost = 0D;
        } 
        
        for (CartItem cartItem : getItems()) {
            if (cartItem.getProduct() != null && cartItem.getProduct().taxGroupObject != null) {
                TaxGroup taxGroup = cartItem.getProduct().taxGroupObject;
                CartTax cartTax = taxes.get(taxGroup.groupNumber);
                if (cartTax == null) {
                    cartTax = new CartTax();
                    cartTax.taxGroup = taxGroup;
                    taxes.put(taxGroup.groupNumber, cartTax);
                }
                
                Double productPrice = getCartItemTotal(cartItem);
                Double newProductPrice = productPrice-remainingCouponCost;
                remainingCouponCost = remainingCouponCost - productPrice;
                if (remainingCouponCost < 0) {
                    remainingCouponCost = 0D;
                }
                
                Double productTax = newProductPrice * taxGroup.getTaxRate();
                cartTax.sum += productTax;
            }
        }
        
        if (shippingTax != null && shippingCost > 0 && shippingTax.taxRate > 0) {
            CartTax cartTax = new CartTax();
            cartTax.taxGroup = shippingTax;
            cartTax.sum = shippingCost * shippingTax.getTaxRate();
            taxes.put(shippingTax.groupNumber, cartTax);
        }
        
        List retTaxes = new ArrayList();
        for (CartTax tax : taxes.values()) {
            if (tax.sum > 0) {
                retTaxes.add(tax);
            }
        }
        
        return retTaxes;
    }
}