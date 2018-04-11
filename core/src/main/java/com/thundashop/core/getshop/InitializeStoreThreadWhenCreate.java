/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshop;

import com.thundashop.core.common.GetShopThread;
import com.thundashop.core.getshop.data.StartData;
import com.thundashop.core.messagemanager.MailFactory;
import com.thundashop.core.usermanager.data.User;

/**
 *
 * @author ktonder
 */
public class InitializeStoreThreadWhenCreate extends GetShopThread {

    private final MailFactory mailFactory;
    private final StartData startData;
    private final String text;

    public InitializeStoreThreadWhenCreate(String storeId, String multiLevelName, User userToRunIn, MailFactory mailFactory, StartData startData, String text) {
        super(storeId, multiLevelName, userToRunIn);
        this.mailFactory = mailFactory;
        this.startData = startData;
        this.text = text;
    }


    @Override
    public void execute() {
        mailFactory.send(startData.email, startData.email, startData.emailSubject, text);
        mailFactory.send("post@getshop.com", "post@getshop.com", startData.emailSubject, text);

        // Just initialize
    }

    
}
