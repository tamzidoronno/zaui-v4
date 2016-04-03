/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopIgnoreBackup;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
@GetShopIgnoreBackup
public class LoginHistory extends DataCommon {
    /** Old Logins before 7 feb 2014 */
    private Map<String, List<Date>> logins = new HashMap();
    
    private Map<String, List<LoginSession>> loginUserList = new HashMap();
    
    public void markLogin(User user, String sessionId) {
        List<LoginSession> loginSessions = loginUserList.get(user.id);
        if (loginSessions == null) {
            loginSessions = new ArrayList();
        }
        
        loginSessions.add(new LoginSession(user, sessionId));
        loginUserList.put(user.id, loginSessions);
    }

    public int getLogins(int year, int month) {
        int i = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        
        for (List<LoginSession> histories : loginUserList.values()) {
            for (LoginSession loginSession : histories) {
                Date date = loginSession.loginDate;
                if (date.before(endDate) && date.after(startDate)) {
                    i++;
                }
            }
        }
        
        return i;
    }


    public void markAdmin(User user, String sessionId) {
        if (user == null) {
            return;
        }
        
        List<LoginSession> loginsessions = loginUserList.get(user.id);
        for (LoginSession loginsession : loginsessions) {
            if (loginsession.sessionId != null && loginsession.sessionId.equals(sessionId)) {
                loginsession.adminActionsCount++;
                break;
            }
        }
    }

    public void markEditor(User user, String sessionId) {
        if (user == null) {
            return;
        }
        
        List<LoginSession> loginsessions = loginUserList.get(user.id);
        for (LoginSession loginsession : loginsessions) {
            if (loginsession.sessionId != null && loginsession.sessionId.equals(sessionId)) {
                loginsession.editorActionsCount++;
                break;
            }
        }
    }
}
