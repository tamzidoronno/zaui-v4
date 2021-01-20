/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.ordermanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class ChangedCloseDateLog extends DataCommon {
    public String description;
    public Date newDate;
    public Date oldDate;
}
