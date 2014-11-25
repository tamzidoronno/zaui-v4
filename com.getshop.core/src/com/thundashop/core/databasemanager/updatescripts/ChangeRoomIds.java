
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.hotelbookingmanager.BookingReference;
import com.thundashop.core.hotelbookingmanager.ReservedRoom;
import java.net.UnknownHostException;

public class ChangeRoomIds  extends UpgradeBase {
    public static void main(String[] args) throws UnknownHostException {
        System.out.println("TEST");
        ChangeRoomIds changer = new ChangeRoomIds();
        changer.upgrade();
    }

    private void upgrade() throws UnknownHostException {
        for(BookingReference reference : getAllBookingReferences()) {
            for(String id : reference.getRoomIdsForConversion()) {
                reference.addRoom(id);
            }
            saveObject(reference, "HotelBookingManager");
        }
    }
}
