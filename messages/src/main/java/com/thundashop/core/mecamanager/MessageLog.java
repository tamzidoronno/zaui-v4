/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class MessageLog implements Serializable {
   public String message;
   private Date date = new Date();
   public String type = "";
}
