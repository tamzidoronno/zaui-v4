/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.app.newsmanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    public String imageText = "";
    public boolean isPublished = false;
    public String userId = "";
    public String pageId = "";
    public String newsListId = "";
    public String pageLayout = "1";
    public String authors = "";
    public String articleName = "";
    public String articleLink = "";
    public String publisher = "";
    public String ISSN = "";
    public String PMID = "";
    public List<String> fileIds = new ArrayList();
    
    @Transient
    public String usersName = "";
}
