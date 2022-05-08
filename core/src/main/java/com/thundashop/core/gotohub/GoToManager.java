package com.thundashop.core.gotohub;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.gotohub.dto.Contact;
import com.thundashop.core.gotohub.dto.Hotel;
import com.thundashop.core.gotohub.dto.RoomType;
import com.thundashop.core.pmsmanager.PmsAdditionalTypeInformation;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@GetShopSession
public class GoToManager extends ManagerBase implements IGoToManager {

    @Autowired
    PmsManager pmsManager;
    @Autowired
    StoreManager storeManager;
    @Autowired
    StorePool storePool;
    @Autowired
    BookingEngine bookingEngine;
    private static final Logger logger = LoggerFactory.getLogger(GoToManager.class);
    private static final String MANAGER = GoToManager.class.getSimpleName();
    private GoToSettings settings = new GoToSettings();

    public synchronized void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof GoToSettings) {
                settings = (GoToSettings) dataCommon;
            }
        }
    }

    @Override
    public boolean changeToken(String newToken) {
        try{
            settings.authToken = newToken;
            saveObject(settings);
            return true;
        } catch (Exception e){
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public String testConnection() throws Exception {
        return settings.authToken;
    }

    @Override
    public Hotel getHotelInformation() {
        Store store = storeManager.getMyStore();
        PmsConfiguration pmsConfiguration = pmsManager.getConfiguration();

        return mapStoreToGoToHotel(store, pmsConfiguration);
    }

    @Override
    public List<RoomType> getRoomTypeDetails() throws Exception{
        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypes();
        List<RoomType> roomTypes = new ArrayList<RoomType>();

        for (BookingItemType type : bookingItemTypes) {
            PmsAdditionalTypeInformation additionalInfo = pmsManager.getAdditionalTypeInformationById(type.id);
            roomTypes.add(mapBookingItemTypeToGoToRoomType(type, additionalInfo));
        }

        return roomTypes;
    }

    @Override
    public void getPriceAndAllotment() throws Exception {
        Calendar cal = Calendar.getInstance();
        Date now = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, pmsManager.getConfigurationSecure().daysAllowedInTheFuture + 20);
        Date end = cal.getTime();

        pmsManager.getAllRoomTypes(now, end);

    }

    RoomType mapBookingItemTypeToGoToRoomType(BookingItemType bookingItemType, PmsAdditionalTypeInformation additionalInfo){
        RoomType roomType = new RoomType();

        roomType.room_type_code = bookingItemType.productId;
        roomType.description = bookingItemType.description;
        roomType.name = bookingItemType.name;
        roomType.max_guest = bookingItemType.size;
        roomType.number_of_adults = additionalInfo.numberOfAdults;
        roomType.number_of_children = additionalInfo.numberOfChildren;
        roomType.number_of_unit = bookingEngine.getBookingItemsByType(bookingItemType.id).size();

        return roomType;
    }

    Hotel mapStoreToGoToHotel(Store store, PmsConfiguration pmsConfiguration){
        Hotel hotel = new Hotel();
        Contact contact = new Contact();

        hotel.name = pmsConfiguration.bookingTag;
        hotel.address = store.configuration.Adress+ ", "+store.configuration.streetAddress;
        hotel.checkin_time = pmsConfiguration.getDefaultStart();
        hotel.checkout_time = pmsConfiguration.getDefaultEnd();
        contact.email = store.configuration.emailAdress;
        contact.organization_number = store.configuration.orgNumber;
        contact.phone_number = store.configuration.phoneNumber;
        String website = getHotelWebAddress(store);
        if(website!=null) contact.website = website;

        hotel.contact_details = contact;
        hotel.hotel_code = store.additionalDomainNames.get(0);

        return hotel;
    }
    String getHotelWebAddress(Store store){
        String webAddress = store.getDefaultWebAddress();
        if(webAddress.contains("getshop.com")) return webAddress;
        for(String address: store.additionalDomainNames){
            if(address.contains("getshop.com")) return address;
        }
        return null;
    }

}
