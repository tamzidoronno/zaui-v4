/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager;

import com.thundashop.core.common.ErrorException;

/**
 *
 * @author ktonder
 */
public interface UserDeletedEventListener {
    public void userDeleted(String userId) throws ErrorException;
}
