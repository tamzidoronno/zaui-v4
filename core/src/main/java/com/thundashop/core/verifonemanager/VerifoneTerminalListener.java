/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.verifonemanager;

import com.thundashop.core.common.GetShopThread;
import com.thundashop.core.usermanager.data.User;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import no.point.paypoint.PayPointEvent;
import no.point.paypoint.PayPointListener;

/**
 *
 * @author ktonder
 */
public class VerifoneTerminalListener extends GetShopThread implements PayPointListener, ActionListener {

    private final VerifoneManager verifoneManager;
    private PayPointEvent ppe;
    private ActionEvent e;

    public VerifoneTerminalListener(String storeId, String multiLevelName, User userToRunIn, VerifoneManager verifoneManager) {
        super(storeId, multiLevelName, userToRunIn);
        this.verifoneManager = verifoneManager;
    }
    
    @Override
    public void execute() {
        if (this.ppe != null) {
            verifoneManager.getPayPointEvent(ppe);
        }
        
        if (this.e != null) {
            verifoneManager.actionPerformed(e);
        }
    }

    @Override
    public void getPayPointEvent(PayPointEvent ppe) {
        clear();
        this.ppe = ppe;
        start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        clear();
        this.e = e;
        start();
    }

    private void clear() {
        this.e = null;
        this.ppe = null;
    }
    
}
