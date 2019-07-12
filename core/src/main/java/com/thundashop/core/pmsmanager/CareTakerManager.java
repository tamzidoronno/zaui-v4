package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class CareTakerManager extends GetShopSessionBeanNamed implements ICareTakerManager {
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    MessageManager messageManager;
    
    
    List<CareTakeRepeatingData> repeatingDataList = new ArrayList();
    
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof CareTakeRepeatingData) {
                repeatingDataList.add((CareTakeRepeatingData)dataCommon);
            }
        }
        createScheduler("caretakerprocessor", "1 1 08 * *", CareTakerDailyProcessor.class);
    }
    
    public void checkForTasksToCreate() {
        TimeRepeater repeater = new TimeRepeater();
        List<PmsCareTaker> careTakerJobs = pmsManager.getCareTakerJobs();
        for(CareTakeRepeatingData repeating : repeatingDataList) {
            LinkedList<TimeRepeaterDateRange> ranges = repeater.generateRange(repeating.repeaterData);
            for(TimeRepeaterDateRange range : ranges) {
                if(range.start.after(new Date())) {
                    continue;
                }
                boolean found = false;
                logPrint("This needs to be created");
                for(PmsCareTaker taker : careTakerJobs) {
                    if(taker.repeatingTaskId.equals(repeating.id) && taker.rowCreatedDate.equals(range.start)) {
                        found = true;
                    }
                }
                if(!found) {
                    PmsCareTaker newJob = new PmsCareTaker();
                    newJob.description = repeating.text;
                    newJob.rowCreatedDate = range.start;
                    newJob.repeatingTaskId = repeating.id;
                    newJob.roomId = repeating.roomId;
                    pmsManager.saveCareTakerJob(newJob);
                }
            }
        }
    }
    
    @Override
    public void assignTask(String taskId, String userId) {
        if(userId == null || userId.isEmpty()) {
            userId = getSession().currentUser.id;
        }
        PmsCareTaker job = pmsManager.getCareTakerJob(taskId);
        job.assignedTo = userId;
        job.assignedToName = userManager.getUserById(userId).fullName;
        job.dateAssigned = new Date();
        pmsManager.saveCareTakerJob(job);
        
        User user = userManager.getUserById(userId);
        messageManager.sendMail(user.emailAddress, user.emailAddress, 
                "Caretake task assgined to you", job.roomName +  " : " + job.description
                , user.emailAddress, user.emailAddress);
    }

    @Override
    public List<CareTakeRoomOverView> getRoomOverview(boolean defectsOnly) {
        List<CareTakeRoomOverView> overview = new ArrayList();
        
        HashMap<String, PmsCareTaker> tasks = pmsManager.getCareTakerTasks();
        List<BookingItem> items = bookingEngine.getBookingItems();
        
        for(BookingItem item : items) {
            CareTakeRoomOverView roomView = new CareTakeRoomOverView();
            roomView.itemId = item.id;
            roomView.roomName = item.bookingItemName;
            for(PmsCareTaker task : tasks.values()) {
                if(task.roomId != null && task.roomId.equals(item.id)) {
                    roomView.total++;
                    if(task.completed) {
                        roomView.completed++;
                    }
                    if(task.assignedTo != null && !task.assignedTo.isEmpty()) {
                        roomView.assigned++;
                    } else if(!task.completed) {
                        roomView.unassigned++;
                    }
                }
            }
            if(defectsOnly) {
                if(!roomView.total.equals(roomView.completed)) {
                    overview.add(roomView);
                }
            } else {
                overview.add(roomView);
            }

        }
        
        
        return overview;
    }

    @Override
    public List<CareTakerRoomList> getCareTakerList(CareTakerFilter filter) {
        
        HashMap<String, PmsCareTaker> tasks = pmsManager.getCareTakerTasks();
        List<CareTakerRoomList> res = new ArrayList();
        

        CareTakerRoomList toAdd = new CareTakerRoomList();
        toAdd.name = "General";
        toAdd.itemId = "";        
        for(PmsCareTaker task : tasks.values()) {
            if(task.roomId == null || task.roomId.isEmpty()) {
                if(filter.view.equals("unassigned") && (task.assignedTo == null || task.assignedTo.isEmpty()) && !task.completed) {
                    toAdd.jobs.add(task);
                }
                if(filter.view.equals("completed") && task.completed) {
                    toAdd.jobs.add(task);
                }
                if(filter.view.equals("assigned") && (task.assignedTo != null && !task.assignedTo.isEmpty()) && !task.completed) {
                    toAdd.jobs.add(task);
                }
            }
        }
        if(!toAdd.jobs.isEmpty()) {
            res.add(toAdd);
        }
                
        
        List<BookingItem> items = bookingEngine.getBookingItems();
        for(BookingItem item : items) {
            toAdd = new CareTakerRoomList();
            toAdd.name = item.bookingItemName;
            toAdd.itemId = item.id;
            
            CareTakeRoomOverView roomView = new CareTakeRoomOverView();
            roomView.itemId = item.id;
            roomView.roomName = item.bookingItemName;
            for(PmsCareTaker task : tasks.values()) {
                if(task.roomId != null && task.roomId.equals(item.id)) {
                    if(filter.view.equals("unassigned") && (task.assignedTo == null || task.assignedTo.isEmpty()) && !task.completed) {
                        toAdd.jobs.add(task);
                    }
                    if(filter.view.equals("completed") && task.completed) {
                        toAdd.jobs.add(task);
                    }
                    if(filter.view.equals("assigned") && (task.assignedTo != null && !task.assignedTo.isEmpty()) && !task.completed) {
                        toAdd.jobs.add(task);
                    }
                }
            }
            if(!toAdd.jobs.isEmpty()) {
                res.add(toAdd);
            }
        }
        return res;
    }

    @Override
    public List<User> getCaretakers() {
        List<User> users = new ArrayList();
        PmsConfiguration config = pmsManager.getConfigurationSecure();
        HashMap<String, List<String>> restrictions = config.mobileViewRestrictions;
        for(String userId : restrictions.keySet()) {
            List<String> views = restrictions.get(userId);
            if(views.contains("caretaker")) {
                users.add(userManager.getUserById(userId));
            }
        }
        return users;
    }

    @Override
    public void completeTask(String taskId) {
        PmsCareTaker task = pmsManager.getCareTakerJob(taskId);
        task.completed = true;
        task.dateCompleted = new Date();
        pmsManager.saveCareTakerJob(task);
    }

    @Override
    public void addRepeatingTask(CareTakeRepeatingData repeatingData) {
        saveObject(repeatingData);
        repeatingDataList.add(repeatingData);
        checkForTasksToCreate();
    }

    @Override
    public List<CareTakeRepeatingData> getRepeatingTasks() {
        return repeatingDataList;
    }

    @Override
    public void deleteRepeatingTask(String id) {
        CareTakeRepeatingData toRemove = null;
        for(CareTakeRepeatingData data : repeatingDataList) {
            if(data.id.equals(id)) {
                toRemove = data;
            }
        }
        if(toRemove != null) {
            deleteObject(toRemove);
            repeatingDataList.remove(toRemove);
        }
    }
    
    
    
}
