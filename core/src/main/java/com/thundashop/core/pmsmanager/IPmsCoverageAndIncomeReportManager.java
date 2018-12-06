/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.GetShopApi;
import java.util.LinkedList;

/**
 *
 * @author boggi
 */
@GetShopApi
public interface IPmsCoverageAndIncomeReportManager {
    public LinkedList<IncomeReportDayEntry> getStatistics(CoverageAndIncomeReportFilter filter);
}
