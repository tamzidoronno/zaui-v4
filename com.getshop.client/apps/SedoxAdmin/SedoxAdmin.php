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
    
    public function isSettingsView() {
        if (!isset($_SESSION['sedox_admin_subpage'])) {
            return false;
        }
        
        return $_SESSION['sedox_admin_subpage'] == "settings";
    }
    
    public function isInvoiceListView() {
        if (!isset($_SESSION['sedox_admin_subpage'])) {
            return false;
        }
        
        return $_SESSION['sedox_admin_subpage'] == "invoicelist";
    }
}

?>
