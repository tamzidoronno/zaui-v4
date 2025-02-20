<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of EventCommon
 *
 * @author ktonder
 */

namespace ns_d5444395_4535_4854_9dc1_81b769f5a0c3;

class EventCommon extends \MarketingApplication {
    public function preProcess() {
        if (isset($_GET['eventId'])) {
            $_SESSION[$this->getAppInstanceId()."_currentEvent"] = $_GET['eventId'];
        }
        if (isset($_POST['data']['eventid'])) {
            $_SESSION[$this->getAppInstanceId()."_currentEvent"] = $_POST['data']['eventid'];
        }
    }
    
    public function getEvent() {
        $event = $this->getApi()->getEventBookingManager()->getEventByPageId($this->getBookingEngineName(), $this->getPage()->getId());
        
        if ($event)
            return $event;
        
        if (!isset($_SESSION[$this->getAppInstanceId()."_currentEvent"])) {
            
            
            if ($event) {
                return $event;
            }
            
            return false;
        }
        
        return $this->getApi()->getEventBookingManager()->getEvent($this->getBookingEngineName(), $_SESSION[$this->getAppInstanceId()."_currentEvent"]);
    }
    
    public function getBookingEngineName() {
        $names = $this->getApi()->getStoreManager()->getMultiLevelNames();
        
        return "booking";
    }
}
