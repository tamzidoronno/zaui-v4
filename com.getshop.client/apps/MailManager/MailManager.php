<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of MailManager
 *
 * @author ktonder
 */
namespace ns_eb03c660_186a_11e3_8ffd_0800200c9a66;
class MailManager extends \ApplicationBase implements \Application {
    public function getName() {
        return $this->__f("MailManager");
    }
    
    public function getDescription() {
        return $this->__f("Use this application to setup the emails sent from your webshop, like order confirmation etc.");
    }
    
    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("mailmanagerconfig");
    }
    
    public function showOrderMailSettings() {
        $this->includefile('ordermail');
    }
    
    public function saveOrderMailSubject() {
        $this->setConfigurationSetting("ordermail_subject", $_POST['data']['subject']);
    }
    
    public function saveContent() {
        $this->setConfigurationSetting("ordermail", $_POST['data']['content']);
    }
    
    public function getOrderEmailText() {
        $text = $this->getConfigurationSetting("ordermail");
        
        if ($text == null) {
            $text = "Click to change text";
        }
        
        return $text;
    }
    
    public function getOrderMailSubject() {
        $text = $this->getConfigurationSetting("ordermail_subject");
        
        if ($text == null) {
            $text = "";
        }
        
        return $text;
    }
}

?>
