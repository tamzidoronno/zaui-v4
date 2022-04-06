package com.thundashop.core.channelmanagerexperiment.gotohub;

import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.channelmanagerexperiment.IChannelManager;
import com.thundashop.core.channelmanagerexperiment.wubook.*;
import com.thundashop.core.pmsmanager.PmsManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class gotomanager extends GetShopSessionBeanNamed implements IChannelManager {

    @Autowired
    PmsManager pmsManager;

    private boolean connectToApi() throws Exception {
        if (!isWubookActive()) {
            return false;
        }

//        client = createClient();
//        generateNewToken();
//        return isTokenValid();
        return true;
    }

    private boolean isWubookActive() {
        if (pmsManager.getConfigurationSecure().wubookusername == null
                || pmsManager.getConfigurationSecure().wubookusername.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean testConnection() throws Exception {
        return false;
    }

    @Override
    public List<WubookLog> getLogEntries() {
        return null;
    }

    @Override
    public void doUpdateMinStay() {

    }

    @Override
    public List<WubookBooking> fetchAllBookings(Integer daysback) throws Exception {
        return null;
    }

    @Override
    public List<Integer> fetchBookingCodes(Integer daysback) throws Exception {
        return null;
    }

    @Override
    public List<WubookBooking> addNewBookingsPastDays(Integer daysback) throws Exception {
        return null;
    }

    @Override
    public void fetchNewBookings() {

    }

    @Override
    public WubookBooking fetchBooking(String rcode) throws Exception {
        return null;
    }

    @Override
    public void activateWubookCallback() throws Exception {

    }

    @Override
    public void addBooking(String rcode) throws Exception {

    }

    @Override
    public boolean updateAvailabilityFromButton() throws Exception {
        return false;
    }

    @Override
    public String updateShortAvailability() {
        return null;
    }

    @Override
    public String markNoShow(String rcode) throws Exception {
        return null;
    }

    @Override
    public String updatePrices() throws Exception {
        return null;
    }

    @Override
    public String updateMinStay() throws Exception {
        return null;
    }

    @Override
    public String markCCInvalid(String rcode) throws Exception {
        return null;
    }

    @Override
    public List<String> insertAllRooms() throws Exception {
        return null;
    }

    @Override
    public void deleteAllRooms() throws Exception {

    }

    @Override
    public String deleteBooking(String rcode) throws Exception {
        return null;
    }

    @Override
    public void doubleCheckDeletedBookings() throws Exception {

    }

    @Override
    public void checkForNoShowsAndMark() throws Exception {

    }

    @Override
    public HashMap<String, WubookRoomData> getWubookRoomData() {
        return null;
    }

    @Override
    public void saveWubookRoomData(HashMap<String, WubookRoomData> res) {

    }

    @Override
    public void addRestriction(WubookAvailabilityRestrictions restriction) {

    }

    @Override
    public List<WubookAvailabilityRestrictions> getAllRestriction() {
        return null;
    }

    @Override
    public void deleteRestriction(String id) {

    }

    @Override
    public List<WubookBooking> fetchBookings(Integer daysBack, boolean registrations) throws Exception {
        return null;
    }

    @Override
    public List<WubookOta> getOtas() throws Exception {
        return null;
    }

    @Override
    public boolean newOta(String type) throws Exception {
        return false;
    }

    @Override
    public List<WubookRoomRateMap> getRoomRates(Integer channelId, Integer channelType) throws Exception {
        return null;
    }

    @Override
    public void setRoomRates(Integer channelId, List<WubookRoomRateMap> rates, Integer channelType) {

    }

    @Override
    public String getCallbackUrl() {
        return null;
    }

    @Override
    public void fetchBookingFromCallback(String rcode) throws Exception {

    }

    @Override
    public String updatePricesBetweenDates(Date now, Date end) throws Exception {
        return null;
    }
}
