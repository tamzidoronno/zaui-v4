/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.productmanager.data;

import java.util.HashMap;

/**
 *
 * @author boggi
 */
public class AttributeSummaryEntry {
    public AttributeValue value;
    public int totalCount = 0;

    void increaseCount() {
        totalCount++;
    }
}
