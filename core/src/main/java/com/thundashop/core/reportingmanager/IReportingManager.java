package com.thundashop.core.reportingmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.reportingmanager.data.EventLog;
import com.thundashop.core.reportingmanager.data.LoggedOnUser;
import com.thundashop.core.reportingmanager.data.OrderCreated;
import com.thundashop.core.reportingmanager.data.PageView;
import com.thundashop.core.reportingmanager.data.ProductViewed;
import com.thundashop.core.reportingmanager.data.Report;
import com.thundashop.core.reportingmanager.data.ReportFilter;
import com.thundashop.core.reportingmanager.data.UserConnected;
import java.util.List;

/**
 * When you need to fetch some statistics for your web shop, this is where you can find it,<br>
 */
@GetShopApi
public interface IReportingManager {
    /**
     * Fetch a report for a given time period.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @param type 0, hourly, 1. daily, 2. weekly, 3. monthly
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<Report> getReport(String startDate, String stopDate, int type) throws ErrorException;
    
    /**
     * Fetch the page id for all page 
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<PageView> getPageViews(String startDate, String stopDate) throws ErrorException;
    
    /**
     * Fetch a list of all users trying / logging on at a given time interval.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<LoggedOnUser> getUserLoggedOn(String startDate, String stopDate) throws ErrorException;
    
    /**
     * Fetch all viewed product for a given time period.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<ProductViewed> getProductViewed(String startDate, String stopDate) throws ErrorException;
    
    
    /**
     * List all orders created at a given time period.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<OrderCreated> getOrdersCreated(String startDate, String stopDate) throws ErrorException;
    
    /**
     * Fetch all users which connected at a given time period.
     * These are users who has been logging on to your store.
     * @param startdate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @param filter A report filter if you want to filter out more information, use null to avoid the filter.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<UserConnected> getConnectedUsers(String startdate, String stopDate, ReportFilter filter) throws ErrorException;
    
    /**
     * Fetch all activity data for a given session at a given period in time.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @param searchSessionId The id of the session to fetch data from.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public List<EventLog> getAllEventsFromSession(String startDate, String stopDate, String searchSessionId) throws ErrorException;
    
}
