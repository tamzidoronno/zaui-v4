/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.mekonomen;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class MekonomenEvent {
    public String eventName;
    public List<MekonomenEventParticipant> participants = new ArrayList();
    public String nodeId;
    
}
