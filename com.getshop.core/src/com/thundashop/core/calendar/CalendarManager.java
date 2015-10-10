package com.thundashop.core.calendar;

import com.thundashop.core.calendarmanager.data.AttendeeMetaInfo;
import com.thundashop.core.calendarmanager.data.Day;
import com.thundashop.core.calendarmanager.data.DiplomaPeriod;
import com.thundashop.core.calendarmanager.data.Entry;
import com.thundashop.core.calendarmanager.data.EntryComment;
import com.thundashop.core.calendarmanager.data.EventPartitipated;
import com.thundashop.core.calendarmanager.data.ExtraDay;
import com.thundashop.core.calendarmanager.data.FilterResult;
import com.thundashop.core.calendarmanager.data.Location;
import com.thundashop.core.calendarmanager.data.LocationArea;
import com.thundashop.core.calendarmanager.data.LocationPoint;
import com.thundashop.core.calendarmanager.data.Month;
import com.thundashop.core.calendarmanager.data.ReminderHistory;
import com.thundashop.core.calendarmanager.data.Signature;
import com.thundashop.core.calendarmanager.data.StatisticResult;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.messagemanager.MailStatus;
import com.thundashop.core.messagemanager.MessageStatusFactory;
import com.thundashop.core.messagemanager.SMSFactory;
import com.thundashop.core.pagemanager.IPageManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Comment;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.User.Type;
import com.thundashop.core.utils.CompanySearchEngine;
import com.thundashop.core.utils.CompanySearchEngineHolder;
import java.awt.Point;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import javax.xml.bind.DatatypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private HashMap<String, Location> locations = new HashMap();
    @Autowired
    public MailFactory mailFactory;
    
    @Autowired
    @Qualifier("SmsFactoryClickatell")
    public SMSFactory smsFactory;
    private List<ReminderHistory> reminderHistory = new ArrayList();
    private HashMap<String, EventPartitipated> eventData = new HashMap();
    private HashMap<String, Signature> signatures = new HashMap();

    @Autowired
    private CompanySearchEngineHolder holder;

    @Autowired
    private MessageStatusFactory messageStatusFactory;
    
    private List<DiplomaPeriod> diplomaPeriods = new ArrayList<DiplomaPeriod>();
    private Map<String, LocationArea> areas = new HashMap();

    @Autowired
    public CalendarManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

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
            if (dataObject instanceof DiplomaPeriod) {
                DiplomaPeriod diploma = (DiplomaPeriod) dataObject;
                diplomaPeriods.add(diploma);
            }
            if (dataObject instanceof LocationArea) {
                LocationArea area = (LocationArea) dataObject;
                areas.put(area.id, area);
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
        if (user.cellPhone != null) {
            text = text.replace("{USER_CELLPHONE}", user.cellPhone);
        }
        if (entry.location != null) {
            text = text.replace("{EVENT_LOCATION}", entry.location.replaceAll("\n", "<BR />"));
        }
        if (entry.locationExtended != null) {
            text = text.replace("{EVENT_LOCATION_EXTENDED}", entry.locationExtended.replaceAll("\n", "<BR />"));
        }
        if (entry.description != null) {
            text = text.replace("{EVENT_DESCRIPTION}", entry.description.replaceAll("\n", "<BR />"));
        }

        if (user.emailAddress != null) {
            text = text.replace("{USER_EMAILADDRESS}", user.emailAddress.replaceAll("\n", "<BR />"));
        }

        if (user.company != null) {
            if (user.company.name != null) {
                text = text.replace("{COMPANY_NAME}", user.company.name.replaceAll("\n", "<BR />"));
            }

            if (user.company.vatNumber != null) {
                text = text.replace("{COMPANY_VAT}", user.company.vatNumber.replaceAll("\n", "<BR />"));
            }

            if (user.company.streetAddress != null) {
                text = text.replace("{COMPANY_STREETADDRESS}", user.company.streetAddress.replaceAll("\n", "<BR />"));
            }

            if (user.company.postnumber != null) {
                text = text.replace("{COMPANY_POSTNUMBER}", user.company.postnumber.replaceAll("\n", "<BR />"));
            }

            if (user.company.country != null) {
                text = text.replace("{COMPANY_COUNTRY}", user.company.country.replaceAll("\n", "<BR />"));
            }

            if (user.company.city != null) {
                text = text.replace("{COMPANY_CITY}", user.company.city.replaceAll("\n", "<BR />"));
            }
        }

        String groupLogo = getGroupLogo(user);
        if (groupLogo != null) {
            String address = "http://" + getStore().webAddressPrimary + "//displayImage.php?id=" + groupLogo;
            String imageTag = "<img width='150' src='" + address + "'/>";
            text = text.replace("{GROUP_LOGO}", imageTag);
        }

        return text;
    }

    private HashMap<String, Setting> getBookingSettings() throws ErrorException {
        HashMap<String, Setting> settings = null;

        IPageManager pageManager = getManager(PageManager.class);
        List<AppConfiguration> calendars = pageManager.getApplicationsBasedOnApplicationSettingsId("6f3bc804-02a1-44b0-a17d-4277f0c6dee8");
        List<AppConfiguration> bookings = pageManager.getApplicationsBasedOnApplicationSettingsId("74ea4e90-2d5a-4290-af0c-230a66e09c78");

        // TODO - remove this shit!
        // There must be a better way, I guess you dont even 
        // understand whats happening here. (hint, getting the correct booking app
        for (AppConfiguration calendar : calendars) {
            if (calendar.settings != null && calendar.settings.get("linkToBookingPage") != null) {
                for (AppConfiguration config : bookings) {
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

        mailFactory.send(getFromAddress(null), user.emailAddress, subject, text);
        if (user.emailAddressToInvoice != null && !user.emailAddressToInvoice.equals("") && !user.emailAddress.equals(user.emailAddressToInvoice)) {
            mailFactory.send(getFromAddress(null), user.emailAddressToInvoice, subject, text);
        }
    }

    private String getFromAddress(String bookingAppId) throws ErrorException {

        // Return the address specified by the app.
        if (bookingAppId != null) {
            PageManager pageManager = getManager(PageManager.class);
            Map<String, Setting> settings = pageManager.getSecuredSettings(bookingAppId);
            if (settings != null && settings.get("email_booking_notification") != null) {
                if (settings.get("email_booking_notification").value != null && settings.get("email_booking_notification").value != "") {
                    return settings.get("email_booking_notification").value;
                }
            }
        }

        String storeEmailAddress = getStore().configuration.emailAdress;
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
            boolean changed = false;
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entryId.equals("")) {
                        entry.attendees.remove(userId);
                        changed = true;
                    } else if (entry.entryId.equals(entryId)) {
                        retentry = entry;
                        entry.attendees.remove(userId);
                        changed = true;
                    }
                }
            }

            if (changed) {
                databaseSaver.saveObject(month, credentials);
            }
        }

        return retentry;
    }

    private Entry removeUserWaitingList(String userId, String entryId) throws ErrorException {
        Entry retentry = null;

        for (Month month : months.values()) {
            boolean changed = false;

            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entryId.equals("")) {
                        entry.waitingList.remove(userId);
                        changed = true;
                    } else if (entry.entryId.equals(entryId)) {
                        retentry = entry;
                        entry.waitingList.remove(userId);
                        changed = true;
                    }
                }
            }

            if (changed) {
                databaseSaver.saveObject(month, credentials);
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
            UserManager usrmgr = getManager(UserManager.class);
            User user = usrmgr.getUserById(userId);
            
            Entry entry = getEntry(eventId);
            String mutatedText = text;
            mutatedText = mutateText("", mutatedText, entry, user);
            if (byEmail) {
                String status1 = null;
                String status2 = null;
                if (files != null) {
                    status1 = mailFactory.sendWithAttachments(getFromAddress(null), user.emailAddress, subject, mutatedText, files, true);
                    status2 = mailFactory.sendWithAttachments(getFromAddress(null), user.emailAddressToInvoice, subject, mutatedText, files, true);
                } else {
                    status1 = mailFactory.send(getFromAddress(null), user.emailAddress, subject, mutatedText);
                    status2 = mailFactory.send(getFromAddress(null), user.emailAddressToInvoice, subject, mutatedText);
                }
                
                MailStatus mailStatus1 = messageStatusFactory.getStatus(status1, storeId);
                MailStatus mailStatus2 = messageStatusFactory.getStatus(status2, storeId);

                emailHistory.emailStatus.put(user.id, mailStatus1);
                emailHistory.invoiceEmailStatus.put(user.id, mailStatus2);
                    
                emailHistory.users.add(user);
            }

            if (bySMS) {
                
                HashMap<String, Setting> settings = getSettings("Booking");
                String from = settings.get("smsfrom").value;
                String message = mutatedText;
                String phoneNumber = user.cellPhone;
                String messageId = smsFactory.send(from, phoneNumber, message);
                smsHistory.users.add(user);
                
                MailStatus smsStatus = messageStatusFactory.getStatus(messageId, storeId);
                smsHistory.smsStatus.put(user.id, smsStatus);
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
        if (userId == null) {
            throw new NullPointerException("Tried to add a user to an event that does not exists");
        }
        
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
                        if (userId != null && metaInfo != null) {
                            entry.metaInfo.put(userId, metaInfo);
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

        for (EntryComment comment : entry.comments) {
            if (comment.id == null || comment.id.isEmpty()) {
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
            UserManager usermanager = getManager(UserManager.class);
            User user = usermanager.getUserById(userId);
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
            if (hist.eventId == null) {
                continue;
            }

            if (hist.eventId.equals(eventId)) {
                finalizeHistory(hist);
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
        UserManager manager = getManager(UserManager.class);
        User user = manager.getUserById(userId);
        if (user != null) {
            IPageManager pageManager = getManager(PageManager.class);
            HashMap<String, Setting> settings = pageManager.getSecuredSettings(bookingAppId);

            Comment comment = new Comment();
            comment.appId = bookingAppId;
            comment.comment = "BookingEvent";
            manager.addComment(user.id, comment);

            String storeOwner = getStore().configuration.emailAdress;
            String content = settings.get("bookingmail").value;
            content = mutateText("", content, new Entry(), user);

            mailFactory.send(getFromAddress(bookingAppId), user.emailAddress, settings.get("subject").value, content);
            mailFactory.send("post@getshop.com", getFromAddress(bookingAppId), settings.get("subject").value, content);
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
        
        for (String userId : entry.attendees) {
            if (entry.participateData.get(userId) == null) {
                entry.participateData.put(userId, "participated");
            }
        }
        
        entry.isInPast = entry.isInPast();
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
        if (getSession() == null) {
            return new ArrayList();
        }
        
        if (getSession().currentUser.type < 50) {
            hasAccessToUserId(userId);
        }
        
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
    public void setSignature(String userid, String signature, String dimplomaId) throws ErrorException {
        DiplomaPeriod diplomaPeriod = getDiplomaPeriod(dimplomaId);
        if (diplomaPeriod != null) {
            diplomaPeriod.addSignature(userid, signature);
        }

        databaseSaver.saveObject(diplomaPeriod, credentials);
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
                    if (day.day >= curDay) {
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
        UserManager userManager = getManager(UserManager.class);
        User user = userManager.getUserById(userId);
        System.out.println("user; " + user);
        System.out.println("entry: " + entry);
        if (entry != null && entry.attendees != null && !entry.attendees.contains(userId) && user != null) {
            entry.attendees.add(userId);
            saveEntry(entry);
        }

    }

    @Override
    public void createANewDiplomaPeriod(Date startDate, Date stopDate) throws ErrorException {
        DiplomaPeriod diplomaPeriod = new DiplomaPeriod();
        diplomaPeriod.startDate = startDate;
        diplomaPeriod.stopDate = stopDate;
        diplomaPeriods.add(diplomaPeriod);
        saveObject(diplomaPeriod);
    }

    @Override
    public List<DiplomaPeriod> getDiplomaPeriods() throws ErrorException {
        List<DiplomaPeriod> periods = new ArrayList();
        for (DiplomaPeriod period : diplomaPeriods) {
            periods.add(finalizePeriod(period));
        }
        return periods;
    }

    @Override
    public void deleteDiplomaPeriode(String id) throws ErrorException {
        DiplomaPeriod toDelete = getDiplomaPeriod(id);

        if (toDelete != null) {
            diplomaPeriods.remove(toDelete);
            database.delete(toDelete, credentials);
        }
    }

    private DiplomaPeriod finalizePeriod(DiplomaPeriod period) throws ErrorException {
        UserManager manager = getManager(UserManager.class);
        for (User user : manager.getAllUsers()) {
            if (user.isAdministrator()) {
                period.addUser(user);
            }
        }

        for (DiplomaPeriod per : diplomaPeriods) {
            per.finalizeObject();
        }

        return period;
    }

    private DiplomaPeriod getDiplomaPeriod(String dimplomaId) throws ErrorException {
        for (DiplomaPeriod period : diplomaPeriods) {
            if (period.id.equals(dimplomaId)) {
                return finalizePeriod(period);
            }
        }

        return null;
    }

    @Override
    public void removeSignature(String userId, String diplomId) throws ErrorException {
        DiplomaPeriod diplomPeriod = getDiplomaPeriod(diplomId);
        if (diplomPeriod != null) {
            diplomPeriod.unsetSignature(userId);
            saveObject(diplomPeriod);
        }
    }

    @Override
    public void setDiplomaPeriodeBackground(String diplomaId, String background) throws ErrorException {
        DiplomaPeriod diplomPeriod = getDiplomaPeriod(diplomaId);
        if (diplomPeriod != null) {
            diplomPeriod.backgroundImage = background;
            saveObject(diplomPeriod);
        }
    }

    @Override
    public DiplomaPeriod getDiplomaPeriod(Date date) throws ErrorException {
        for (DiplomaPeriod period : diplomaPeriods) {
            if (period.startDate.before(date) && period.stopDate.after(date)) {
                return finalizePeriod(period);
            }
        }

        return null;
    }

    @Override
    public void setDiplomaTextColor(String diplomaId, String textColor) throws ErrorException {
        DiplomaPeriod diplomPeriod = getDiplomaPeriod(diplomaId);
        if (diplomPeriod != null) {
            diplomPeriod.textColor = textColor;
            saveObject(diplomPeriod);
        }
    }

    @Override
    public void saveLocationArea(LocationArea area) throws ErrorException {
        saveObject(area);
        areas.put(area.id, area);
    }

    @Override
    public List<LocationArea> getArea() {
        List<LocationArea> retareas = new ArrayList(areas.values());
//        retareas.add(new LocationArea());
        return retareas;
    }

    @Override
    public LocationArea getEntriesByPosition(LocationPoint pointFromDevice) throws ErrorException {
        savePoint(pointFromDevice);
        
        PageManager pageManager = getManager(PageManager.class);
        HashMap<String, Setting> settings = pageManager.getSecuredSettingsInternal("Settings");
        if (settings.get("promeisterAppBasedOnDistanceInsteadOfRegion") != null && settings.get("promeisterAppBasedOnDistanceInsteadOfRegion").value.equals("true")) {
            return getEntriesBasedOnDistance(pointFromDevice);
        } else {
            return getRegionsList(pointFromDevice);
        }
    }
    
    private LocationArea getEntriesBasedOnDistance(LocationPoint pointFromDevice) throws ErrorException {
        LocationArea area = new LocationArea();
        area.name = "Distance";
        for (Month month : getMonths()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    finalizeEntry(entry);
                    area.entries.add(entry);
                }
            }
        }
        
        return area;
    }
    
    private LocationArea getRegionsList(LocationPoint pointFromDevice) throws ErrorException {
        LocationArea foundArea = null;
        Point point = new Point((int)pointFromDevice.x, (int)pointFromDevice.y);
        for (LocationArea area : areas.values()) {
            if (area.contains(point)) {
                foundArea = area;
                break;
            }
        }
        
        List<Entry> retList = new ArrayList();
        
        if (foundArea != null) {
            for (String locationId : foundArea.locations) {
                for (Month month : getMonths()) {
                    for (Day day : month.days.values()) {
                        for (Entry entry : day.entries) {
                            if (entry.locationId.equals(locationId)) {
                                finalizeEntry(entry);
                                retList.add(entry);
                            }
                        }
                    }
                }
            }
            
            Collections.sort(retList);
            Collections.reverse(retList);
            foundArea.entries = retList;
            
            return foundArea;
        }
        
        return null;
    }

    private void savePoint(LocationPoint pointFromDevice) throws ErrorException {
        saveObject(pointFromDevice);
    }

    @Override
    public void registerToken(String token) throws ErrorException {
        User user = getSession().currentUser;

        if (user != null && token != null && !token.isEmpty()) {
            UserManager userManager = getManager(UserManager.class);
            User storedUser = userManager.getUserById(user.id);
            storedUser.lastRegisteredToken = token;
            userManager.saveUserDirect(storedUser);
        }
    }

    public void replaceUserId(String clone, String newUserId) {
        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entry.dropDiploma.contains(clone)) {
                        entry.dropDiploma.remove(clone);
                        entry.dropDiploma.add(newUserId);
                    }
                    
                    if (entry.attendees.contains(clone)) {
                        entry.attendees.remove(clone);
                        entry.attendees.add(newUserId);
                    }
                    
                    if (entry.waitingList.contains(clone)) {
                        entry.waitingList.remove(clone);
                        entry.waitingList.add(newUserId);
                    }
                    
                    if (entry.metaInfo != null) {
                        AttendeeMetaInfo metaInfo = entry.metaInfo.remove(clone);
                        if (metaInfo != null) {
                            metaInfo.userId = newUserId;
                            if (newUserId != null && metaInfo != null) {
                                entry.metaInfo.put(newUserId, metaInfo);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<Entry> getMyEvents() {
        List<Entry> entries = new ArrayList();
        
        if (getSession() == null || getSession().currentUser == null) {
            return entries;
        }
        
        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    if (entry.attendees.contains(getSession().currentUser.id)) {
                        finalizeEntry(entry);
                        entries.add(entry);
                    }
                }
            }
        }
        
        return entries;
    }

    @Override
    public boolean isUserOnEvent(String userId, String eventId) throws ErrorException {
        Entry entry = getEntry(eventId);
        
        if (entry != null) {
            if (entry.attendees.contains(userId)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean isUserOnWaiting(String userId, String eventId) throws ErrorException {
        Entry entry = getEntry(eventId);
        
        if (entry != null) {
            if (entry.waitingList.contains(userId)) {
                return true;
            }
        }
        
        return false;

    }

    @Override
    public String getAgreementText() {
        if (storeId.equals("d27d81b9-52e9-4508-8f4c-afffa2458488")) {
            return "Utebliven ankomst till anmäld kursplats medför fakturering 50% av kurskostnad.\n" +
            "Kurspris för övriga verkstäder är 3450,- per. dag, Plats för övriga verkstäder medges bara i mån av plats.\n" +
            "Våra konceptanslutna verkstäder har alltid förtur.";
        } else {
            return "Kurspris fra 01.01.2015 er kr. 2500,- pr. dag for kjedemedlemmer, dette inkluderer kursmateriell, enkel servering og kursbevis. Kurs som gjennomføres på kun en kveld eller halv dag faktureres med kr. 1250,-.  Påmelding uten fremmøte vil bli fakturert med 50% av kursprisen.\n" +
                "Kurspris for ikke kjedemedlemmer er kr. 3750,- pr. dag, og disse vil kun få tilgang til kursene dersom det er ledige plasser.";
        }
        
    }

    private void finalizeHistory(ReminderHistory hist) {
        HashMap<String, MailStatus> emailStatus = checkMap(hist.emailStatus);
        hist.emailStatus = emailStatus;
        
        HashMap<String, MailStatus> invoiceEmailStatus = checkMap( hist.invoiceEmailStatus);
        hist.invoiceEmailStatus = invoiceEmailStatus;
        
        HashMap<String, MailStatus> smsStatus = checkMap( hist.smsStatus);
        hist.smsStatus = smsStatus;
    }

    private HashMap<String, MailStatus> checkMap(HashMap<String, MailStatus> mapToCheck) {
        HashMap<String, MailStatus> retStatus = new HashMap();
        for (String userId : mapToCheck.keySet()) {
            MailStatus status = mapToCheck.get(userId);
            MailStatus newStatus = messageStatusFactory.getStatus(status.id, storeId);
            retStatus.put(userId, newStatus);
        }
        
        return retStatus;
    }

    @Override
    public List<StatisticResult> getStatistic(Date from, Date to) throws ErrorException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        
        from = calendar.getTime();
        
        calendar.setTime(to);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        to = calendar.getTime();
        
        List<Entry> allEntries = getAllEntriesForStatistic(from, to);
        List<StatisticResult> results = new ArrayList();
        
        UserManager userManager = getManager(UserManager.class);
        List<Group> groups = userManager.getAllGroups();
        
        for (Group group : groups) {
            StatisticResult result = getStatisticResult(from, to, group, allEntries);    
            results.add(result);
        }
        
        return results;
    }

    private StatisticResult getStatisticResult(Date from, Date to, Group group, List<Entry> allEntries) throws ErrorException {
        StatisticResult result = new StatisticResult();
        result.from = from;
        result.to = to;
        result.group = group;
        result.signedOn = getSignedOnCount(allEntries, group);
        result.waitingList = getWaitingListCount(allEntries, group);
        return result;
    }

    private List<Entry> getAllEntriesForStatistic(Date from, Date to) {
        List<Entry> entries = new ArrayList();
        for (Month month : months.values()) {
            for (Day day : month.days.values()) {
                for (Entry entry : day.entries) {
                    Date date = entry.getDate();
                    
                    if (date.after(from) && date.before(to)) {
                        entries.add(entry);
                    }
                }
            }
        }
        
        return entries;
    }

    private int getSignedOnCount(List<Entry> allEntries, Group group) throws ErrorException {
        int i = 0;
        
        for (Entry entry : allEntries) {
            int count = getCount(entry.attendees, group);
            if (entry.otherDays != null) {
                count = (entry.otherDays.size() + 1) * count;
            }
            i += count;
        }
        
        return i;
    }
    
    private int getCount(List<String> attendees, Group group) throws ErrorException {
        int i = 0;
        
        UserManager manager = getManager(UserManager.class);
        
        for (String userId : attendees) {
            User user = manager.getUserById(userId);
            if (user != null) {
                if (group != null && user.groups != null) {
                    if (user != null && user.groups.contains(group.id)) {
                        i++;
                    }                    
                } else {
                    if (user.groups == null || user.groups.size() == 0) {
                        i++;
                    }
                }
            }
            
        }
        
        return i;
    }

    private int getWaitingListCount(List<Entry> allEntries, Group group) throws ErrorException {
        int i = 0;
        
        for (Entry entry : allEntries) {
            int count = getCount(entry.waitingList, group);
            if (entry.otherDays != null) {
                count = (entry.otherDays.size() + 1) * count;
            }
            i += count;
        }
        
        return i;
    }

    private void hasAccessToUserId(String userId) throws ErrorException {
        UserManager userManager = getManager(UserManager.class);
        List<User> users = userManager.getUsersWithinTheSameCompany();
        for (User iuser : users) {
            if (iuser.id.equals(userId)) {
                return;
            }
        }
        
        if (getSession() != null && getSession().currentUser != null) {
            System.out.println(getSession().currentUser.fullName + " dont have access to user: " + userManager.getUserById(userId).fullName);
        }
        
        throw new ErrorException(26);
    }
}
