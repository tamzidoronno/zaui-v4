/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.app.newsmanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

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
    public String userId = "";
    public String pageId = "";
    public String newsListId = "";
    
    @Transient
    public String usersName = "";
}
