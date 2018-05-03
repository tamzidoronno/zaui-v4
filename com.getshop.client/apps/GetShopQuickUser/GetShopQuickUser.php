<?php
namespace ns_b5e9370e_121f_414d_bda2_74df44010c3b;

class GetShopQuickUser extends \SystemApplication implements \Application {
    public $user;
    public $extraArgs = array();
    public $printEditDirect = false;
    public $hideWarning = false;
    public $avoidSaving = false;
    
    public function getDescription() {
    }

    public function getName() {
        return "GetShopQuickUser";
    }

    public function render() {
        $this->includefile("front");
        if($this->printEditDirect) {
            echo '<div class="edit_details_directprint">';
            $this->includefile("edituser");
            echo "</div>";
        }
    }
 
    public function searchbrreg() {
        $company = $this->getApi()->getUtilManager()->getCompaniesFromBrReg($_POST['data']['name']);
        echo json_encode($company);
    }
    public function setUser($user) {
        $this->user = $user;
    }

    public function getExtraArgs() {
        if (isset($_POST['data']['gs_extras'])) {
            $toAdd = explode(",", $_POST['data']['gs_extras']);
            
            foreach ($toAdd as $key) {
                if ($key) {
                    $this->extraArgs[$key] = $_POST['data'][$key];
                }
            }
        }
        
        return $this->extraArgs;
    }
    
    function setExtraArgs($extraArgs) {
        $this->extraArgs = $extraArgs;
    }
    
    public function hasAccountingTransfer() {
        return false;
    }
    
    public function saveUser() {
        
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        
        $this->user = $user;
        
        $user->fullName = $_POST['data']['fullName'];
        $user->prefix = $_POST['data']['prefix'];
        $user->emailAddress = $_POST['data']['emailAddress'];
        $user->cellPhone = $_POST['data']['cellPhone'];
        if(!$user->address) {
            $user->address = new \core_usermanager_data_Address();
        }
        $user->address->address = $_POST['data']['address.address'];
        $user->address->postCode = $_POST['data']['address.postCode'];
        $user->address->city = $_POST['data']['address.city'];
        $user->address->countrycode = $_POST['data']['countryCode'];
        $user->birthDay = $_POST['data']['birthDay'];
        $user->relationship = $_POST['data']['relationship'];
        $user->preferredPaymentType = $_POST['data']['preferredpaymenttype'];
        if(!isset($_POST['data']['avoidsaving'])) {
            $this->getApi()->getUserManager()->saveUser($user);
        }
//      
        $instance = $this->getCallBackApp();
        $instance->saveUser($user);
        $this->includefile("edituser");
//        die();
        
    }

    public function searchForUsers() {
        $this->includefile("searchusers");
        die();
    }
    
    public function changeUser() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $instance = $this->getCallBackApp();
        $instance->changeUser($user);
        
        $this->user = $user;
        $this->includefile("edituser");
        die();
    }
    
    public function createCompany() {
        $instance = $this->getCallBackApp();
        $this->user = $instance->createCompany();
        $this->includefile("edituser");
        die();
    }
    
    public function createNewUser() {
        $instance = $this->getCallBackApp();
        $this->user = $instance->createNewUser();
        $this->includefile("edituser");
        die();
    }
}
?>
