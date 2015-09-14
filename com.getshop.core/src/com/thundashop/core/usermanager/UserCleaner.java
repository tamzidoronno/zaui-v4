/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager;

import com.thundashop.core.calendar.CalendarManager;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class UserCleaner {
    private List<User> originalUsers = new ArrayList();
    private Map<User, List<User>> users = new HashMap();
    private int cleanerLevel ;
    private MessageManager messageManager;

    public UserCleaner(int cleanerLevel, MessageManager messageManager) {
        this.cleanerLevel = cleanerLevel;
        this.messageManager = messageManager;
    }
    
    public void addUser(User user) {
        User originalUser = getOriginalUser(user);
        if (originalUser == null) {
            originalUsers.add(user);
        } else {
            List<User> clones = users.get(originalUser);
            if (clones == null) {
                clones = new ArrayList();
            }
            
            clones.add(user);
            users.put(originalUser, clones);
            
        }
    }

    private User getOriginalUser(User user) {
        for (User origUser : originalUsers) {
            if (cleanerLevel == 0) {
                if (origUser.emailAddress.equalsIgnoreCase(user.emailAddress)) {
                    return origUser;
                }
            }
            
            if (cleanerLevel == 1) {
                if (similarity(origUser.fullName, user.fullName) > 0.5) {
                    return origUser;
                }
            }
        }
        
        return null;
    }
    
    public Map<String, String> cleanNextLevel(UserManager userManager, CalendarManager calManager) throws ErrorException {
        Map<String, String> rets = printSummary();
        
        for (User user : originalUsers) {
            UserCleaner cleaner = new UserCleaner(cleanerLevel+1, messageManager);
            cleaner.addUser(user);
            List<User> clones = users.get(user);
            if (clones != null) {
                for (User clone : clones) {
                    cleaner.addUser(clone);
                }
            }
            cleaner.cleanUp(userManager, calManager);
        }
        
        return rets;
    }

    private Map<String, String> printSummary() {
        Map<String, String> returnResult = new HashMap();
        
        for (User user : originalUsers) {
            List<User> clones = users.get(user);
            
            if (clones == null || clones.size() == 0) {
                continue;
            }
            
            String text = "Hei,";
            text += "<br/><br/> Vi jobber med å rydde opp i databasen vår, og har funnet flere kontoer med forskjellig navn på samme epostadresse ("+user.emailAddress+"). Vi trenger din hjelp til å rydde opp i dette.";
            text += "<br/>";
            text += "<br/> Svar på denne eposten og fyll ut manglende eposter.";
            
            if (clones != null) {
                if (clones.size() == 0) {
                    continue;
                }
                
                text += "<br/>Navn: <b>" + user.fullName + " - </b> Epost?: ";
                
                for (User clone : clones) {
                    text += "<br/>Navn: <b>" + clone.fullName + " - </b> Epost?: ";
                    
                }
                
                text += "<br/><br/> Tusen takk!";
                text += "<br/><br/> Med Vennlig Hilsen";
                text += "<br/> ProMeister Academy";
                
                returnResult.put(user.emailAddress, text);
            }
        }
        
        return returnResult;
    }
    
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
          longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
    }
    
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
          int lastValue = i;
          for (int j = 0; j <= s2.length(); j++) {
            if (i == 0)
              costs[j] = j;
            else {
              if (j > 0) {
                int newValue = costs[j - 1];
                if (s1.charAt(i - 1) != s2.charAt(j - 1))
                  newValue = Math.min(Math.min(newValue, lastValue),
                      costs[j]) + 1;
                costs[j - 1] = lastValue;
                lastValue = newValue;
              }
            }
          }
          if (i > 0)
            costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    private void cleanUp(UserManager userManager, CalendarManager calManager) throws ErrorException {
        for (User user : originalUsers) {
            List<User> clones = users.get(user);
            
            if (clones == null || clones.size() == 0) {
                continue;
            }
            
            
            if (clones != null) {
                for (User clone : clones) {
                    if (user.comments == null) {
                        user.comments = new HashMap();
                    }
                    
                    if (clone.comments != null) {
                        user.comments.putAll(clone.comments);
                    }
                    
//                    calManager.replaceUserId(clone.id, user.id);
//                    userManager.deleteUser(clone.id);
                }
            }
        }
    }

}
