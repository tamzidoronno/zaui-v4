/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.calendarmanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.Comment;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Location extends DataCommon {
    public List<GroupLocationInformation> groupLocationInformation = new ArrayList();
    public List<Comment> comments = new ArrayList();
    public String location = "";
    public String locationExtra = "";
    public String contactPerson = "";
    public String emailAddress = "";
    public String mobile = "";
    public String other = "";
}