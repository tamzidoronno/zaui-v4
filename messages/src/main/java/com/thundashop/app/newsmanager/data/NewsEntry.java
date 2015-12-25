/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.app.newsmanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class NewsEntry extends DataCommon {
    public Date date = new Date();
    public String content = "";
    public String subject = "";
    public String image = "";
    public boolean isPublished = false;
}
