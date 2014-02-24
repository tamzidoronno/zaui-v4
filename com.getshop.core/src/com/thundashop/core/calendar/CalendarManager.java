package com.thundashop.core.calendar;

import com.thundashop.core.calendarmanager.data.Day;
import com.thundashop.core.calendarmanager.data.Entry;
import com.thundashop.core.calendarmanager.data.ExtraDay;
import com.thundashop.core.calendarmanager.data.Month;
import com.thundashop.core.calendarmanager.data.ReminderHistory;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.messagemanager.SMSFactory;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.User.Type;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class CalendarManager extends ManagerBase implements ICalendarManager {
    private HashMap<String, Month> months = new HashMap();
    
    @Autowired
    public MailFactory mailFactory;
    
    @Autowired
    public SMSFactory smsFactory;

    private List<ReminderHistory> reminderHistory = new ArrayList();
    
    @Autowired
    public CalendarManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataObject : data.data) {
            if (dataObject instanceof Month) {
                Month month = (Month)dataObject;
                months.put(month.id, month);
            }
            if (dataObject instanceof ReminderHistory) {
                ReminderHistory hist = (ReminderHistory)dataObject;
                reminderHistory.add(hist);
            }
        }
    }

    private Month getMonth(int year, int month) {
        for (Month mountObject : months.values()) {
            if (mountObject.is(year, month)) {
                return mountObject;
            }
        }

        Month mountObject = new Month();
        mountObject.year = year;
        mountObject.month = month;
        mountObject.storeId = storeId;
        return mountObject;
    }

    private void registerEntryInternal(int year, int month, int day, Entry entry) throws ErrorException {
        Month mymonth = this.getMonth(year, month);

        if (entry.entryId == null || entry.entryId == "") {
            entry.entryId = UUID.randomUUID().toString();
        }

        entry.year = year;
        entry.month = month;
        entry.day = day;
        
        mymonth.addEntry(day, entry);
        mymonth.storeId = storeId;
        databaseSaver.saveObject(mymonth, credentials);
        months.put(mymonth.id, mymonth);
    }

    private boolean isNewerOrEquals(Day day, Month month, int compareMonth, int compareDay, int compareYear) {
        if (month.year > compareYear)
            return true;
        
        if (month.month > compareMonth) {
            return true;
        }

        if (month.month == compareMonth && day.day >= compareDay) {
            return true;
        }

        return false;
    }

    private String mutateText(String password, String text, Entry entry, User user) throws ErrorException {
        String date = entry.day + "/" + entry.month + "-" + entry.year + " : " + entry.starttime;
       
        if (entry.stoptime != null) {
            date += "->"+entry.stoptime;
        }
        
        Collections.sort(entry.otherDays);
        for (ExtraDay extraDay : entry.otherDays) {
            date += "<br>" + extraDay.day + "/" + extraDay.month + "-" + extraDay.year + " : " +extraDay.starttime + "->" + extraDay.stoptime;
        }
        
        String time = entry.starttime + "-" + entry.stoptime;

        if (user.username != null)
            text = text.replace("{USERNAME}", user.username);
        if (password != null)
            text = text.replace("{PASSWORD}", password);
        if (user.fullName != null)
            text = text.replace("{FULLNAME}", user.fullName);
        if (entry.title != null)
            text = text.replace("{EVENT_TITLE}", entry.title);
        if (date != null)
            text = text.replace("{EVENT_DATE}", date);
        if (time != null)
            text = text.replace("{EVENT_TIME}", time);
        if (entry.location != null)
            text = text.replace("{EVENT_LOCATION}", entry.location.replaceAll("\n", "<BR />"));
        if (entry.description != null)
            text = text.replace("{EVENT_DESCRIPTION}", entry.description.replaceAll("\n", "<BR />"));
        
        String groupLogo = getGroupLogo(user);
        if (groupLogo != null) {
            String address = "http://"+getStore().webAddressPrimary+"//displayImage.php?id="+groupLogo;
            String imageTag = "<img width='150' src='"+address+"'/>";
            text = text.replace("{GROUP_LOGO}", imageTag);
        }
        
        
        return text;
    }

    private void sendMailNotification(String password, Entry entry, User user, boolean waitingList) throws ErrorException {
        String sendmail = null;
        HashMap<String, Setting> settings = getSettings("Booking");
        if(settings != null && settings.get("sendmail") != null) {
            sendmail = settings.get("sendmail").value;
        }

        if (sendmail == null || sendmail.equals("") || sendmail.equals("false")) {
            return;
        }

        if (waitingList) {
            if (settings.get("subjectwaiting") == null || settings.get("bookingmailwaiting") == null) {
                return;
            }
        }
        
        String subject = settings.get("subject").value;
        String text = settings.get("bookingmail").value;

        if (waitingList) {
            subject = settings.get("subjectwaiting").value;
            text = settings.get("bookingmailwaiting").value;
        }
        
        if (subject == null || subject.isEmpty()) {
            throw new ErrorException(72);
        }

        if (text == null || text.isEmpty()) {
            throw new ErrorException(73);
        }

        text = mutateText(password, text, entry, user);
        
        mailFactory.send(getFromAddress(), user.emailAddress, subject, text);
        if (user.emailAddressToInvoice != null && !user.emailAddressToInvoice.equals("")) {
            mailFactory.send(getFromAddress(), user.emailAddressToInvoice, subject, text);
        }
    }
    
    private String getFromAddress() throws ErrorException {
        String storeEmailAddress = getStore().configuration.emailAdress;
        if (storeEmailAddress != null) {
            return storeEmailAddress;
        }
        
        return "noreply@getshop.com";
    }

    private void sendSms(String password, Entry entry, User user, boolean waitingList) throws ErrorException {
        String sendsms = null;
        HashMap<String, Setting> settings = getSettings("Booking");
        if(settings != null && settings.get("sendsms") != null) {
            sendsms = settings.get("sendsms").value;
        }
        if (sendsms == null || sendsms.equals("") || sendsms.equals("false")) {
            return;
        }

        String text = settings.get("smstext").value;
        text = mutateText(password, text, entry, user);
        String from = settings.get("smsfrom").value;
        String message = text;
        String phoneNumber = user.cellPhone;
        smsFactory.send(from, phoneNumber, message);
    }

    private Entry removeUserAttendee(String userId, String entryId) throws ErrorException {
        Entry retentry = null;

        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entryId.equals("")) {
                        entry.attendees.remove(userId);
                    } else if (entry.entryId.equals(entryId)) {
                        retentry = entry;
                        entry.attendees.remove(userId);
                    }
                    databaseSaver.saveObject(month, credentials);
                }
            }
        }

        return retentry;
    }
    
    private Entry removeUserWaitingList(String userId, String entryId) throws ErrorException {
        Entry retentry = null;

        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entryId.equals("")) {
                        entry.waitingList.remove(userId);
                    } else if (entry.entryId.equals(entryId)) {
                        retentry = entry;
                        entry.waitingList.remove(userId);
                    }
                    databaseSaver.saveObject(month, credentials);
                }
            }
        }

        return retentry;
    }

    @Override
    public void onEvent(String eventName, String eventReferance) throws ErrorException {
        if (Events.USER_DELETED.equals(eventName)) {
            removeUserAttendee(eventReferance, "");
            removeUserWaitingList(eventReferance, "");
        }
    }

    @Override
    public Entry createEntry(int year, int month, int day) throws ErrorException {
        Entry entry = new Entry();
        
        if (getSession() == null || 
                getSession().currentUser == null || 
                getSession().currentUser.isCustomer()) {
            entry.needConfirmation = true;
        }
        
        registerEntryInternal(year, month, day, entry);
        
        return entry;
    }

    @Override
    public Month getMonth(int year, int month, boolean includeExtraEvents) throws ErrorException {
        return getMonthInternal(year, month, includeExtraEvents);
    }

    @Override
    public void deleteEntry(String id) throws ErrorException {
        deleteEntryInternal(id);
    }

    @Override
    public void sendReminderToUser(boolean byEmail, boolean bySMS, List<String> users, String text, String subject, String eventId) throws ErrorException {
        remindUserInternal(byEmail, bySMS, users, text, subject, eventId);
    }

    @Override
    public List<Entry> getEntries(int year, int month, int day, List<String> filters) throws ErrorException {
        List<Entry> entries = new ArrayList();

        for (Month myMonth : months.values()) {
            if (myMonth.isNewerOrEqual(year, month)) {
                for (Day myDay : myMonth.days.values()) {
                    if (isNewerOrEquals(myDay, myMonth, month, day, year)) {
                        for (Entry entry : myDay.entries) {
                            entry.year = myMonth.year;
                            entry.month = myMonth.month;
                            entry.day = myDay.day;
                        }

                        entries.addAll(myDay.entries);
                    }
                }
            }
        }

        Collections.sort(entries);

        return entries;
    }

    @Override
    public void addUserToEvent(String userId, String eventId, String password, String username) throws ErrorException {
        addUserToEventInternal(userId, eventId, password, username);
    }

    @Override
    public void removeUserFromEvent(String userId, String eventId) throws ErrorException {
        removeUserAttendee(userId, eventId);
    }

    private Month getMonthInternal(int year, int month, boolean includeExtraEvents) {
        Month mymonth = new Month(getMonth(year, month));

        if (!includeExtraEvents) {
            return mymonth;
        }

        for (Month checkMonth : this.months.values()) {
            for (Day checkDay : checkMonth.days.values()) {
                for (Entry checkEntry : checkDay.entries) {
                    for (ExtraDay extraDay : checkEntry.otherDays) {
                        if (extraDay.month == month && extraDay.year == year) {
                            Day day = mymonth.days.get(extraDay.day);
                            if (day == null) {
                                day = new Day();
                                day.day = extraDay.day;
                                mymonth.days.put(day.day, day);
                            }

                            checkEntry.isOriginal = false;
                            day.entries.add(checkEntry);
                        }
                    }
                }
            }
        }

        
        return finalizeMonth(mymonth);
    }

    private Month deleteEntryInternal(String id) throws ErrorException {
        Day onDay = null;
        Entry removeEntry = null;
        Month onMonth = null;

        for (Month month : months.values()) {
            onMonth = month;

            for (Day day : month.days.values()) {
                onDay = day;
                for (Entry entry : day.entries) {
                    if (entry.entryId != null && entry.entryId.equals(id)) {
                        removeEntry = entry;
                        break;
                    }
                }

                if (removeEntry != null) {
                    break;
                }
            }

            if (removeEntry != null) {
                break;
            }
        }

        if (onDay != null && removeEntry != null && onMonth != null) {
            onDay.entries.remove(removeEntry);
            databaseSaver.saveObject(onMonth, credentials);
        }

        return onMonth;
    }
    
    private ReminderHistory createReminderHistory(String text, String subject, String eventId, boolean byEmail) {
        ReminderHistory smsHistory = new ReminderHistory();
        smsHistory.byEmail = byEmail;
        smsHistory.text = text;
        smsHistory.subject = subject;
        smsHistory.storeId = storeId;
        smsHistory.eventId = eventId;
        return smsHistory;
    }

    private void remindUserInternal(boolean byEmail, boolean bySMS, List<String> users, String text, String subject, String eventId) throws ErrorException {
        ReminderHistory smsHistory = createReminderHistory(text, subject, eventId, byEmail);
        ReminderHistory emailHistory = createReminderHistory(text, subject, eventId, byEmail);
        
        for (String userId : users) {
            UserManager usrmgr = getManager(UserManager.class);
            User user = usrmgr.getUserById(userId);
            
            if (byEmail) {
                mailFactory.send(getFromAddress(), user.emailAddress, subject, text);
                emailHistory.users.add(user);
            }

            if (bySMS) {
                HashMap<String, Setting> settings = getSettings("Booking");
                String from = settings.get("smsfrom").value;
                String message = text;
                String phoneNumber = user.cellPhone;
                smsFactory.send(from, phoneNumber, message);
                smsHistory.users.add(user);
            }
        }
        
        if (smsHistory.users.size() > 0) {
            databaseSaver.saveObject(smsHistory, credentials);
            reminderHistory.add(smsHistory);
        }
        
        if (emailHistory.users.size() > 0) {
            databaseSaver.saveObject(emailHistory, credentials);
            reminderHistory.add(emailHistory);
        }
    }

    private void addUserToEventInternal(String userId, String eventId, String password, String username) throws ErrorException {
        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entry.entryId.equals(eventId)) {

                        if (entry.attendees.contains(userId)) {
                            throw new ErrorException(67);
                        }

                        boolean waitingList = entry.maxAttendees <= entry.attendees.size();
                        if (waitingList) {
                            entry.waitingList.add(userId);
                        } else {
                            entry.attendees.add(userId);
                        }
                        
                        databaseSaver.saveObject(month, credentials);

                        UserManager usrmgr = getManager(UserManager.class);
                        User user = usrmgr.getUserById(userId);
                        sendMessages(user, entry, password, waitingList);
                    }
                }
            }
        }
    }
    
    private void sendMessages(User user, Entry entry, String password, boolean waitingList) throws ErrorException {
        if (!entry.needConfirmation) {
            sendMailNotification(password, entry, user, waitingList);
            sendSms(password, entry, user, waitingList);
        }
    }
    
    @Override
    public Entry getEntry(String entryId) {
        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entry.entryId.equals(entryId))
                        return entry;
                }
            }
        }
        
        return null;
    }

    @Override
    public void saveEntry(Entry entry) throws ErrorException {
        Entry oldEntry = getEntry(entry.entryId);
        if(oldEntry == null) {
            throw new ErrorException(1012);
        }
        
        if (!entry.needConfirmation && (getSession().currentUser.type < Type.EDITOR)) {
            throw new ErrorException(26);
        }
        
        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                Entry toRemove = null;
                for (Entry tmpEntry : day.entries) {
                    if (tmpEntry.entryId.equals(entry.entryId))
                        toRemove = tmpEntry;
                }
                
                if(toRemove != null) {
                    day.entries.remove(toRemove);
                    day.entries.add(entry);
                }
            }
            
            databaseSaver.saveObject(month, credentials);
        }
    }

    @Override
    public void confirmEntry(String entryId) throws ErrorException {
        Entry entry = getEntry(entryId);
        entry.needConfirmation = false;
        saveEntry(entry);
        
        for (String userId : entry.attendees) {
            UserManager usermanager = getManager(UserManager.class);
            User user = usermanager.getUserById(userId);
            sendMessages(user, entry, "", false);
        }
    }

    @Override
    public List<String> getFilters() throws ErrorException {
        List<String> filters = new ArrayList();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
       
        for (Entry entry : getEntries(year, month, day, null)) {
            if (entry.location == null) {
                continue;
            }
            String camelCasedLocation = toCamelCase(entry.location);
            if (!filters.contains(camelCasedLocation)) {
                filters.add(camelCasedLocation);
            }

        }
        
        java.util.Collections.sort(filters);
        return filters;
    }
    
    private String toCamelCase(String s){
        if (s == null) {
            return "";
        }
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts){
           camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
     }
    
    private String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                   s.substring(1).toLowerCase();
    }

    private void filterResult(List<Entry> entries, List<String> filters) {
        List<Entry> removeEntries = new ArrayList();
        for (Entry entry : entries) {
            String camelCasedLocation = toCamelCase(entry.location);
            
            boolean found = false;
            for (String s : filters) {
                if (s.equalsIgnoreCase(camelCasedLocation)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                removeEntries.add(entry);
            }
        }
        
        for (Entry entry : removeEntries) {
            entries.remove(entry);
        }
    }

    @Override
    public void applyFilter(List<String> filters) {
        if (getSession() != null) {
            if (filters != null) {
                if (filters.size() > 0) {
                    List<String> setThisFilters = new ArrayList();
                    for (String filter : filters) {
                        setThisFilters.add(toCamelCase(filter));
                    }
                    
                    getSession().put("filters", setThisFilters);
                } else {
                    getSession().remove("filters");
                }
            }
        }
    }

    private Month finalizeMonth(Month mymonth) {
        Session lsession = getSession();
        if (lsession == null) {
            return mymonth;
        }
        
        List<String> filters = (List<String>) lsession.get("filters");
        
        if (filters == null || filters.isEmpty()) {
            return mymonth;
        }
        
        mymonth = mymonth.clone();
        for (Day day : mymonth.days.values()) {
            filterResult(day.entries, (List)lsession.get("filters"));
        }
        
        return mymonth;
    }

    @Override
    public List<String> getActiveFilters() {
        if (getSession() == null) {
            return new ArrayList<String>();
        }
        
        List<String> filters = (List<String>) getSession().get("filters");
        if (filters == null) {
            filters = new ArrayList<String>();
        }
        return filters;
    }

    @Override
    public void transferFromWaitingList(String eventId, String userId) throws ErrorException {
        Entry entry = getEntry(eventId);
        for (String user : entry.waitingList) {
            if (user.equals(userId)) {
                entry.waitingList.remove(userId);
                entry.attendees.add(userId);
                Month month = getMonth(entry.year, entry.month);
                databaseSaver.saveObject(month, credentials);

                UserManager usrmgr = getManager(UserManager.class);
                User userObject = usrmgr.getUserById(userId);
                sendMessages(userObject, entry, "", false);            
                return;
            }
        }
        
        throw new ErrorException(101);
    }

    private String getGroupLogo(User user) throws ErrorException {
        if (user.groups == null || user.groups.isEmpty()) {
            return null;
        }
        
        UserManager userManager = getManager(UserManager.class);
        String groupId = user.groups.iterator().next();
        for (Group group : userManager.getAllGroups()) {
            if (group.id.equals(groupId)) {
                return group.imageId;
            }
        }

        return null;
    }

    @Override
    public List<ReminderHistory> getHistory(String eventId) {
        Set<ReminderHistory> allHistory = new TreeSet();
        for (ReminderHistory hist : reminderHistory) {
            if (hist.eventId.equals(eventId)) {
                allHistory.add(hist);
            }
        }
        
        List<ReminderHistory> sortedList = new ArrayList(allHistory);
        Collections.reverse(sortedList);
        return sortedList;
    }

    @Override
    public void transferUser(String fromEventId, String toEventId, String userId) throws ErrorException {
        UserManager userManager = this.getManager(UserManager.class);
        User user = userManager.getUserById(userId);
        removeUserAttendee(userId, fromEventId);
        removeUserWaitingList(userId, fromEventId);
        
        String newPassord = ""+(11005 + (int)(Math.random() * ((98999 - 11005) + 1)));
        userManager.updatePassword(userId, "", newPassord);
        addUserToEvent(userId, toEventId, newPassord, user.username);
    }

    @Override
    public List<Entry> getAllEventsConnectedToPage(String pageId) {
        List<Entry> entries = new ArrayList();
        
        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entry.linkToPage != null 
                            && entry.linkToPage.equals(pageId) 
                            && !entry.isInPast() 
                            && entry.availableForBooking 
                            && !entry.lockedForSignup) {
                        entries.add(entry);
                    }
                }
            }
        }
        
        return entries; 
   }
}
