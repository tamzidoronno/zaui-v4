package com.thundashop.core.availability;

import com.thundashop.core.availability.dto.AvailabilityRequest;
import com.thundashop.core.availability.dto.AvailabilityResponse;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.*;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsbookingprocess.StartBooking;
import com.thundashop.core.pmsbookingprocess.StartBookingResult;
import com.thundashop.core.pmsmanager.*;
import com.thundashop.core.usermanager.data.User;

import java.util.*;

/**
 * Property management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IAvailabilityManager {

    @Administrator
    public AvailabilityResponse checkAvailability(AvailabilityRequest arg);
}