package com.thundashop.core.calendar;

import com.thundashop.core.calendarmanager.data.Day;
import com.thundashop.core.calendarmanager.data.Entry;
import com.thundashop.core.calendarmanager.data.ExtraDay;
import com.thundashop.core.calendarmanager.data.Month;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.messagemanager.SMSFactory;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
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

    private String mutateText(String username, String password, String text, Month month, Day day, Entry entry, User user) {
        String date = day.day + "/" + month.month + "-" + month.year + " : " + entry.starttime+"->"+entry.stoptime;
       
        Collections.sort(entry.otherDays);
        for (ExtraDay extraDay : entry.otherDays) {
            date += "<br>" + extraDay.day + "/" + extraDay.month + "-" + extraDay.year + " : " +extraDay.starttime + "->" + extraDay.stoptime;
        }
        String time = entry.starttime + "-" + entry.stoptime;

        text = text.replace("{USERNAME}", username);
        text = text.replace("{PASSWORD}", password);
        text = text.replace("{FULLNAME}", user.fullName);
        text = text.replace("{EVENT_TITLE}", entry.title);
        text = text.replace("{EVENT_DATE}", date);
        text = text.replace("{EVENT_TIME}", time);
        text = text.replace("{EVENT_LOCATION}", entry.location.replaceAll("\n", "<BR />"));
        text = text.replace("{EVENT_DESCRIPTION}", entry.description.replaceAll("\n", "<BR />"));
        return text;
    }

    private void sendMailNotification(String username, String password, Month month, Day day, Entry entry, Map<String, Setting> settings, User user) throws ErrorException {
        String sendmail = null;
        if(settings != null && settings.get("sendmail") != null) {
            sendmail = settings.get("sendmail").value;
        }

        if (sendmail == null || sendmail.equals("") || sendmail.equals("false")) {
            return;
        }

        String subject = settings.get("subject").value;
        String text = settings.get("bookingmail").value;

        if (subject == null || subject.isEmpty()) {
            throw new ErrorException(72);
        }

        if (text == null || text.isEmpty()) {
            throw new ErrorException(73);
        }

        text = mutateText(username, password, text, month, day, entry, user);
        mailFactory.send("noreply@getshop.com", user.emailAddress, subject, text);
    }

    private void sendSms(String username, String password, Month month, Day day, Entry entry, Map<String, Setting> settings, User user) throws ErrorException {
        String sendsms = null;
        if(settings != null && settings.get("sendsms") != null) {
            sendsms = settings.get("sendsms").value;
        }
        if (sendsms == null || sendsms.equals("") || sendsms.equals("false")) {
            return;
        }

        String text = settings.get("smstext").value;
        text = mutateText(username, password, text, month, day, entry, user);
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

    @Override
    public void onEvent(String eventName, String eventReferance) throws ErrorException {
        if (Events.USER_DELETED.equals(eventName))
            removeUserAttendee(eventReferance, "");
    }

    @Override
    public Entry createEntry(int year, int month, int day) throws ErrorException {
        Entry entry = new Entry();
        
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
    public void sendReminderToUser(boolean byEmail, boolean bySMS, List<String> users, String text, String subject) throws ErrorException {
        remindUserInternal(byEmail, bySMS, users, text, subject);
    }

    @Override
    public List<Entry> getEntries(int year, int month, int day) throws ErrorException {
        return getEntriesInternal(year, month, day);
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

        return mymonth;
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

    private void remindUserInternal(boolean byEmail, boolean bySMS, List<String> users, String text, String subject) throws ErrorException {
        for (String userId : users) {
            UserManager usrmgr = getManager(UserManager.class);
            User user = usrmgr.getUserById(userId);
            
            if (byEmail) {
                mailFactory.send("noreply@getshop.com", user.emailAddress, subject, text);
            }

            if (bySMS) {
                HashMap<String, Setting> settings = getSettings("Booking");
                String from = settings.get("smsfrom").value;
                String message = text;
                String phoneNumber = user.cellPhone;
                smsFactory.send(from, phoneNumber, message);
            }
        }
    }

    private List<Entry> getEntriesInternal(int year, int month, int day) {
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

    private void addUserToEventInternal(String userId, String eventId, String password, String username) throws ErrorException {
        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entry.entryId.equals(eventId)) {
                        if (entry.maxAttendees <= entry.attendees.size()) {
                            throw new ErrorException(71);
                        }

                        if (entry.attendees.contains(userId)) {
                            throw new ErrorException(67);
                        }

                        entry.attendees.add(userId);
                        databaseSaver.saveObject(month, credentials);

                        HashMap<String, Setting> settings = getSettings("Booking");
                        UserManager usrmgr = getManager(UserManager.class);
                        User user = usrmgr.getUserById(userId);
                        sendMailNotification(username, password, month, day, entry, settings, user);
                        sendSms(username, password, month, day, entry, settings, user);
                    }
                }
            }
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
}