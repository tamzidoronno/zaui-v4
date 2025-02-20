/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author boggi
 */
public class PmsConferenceEventEntry extends DataCommon {
    public Date from;
    public Date to;
    public String text;
    public Integer count = null;
    public String extendedText = "";
    public String pmsEventId = "";
    
    @Transient
    public String meetingTitle = "";
    
    @Transient
    public String conferenceItem = "";
    
    @Transient
    public String conferenceId = "";

    boolean inTime(PmsConferenceEventFilter filter) {
        try {
            if(filter.start == null || filter.end == null) {
                return true;
            }
            
            if(from == null) {
                return false;
            }
            if(filter.start.before(from) && filter.end.after(from)) {
                return true;
            }
            return false;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
