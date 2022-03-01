/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.cartmanager.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.usermanager.data.Address;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class Cart extends DataCommon {
    private List<CartItem> items = new ArrayList<>();
    private double shippingCost = 0;
    public boolean isShippingFree = false;
    public TaxGroup shippingTax = null;
    public String reference = "";
    
    /**
     * Used if there is multiple references for a order.
     * Example, it could be two different bookings that this cart
     * reflects.
     */
    public List<String> references = new ArrayList<>();
    
    public String createByGetShopModule = "";
    
    public Address address;
    public Coupon coupon;
    public Double couponCost;
    public Date overrideDate;
    
    private CartItem getCartItem(String productId, Map<String, String> variations) {
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
    
    public CartItem addProduct(Product product, Map<String, String> variations) {
        CartItem cartItem = getCartItem(product.id, variations);
        
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setVariations(variations);
            cartItem.setCount(0);
            items.add(cartItem);
        }
        
        cartItem.increseCounter();
        return cartItem;
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
        overrideDate = null;
        items.clear();
    }

    private Double getProductTotal(boolean excludeFreeShipping) {
        double total = 0D;
        for (CartItem cartItem : getItems()) {
            if(cartItem.disabled) {
                continue;
            }
            if(excludeFreeShipping && cartItem.getProduct().freeShipping) {
                continue;
            }
            total += getCartItemTotal(cartItem);
        }
        return total;
    }
    
    private Double getCartItemTotal(CartItem cartItem) {
        Product product = cartItem.getProduct();
        double usePrice = 0;
        if (product != null) {
            usePrice = product.price;
        }
        
        if (cartItem.overridePriceIncTaxes != null) {
            usePrice = cartItem.overridePriceIncTaxes;
        }
        
        return usePrice * cartItem.getCount();
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
        
        return retValue;
    }
    
    public List<CartItem> getItemsUnfinalized() {
        return items;
    }
    
    public List<CartItem> getItems() {
        List<CartItem> itemsToReturn = new ArrayList<>();
        for(CartItem it : items) {
            if(it != null) {
                it.doFinalize();
                itemsToReturn.add(it);
            }
        }
        return itemsToReturn;
    }

    @Override
    public Cart clone() {
        Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().serializeNulls().disableInnerClassSerialization().create();
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
            Double priceExTax = new Double(item.getProduct().priceExTaxes);
            if(priceExTax.isNaN()) {
                item.getProduct().priceExTaxes = 0.0;
            }
        }
        
        isShippingFree = true;
    }     

    public List<CartTax> getCartTaxes() {
        Map<Integer, CartTax> taxes = new HashMap<>();
        
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
                Double productTax = productPrice - (productPrice/(taxGroup.getTaxRate()+1));
                cartTax.sum += productTax;
            }
        }
        
        if (shippingTax != null && shippingCost > 0 && shippingTax.taxRate > 0) {
            CartTax cartTax = new CartTax();
            cartTax.taxGroup = shippingTax;
            cartTax.sum = shippingCost - (shippingCost/(shippingTax.getTaxRate()+1));
            taxes.put(shippingTax.groupNumber, cartTax);
        }

        List<CartTax> retTaxes = new ArrayList<>(taxes.values());
        
        return retTaxes;
    }

    public void saveCartItem(CartItem cartItem) {
        List<CartItem> newList = new ArrayList<>();
        
        for (CartItem item : items) {
            if (item.getCartItemId().equals(cartItem.getCartItemId())) {
                newList.add(cartItem);
            } else {
                newList.add(item);
            }
        }
        
        items = newList;
    }

    public void updatePrice(String cartItemId, double price) {
        CartItem cartItem = getCartItem(cartItemId);
        if (cartItem != null) {
            cartItem.getProduct().price = price;
            cartItem.getProduct().doFinalize(); 
       }
    }

    public void addCartItems(List<CartItem> items) {
        this.items.addAll(items);
    }

    public List<CartItem> getItemsByProductId(String productId) {
        return items.stream()
                .filter(item -> item.getProduct().id.equals(productId))
                .collect(Collectors.toList());
    }

    public void updateCartItem(CartItem item) {
        CartItem toremove = null;
        for(CartItem tmp : items) {
            if(tmp.getCartItemId().equals(item.getCartItemId())) {
                toremove = tmp;
            }
        }
        if(toremove != null) {
            items.remove(toremove);
            items.add(item);
        }
    }

    public boolean isNullCart() {
        return getItems().isEmpty();        
    }

    public void clearDisabledItems() {
        items.removeIf(i -> i.disabled);
    }

    public void sortByProducIds() {
        Collections.sort(items, (CartItem o1, CartItem o2) -> {
            if (o1.getProductId().equals(o2.getProductId()) && o1.getProduct().description != null && o2.getProduct().description != null) {
                return o2.getProduct().description.compareTo(o1.getProduct().description);
            }
            return o1.getProductId().compareTo(o2.getProductId());
        });
    }
}