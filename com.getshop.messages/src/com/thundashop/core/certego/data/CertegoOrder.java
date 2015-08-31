/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.certego.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CertegoOrder  implements Comparable<CertegoOrder> {
    public String id = UUID.randomUUID().toString();
    public Date created = new Date();
    public String data = "";

    @Override
    public int compareTo(CertegoOrder o) {
        return created.compareTo(o.created);
    }
}
