<?php

namespace ns_37d409be_1207_45e8_bf3b_6465442b58d9;

class WilhelmsenHouseVisma extends \ApplicationBase implements \Application {

    public function getDescription() {
        return "Visma integration for WH";
    }

    public function getName() {
        return "WilhelmsenHouseVisma";
    }

    public function render() {
    }
    
    public function renderConfig() {
        $this->includefile("vismaconfig");
    }

    public function saveSettings() {
        $this->setConfigurationSetting("vismaaddress", $_POST['vismaaddress']);
        $this->setConfigurationSetting("vismausername", $_POST['vismausername']);
        $this->setConfigurationSetting("ordertype", $_POST['ordertype']);
        $this->setConfigurationSetting("paymentterm", $_POST['paymentterm']);
        $this->setConfigurationSetting("paymenttype", $_POST['paymenttype']);
        $this->setConfigurationSetting("vismadb", $_POST['vismadb']);
        $this->setConfigurationSetting("vismafilelocation", $_POST['vismafilelocation']);
        $this->setConfigurationSetting("vismadebitaccount", $_POST['vismadebitaccount']);
    }
}

?>