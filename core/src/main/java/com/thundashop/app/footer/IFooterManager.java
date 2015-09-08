package com.thundashop.app.footer;

import com.thundashop.app.footermanager.data.Configuration;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;

/**
 * This manager helps you sort out your footer. By changing its column number,<br>
 * remember what type each column has and giving each column an unique id which can be used for other purposes.
 */
@GetShopApi
public interface IFooterManager {
    /**
     * Change the layout for the columns.<br>
     * Defaults to 1 if nothing else is set.<br>
     * @param numberOfColumns The number of columns you want to display.
     * @throws ErrorException 
     */
    @Administrator
    public Configuration setLayout(Integer numberOfColumns) throws ErrorException;
    
    /**
     * Get the current configuration.
     * @return
     * @throws ErrorException 
     */
    public Configuration getConfiguration() throws ErrorException;
    
    /**
     * Change the type of a given column.
     * @param column The column it regards
     * @param type The type,0 for text, 1 for list.
     * @return
     * @throws ErrorException 
     */
     @Administrator
     public Configuration setType(Integer column, Integer type) throws ErrorException;
}
