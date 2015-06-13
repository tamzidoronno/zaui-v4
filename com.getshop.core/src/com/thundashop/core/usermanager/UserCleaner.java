/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager;

import com.thundashop.core.calendar.CalendarManager;
import com.thundashop.core.common.ErrorException;
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

    public UserCleaner(int cleanerLevel) {
        this.cleanerLevel = cleanerLevel;
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
                if (origUser.cellPhone != null 
                        && user.cellPhone != null 
//                        && origUser.cellPhone.equals(user.cellPhone) 
                        && similarity(origUser.fullName, user.fullName) > 0.5) {
                    return origUser;
                }
            }
        }
        
        return null;
    }
    
    public void cleanNextLevel(UserManager userManager, CalendarManager calManager) throws ErrorException {
        printSummary();
        System.out.println("=========== Next level =============");
        
        for (User user : originalUsers) {
            UserCleaner cleaner = new UserCleaner(cleanerLevel+1);
            cleaner.addUser(user);
            List<User> clones = users.get(user);
            if (clones != null) {
                for (User clone : clones) {
                    cleaner.addUser(clone);
                }
            }
            cleaner.cleanUp(userManager, calManager);
        }
        
        
    }

    private void printSummary() {
        for (User user : originalUsers) {
            List<User> clones = users.get(user);
            
            if (clones == null || clones.size() == 0) {
                continue;
            }
            
            System.out.println("orig user: " +user.fullName + " email: " + user.emailAddress);
            
            if (clones != null) {
                for (User clone : clones) {
                    System.out.println("   - " + clone.fullName + " email: " + clone.emailAddress);
                }
            }
            System.out.println("");
        }
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
            
            System.out.println("orig user: " +user.fullName + " email: " + user.emailAddress);
            
            if (clones != null) {
                for (User clone : clones) {
                    calManager.replaceUserId(clone.id, user.id);
                    userManager.deleteUser(clone.id);
                }
            }
        }
    }

}
