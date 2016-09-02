/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.asanamanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class AsanaTask {
    public long id;
    public String name;
    
    public Date created_at;
    public Date modified_at;
    public String notes;
    public Boolean completed;
    public String assignee_status;
    public Date completed_at;
    public String assignee;
    
    public long projectId;
    
    public List<AsanaCustomField> custom_fields = new ArrayList();
}
