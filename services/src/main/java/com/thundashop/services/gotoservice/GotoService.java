package com.thundashop.services.gotoservice;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.core.gotohub.dto.Contact;
import com.thundashop.core.gotohub.dto.GoToConfiguration;
import com.thundashop.core.gotohub.dto.Hotel;
import com.thundashop.core.gotohub.dto.LongTermDeal;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsConfiguration;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.repository.gotohubrepository.IGotoConfigRepository;
import com.thundashop.repository.pmsbookingrepository.IPmsBookingRepository;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.services.pmspricing.IPmsPricingService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GotoService implements IGotoService {
    @Autowired
    private IPmsPricingService pmsPricingService;

    @Autowired
    private IGotoConfigRepository gotoConfigRepository;

    @Autowired
    private IPmsBookingRepository pmsBookingRepository;

    @Override
    public GoToConfiguration getGotoConfiguration(SessionInfo sessionInfo) {
        try {
            return gotoConfigRepository.getGotoConfiguration(sessionInfo);
        } catch (Exception ex) {
            log.error("Failed to get gotohub configuration. Actual exception: {}", ex);
            return null;
        }
    }

    @Override
    public List<LongTermDeal> getLongTermDeals(SessionInfo sessionInfo) {
        PmsPricing pricing = pmsPricingService.getByCodeOrDefaultCode("", sessionInfo);
        return pricing.longTermDeal.keySet()
                .stream()
                .filter(Objects::nonNull)
                .map(numOfDays -> new LongTermDeal(numOfDays, pricing.longTermDeal.get(numOfDays)))
                .sorted(Comparator.comparingInt(LongTermDeal::getNumbOfDays))
                .collect(Collectors.toList());
    }

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

    @Override
    public List<PmsBooking> getUnpaidGotoBookings(int autoDeletionTime, SessionInfo sessionInfo) {
        return pmsBookingRepository.getGotoBookings(sessionInfo).stream()
                .filter(booking -> booking.getActiveRooms() != null && !booking.getActiveRooms().isEmpty())
                .filter(booking -> (booking.orderIds == null || booking.orderIds.isEmpty())
                        && booking.isOlderThan(autoDeletionTime))
                .collect(Collectors.toList());
    }

}
