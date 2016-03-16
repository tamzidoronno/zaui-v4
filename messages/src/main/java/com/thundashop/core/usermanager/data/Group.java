/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class Group extends DataCommon {
    public String groupName = "";
    public String imageId = "";

    public boolean usersRequireGroupReference = false;
    public String usersRequireGroupReferencePlaceholder = "";
    public int usersRequireGroupReferenceValidationMin = 0;
    public int usersRequireGroupReferenceValidationMax = 0;
    
    public List<Address> extraAddresses = new ArrayList();
    public Address defaultDeliveryAddress = new Address();
    public Address invoiceAddress = new Address();
    
    public boolean isPublic = true;
    
}
