/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author boggi
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IPmsCoverageAndIncomeReportManager {
    @Administrator
    public IncomeReportResultData getStatistics(CoverageAndIncomeReportFilter filter);

    @Administrator
    public List<PmsSegment> getSegments();

    @Administrator
    public void forceUpdateSegmentsOnBooking(String bookingId, String segmentId);
    
    @Administrator
    public void saveSegments(PmsSegment segment);

    @Administrator
    public PmsSegment getSegment(String segmentId);
    
    @Administrator
    public void deleteSegment(String segmentId);
    
    @Administrator
    public PmsSegment getSegmentForBooking(String bookingId);
    
    @Administrator
    public void recalculateSegments(String segmentId);
    
    @Administrator
    public PmsSegment getSegmentForRoom(String roomId);
}
