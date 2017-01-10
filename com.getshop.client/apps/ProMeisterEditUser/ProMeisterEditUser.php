<?php
namespace ns_1859cf75_9323_407d_b0b1_5050aaa89ff8;

class ProMeisterEditUser extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function updateUser() {
        $notMySelf = isset($_SESSION['ProMeisterSpiderDiager_current_user_id_toshow']) && $_SESSION['ProMeisterSpiderDiager_current_user_id_toshow'] != \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        $userId = $notMySelf ? $_SESSION['ProMeisterSpiderDiager_current_user_id_toshow'] : \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        $user->fullName = $_POST['data']['name'];
        $user->emailAddress = $_POST['data']['email'];
        $user->cellPhone = $_POST['data']['phone'];
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function getName() {
        return "ProMeisterEditUser";
    }
    
    public function suspendAccount() {
        $notMySelf = isset($_SESSION['ProMeisterSpiderDiager_current_user_id_toshow']) && $_SESSION['ProMeisterSpiderDiager_current_user_id_toshow'] != \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        $userId = $notMySelf ? $_SESSION['ProMeisterSpiderDiager_current_user_id_toshow'] : \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        $user->suspended = true;
        $this->getApi()->getUserManager()->saveUser($user);     
    }
    
    public function render() {
        if(isset($_POST['event']) && $_POST['event'] == "suspendAccount"){
            echo "<script> 
                    thundashop.common.Alert('test','khjkhkjhjk');
                    thundashop.framework.reprintPage();
                  </script>";
        }
        
        $this->includefile("edituser");
    }
}
?>
