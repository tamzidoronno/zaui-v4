package com.thundashop.core.calendar;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.calendarmanager.data.AttendeeMetaInfo;
import com.thundashop.core.calendarmanager.data.Day;
import com.thundashop.core.calendarmanager.data.Entry;
import com.thundashop.core.calendarmanager.data.EntryComment;
import com.thundashop.core.calendarmanager.data.Event;
import com.thundashop.core.calendarmanager.data.EventPartitipated;
import com.thundashop.core.calendarmanager.data.ExtraDay;
import com.thundashop.core.calendarmanager.data.FilterResult;
import com.thundashop.core.calendarmanager.data.Location;
import com.thundashop.core.calendarmanager.data.Month;
import com.thundashop.core.calendarmanager.data.ReminderHistory;
import com.thundashop.core.calendarmanager.data.Signature;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.messagemanager.SMSFactory;
import com.thundashop.core.pagemanager.IPageManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserDeletedEventListener;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Comment;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.User.Type;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class CalendarManager extends ManagerBase implements ICalendarManager, UserDeletedEventListener {

    private HashMap<String, Month> months = new HashMap();
    private HashMap<String, Location> locations = new HashMap();
    private HashMap<String, Event> events = new HashMap();
    
    @Autowired
    public MailFactory mailFactory;
    
    public SMSFactory smsFactory;
    
    private List<ReminderHistory> reminderHistory = new ArrayList();
    private HashMap<String, EventPartitipated> eventData = new HashMap();
    private HashMap<String, Signature> signatures = new HashMap();

    @Autowired
    private IPageManager pageManager;
    
    @Autowired
    private StoreManager storeManager;
    
    @Autowired
    private UserManager userManager;

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataObject : data.data) {
            if (dataObject instanceof Location) {
                Location location = (Location) dataObject;
                locations.put(location.id, location);
            }
            if (dataObject instanceof Month) {
                Month month = (Month) dataObject;
                months.put(month.id, month);
            }
            if (dataObject instanceof ReminderHistory) {
                ReminderHistory hist = (ReminderHistory) dataObject;
                reminderHistory.add(hist);
            }
            if (dataObject instanceof EventPartitipated) {
                EventPartitipated evp = (EventPartitipated) dataObject;
                eventData.put(evp.pageId, evp);
            }
            if (dataObject instanceof Event) {
                Event event = (Event)dataObject;
                events.put(event.id, event);
            }
            if (dataObject instanceof Signature) {
                Signature signature = (Signature) dataObject;
                signatures.put(signature.userid, signature);
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
        if (month.year > compareYear) {
            return true;
        }

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
            date += "->" + entry.stoptime;
        }

        Collections.sort(entry.otherDays);
        for (ExtraDay extraDay : entry.otherDays) {
            date += "<br>" + extraDay.day + "/" + extraDay.month + "-" + extraDay.year + " : " + extraDay.starttime + "->" + extraDay.stoptime;
        }

        String time = entry.starttime + "-" + entry.stoptime;

        if (user.username != null) {
            text = text.replace("{USERNAME}", user.username);
        }
        if (password != null) {
            text = text.replace("{PASSWORD}", password);
        }
        if (user.fullName != null) {
            text = text.replace("{FULLNAME}", user.fullName);
        }
        if (entry.title != null) {
            text = text.replace("{EVENT_TITLE}", entry.title);
        }
        if (date != null) {
            text = text.replace("{EVENT_DATE}", date);
        }
        if (time != null) {
            text = text.replace("{EVENT_TIME}", time);
        }
        if (entry.location != null) {
            text = text.replace("{EVENT_LOCATION}", entry.location.replaceAll("\n", "<BR />"));
        }
        if (entry.description != null) {
            text = text.replace("{EVENT_DESCRIPTION}", entry.description.replaceAll("\n", "<BR />"));
        }

        String groupLogo = getGroupLogo(user);
        if (groupLogo != null) {
            String address = "http://" + storeManager.getMyStore().webAddressPrimary + "//displayImage.php?id=" + groupLogo;
            String imageTag = "<img width='150' src='" + address + "'/>";
            text = text.replace("{GROUP_LOGO}", imageTag);
        }


        return text;
    }

    private HashMap<String, Setting> getBookingSettings() throws ErrorException {
        HashMap<String, Setting> settings = null;

        List<ApplicationInstance> calendars = pageManager.getApplicationsBasedOnApplicationSettingsId("6f3bc804-02a1-44b0-a17d-4277f0c6dee8");
        List<ApplicationInstance> bookings = pageManager.getApplicationsBasedOnApplicationSettingsId("74ea4e90-2d5a-4290-af0c-230a66e09c78");

        // TODO - remove this shit!
        // There must be a better way, I guess you dont even 
        // understand whats happening here. (hint, getting the correct booking app
        for (ApplicationInstance calendar : calendars) {
            if (calendar.settings != null && calendar.settings.get("linkToBookingPage") != null) {
                for (ApplicationInstance config : bookings) {
                    ArrayList<String> list = new ArrayList();
                    list.add(config.id);
                    Map<String, List<String>> pages = pageManager.getPagesForApplications(list);
                    for (String appId : pages.keySet()) {
                        List<String> pageIds = pages.get(appId);
                        for (String pageId : pageIds) {
                            if (pageId.equals(calendar.settings.get("linkToBookingPage").value)) {
                                settings = config.settings;
                            }
                        }
                    }
                }
            }
        }

        return settings;
    }

    private void sendMailNotification(String password, Entry entry, User user, boolean waitingList) throws ErrorException {
        String sendmail = null;

        HashMap<String, Setting> settings = getBookingSettings();

        if (settings != null && settings.get("sendmail") != null) {
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
        String storeEmailAddress = storeManager.getMyStore().configuration.emailAdress;
        if (storeEmailAddress != null) {
            return storeEmailAddress;
        }

        return "noreply@getshop.com";
    }

    private void sendSms(String password, Entry entry, User user, boolean waitingList) throws ErrorException {
        String sendsms = null;

        HashMap<String, Setting> settings = getBookingSettings();

        if (settings != null && settings.get("sendsms") != null) {
            sendsms = settings.get("sendsms").value;
        }

        if (sendsms == null || sendsms.equals("") || sendsms.equals("false")) {
            return;
        }

        if (waitingList && settings.get("smstextwaiting") == null || settings.get("smstextwaiting").value.equals("")) {
            return;
        }

        String text = settings.get("smstext").value;

        if (waitingList) {
            text = settings.get("smstextwaiting").value;
        }

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
    public Entry createEntry(int year, int month, int day) throws ErrorException {
        Entry entry = new Entry();

        if (getSession() == null
                || getSession().currentUser == null
                || getSession().currentUser.isCustomer()) {
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
    public void sendReminderToUser(boolean byEmail, boolean bySMS, List<String> users, String text, String subject, String eventId, String attachment, String filename) throws ErrorException {
        remindUserInternal(byEmail, bySMS, users, text, subject, eventId, attachment, filename);
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

                        for (Entry entry : myDay.entries) {
                            finalizeEntry(entry);
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
    public void addUserToEvent(String userId, String eventId, String password, String username, String source) throws ErrorException {
        addUserToEventInternal(userId, eventId, password, username, source);
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

    private Map<String, String> getAttachedFiles(String attachment, String filename) {
        Map<String, String> files = null;
        if (attachment != null && !attachment.equals("")) {
            try {
                byte[] data = DatatypeConverter.parseBase64Binary(attachment);
                String tmpFileName = "/tmp/" + UUID.randomUUID().toString();
                FileOutputStream fos = new FileOutputStream(tmpFileName);
                fos.write(data);
                fos.close();

                files = new HashMap();
                files.put(tmpFileName, filename);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return files;
    }
    

    private void remindUserInternal(boolean byEmail, boolean bySMS, List<String> users, String text, String subject, String eventId, String attachment, String filename) throws ErrorException {
        ReminderHistory smsHistory = createReminderHistory(text, subject, eventId, byEmail);
        ReminderHistory emailHistory = createReminderHistory(text, subject, eventId, byEmail);

        Map<String, String> files = getAttachedFiles(attachment, filename);

        for (String userId : users) {
            User user = userManager.getUserById(userId);

            if (byEmail) {
                if (files != null) {
                    mailFactory.sendWithAttachments(getFromAddress(), user.emailAddress, subject, text, files, true);
                } else {
                    mailFactory.send(getFromAddress(), user.emailAddress, subject, text);
                }

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

    private void addUserToEventInternal(String userId, String eventId, String password, String username, String source) throws ErrorException {
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
                        
                        AttendeeMetaInfo metaInfo = new AttendeeMetaInfo();
                        metaInfo.source = source;
                        metaInfo.userId = userId;
                        entry.metaInfo.put(userId, metaInfo);

                        databaseSaver.saveObject(month, credentials);

                        User user = userManager.getUserById(userId);
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
                    if (entry.entryId.equals(entryId)) {
                        finalizeEntry(entry);
                        return entry;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void saveEntry(Entry entry) throws ErrorException {
        Entry oldEntry = getEntry(entry.entryId);
        
        for(EntryComment comment : entry.comments) {
            if(comment.id == null || comment.id.isEmpty()) {
                comment.id = UUID.randomUUID().toString();
            }
        }
        
        if (oldEntry == null) {
            throw new ErrorException(1012);
        }

        if (!entry.needConfirmation && (getSession().currentUser.type < Type.EDITOR)) {
            throw new ErrorException(26);
        }

        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                Entry toRemove = null;
                for (Entry tmpEntry : day.entries) {
                    if (tmpEntry.entryId.equals(entry.entryId)) {
                        toRemove = tmpEntry;
                    }
                }

                if (toRemove != null) {
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
            User user = userManager.getUserById(userId);
            sendMessages(user, entry, "", false);
        }
    }

    @Override
    public List<String> getFilters() throws ErrorException {
        List<String> filterIds = new ArrayList();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        for (Entry entry : getEntries(year, month, day, null)) {
            if (entry.locationId == null) {
                continue;
            }

            if (!filterIds.contains(entry.locationId)) {
                filterIds.add(entry.locationId);
            }
        }

        ArrayList<String> filters = new ArrayList();
        for (String filterId : filterIds) {
            Location loc = locations.get(filterId);
            if (loc != null) {
                filters.add(loc.location);
            }
        }

        java.util.Collections.sort(filters);


        return filters;
    }

    private String toCamelCase(String s) {
        if (s == null) {
            return "";
        }
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    private String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase()
                + s.substring(1).toLowerCase();
    }

    private void filterResult(List<Entry> entries, List<String> filters) {
        List<Entry> removeEntries = new ArrayList();
        for (Entry entry : entries) {

            boolean found = false;
            for (String s : filters) {
                Location loc = getLocationByName(s);
                if (loc != null && loc.id.equals(entry.locationId)) {
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

        for (Day day : mymonth.days.values()) {
            for (Entry entry : day.entries) {
                finalizeEntry(entry);
            }
        }

        if (lsession == null) {
            return mymonth;
        }

        List<String> filters = (List<String>) lsession.get("filters");

        if (filters == null || filters.isEmpty()) {
            return mymonth;
        }

        mymonth = mymonth.clone();


        for (Day day : mymonth.days.values()) {
            filterResult(day.entries, (List) lsession.get("filters"));
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

                User userObject = userManager.getUserById(userId);
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
            if (hist.eventId == null) {
                continue;
            }

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
        User user = userManager.getUserById(userId);
        removeUserAttendee(userId, fromEventId);
        removeUserWaitingList(userId, fromEventId);

        String newPassord = "" + (11005 + (int) (Math.random() * ((98999 - 11005) + 1)));
        userManager.updatePassword(userId, "", newPassord);
        addUserToEvent(userId, toEventId, newPassord, user.username, "system");
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


        Collections.sort(entries);
        Collections.reverse(entries);

        return entries;
    }

    @Override
    public void addUserToPageEvent(String userId, String bookingAppId) throws ErrorException {
        User user = userManager.getUserById(userId);
        if (user != null) {
            HashMap<String, Setting> settings = pageManager.getSecuredSettings(bookingAppId);

            Comment comment = new Comment();
            comment.appId = bookingAppId;
            comment.comment = "BookingEvent";
            userManager.addComment(user.id, comment);

            String storeOwner = storeManager.getMyStore().configuration.emailAdress;
            String content = settings.get("bookingmail").value;
            content = mutateText("", content, new Entry(), user);

            mailFactory.send(getFromAddress(), user.emailAddress, settings.get("subject").value, content);
            mailFactory.send("post@getshop.com", getFromAddress(), settings.get("subject").value, content);
        }
    }

    @Override
    public Location saveLocation(Location location) throws ErrorException {
        location.storeId = storeId;

        for (Comment comment : location.comments) {
            if (comment.createdByUserId == null) {
                comment.createdByUserId = getSession().currentUser.id;
            }
        }

        databaseSaver.saveObject(location, credentials);
        locations.put(location.id, location);

        return location;
    }

    @Override
    public List<Location> getAllLocations() {
        return new ArrayList(locations.values());
    }

    private void finalizeEntry(Entry entry) {
        entry.locationObject = locations.get(entry.locationId);
        if (entry.locationObject != null) {
            entry.location = entry.locationObject.location;
            entry.locationExtended = entry.locationObject.locationExtra;
        }
        
        if (entry.eventId != null && !entry.eventId.isEmpty()) {
            entry.event = events.get(entry.eventId);
            entry.maxAttendees = entry.event.capacity;
        }
        
        if (entry.isInPast()) {
            entry.availableForBooking = false;
        }
        
        if (entry.lockedForSignup) {
            entry.availableForBooking = false;
        }
        
        if (entry.attendees.size() >= entry.maxAttendees) {
            entry.availableForBooking = false;
        }
    }

    @Override
    public void deleteLocation(String locationId) throws ErrorException {
        Location loc = locations.remove(locationId);
        if (loc != null) {
            databaseSaver.deleteObject(loc, credentials);
        }
    }

    private Location getLocationByName(String s) {
        for (Location loc : locations.values()) {
            String locationName = toCamelCase(s);
            if (locationName.equalsIgnoreCase(loc.location)) {
                return loc;
            }
        }

        return null;
    }

    @Override
    public List<Month> getMonthsAfter(int year, int month) {
        List<Month> months = new ArrayList();
        for (Month imonth : this.months.values()) {
            if (imonth.isNewerOrEqual(year, month)) {
                months.add(finalizeMonth(imonth));
            }
        }

        return months;
    }

    @Override
    public List<FilterResult> getEventsGroupedByPageId() throws ErrorException {
        List<FilterResult> retEntries = new ArrayList();

        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    String pageId = entry.linkToPage;
                    if (pageId == null) {
                        pageId = "null";
                    }

                    FilterResult retResult = null;
                    for (FilterResult result : retEntries) {
                        if (result.pageId.equals(pageId)) {
                            retResult = result;
                            break;
                        }
                    }


                    if (retResult == null) {
                        retResult = new FilterResult();
                        retResult.pageId = pageId;

                        retEntries.add(retResult);
                    }

                    finalizeEntry(entry);
                    retResult.entries.add(entry);
                }
            }
        }

        return retEntries;
    }

    @Override
    public List<Entry> getEntriesByUserId(String userId) throws ErrorException {
        List<Entry> entries = new ArrayList();
        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entry.attendees.contains(userId)) {
                        finalizeEntry(entry);
                        entries.add(entry);
                    }
                }
            }
        }
        return entries;
    }

    @Override
    public EventPartitipated getEventPartitipatedData(String pageId) throws ErrorException {
        return eventData.get(pageId);
    }

    @Override
    public void setEventPartitipatedData(EventPartitipated data) throws ErrorException {
        EventPartitipated event = getEventPartitipatedData(data.pageId);

        if (event != null) {
            //Update the exsiting one.
            event.body = data.body;
            event.title = data.title;
            event.title2 = data.title2;
            event.heading = data.heading;
            data = event;
        }

        data.storeId = storeId;
        databaseSaver.saveObject(data, credentials);
        eventData.put(data.pageId, data);
    }

    @Override
    public String getSignature(String id) throws ErrorException {
        return signatures.get(id).signature;
    }

    @Override
    public void setSignature(String userid, String signature) throws ErrorException {
        Signature cursign = signatures.get(userid);
        if (cursign != null) {
            cursign.signature = signature;
        } else {
            cursign = new Signature();
            cursign.storeId = storeId;
            cursign.userid = userid;
            cursign.signature = signature;
            signatures.put(cursign.userid, cursign);
        }
        databaseSaver.saveObject(cursign, credentials);
    }

    @Override
    public List<Month> getMonths() throws ErrorException {
        Set<Month> calendars = new TreeSet();

        for (Month imonth : months.values()) {
            if (imonth.isOutOfDate()) {
                continue;
            }
            
            Month month = imonth.clone();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            int curmonth = calendar.get(Calendar.MONTH) + 1;
            int curDay = calendar.get(Calendar.DAY_OF_MONTH);

            if (curmonth == month.month) {
                TreeMap<Integer, Day> days = new TreeMap();
                for (Day day : month.days.values()) {
                    if (day.day > curDay) {
                        days.put(day.day, day);
                    }
                }
                month.days = days;
            }
            
            calendars.add(finalizeMonth(month));
        }

        return new ArrayList(calendars);
    }

    @Override
    public void addUserSilentlyToEvent(String eventId, String userId) throws ErrorException {
        Entry entry = getEntry(eventId);
        User user = userManager.getUserById(userId);
        System.out.println("user; " + user);
        System.out.println("entry: " + entry);
        if (entry != null && entry.attendees != null && !entry.attendees.contains(userId) && user != null) {
            entry.attendees.add(userId);
            saveEntry(entry);
        }
   
    }

    @Override
    public void userDeleted(String userId) throws ErrorException {
        removeUserAttendee(userId, "");
        removeUserWaitingList(userId, "");
    }

	private HashMap<String, Setting> getSettings(String booking) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

    @Override
    public void addEvent(Event event) {
        saveObject(event);
        events.put(event.id, event);
    }

    @Override
    public List<Event> getEvents() {
        return new ArrayList(events.values());
    }

    @Override
    public Event getEvent(String eventId) {
        return events.get(eventId);
    }

}
