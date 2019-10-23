package com.thundashop.core.ratemanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bcomratemanager.RateManagerConfig;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsInvoiceManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.pmsmanager.PmsPricing;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.webmanager.WebManager;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.opentravel.ota._2003._05.BaseInvCountType;
import org.opentravel.ota._2003._05.InvCountType;
import org.opentravel.ota._2003._05.OTAHotelInvCountNotifRQ;
import org.opentravel.ota._2003._05.StatusApplicationControlType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class BookingComRateManagerManager extends GetShopSessionBeanNamed implements IBookingComRateManagerManager {

    @Override
    public void pushInventoryList() {
        return;
    }

    @Override
    public void pushAllBookings() {
        return;
    }

    @Override
    public void saveRateManagerConfig(RateManagerConfig config) {
        return;
    }

    @Override
    public RateManagerConfig getRateManagerConfig() {
        return null;
    }

    @Override
    public String updateRate(Date start, Date end, String roomId, Double rate) {
        return null;
    }
    
}
