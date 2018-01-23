<?php
namespace ns_acb219a1_4a76_4ead_b0dd_6f3ba3776421;

class CrmCustomerView extends \MarketingApplication implements \Application {
    private $user;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "CrmCustomerView";
    }

    public function render() {
        $this->loadData();
        $this->includefile("main");
    }

    public function setUserId($userId) {
        $_SESSION['ns_acb219a1_4a76_4ead_b0dd_6f3ba3776421_userid'] = $userId;
        $this->loadData();
    }

    public function loadData() {
        if ($this->getModalVariable("userid")) {
            $_SESSION['ns_acb219a1_4a76_4ead_b0dd_6f3ba3776421_userid'] = $this->getModalVariable("userid");
        }
        
        if (isset($_SESSION['ns_acb219a1_4a76_4ead_b0dd_6f3ba3776421_userid']) && !$this->user) {
            $this->user = $this->getApi()->getUserManager()->getUserById($_SESSION['ns_acb219a1_4a76_4ead_b0dd_6f3ba3776421_userid']);
        }
    }

    /**
     * @return \core_usermanager_data_User
     */
    function getUser() {
        return $this->user;
    }

      
    public function isTabActive($tabName) {
        if (isset($_SESSION['CrmCustomerView_current_tab'])) {
            return ($tabName == $_SESSION['CrmCustomerView_current_tab']) ? "active" : false;
        }
        
        if ($tabName == "details") {
            return "active";
        }
        
        return false;
    }
    
    
    public function subMenuChanged() {
        $_SESSION['CrmCustomerView_current_tab'] = $_POST['data']['selectedTab'];
    }

    public function loadOrder($orderId) {
        $_SESSION['CrmCustomerView_current_order'] = $orderId;
        $this->setData();
    }
    
    public function updateUser() {
        $this->loadData();
        $user = $this->getUser();
        
        $user->accountingId = $_POST['data']['accountingId'];
        $user->externalAccountingId = $_POST['data']['externalAccountingId'];
        
        $user->fullName = $_POST['data']['fullName'];
        $user->emailAddress = $_POST['data']['emailAddress'];
        $user->prefix = $_POST['data']['prefix'];
        $user->cellPhone = $_POST['data']['cellPhone'];
        $user->emailAddressToInvoice = $_POST['data']['emailAddressToInvoice'];
        
        if (!$user->address) {
            $user->address = new \core_usermanager_data_Address();
        }
        
        $user->address->fullName = $_POST['data']['address_fullname'];
        $user->address->address = $_POST['data']['address_address'];
        $user->address->postCode = $_POST['data']['address_postcode'];
        $user->address->city = $_POST['data']['address_city'];
        
        $user->type = $_POST['data']['type'];
        
        if (isset($_POST['data']['canchangepagelayout'])) {
            $user->canChangeLayout = $_POST['data']['canchangepagelayout'];
        }
        
        $this->getApi()->getUserManager()->saveUser($user);
        
        $this->user = $this->getApi()->getUserManager()->getUserById($user->id);
    }
    
    public function regeneratTotpKey() {
        $this->loadData();
        $user = $this->getUser();
        $this->getApi()->getUserManager()->createGoogleTotpForUser($user->id);
    } 
}
?>
