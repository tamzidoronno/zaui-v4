<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of EngineCommon
 *
 * @author ktonder
 */

namespace ns_83df5ae3_ee55_47cf_b289_f88ca201be6e;

class EngineCommon extends \MarketingApplication {
    public function getBookingEgineName() {
        return $this->getConfigurationSetting("bookingEngineName");
    }
    
    public function setBookingEngineName() {
        $this->setConfigurationSetting("bookingEngineName", $_POST['data']['bookingEngineName']);
    }

    public function printNotConnectedWarning() {
        $this->includefile("notspecifiedenginename", "ns_83df5ae3_ee55_47cf_b289_f88ca201be6e");
    }
    
}
