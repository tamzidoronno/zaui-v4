package com.thundashop.core.external;


import com.thundashop.core.common.DataCommon;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ktonder
 */
public class ExternalPosAccess extends DataCommon {
    public String accessToken;
    public boolean confirmed = false;
    public String confirmedBy;
    public Date confirmedDate;
}
