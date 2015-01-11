<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Emails
 *
 * @author ktonder
 */

namespace ns_6ad9d19a_ccc7_4549_bec1_b240a95366bc;

class Emails extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("Used for configuring different emails sent from the system");
    }

    public function getName() {
        return $this->__f("Emails");
    }

    public function render() {
        echo "";
    }
    
    public function renderConfig() {
        $this->includefile("emails");
    }
    
    public function getAllAppsThatHaveEmailConfig() {
        $apps = $this->getApi()->getStoreApplicationPool()->getApplications();
        $retApps = array();
        
        foreach ($apps as $app) {
            $instance = $this->getFactory()->getApplicationPool()->createInstace($app);
            if (method_exists($instance, "gsEmailSetup")) {
                $retApps[] = $instance;
            }
        }
        
        return $retApps;
    }
    
    public function saveEmailSettings() {
        $app = $this->getEmailApp($_POST['gsEmailAppId']);
        $app->gsEmailSetup($_POST);
    }
    
    public function getEmailApp($appId) {
        $apps = $this->getAllAppsThatHaveEmailConfig();
        foreach ($apps as $app) {
            if ($app->getApplicationSettings()->id == $appId) {
                return $app;
            }
        }
        
        return null;
    }
}
