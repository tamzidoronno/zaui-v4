<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_fb19a166_5465_4ae7_9377_779a8edb848e;

class CreateNewInstanceApplication extends \ApplicationBase implements \Application {
    public function getDescription() {
        return "If you add this application to a area for a template page, it will automatically create new instances of the application you select";
    }

    public function getName() {
        return "Create new application instance";
    }

    public function render() {
        $apps = $this->getApi()->getStoreApplicationPool()->getApplications();

        $selectedAppId = $this->getConfigurationSetting("appId");
        
        echo "<select class='selectApplication'>";
        foreach ($apps as $app) {
            $appId = $app->id;
            $selected = $selectedAppId != null && $appId == $selectedAppId ? "selected='selected'" : "";
            echo "<option $selected value='$appId'>$app->appName</option>";
        }
        echo "</select>";
    }
    
    public function saveApplicationSelected() {
        $this->setConfigurationSetting("appId", $_POST['data']['appId']);
    }

}