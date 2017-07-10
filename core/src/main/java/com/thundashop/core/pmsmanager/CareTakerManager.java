package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
                if(task.roomId.equals(item.id)) {
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
        
        List<BookingItem> items = bookingEngine.getBookingItems();
        for(BookingItem item : items) {
            CareTakerRoomList toAdd = new CareTakerRoomList();
            toAdd.name = item.bookingItemName;
            toAdd.itemId = item.id;
            
            CareTakeRoomOverView roomView = new CareTakeRoomOverView();
            roomView.itemId = item.id;
            roomView.roomName = item.bookingItemName;
            for(PmsCareTaker task : tasks.values()) {
                if(task.roomId.equals(item.id)) {
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
    
}
