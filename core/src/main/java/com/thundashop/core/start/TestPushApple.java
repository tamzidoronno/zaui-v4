/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.start;

import javapns.Push;
import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;

/**
 *
 * @author ktonder
 */
public class TestPushApple {
    public static void main(String[] args) throws CommunicationException, KeystoreException {
        Push.alert("Pizzaen er snart ferdig!", "GetShop.p12", "auto1000", false, "6bd58bd10e05557c396686b6bbf270ec2e69829c71e837589eb31e6aac5dbc06");
    }
}
