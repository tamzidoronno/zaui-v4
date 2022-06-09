package com.thundashop.core.pmsmanager;

import com.thundashop.core.utils.ExecutorServiceTask;

public class RemoveAccruedPaymentsTask extends ExecutorServiceTask {
    PmsManager pmsManager;
    PmsBooking booking;
    PmsOrderCreateRow row;

    public RemoveAccruedPaymentsTask(PmsManager pmsManager, PmsBooking booking, PmsOrderCreateRow row){
        this.pmsManager = pmsManager;
        this.booking = booking;
        this.row = row;
    }

    @Override
    public void run(){
        super.run();
        pmsManager.setSession(scope.getSessionFromThreadSessions(Thread.currentThread().getId()));

        try{
            System.out.println("Execution starting for room: "+ row.roomId);
            pmsManager.removeAccrudePayments(booking, row.roomId);
            System.out.println("Ended for room:"+ row.roomId);
        }
        catch(Exception ex){
            logger.error("Failed to remove accured payment for booking" + booking.id + " room id:" + row.roomId);
            logger.error("Accrued payment remove exception: " + ex.getMessage());
            pmsManager.logPrintException(ex);
            ex.printStackTrace();
        }

        if(row.conferenceId != null){
            try{
                System.out.println("Execution starting for conference "+ row.conferenceId);
                pmsManager.removeAccrudePaymentsForConference(row.conferenceId, row.items);
                System.out.println("Ended for conference:"+ row.conferenceId);
            }
            catch(Exception ex){
                logger.error("Failed to remove accured payment for booking" + booking.id + " conference id:" + row.conferenceId);
                logger.error("Accrued payment remove exception: " + ex.getMessage());
                pmsManager.logPrintException(ex);
                ex.printStackTrace();
            }
        }

    }
}
