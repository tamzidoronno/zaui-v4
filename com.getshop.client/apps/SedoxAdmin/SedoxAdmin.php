<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_e22e25dd_8000_471c_89a3_6927d932165e;

class SedoxAdmin extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return "Sedox Admin";
    }

    public function getName() {
        return "Sedox Admin";
    }

    public function render() {
        $this->includefile("topmenu");        
        echo "<div class='sedox_admin_subarea'>";
        if ($this->isDailyView())
            $this->includefile("daily");
        
        if ($this->isInvoiceListView())
            $this->includefile("invoicelist");
        
        if ($this->isSettingsView())
            $this->includefile("settings");
        
        if ($this->isUserSettings())
            $this->includefile("usersettings");
        
        echo "</div>";
    }

    public function getAccountsWithNegativeCreditValue() {
        return $this->getApi()->getSedoxProductManager()->getAllUsersWithNegativeCreditLimit();
    }
    
    public function changeToSubMenu() {
        if ($_POST['data']['menu'] == "main") {
            $_SESSION['sedox_admin_subpage'] = $_POST['data']['changeTo'];
        }
        
        if ($_POST['data']['menu'] == "daylist") {
            $_SESSION['sedox_days_back'] = $_POST['data']['changeTo'];
        }
        
        if ($_POST['data']['menu'] == "usersettings") {
            $_SESSION['sedox_admin_subpage'] = $_POST['data']['changeTo'];
        }
    }
    
    public function getDayList() {
        $daysBack = isset($_SESSION['sedox_days_back']) ? $_SESSION['sedox_days_back'] : 0;
        $sedoxProducts = $this->getApi()->getSedoxProductManager()->getProductsByDaysBack($daysBack);
        return $sedoxProducts;
    }
    
    public function isDailyView() {
        if (!isset($_SESSION['sedox_admin_subpage'])) {
            return true;
        }
        
        return $_SESSION['sedox_admin_subpage'] == "daily";
    }
    
    public function isUserSettings() {
        return isset($_SESSION['sedox_admin_subpage']) && $_SESSION['sedox_admin_subpage'] == "usersettings";
    }
    
    public function isSettingsView() {
        if (!isset($_SESSION['sedox_admin_subpage'])) {
            return false;
        }
        
        return $_SESSION['sedox_admin_subpage'] == "settings";
    }
    
    public function searchForUsers() {
        $_SESSION['sedox_admin_searchString'] = $_POST['data']['searchString'];
    }
    
    public function getSearchUsers() {
        if (!isset($_SESSION['sedox_admin_searchString'])) {
            return array();
        }
        
        return $this->getApi()->getSedoxProductManager()->searchForUsers($_SESSION['sedox_admin_searchString']);
    }
    
    public function isInvoiceListView() {
        if (!isset($_SESSION['sedox_admin_subpage'])) {
            return false;
        }
        
        return $_SESSION['sedox_admin_subpage'] == "invoicelist";
    }
    
    public function showUserInformation() {
        $this->includefile("userinfo");
    }
    
    public function saveUserInfo() {
        $this->getApi()->getSedoxProductManager()->toggleAllowNegativeCredit($_POST['data']['userId'], $_POST['data']['allowNegativeCredit']);
    }
    
    public function updateCredit() {
        $id = $_POST['data']['userId'];
        $desc = $_POST['data']['desc'];
        $amount = $_POST['data']['amount'];
        $this->getApi()->getSedoxProductManager()->addUserCredit($id, $desc, $amount);
    }
    
    public function toggleDevelopers() {
        $sedoxProductManager = $this->getApi()->getSedoxProductManager();
        
        if (isset($_POST['data']['activeDevelopers'])) {
            $activeDevelopers = $_POST['data']['activeDevelopers'];
        } else {
            $activeDevelopers = array();
        }
        
        $developers = $sedoxProductManager->getDevelopers();
        foreach ($developers as $developer) {
            $devId = $developer->id;
            $active = in_array($devId, $activeDevelopers);
            $sedoxProductManager->changeDeveloperStatus($devId, $active);
        }
    }
}

?>
