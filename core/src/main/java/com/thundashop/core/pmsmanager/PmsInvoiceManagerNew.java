/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.cartmanager.CartManager;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pos.PosConference;
import com.thundashop.core.pos.PosManager;
import com.thundashop.core.pos.PosTab;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.usermanager.data.Address;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.zauiactivity.constant.ZauiConstants;

/**
 *
 * @author ktonder
 */
public class PmsInvoiceManagerNew {
    private OrderManager orderManager;
    private CartManager cartManager;
    private ProductManager productManager;
    private PmsManager pmsManager;
    private final SimpleDateFormat sdf;
    private final PosManager posManager;

    public PmsInvoiceManagerNew(OrderManager orderManager, CartManager cartManager, ProductManager productManager,
            PmsManager pmsManager, PosManager posManager) {
        this.orderManager = orderManager;
        this.cartManager = cartManager;
        this.productManager = productManager;
        this.pmsManager = pmsManager;
        this.posManager = posManager;
        this.sdf = new SimpleDateFormat("dd-MM-yyyy");
    }

    public Order createOrder(List<PmsOrderCreateRow> rows, String paymentMethodId, String userId) {
        cartManager.clear();

        Map<String, List<CartItem>> tabItemsAdded = new HashMap<>();

        for (PmsOrderCreateRow roomData : rows) {
            if (roomData.roomId != null && !roomData.roomId.isEmpty() && !roomData.roomId.equals("virtual")) {
                addAccomodationToCart(roomData);
                addAddonsToCart(roomData);
            }

            if (roomData.roomId != null && !roomData.roomId.isEmpty() && roomData.roomId.equals("virtual")) {
                addCartItemsNotConnectedToARoom(roomData);
            }

            if (roomData.conferenceId != null && !roomData.conferenceId.isEmpty()) {
                List<CartItem> itemsAdded = addConferenceCartItems(roomData);
                tabItemsAdded.put(roomData.conferenceId, itemsAdded);
            }

        }

        if (cartManager.isCartConflictingWithClosedPeriode()) {
            cartManager.getCart().overrideDate = orderManager.getOrderManagerSettings().closedTilPeriode;
        }

        User user = pmsManager.userManager.getUserById(userId);

        if (userId.equals("false") || user == null) {
            Logger.getLogger(PmsInvoiceManagerNew.class.getName()).log(Level.WARNING,
                    "One of the bookings on ZReport has no user assigned to the booking rooms:");
            rows.stream().flatMap(row -> row.items.stream()).forEach(
                    item -> Logger.getLogger(PmsInvoiceManagerNew.class.getName()).log(Level.WARNING,
                            "Booking: room " + (item.textOnOrder != null ? item.textOnOrder : "") + " for date "
                                    + (item.date != null ? item.date : "")));

            user = pmsManager.getSession().currentUser;
            userId = user.id;
        }

        Address address = user != null ? user.address : null;

        Order order = orderManager.createOrder(address);
        order.supportMultipleBookings = true;
        order.createByManager = "PmsDailyOrderGeneration";
        order.userId = userId;
        if (user.defaultDueDate >= 0) {
            order.dueDays = user.defaultDueDate;
        }
        order.correctStartEndDate();

        orderManager.saveOrder(order);
        orderManager.changeOrderType(order.id, paymentMethodId);

        finishTabs(tabItemsAdded, order);

        return order;
    }

    private void addAccomodationToCart(PmsOrderCreateRow roomData) {
        Map<String, List<PmsOrderCreateRowItemLine>> res = roomData.items.stream()
                .filter(o -> o.isAccomocation)
                .collect(Collectors.groupingBy(o -> o.createOrderOnProductId));

        Application settings = pmsManager.storeApplicationPool
                .getApplicationIgnoreActive("70ace3f0-3981-11e3-aa6e-0800200c9a66");
        String language = pmsManager.getStoreSettingsApplicationKey("language");
        if (settings.getSetting("language") != null && !settings.getSetting("language").isEmpty()) {
            language = settings.getSetting("language");
        }
        boolean useSingleRoomTypes = false;
        if (settings.getSetting("useSingleRoomTypes") != null && !settings.getSetting("useSingleRoomTypes").isEmpty()) {
            useSingleRoomTypes = settings.getSetting("useSingleRoomTypes").equals("true");
        }

        for (String productId : res.keySet()) {
            List<PmsOrderCreateRowItemLine> days = res.get(productId);
            CartItem item = cartManager.addProductItem(productId, getCount(days));
            String roomId = roomData.roomId;
            Product prod = item.getProduct();
            prod.price = getAveragePrice(days);
            prod.discountedPrice = prod.price;
            prod.externalReferenceId = roomId;

            if (prod.price != 0 && item.getCount() == 0) {
                item.setCount(1);
            }

            if (useSingleRoomTypes) {
                String name = "Overnatting";
                if (language != null && (language.equalsIgnoreCase("en_en") || language.equalsIgnoreCase("en"))) {
                    name = "Accommodation";
                }
                if (language != null && (language.equalsIgnoreCase("de"))) {
                    name = "Unterkunft Hotelzimmer";
                }
                prod.name = name;
            }
            item.priceMatrix = new HashMap<>();
            setMetaData(item, roomId);
            setGuestName(item, roomId);
            setDates(item, roomId);

            days.stream().forEach(o -> {
                item.priceMatrix.put(o.date, (o.price * o.count));
            });

            item.getProduct().doFinalize();
        }
    }

    private double getAveragePrice(List<PmsOrderCreateRowItemLine> days) {
        double sum = days.stream()
                .mapToDouble(o -> {
                    return o.price * o.count;
                })
                .sum();

        int count = getCount(days);

        if (count == 0 && sum != 0) {
            count = 1;
        }

        if (count == 0)
            return 0;

        return sum / count;
    }

    private int getCount(List<PmsOrderCreateRowItemLine> days) {
        int count = days.stream()
                .mapToInt(o -> o.count)
                .sum();

        return count;
    }

    public void setMetaData(CartItem item, String roomId) {
        PmsBooking booking = pmsManager.getBookingFromRoomSecure(roomId);
        PmsBookingRooms room = booking.getRoom(roomId);

        item.getProduct().additionalMetaData = "";

        if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
            BookingItem bookingItem = pmsManager.bookingEngine.getBookingItem(room.bookingItemId);
            item.getProduct().additionalMetaData = bookingItem.bookingItemName;
            item.getProduct().pmsData.put("guestcount", "" + room.guests.size());
            item.getProduct().pmsData.put("segment", "" + booking.segmentId);
            item.getProduct().pmsData.put("bookingItemTypeId", "" + room.bookingItemTypeId);
            item.getProduct().pmsData.put("channel", "" + booking.channel);
        }
    }

    public void setGuestName(CartItem item, String roomId) {
        PmsBooking booking = pmsManager.getBookingFromRoomSecure(roomId);
        PmsBookingRooms room = booking.getRoom(roomId);

        item.getProduct().metaData = "";

        if (room.guests.size() > 0) {
            item.getProduct().metaData = room.guests.get(0).name;
        }
    }

    public void setDates(CartItem item, String roomId) {
        PmsBooking booking = pmsManager.getBookingFromRoomSecure(roomId);
        PmsBookingRooms room = booking.getRoom(roomId);

        item.startDate = room.date.start;
        item.endDate = room.date.end;
        item.periodeStart = room.date.start;
    }

    private void addAddonsToCart(PmsOrderCreateRow roomData) {
        Map<String, List<PmsOrderCreateRowItemLine>> res = roomData.items.stream()
                .filter(o -> !o.isAccomocation && !o.createOrderOnProductId.isEmpty())
                .collect(Collectors.groupingBy(o -> o.getKey()));

        for (String rowKey : res.keySet()) {
            String[] splitted = rowKey.split(";");
            String productId = splitted[0];
            Product product = productManager.getProduct(productId);

            if (product == null) {
                continue;
            }

            String refId = "";
            if (splitted.length > 1) {
                refId = splitted[1];
            }
            String referenceId = refId;

            List<PmsOrderCreateRowItemLine> days = res.get(rowKey);
            String orderText = getOrderText(days, rowKey);
            String roomId = roomData.roomId;
            CartItem item = cartManager.addProductItem(productId, getCount(days));

            Product prod = item.getProduct();
            prod.price = getAveragePrice(days);
            prod.discountedPrice = prod.price;
            prod.externalReferenceId = roomData.roomId;
            if (orderText != null && orderText.length() > 0) {
                prod.name = orderText;
            }

            PmsBookingAddonItem addonByReferenceId = setTaxGroupToProduct(referenceId, roomData, prod, item);

            item.setCount(getCount(days));

            setMetaData(item, roomId);
            setGuestName(item, roomId);
            setDates(item, roomId);

            item.itemsAdded = new ArrayList<>();

            days.stream().forEach(d -> {
                PmsBookingAddonItem addonItem = new PmsBookingAddonItem();
                addonItem.count = d.count;
                addonItem.price = d.price;
                addonItem.productId = item.getProduct().id;

                if (referenceId != null && !referenceId.isEmpty()) {
                    addonItem.addonId = referenceId;
                    addonItem.isUniqueOnOrder = true;
                }

                addonItem.setName(prod.name);

                try {
                    addonItem.date = sdf.parse(d.date);
                } catch (ParseException ex) {
                    Logger.getLogger(PmsInvoiceManagerNew.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (addonByReferenceId != null) {
                    addonItem.date = addonByReferenceId.date;
                }

                addonItem.isIncludedInRoomPrice = d.includedInRoomPrice;
                item.itemsAdded.add(addonItem);
            });
        }
    }

    private PmsBookingAddonItem setTaxGroupToProduct(String referenceId, PmsOrderCreateRow roomData, Product prod,
            CartItem item) {
        if (referenceId != null && !referenceId.isEmpty()) {
            PmsBooking booking = pmsManager.getBookingFromRoomSecure(roomData.roomId);
            if (booking != null) {
                PmsBookingRooms pmsRoom = booking.getRoom(roomData.roomId);
                if (pmsRoom != null) {
                    PmsBookingAddonItem addon = pmsRoom.addons.stream()
                            .filter(o -> referenceId.equals(o.addonId))
                            .findAny()
                            .orElse(null);

                    if (addon != null && addon.departmentRemoteId != null) {
                        item.departmentRemoteId = addon.departmentRemoteId;
                    }

                    if (addon != null && addon.taxGroupNumber != null) {
                        TaxGroup taxGroup = productManager.getTaxGroup(addon.taxGroupNumber);
                        if (taxGroup != null) {
                            prod.taxGroupObject = taxGroup;
                            prod.taxgroup = addon.taxGroupNumber;
                        }
                    }

                    return addon;
                }
            }
        }

        return null;
    }

    private List<CartItem> addConferenceCartItems(PmsOrderCreateRow roomData) {
        PosConference posConference = posManager.getPosConference(roomData.conferenceId);
        PosTab tab = posManager.getTab(posConference.tabId);

        List<CartItem> itemsToAdd = roomData.items.stream()
                .map(row -> {
                    try {
                        CartItem item = (CartItem) tab.getCartItem(row.cartItemId).clone();
                        item.setProduct(item.getProduct().clone());
                        item.setCount(row.count);
                        item.getProduct().price = row.price;
                        item.conferenceId = roomData.conferenceId;
                        return item;
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(PmsInvoiceManagerNew.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    return null;
                })
                .filter(o -> o != null)
                .collect(Collectors.toList());

        if (!itemsToAdd.isEmpty()) {
            cartManager.getCart().addCartItems(itemsToAdd);
        }

        return itemsToAdd;
    }

    private void finishTabs(Map<String, List<CartItem>> tabItemsAdded, Order order) {
        for (String conferenceId : tabItemsAdded.keySet()) {
            List<CartItem> cartItems = tabItemsAdded.get(conferenceId);
            if (!cartItems.isEmpty()) {
                PosConference posConference = posManager.getPosConference(conferenceId);
                posManager.finishTabAndOrder(posConference.tabId, order, null, null);
                order.conferenceIds.add(conferenceId);
            }
        }

        orderManager.saveObject(order);
    }

    private String getOrderText(List<PmsOrderCreateRowItemLine> days, String rowKey) {
        for (PmsOrderCreateRowItemLine l : days) {
            if (l.getKey().equals(rowKey)) {
                return l.textOnOrder;
            }
        }
        return "";
    }

    private void addCartItemsNotConnectedToARoom(PmsOrderCreateRow roomData) {

        for (PmsOrderCreateRowItemLine itemLine : roomData.items) {
            if (Objects.equals(itemLine.orderItemType, ZauiConstants.ZAUI_ACTIVITY_TAG)) {
                cartManager.addZauiActivityItem(itemLine.createOrderOnProductId, itemLine.addonId);
                continue;
            }
            CartItem item = cartManager.addProductItem(itemLine.createOrderOnProductId, itemLine.count);
            item.getProduct().price = itemLine.price;
            item.getProduct().name = itemLine.textOnOrder;

        }
    }
}
