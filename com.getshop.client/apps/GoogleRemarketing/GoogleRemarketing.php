<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_5eda7754_932f_4108_8952_94d66becf3f2;

class GoogleRemarketing extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("Google remarketing is an excelent way reattract your customers. If a customer has checked out your products you can use this to attract them back again.");
    }

    public function getName() {
        return "GoogleMarketing";
    }

    public function render() {
        echo "code snippet";
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }
    
    public function renderBottom() {
        $this->includefile("codesnippet");
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("remarketingcode", $_POST['remarketingcode']);
    }

}