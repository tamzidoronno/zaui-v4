package com.thundashop.core.excelmanager;

import com.thundashop.core.common.GetShopApi;
import java.util.HashMap;
import java.util.List;

/**
 * Excel management.
 */
@GetShopApi
public interface IExcelManager {
    public void startExcelSheet();
    public void prepareExcelSheet(String name, List<List<String>> array);
    public String getPreparedExcelSheet();
    public String getBase64Excel(List<List<String>> array);
}
