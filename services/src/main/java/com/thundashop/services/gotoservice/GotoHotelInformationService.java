package com.thundashop.services.gotoservice;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.springframework.stereotype.Service;

import com.thundashop.core.gotohub.dto.Contact;
import com.thundashop.core.gotohub.dto.Hotel;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.core.storemanager.data.Store;

@Service
public class GotoHotelInformationService implements IGotoHotelInformationService {
    @Override
    public Hotel getHotelInformation(Store store, PmsConfiguration pmsConfiguration, String currencyCode) {
        Hotel hotel = new Hotel();
        Contact contact = new Contact();

        hotel.setName(store.configuration.shopName);
        String address = "";
        if (isNotBlank(store.configuration.streetAddress)) {
            address = store.configuration.streetAddress;
        }
        address += store.configuration.Adress;
        hotel.setAddress(address);
        hotel.setCurrencyCode(currencyCode);
        hotel.setCheckinTime(pmsConfiguration.getDefaultStart());
        hotel.setCheckoutTime(pmsConfiguration.getDefaultEnd());
        contact.setEmail(store.configuration.emailAdress);
        contact.setOrganizationNumber(store.configuration.orgNumber);
        contact.setPhoneNumber(store.configuration.phoneNumber);
        String website = getHotelWebAddress(store);
        if (isNotBlank(website))
            contact.setWebsite(website);

        hotel.setContactDetails(contact);
        hotel.setDescription("");

        String imageUrlPrefix = "https://" + store.webAddressPrimary + "//displayImage.php?id=";
        if (isNotBlank(store.configuration.mobileImagePortrait))
            hotel.getImages().add(imageUrlPrefix + store.configuration.mobileImagePortrait);
        if (isNotBlank(store.configuration.mobileImageLandscape))
            hotel.getImages().add(imageUrlPrefix + store.configuration.mobileImageLandscape);
        hotel.setStatus(store.deactivated ? "Deactivated" : "Active");
        return hotel;
    }

    private String getHotelWebAddress(Store store) {
        String webAddress = store.getDefaultWebAddress();
        if (webAddress.contains("getshop.com"))
            return webAddress;
        for (String address : store.additionalDomainNames) {
            if (address.contains("getshop.com"))
                return address;
        }
        return null;
    }

}
