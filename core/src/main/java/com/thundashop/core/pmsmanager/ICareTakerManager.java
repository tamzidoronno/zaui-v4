package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.usermanager.data.User;
import java.util.List;

/**
 * Caretaker action for hotel.
 * @author boggi
 */
@GetShopApi
@GetShopMultiLayerSession
public interface ICareTakerManager {
    @Administrator
    public void assignTask(String taskId, String userId);
    @Administrator
    public List<CareTakeRoomOverView> getRoomOverview(boolean defectsOnly);
    
    @Administrator
    public List<CareTakerRoomList> getCareTakerList(CareTakerFilter filter);
    
    @Administrator
    public List<User> getCaretakers();
    
    @Administrator
    public void completeTask(String taskId);
    
    @Administrator
    public void addRepeatingTask(CareTakeRepeatingData repeatingData);
    
    @Administrator
    public List<CareTakeRepeatingData> getRepeatingTasks();
    
    @Administrator
    public void deleteRepeatingTask(String id);
    
    @Administrator
    public void checkForTasksToCreate();
}
