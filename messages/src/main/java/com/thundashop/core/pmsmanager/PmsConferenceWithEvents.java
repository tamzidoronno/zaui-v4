package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

public class PmsConferenceWithEvents extends PmsConference{
    public List<PmsConferenceEvent> events = new ArrayList<>();    

    public PmsConferenceWithEvents(PmsConference conference){
        BeanUtils.copyProperties(this, conference);
    }
}