<?php
namespace ns_a22fa681_6882_4869_8add_b1cc9c7b661b;

class GetShopCompanySettings extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopCompanySettings";
    }
    
    public function getSelectedCompany() {
        $companyView = new \ns_2f62f832_5adb_407f_a88e_208248117017\CompanyView();
        return $companyView->getSelectedCompany();
    }

    public function render() {
        
        echo "<div class='workarea'>";
            $selectedCompany = $this->getSelectedCompany();
            if (!$selectedCompany) {
                $this->includefile("notselected");
            } else {
                $this->includefile($this->getSelectedTab());
            }
        echo "</div>";
        
        $this->includefile("rightmenu");
    }
    
    public function fetchMessages() {
        $this->getApi()->getGmailApiManager()->fetchAllMessages("post@akershave.no");
    }
    
    public function getSelectedTab() {
        if (!isset($_SESSION['ns_a22fa681_6882_4869_8add_b1cc9c7b661b_tab'])) {
            return "getshopdetails";
        }
        
        return $_SESSION['ns_a22fa681_6882_4869_8add_b1cc9c7b661b_tab'];
    }
    
    public function setTab() {
        $_SESSION['ns_a22fa681_6882_4869_8add_b1cc9c7b661b_tab'] = $_POST['data']['tab'];
    }
}
?>
