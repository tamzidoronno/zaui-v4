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
    
    public function triggerTest() {
        $this->getApi()->getDirectorManager()->createOrder($this->getSelectedCompany()->id, 11, 2018);
    }
    
    public function fetchMessages() {
//        $this->getApi()->getGmailApiManager()->fetchAllMessages("post@akershave.no");
        $this->getApi()->getDirectorManager()->syncFromOld();
    }
    
    public function create() {
        $creator = new \ns_6fc2b3f8_d6fa_4f70_9062_56179a701119\GetShopStoreCreator();
        $data = $creator->create();
        
        $company = $this->getSelectedCompany();
        $system = $this->getApi()->getSystemManager()->createSystem($_POST['data']['systemname'], $company->id);
        $system->remoteStoreId = $data->newStoreId;
        $system->webAddresses = $data->url;
        $system->productId = $_POST['data']['productId'];
        $system->serverVpnIpAddress = "10.0." . $_POST['data']['cluster'] . ".33";
        $this->getApi()->getSystemManager()->saveSystem($system);
        
        $_SESSION['ns_a22fa681_6882_4869_8add_b1cc9c7b661b_systemid'] = $system->id;
        $_SESSION['ns_a22fa681_6882_4869_8add_b1cc9c7b661b_tab'] = "systemview";
    }
    
    public function getSelectedTab() {
        if (!isset($_SESSION['ns_a22fa681_6882_4869_8add_b1cc9c7b661b_tab']) || !$_SESSION['ns_a22fa681_6882_4869_8add_b1cc9c7b661b_tab']) {
            return "getshopdetails";
        }
        
        return $_SESSION['ns_a22fa681_6882_4869_8add_b1cc9c7b661b_tab'];
    }
    
    public function setTab() {
        $_SESSION['ns_a22fa681_6882_4869_8add_b1cc9c7b661b_tab'] = $_POST['data']['tab'];
    }
    
    public function createNewSystem() {
        $company = $this->getSelectedCompany();
        $this->getApi()->getSystemManager()->createSystem($_POST['data']['name'], $company->id);
    }
    
    public function updateSystem() {
        $system = $this->getApi()->getSystemManager()->getSystem($_POST['data']['systemid']);
        $system->systemName = $_POST['data']['systemName'];
        $system->serverVpnIpAddress = $_POST['data']['serverVpnIpAddress'];
        $system->webAddresses = $_POST['data']['webAddresses'];
        $system->remoteStoreId = $_POST['data']['remoteStoreId'];
        $system->monthlyPrice = $_POST['data']['monthlyPrice'];
        $system->activeFrom = $_POST['data']['activeFrom'] ? $this->convertToJavaDate(strtotime($_POST['data']['activeFrom'])) : null;
        $system->activeTo = $_POST['data']['activeTo'] ? $this->convertToJavaDate(strtotime($_POST['data']['activeTo'])) : null;
        $system->numberOfMonthsToInvoice = $_POST['data']['numberOfMonthsToInvoice'];
        $system->productId = $_POST['data']['productid'];
        $system->note = $_POST['data']['note'];
        $this->getApi()->getSystemManager()->saveSystem($system);
    }
    
    public function deleteSystem() {
        $this->getApi()->getSystemManager()->deleteSystem($_POST['data']['systemid']);
    }

    public function clear() {
        unset($_SESSION['ns_a22fa681_6882_4869_8add_b1cc9c7b661b_tab']);
    }

    public function syncSystem() {
        $this->getApi()->getSystemManager()->syncSystem($_POST['data']['systemid']);
    }
    
    public function searchForCustomerToMoveSystemTo() {
        $this->includefile("serachresultmoveto");
    }
    
    public function moveSystemToCustomer() {
        $this->getApi()->getSystemManager()->moveSystemToCustomer($_POST['data']['systemid'], $_POST['data']['userid']);
    }
}
?>