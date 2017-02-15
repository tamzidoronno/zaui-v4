/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class GroupedConferenceData implements Serializable {
    public Date dateToDisplay = null;
    public List<ConferenceData> conferences = new ArrayList();
}
