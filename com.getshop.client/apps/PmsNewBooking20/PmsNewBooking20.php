<?php
namespace ns_bf644a39_c932_4e3b_a6c7_f6fd16baa34d;

class PmsNewBooking20 extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsNewBooking20";
    }

    public function render() {
        echo "<div style='max-width: 1500px; margin: auto;'>";
        $this->includefile("newbookingstep1");
        $this->includefile("newbookingstep2");
        echo "</div>";
    }
    
    public function changeUser($user) {
        
    }
    
    public function createCompany() {
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $user = $this->getApi()->getUserManager()->createCompany($vat, $name);
        return $user;
    }
    
    public function createNewUser() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $user = $this->getApi()->getUserManager()->createUser($user);
        return $user;
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function saveUser($user) {
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
}
?>
