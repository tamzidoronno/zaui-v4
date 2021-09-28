/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class UserCompanyHistory implements Serializable {
    public String id = UUID.randomUUID().toString();
    public Date suspendedDate = new Date();
    public List<String> companyIds = new ArrayList<>();
}
