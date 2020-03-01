/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.pos;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.pmsmanager.PmsConference;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class ZReportConferenceSummary {
    public PmsConference pmsConference;
    public List<CartItem> cartItems = new ArrayList();
    public Double total;
}
