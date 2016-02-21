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
    }
    
    public function getEvent() {
        if (!isset($_SESSION[$this->getAppInstanceId()."_currentEvent"])) {
            return false;
        }
        
        return $this->getApi()->getEventBookingManager()->getEvent($this->getBookingEngineName(), $_SESSION[$this->getAppInstanceId()."_currentEvent"]);
    }
    
    public function getBookingEngineName() {
        $names = $this->getApi()->getStoreManager()->getMultiLevelNames();
        return $names[0];
    }
}
