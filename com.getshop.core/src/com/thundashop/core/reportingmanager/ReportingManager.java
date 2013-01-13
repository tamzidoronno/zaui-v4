package com.thundashop.core.reportingmanager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.loggermanager.data.LoggerData;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductCriteria;
import com.thundashop.core.reportingmanager.data.EventLog;
import com.thundashop.core.reportingmanager.data.LoggedOnUser;
import com.thundashop.core.reportingmanager.data.OrderCreated;
import com.thundashop.core.reportingmanager.data.PageView;
import com.thundashop.core.reportingmanager.data.ProductViewed;
import com.thundashop.core.reportingmanager.data.Report;
import com.thundashop.core.reportingmanager.data.ReportFilter;
import com.thundashop.core.reportingmanager.data.UserConnected;
import com.thundashop.core.socket.JsonObject2;
import com.thundashop.core.usermanager.UserManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ReportingManager extends ManagerBase implements IReportingManager {

    private static final int PageViews = 1;
    private static final int UsersLoggedOn = 2;
    private static final int ProductViewed = 3;
    private static final int OrdersCreated = 4;
    private static final int UsersCreated = 5;
    HashMap<Long, Report> data;
    LinkedHashMap<String, Integer> popularProducts;
    @Autowired
    Database db;
    private ProductManager prodManager;
    private ListManager listManager;
    private HashMap<String, String> cachedNames;

    @Autowired
    public ReportingManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
        data = new HashMap();
        popularProducts = new LinkedHashMap();
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon obj : data.data) {
            if (obj instanceof Report) {
                addReport((Report) obj);
            }
        }
    }

    public void addReport(Report report) {
        data.put(report.timestamp.getTime(), report);
    }

    public void processApiCall(JsonObject2 data) throws ErrorException {
        Report report = getReportCurrent();

        processPageReport(data, report);
        processProductReport(data, report);
        processUserManagerReport(data, report);
        processOrderManagerReport(data, report);

        if (report.changed) {
            report.changed = false;

            report.storeId = storeId;
            databaseSaver.saveObject(report, credentials);
        }
    }

    private Date convertToDate(String date) throws ErrorException {
        String[] dateArray = date.split("-");
        try {
            int startYear = new Integer(dateArray[0]);
            int startMonth = new Integer(dateArray[1]);
            int startDay = new Integer(dateArray[2]);
            Date start = createDate(startYear, startMonth, startDay);
            return start;
        } catch (Exception e) {
            throw new ErrorException(1000009);
        }
    }

    @Override
    public List<Report> getReport(String startDate, String stopDate, int type) throws ErrorException {
        Date start = convertToDate(startDate);
        Date end = convertToDate(stopDate);
        List<Report> reports = generateReport(start, end, type);
        return reports;
    }

    private Report getReportCurrent() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date d = cal.getTime();

        Report report = data.get(d.getTime());
        if (report == null) {
            report = createNewReport();
        }

        return report;
    }

    private Report createNewReport() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date d = cal.getTime();

        Report report = new Report();
        report.timestamp = d;

        data.put(d.getTime(), report);
        return report;
    }

    private void processProductReport(JsonObject2 data, Report report) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        if (data.interfaceName.equals(ProductManager.class.getName().replace("com.thundashop.", ""))) {
            if (data.method.equals("getProduct")) {
                report.incrementProductAccess();
            }
            if (data.method.equals("getProducts")) {
                ProductCriteria criteria = gson.fromJson(data.args.get("core_productmanager_data_ProductCriteria"), ProductCriteria.class);
                if (criteria.pageIds != null) {
                    for (String key : criteria.pageIds) {
                        report.incrementProductAccess();
                    }
                }
                if (criteria.parentPageIds != null) {
                    for (String key : criteria.parentPageIds) {
                        report.incrementProductAccess();
                    }
                }
            }
        }
    }

    private void processPageReport(JsonObject2 data, Report report) {
        if (data.interfaceName.equals(PageManager.class.getName().replace("com.thundashop.", ""))) {
            if (data.method.equals("getPage")) {
                report.increasePageAccess();
            }
        }
    }

    private void processUserManagerReport(JsonObject2 data, Report report) {
        if (data.interfaceName.equals(UserManager.class.getName().replace("com.thundashop.", ""))) {
            if (data.method.equals("logOn")) {
                report.incrementUserLoggedIn();
            }
            if (data.method.equals("createUser")) {
                report.increaseUserCreated();
            }
        }
    }

    private void processOrderManagerReport(JsonObject2 data, Report report) {
        if (data.interfaceName.equals(OrderManager.class.getName().replace("com.thundashop.", ""))) {
            if (data.method.equals("createOrder")) {
                report.incrementOrderCount();
            }
        }

    }

    private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);

        return cal.getTime();
    }

    private List<Report> generateReport(Date start, Date end, int type) {
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(start);

        //End of the given day.
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(end);
        endTime.add(Calendar.DAY_OF_YEAR, 1);
        end = endTime.getTime();

        if (type == 2) {
            calStart.set(Calendar.DAY_OF_WEEK, 2);
        }
        if (type == 3) {
            calStart.set(Calendar.DAY_OF_MONTH, 1);
        }

        List<Report> retval = new ArrayList();
        while (true) {
            Date startOfPeriode = calStart.getTime();

            if (type == 0) {
                calStart.add(Calendar.HOUR_OF_DAY, 1);
            } else if (type == 1) {
                calStart.add(Calendar.DATE, 1);
            } else if (type == 2) {
                calStart.add(Calendar.DATE, 7);
            } else if (type == 3) {
                calStart.add(Calendar.DATE, calStart.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
            Date endOfPeriode = calStart.getTime();

            if (endOfPeriode.after(end)) {
                endOfPeriode = end;
            }

            Report generatedReport = new Report();
            generatedReport.timestamp = startOfPeriode;
            for (Report report : data.values()) {
                if ((report.timestamp.after(startOfPeriode) && report.timestamp.before(endOfPeriode)) || report.timestamp.equals(startOfPeriode)) {
                    generatedReport.append(report);
                }
            }
            retval.add(generatedReport);

            if (end.equals(endOfPeriode)) {
                break;
            }
        }

        return retval;
    }

    @Override
    public List<PageView> getPageViews(String startDate, String stopDate) throws ErrorException {
        List<DataCommon> result = findFilteredData(startDate, stopDate, "getPage", "core.pagemanager.PageManager");
        return createFilteredResult(result, ReportingManager.PageViews);
    }

    @Override
    public List<LoggedOnUser> getUserLoggedOn(String startDate, String stopDate) throws ErrorException {
        List<DataCommon> result = findFilteredData(startDate, stopDate, "logOn", "core.usermanager.UserManager");
        return createFilteredResult(result, ReportingManager.UsersLoggedOn);
    }

    @Override
    public List<ProductViewed> getProductViewed(String startDate, String stopDate) throws ErrorException {
        List<DataCommon> result = findFilteredData(startDate, stopDate, "getProducts", "core.productmanager.ProductManager");
        return createFilteredResult(result, ReportingManager.ProductViewed);
    }

    @Override
    public List<OrderCreated> getOrdersCreated(String startDate, String stopDate) throws ErrorException {
        List<DataCommon> result = findFilteredData(startDate, stopDate, "createOrder", "core.ordermanager.OrderManager");
        return createFilteredResult(result, ReportingManager.OrdersCreated);
    }

    @Override
    public List<EventLog> getAllEventsFromSession(String startDate, String stopDate, String sessionId) throws ErrorException {
        resetManagersAndCachedData();
        Date start = convertToDate(startDate);
        Date stop = convertToDate(stopDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(stop);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        stop = cal.getTime();

        HashMap<String, String> search = new HashMap();
        search.put("data.sessionId", sessionId);

        List<DataCommon> result = db.find("col_" + storeId, start, stop, "LoggerManager", search);
        List<EventLog> retval = new ArrayList();
        for (DataCommon common : result) {
            if (common instanceof LoggerData) {
                LoggerData loggerData = (LoggerData) common;
                JsonObject2 jsonObject = (JsonObject2) loggerData.data;
                if (makesSenseToAdd(jsonObject)) {
                    EventLog logEntry = new EventLog();
                    logEntry.interfaceName = jsonObject.interfaceName;
                    logEntry.method = jsonObject.method;
                    logEntry.when = common.rowCreatedDate;
                    if(jsonObject.method.endsWith("getPage")) {
                        logEntry.additionalText = translateEntry(jsonObject.args.get("id").replace("\"", ""));
                    }
                    retval.add(logEntry);
                }
            }
        }
        
        return retval;
    }

    @Override
    public List<UserConnected> getConnectedUsers(String startDate, String stopDate, ReportFilter filter) throws ErrorException {
        resetManagersAndCachedData();
        List<DataCommon> result = findFilteredData(startDate, stopDate, "", "");
        
        LinkedHashMap<String, UserConnected> sessionIds = new LinkedHashMap();

        LinkedHashMap<String, ArrayList<String>> filteredUsers = new LinkedHashMap();
        
        for (DataCommon common : result) {
            if (common instanceof LoggerData) {
                LoggerData loggerData = (LoggerData) common;
                if (loggerData.data instanceof JsonObject2) {
                    JsonObject2 jsonObject = (JsonObject2) loggerData.data;
                    UserConnected connected = sessionIds.get(jsonObject.sessionId);
                    if (connected == null) {
                        connected = new UserConnected();
                        connected.sessionId = jsonObject.sessionId;
                        connected.connectedWhen = common.rowCreatedDate;
                        sessionIds.put(jsonObject.sessionId, connected);
                        filteredUsers.put(jsonObject.sessionId, new ArrayList());
                    }

                    if (makesSenseToAdd(jsonObject)) {
                        connected.activity++;
                    }


                    if (jsonObject.method.equals("logOn")) {
                        connected.username = jsonObject.args.get("username").replace("\"", "");
                    }
                    
                    if (jsonObject.method.equals("getPage") && jsonObject.interfaceName.contains("PageManager")) {
                        String translated = translateEntry(jsonObject.args.get("id").replace("\"", ""));
                        if(translated != null) {
                            if(connected.firstPage == null) {
                                connected.firstPage = translated;
                            }
                            if(!filteredUsers.get(connected.sessionId).contains(translated)) {
                                filteredUsers.get(connected.sessionId).add(translated);
                            }
                        }
                    }
                }
            }
        }

        List<UserConnected> retval = new ArrayList();
        if(filter.includeOnlyPages != null && filter.includeOnlyPages.size() > 0) {
            List<String> compared = compareResultWithPages(filter.includeOnlyPages, filteredUsers);
            for(String id : compared) {
                retval.add(sessionIds.get(id));
            }
        } else {
            retval.addAll(sessionIds.values());
        }

        return retval;
    }

    private List createFilteredResult(List<DataCommon> data, int type) throws ErrorException {
        resetManagersAndCachedData();

        Gson gson = new GsonBuilder().serializeNulls().create();

        List<Object> retval = new ArrayList();
        for (DataCommon db : data) {
            if (db instanceof LoggerData) {
                LoggerData collection = (LoggerData) db;
                if (collection.data instanceof JsonObject2) {
                    JsonObject2 obj = (JsonObject2) collection.data;

                    if (type == ReportingManager.ProductViewed) {
                        retval.addAll(generateProductViewLog(obj, collection, gson));
                    } else if (type == ReportingManager.UsersLoggedOn) {
                        retval.add(generateUsersLoggedOnLog(obj, collection, gson));
                    } else if (type == ReportingManager.PageViews) {
                        retval.add(generatePageViewLog(obj, collection, gson));
                    } else if (type == ReportingManager.OrdersCreated) {
                        retval.add(generateOrdersCreatedLog(obj, collection, gson));
                    }
                }
            }
        }

        return retval;
    }

    private List<DataCommon> findFilteredData(String startDate, String stopDate, String method, String interfaceName) throws ErrorException {
        Date start = convertToDate(startDate);
        Date stop = convertToDate(stopDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(stop);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        stop = cal.getTime();

        HashMap<String, String> search = new HashMap();
        if (method.length() > 0) {
            search.put("data.method", method);
            search.put("data.interfaceName", interfaceName);
        }

        List result = db.find("col_" + storeId, start, stop, "LoggerManager", search);
        return result;
    }

    private List<ProductViewed> generateProductViewLog(JsonObject2 jsobj, LoggerData collection, Gson gson) {
        String object = jsobj.args.get("core_productmanager_data_ProductCriteria");
        if (object != null) {
            ProductCriteria criteria = gson.fromJson(object, ProductCriteria.class);
            List<Product> products = prodManager.getProducts(criteria);
            List<ProductViewed> retval = new ArrayList();
            for (Product prod : products) {
                ProductViewed viewed = new ProductViewed();
                viewed.productId = prod.id;
                viewed.productName = prod.name;
                viewed.viewedWhen = collection.rowCreatedDate;
                retval.add(viewed);
            }
            return retval;
        }

        return new ArrayList();
    }

    private LoggedOnUser generateUsersLoggedOnLog(JsonObject2 obj, LoggerData collection, Gson gson) {
        LoggedOnUser lluser = new LoggedOnUser();
        lluser.email = obj.args.get("username").replace("\"", "");
        lluser.loggedOnWhen = collection.rowCreatedDate;

        return lluser;
    }

    private PageView generatePageViewLog(JsonObject2 jsobj, LoggerData collection, Gson gson) throws ErrorException {
        PageView view = new PageView();
        view.pageId = jsobj.args.get("id").replace("\"", "").trim();
        view.viewedWhen = collection.rowCreatedDate;
        view.pageName = translateEntry(view.pageId);

        return view;
    }

    private OrderCreated generateOrdersCreatedLog(JsonObject2 obj, LoggerData collection, Gson gson) {
        String cartJson = obj.args.get("core_cartmanager_data_Cart");
        Cart cart = gson.fromJson(cartJson, Cart.class);

        OrderCreated created = new OrderCreated();
        created.cart = cart;
        created.createdWhen = collection.rowCreatedDate;

        return created;
    }

    private boolean makesSenseToAdd(JsonObject2 jsonObject) {
        if (jsonObject.interfaceName.contains("PageManager") && jsonObject.method.equals("getPage")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("PageManager") && jsonObject.method.equals("createPage")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("PageManager") && jsonObject.method.equals("changePageUserLevel")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("PageManager") && jsonObject.method.equals("changePageLayout")) {
            return true;
        }

        if (jsonObject.interfaceName.contains("UserManager") && jsonObject.method.equals("logOn")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("UserManager") && jsonObject.method.equals("logOut")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("UserManager") && jsonObject.method.equals("resetPassword")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("UserManager") && jsonObject.method.equals("sendResetCode")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("UserManager") && jsonObject.method.equals("createUser")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("UserManager") && jsonObject.method.equals("createUser")) {
            return true;
        }

        if (jsonObject.interfaceName.contains("NewsManager") && jsonObject.method.equals("addNews")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("NewsManager") && jsonObject.method.equals("deleteNews")) {
            return true;
        }

        if (jsonObject.interfaceName.contains("CartManager") && jsonObject.method.equals("addProduct")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("CartManager") && jsonObject.method.equals("removeProduct")) {
            return true;
        }
        if (jsonObject.interfaceName.contains("CartManager") && jsonObject.method.equals("updateProductCount")) {
            return true;
        }

        if (jsonObject.interfaceName.contains("OrderManager")) {
            return true;
        }


        return false;
    }

    private String translateEntry(String pageId) throws ErrorException {
        if (cachedNames.containsKey(pageId)) {
            return cachedNames.get(pageId);
        } else {
            List<String> toTranslate = new ArrayList();
            toTranslate.add(pageId);
            HashMap<String, String> translatedResult = listManager.translateEntries(toTranslate);
            String pageName = translatedResult.get(pageId);
            if (pageName == null) {
                translatedResult = prodManager.translateEntries(toTranslate);
                pageName = translatedResult.get(pageId);
            }
            cachedNames.put(pageId, pageName);
            return pageName;
        }
    }

    private void resetManagersAndCachedData() {
        prodManager = getManager(ProductManager.class);
        listManager = getManager(ListManager.class);
        cachedNames = new HashMap();
    }

    private ArrayList<String> compareResultWithPages(List<String> pages, LinkedHashMap<String, ArrayList<String>> filteredUsers) {
        ArrayList<String> retval = new ArrayList();
        for(String sessionId : filteredUsers.keySet()) {
            ArrayList<String> pagesFound = filteredUsers.get(sessionId);
            boolean isOk = true;
            for(String page : pages) {
                boolean foundPage = false;
                for(String found : pagesFound) {
                    if(found.equals(page)) {
                        foundPage = true;
                        break;
                    }
                }
                if(!foundPage) {
                    isOk = false;
                    break;
                }
            }
            
            if(isOk) {
                retval.add(sessionId);
            }
        }
        
        return retval;
    }
}
