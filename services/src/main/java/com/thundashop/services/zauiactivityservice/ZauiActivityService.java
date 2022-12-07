package com.thundashop.services.zauiactivityservice;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.zauiactivity.constant.ZauiConstants;
import com.thundashop.zauiactivity.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thundashop.repository.exceptions.ZauiException;
import com.thundashop.repository.utils.SessionInfo;
import com.thundashop.repository.utils.ZauiStatusCodes;
import com.thundashop.repository.zauiactivityrepository.IZauiActivityConfigRepository;
import com.thundashop.repository.zauiactivityrepository.IZauiActivityRepository;
import com.thundashop.services.octoapiservice.IOctoApiService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ZauiActivityService implements IZauiActivityService {
    @Autowired
    private IZauiActivityConfigRepository zauiActivityConfigRepository;
    @Autowired
    private IZauiActivityRepository zauiActivityRepository;
    @Autowired
    private IOctoApiService octoApiService;

    public ZauiActivityConfig getZauiActivityConfig(SessionInfo sessionInfo) {
        try{
            return zauiActivityConfigRepository.getZauiActivityConfig(sessionInfo).orElse(new ZauiActivityConfig());
        } catch(Exception ex){
            log.error("Failed to get zaui activity config. Reason: {}, Actual Exception: {}", ex.getMessage(), ex);
            return null;
        }
    }

    public ZauiActivityConfig setZauiActivityConfig(ZauiActivityConfig zauiActivityConfig, SessionInfo sessionInfo) {
        return zauiActivityConfigRepository.save(zauiActivityConfig, sessionInfo);
    }

    public List<ZauiActivity> getZauiActivities(SessionInfo sessionInfo) {
        return zauiActivityRepository.getAll(sessionInfo);
    }

    @Override
    public Optional<ZauiActivity> getZauiActivityById(String Id, SessionInfo sessionInfo) {
        return zauiActivityRepository.getById(Id, sessionInfo);
    }

    public void fetchZauiActivities(SessionInfo sessionInfo) throws ZauiException {
        ZauiActivityConfig zauiActivityConfig = zauiActivityConfigRepository.getZauiActivityConfig(sessionInfo).orElse(null);
        if (zauiActivityConfig == null) {
            return;
        }
        zauiActivityConfig.getSupplierIds().forEach(supplierId ->
                octoApiService.getOctoProducts(supplierId).forEach(
                        octoProduct -> zauiActivityRepository.save(mapOctoToZauiActivity(octoProduct, supplierId), sessionInfo)));

    }
    @Override
    public void addActivityToBooking(BookingZauiActivityItem activityItem, PmsBooking booking) throws ZauiException {
        if(activityItem.units == null)
            throw new ZauiException(ZauiStatusCodes.MISSING_PARAMS);
        OctoBookingReserveRequest bookingReserveRequest = new OctoBookingReserveRequest()
                .setProductId(activityItem.octoProductId)
                .setOptionId(activityItem.optionId)
                .setAvailabilityId(activityItem.availabilityId)
                .setNotes("Zaui Stay Booking")
                .setUnitItems(activityItem.units.stream().map(o -> new UnitItemReserveRequest(o.id)).collect(Collectors.toList()));
        OctoBookingReserve octoBookingReserve = octoApiService.reserveBooking(activityItem.supplierId,bookingReserveRequest);
        activityItem.setOctoBooking(octoBookingReserve);
        booking.bookingZauiActivityItems.add(activityItem);
    }


    private ZauiActivity mapOctoToZauiActivity(OctoProduct octoProduct, Integer supplierId) {
        ZauiActivity zauiActivity = new ZauiActivity();
        zauiActivity.name = octoProduct.getTitle();
        zauiActivity.productId = octoProduct.getId();
        zauiActivity.supplierId = supplierId;
        zauiActivity.shortDescription = octoProduct.getShortDescription();
        zauiActivity.description = octoProduct.getPrimaryDescription();
        zauiActivity.activityOptionList = octoProduct.getOptions();
        zauiActivity.mainImage = octoProduct.getCoverImage();
        zauiActivity.tag = ZauiConstants.ZAUIACTIVITY_TAG;
        return zauiActivity;
    }
}
