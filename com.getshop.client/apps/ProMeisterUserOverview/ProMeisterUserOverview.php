<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_18013065_9122_4181_8ba7_8be3e0b5b445;

class ProMeisterUserOverview extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return "ProMeisterUserOverview";
    }

    public function getName() {
        return "ProMeisterUserOverview";
    }

    public function render() {
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() == null) {
            return;
        }
        
        $this->includefile("useroverview");
    }
}