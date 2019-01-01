/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.giftcard;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.gsd.GdsManager;
import com.thundashop.core.gsd.GiftCardPrintMessage;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.InvoiceManager;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class GiftCardManager extends ManagerBase implements IGiftCardManager {
    
    @Autowired
    private InvoiceManager invoiceManager;
    
    @Autowired
    private GdsManager gdsManager;
    
    public Map<String, GiftCard> cards = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream()
            .forEach(d -> {
                if (d instanceof GiftCard) {
                    cards.put(d.id, (GiftCard)d);
                }
            });
    }
    
    public void createNewCards(Order order) {
        if (alreadyExistsCardsForOrders(order)) {
            return;
        }
        
        List<GiftCard> newCards = order.cart.getItems().stream()
                .filter(item -> item.getProduct().id.equals("giftcard"))
                .flatMap(item -> createGiftCards(order, item).stream())
                .collect(Collectors.toList());
        
        if (newCards.isEmpty()) {
            return;
        }
        
        newCards.stream()
                .forEach(giftCard -> {
                    saveObject(giftCard);
                    cards.put(giftCard.id, giftCard);
                });
        
        List<String> giftCardIds = newCards.stream()
                .map(o -> o.id)
                .collect(Collectors.toList());
        
        OrderGiftCard orderGiftCard = new OrderGiftCard();
        orderGiftCard.orderId = order.id;
        orderGiftCard.giftCardIds.addAll(giftCardIds);
        saveObject(orderGiftCard);
    }
    
    private boolean alreadyExistsCardsForOrders(Order order) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", OrderGiftCard.class.getName());
        query.put("orderId", order.id);
        return database.query("GiftCardManager", storeId, query)
                .size() > 0;
    }

    private List<GiftCard> createGiftCards(Order order, CartItem item) {
        List<GiftCard> cards = new ArrayList();
        
        for (int i = 0; i<item.getCount(); i++) {
            GiftCard card = new GiftCard();
            card.cardCode = getNewCardCode();
            card.giftCardValue = item.getProductPrice();
            card.remainingValue = card.giftCardValue;
            card.createdByUser = getSession().currentUser.id;
            card.createdByOrderId = order.id;
            card.createdByCartItemId = item.getCartItemId();
            
            cards.add(card);
        }
        
        return cards;
    }

    private String getNewCardCode() {
        String easy = RandomString.digits + "abcdefhijkprstuvwx";
        RandomString tickets = new RandomString(6, new SecureRandom(), easy);
        
        String giftCardCode = "";
        
        for (int i=0; i<100000; i++) {
            giftCardCode = tickets.nextString();
            if (!isInUse(giftCardCode)) {
                break;
            }
        }
        
        if (giftCardCode.isEmpty()) {
            throw new RuntimeException("This should not really happend, how many giftscard is there in this database?");
        }
        
        return giftCardCode;
    }

    private boolean isInUse(String giftCardCode) {
        return cards.values()
                .stream()
                .filter(o -> o.cardCode.equals(giftCardCode))
                .count() > 0;
    }

    @Override
    public List<GiftCard> getAllCards() {
        return new ArrayList(cards.values());
    }
    
    public List<GiftCard> getGiftCardsCreatedByOrderId(String orderId) {
        
        return cards.values()
                .stream()
                .filter(card -> card.createdByOrderId != null && card.createdByOrderId.equals(orderId))
                .collect(Collectors.toList());
    }
    
    
    public void printGiftCard(String gdsDeviceId, String giftCardId) {
        GiftCard card = cards.get(giftCardId);
        
        if (card == null)
            return;
        
        GiftCardPrintMessage printMessage = new GiftCardPrintMessage();
        printMessage.accountDetails = invoiceManager.getAccountingDetails();
        printMessage.code = card.cardCode;
        printMessage.giftCardValue = card.giftCardValue;
        
        gdsManager.sendMessageToDevice(gdsDeviceId, printMessage);
    }

    @Override
    public GiftCard getGiftCard(String giftCardCode) {
        return cards.values()
                .stream()
                .filter(o -> o.cardCode != null && o.cardCode.equals(giftCardCode))
                .findFirst()
                .orElse(null);
    }

    public void registerOrderAgainstGiftCard(Order order, Double amount) {
        GiftCard giftCard = getGiftCard(order.payment.metaData.get("giftCardCode"));
        giftCard.addOrder(order.id, amount);
        saveObject(giftCard);
    }
}
