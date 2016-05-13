/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.listmanager;

import com.thundashop.core.listmanager.data.Entry;

/**
 *
 * @author ktonder
 */
public interface ListBadgetAware {
    int getBadges(Entry entry);
}
