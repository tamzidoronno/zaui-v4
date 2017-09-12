package com.thundashop.core.excelmanager;

import com.thundashop.core.common.GetShopApi;
import java.util.HashMap;
import java.util.List;

/**
 * Excel management.
 */
@GetShopApi
public interface IExcelManager {
    public String getBase64Excel(List<List<String>> array);
}
