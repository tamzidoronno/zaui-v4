package com.thundashop.core.timeregisteringmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;


/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class TimeRegisteringManager  extends ManagerBase implements ITimeRegisteringManager {

    HashMap<String, TimeRegistered> registeredTimes = new HashMap();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon res : data.data) {
            if(res instanceof TimeRegistered) {
                registeredTimes.put(res.id, (TimeRegistered) res);
            }
        }
    }
    
    @Override
    public void registerTime(Date start, Date end, String comment) {
        TimeRegistered newTime = new TimeRegistered();
        newTime.start = start;
        newTime.end = end;
        newTime.comment = comment;
        newTime.userId = getSession().currentUser.id;
        saveObject(newTime);
        registeredTimes.put(newTime.id, newTime);
    }

    @Override
    public void deleteTimeUnsecure(String id) {
        TimeRegistered toCheck = registeredTimes.get(id);
        User currentUser = getSession().currentUser;
        if(currentUser.isAdministrator() || currentUser.id.equals(toCheck.userId)) {
            registeredTimes.remove(id);
            deleteObject(toCheck);
        }
    }

    @Override
    public List<TimeRegistered> getMyHours() {
        List<TimeRegistered> result = new ArrayList();
        User user = getSession().currentUser;
        for(TimeRegistered reg : registeredTimes.values()) {
            if(reg.userId.equals(user.id)) {
                result.add(reg);
            }
        }
        return sortList(result);
    }

    @Override
    public List<TimeRegistered> getAllTimesRegistered() {
        List<TimeRegistered> res = new ArrayList(registeredTimes.values());
        for(TimeRegistered reg : res) {
            finalize(reg);
        }
        return sortList(res);
    }

    private TimeRegistered finalize(TimeRegistered reg) {
        long diff = reg.end.getTime() - reg.start.getTime();
        reg.hours = (int)(diff / (60*1000*60));
        reg.minutes = (int)((reg.hours*(60*1000*60)) - diff)/60;
        return reg;
    }

    @Override
    public List<TimeRegistered> getRegisteredHoursForUser(String userId, Date start, Date end) {
        List<TimeRegistered> toCheck = new ArrayList();
        for(TimeRegistered test : registeredTimes.values()) {
            if(test.userId.equals(userId)) {
                toCheck.add(test);
            }
        }
        List<TimeRegistered> finalResult = new ArrayList();
        for(TimeRegistered test : toCheck) {
            if(start.equals(test.start) || end.equals(test.start)) {
                finalResult.add(finalize(test));
            } else if(start.before(test.start) && end.after(test.start)) {
                finalResult.add(finalize(test));
            }
        }
        
        return sortList(finalResult);
    }

    private List<TimeRegistered> sortList(List<TimeRegistered> finalResult) {
        Collections.sort(finalResult, new Comparator<TimeRegistered>(){
            public int compare(TimeRegistered o1, TimeRegistered o2){
                return o2.start.compareTo(o1.start);
            }
       });
        
        return finalResult;
    }
    
}
