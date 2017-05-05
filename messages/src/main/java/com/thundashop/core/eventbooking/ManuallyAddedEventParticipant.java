/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.UploadedFiles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ManuallyAddedEventParticipant extends DataCommon {
    public Date date;
    public String name;
    public List<UploadedFiles> files = new ArrayList();
    public String userId;
}
