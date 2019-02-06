<?php
namespace ns_2f62f832_5adb_407f_a88e_208248117017;

class CompanyView extends \MarketingApplication implements \Application {
    /**
     * @var \core_usermanager_data_Company
     */
    private $selectedCompany = null;
    
    public $userToEdit = false;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "CompanyView";
    }

    public function render() {
        echo "<div class='leftmenu'>";
            $this->includefile("leftmenu");
        echo "</div>";
        
        echo "<div class='workarea'>";
            $this->includefile("workarea");
        echo "</div>";
    }
    
    public function doSearch() {
        
    }
    
    public function changeMenu() {
        $this->unselectCompany();
        
        $_SESSION['ns_2f62f832_5adb_407f_a88e_208248117017_currenttab'] = $_POST['data']['tab'];
    }
    
    public function getIncludeTemplate() {
        if (isset($_SESSION['ns_2f62f832_5adb_407f_a88e_208248117017_selectedcompany'])) {
            return "companyview";
        }
        
        if (!isset($_SESSION['ns_2f62f832_5adb_407f_a88e_208248117017_currenttab'])) {
            return "search";
        }
        
        return $_SESSION['ns_2f62f832_5adb_407f_a88e_208248117017_currenttab'];
    }
    
    public function changeMenuSub() {
        $_SESSION['ns_2f62f832_5adb_407f_a88e_208248117017_currenttab_sub'] = $_POST['data']['tab'];
    }
    
    public function selectCompany() {
        $_SESSION['ns_2f62f832_5adb_407f_a88e_208248117017_selectedcompany'] = $_POST['data']['companyid'];
    }
    
    public function unselectCompany() {
        $getshopCompanySettings = new \ns_a22fa681_6882_4869_8add_b1cc9c7b661b\GetShopCompanySettings();
        $getshopCompanySettings->clear();
        
        unset($_SESSION['ns_2f62f832_5adb_407f_a88e_208248117017_selectedcompany']);
    }
    
    public function getSelectedCompany() {
        if (!$this->selectedCompany) {
            $this->selectedCompany = $this->getApi()->getUserManager()->getCompany($_SESSION['ns_2f62f832_5adb_407f_a88e_208248117017_selectedcompany']);
        }
        
        return $this->selectedCompany;
    }
    
    public function gsAlsoUpdate() {
        return array('a22fa681-6882-4869-8add-b1cc9c7b661b');
    }
    
    public function getSubWorkArea() {
        if (!isset($_SESSION['ns_2f62f832_5adb_407f_a88e_208248117017_currenttab_sub'])) {
            return "details";
        }
        
        return $_SESSION['ns_2f62f832_5adb_407f_a88e_208248117017_currenttab_sub'];
    }
    
    public function changeUser() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        
        if ($user != null) {
            if (!$user->company) {
                $user->company = array();
            }
            $user->company[] = $this->getSelectedCompany()->id;
            $this->getApi()->getUserManager()->saveUser($user);
            $this->getApi()->getGmailApiManager()->reScanCompanyConnection();
        }
    }
    
    public function createNewUser() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $user->company = array();
        $user->company[] = $this->getSelectedCompany()->id;
        $user = $this->getApi()->getUserManager()->createUser($user);
        return $user;
    }
    
    public function editUser() {
        $this->userToEdit = $_POST['data']['userid'];
    }
    
    public function saveUser($user) {
        $this->getApi()->getGmailApiManager()->reScanCompanyConnection();
    }
    
    public function removeUserFromCompany() {
        $this->getApi()->getUserManager()->removeUserFromCompany($this->getSelectedCompany()->id, $_POST['data']['userid']);
    }
}
?>
