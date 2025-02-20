<?php
namespace ns_27656859_aeed_41f7_9941_f01d0f860212;

class UserAccountSettings extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }
    
    public function syncuserstoclusters() {
        $this->getApi()->getUserManager()->syncUsersToClusters();
    }
    
    public function getName() {
        return "UserAccountSettings";
    }

    public function render() {
        $this->includefile("existingadminusers");
    }
    
    public function formatcms($user) {
        if(in_array("cms", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    /**
     * @param \core_usermanager_data_User $user
     * @return string
     */
    public function formatpms($user) { 
        $access = (array)$user->pmsPageAccess;
        $size = sizeof($access);
        if(in_array("pms", $user->hasAccessToModules)) {
            if($size > 0) {
                return $this->formatAccessToPages($access);
            }
            return "<i class='fa fa-check'></i>";
        }
        if($size > 0) {
            return $this->formatAccessToPages($access);
        }
    }
    public function formatcomfort($user) {
        if(in_array("comfort", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    
    public function formatsalespoint($user) {
        if(in_array("salespoint", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    public function formatecommerce($user) {
        if(in_array("ecommerce", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    public function formatcrm($user) {
        if(in_array("crm", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    public function formatapac($user) {
        if(in_array("apac", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    public function formatsettings($user) {
        if(in_array("settings", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    
    public function formatexpress() {
        if(in_array("express", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    
    public function formatsrs($user) {
        if(in_array("srs", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    public function formataccount($user) {
        if(in_array("account", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    public function formatpmsconference($user) {
        if(in_array("pmsconference", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    public function formatticket($user) {
        if(in_array("ticket", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    public function formatgetshopnone($user) {
        return "";
    }
    
    public function formatinvoice($user) {
        if(in_array("invoice", $user->hasAccessToModules)) {
            return "<i class='fa fa-check'></i>";
        }
    }
    
    public function formatanalytics() {
        return "";
    }
    
    public function createAdministrator() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $user->password = $_POST['data']['password'];
        $user->emailAddress = $_POST['data']['email'];
        $user->type = 100;
        $this->getApi()->getUserManager()->createUser($user);
    }
    
    public function UserAccountSettings_loadUser() {
        $_SESSION['usersrow_selectedarea'] = "modules";
        $app = new \ns_acb219a1_4a76_4ead_b0dd_6f3ba3776421\CrmCustomerView();
        $app->loadUser($_POST['data']['id']);
        $app->renderApplication(true, $this);
    }

    public function formatAccessToPages($access) {
        $menu = new \ModulePage(null);
        $size = sizeof($access);
        $title = "Has access to $size pages<br>";
        foreach ($menu->getTopMenuPms()->getEntries() as $entry) {
            if(in_array($entry->getPageId(),$access)) {
                $title .= $entry->getName() . "<br>";
            }
        }
        return "<span title='$title'>$size</span>";
    }

}
?>
