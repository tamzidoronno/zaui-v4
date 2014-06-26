<?php
namespace ns_6c245631_effb_4fe2_abf7_f44c57cb6c5b;

class Account extends \SystemApplication implements \Application {

    //put your code here
    public function getDescription() {
        
    }

    public function getName() {
        
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function render() {
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();

        if ($user) {
            $this->includeFile("LoggedOnMyAccount");
            return;
        }
        if (isset($_POST['register'])) {
            $this->includeFile("Registration");
        } else {
            $this->includeFile("Logon");
        }
    }
    
    /**
     * 
     * @return \core_appmanager_data_ApplicationSubscription[]
     */
    public function getAllSubscriptions() {
        return $this->getApi()->getAppManager()->getAllApplicationSubscriptions(true);
    }

    public function isLoggedOn() {
        /* @var $event core_usermanager_events_IsLoggedIn */
        return $this->getApi()->getUserManager()->isLoggedIn();
    }

    /**
     * @return core_usermanager_data_User
     */
    public function getUserData() {
        return $this->getApi()->getUserManager()->getLoggedOnUser();
    }

    public function registerUser() {
        $this->updatePostData();
    }

    public function updatePassword() {
        $old = $_POST['data']['old'];
        $new = $_POST['data']['new'];
        $new2 = $_POST['data']['new_repeat'];

        $this->preProcess();
        $user = $this->getUserData();
        $userId = $user->id;

        if ($new == $new2) {
            $this->getApi()->getUserManager()->updatePassword($userId, $old, $new);
        }
    }

    public function getOrders() {
        $order['date'] = date("Y-m-d h:ms", time());
        $order['id'] = "1234ddd" . rand(0, 100000);
        $order['products'] = array();
        $order['products'][1] = "Shaver 123";
        $order['products'][2] = "The ferminator";
        $order['products'][3] = "Thames jameson";

        $orders[] = $order;
        $orders[] = $order;
        $orders[] = $order;
        $orders[] = $order;

        return $orders;
    }

    public function updatePostData() {
        $name = $_POST['data']['name'];
        $email = $_POST['data']['email'];
        $user_id = $_POST['data']['user_id'];
        $city = $_POST['data']['city'];
        $postCode = $_POST['data']['postalCode'];
        $street = $_POST['data']['streetAddress'];
        $userlevel = $_POST['data']['userlevel'];
        if($_POST['data']['expireDate']) {
            $expire = $_POST['data']['expireDate'];
            $expire = date_create_from_format("m/d/Y", $expire);
        }
        $cellPhone = $_POST['data']['cellPhone'];
        $companyName = isseT($_POST['data']['companyName']) ? $_POST['data']['companyName'] : "";
        $birthDay = $_POST['data']['birthDay'];
        
        
        if ($user_id) {
            $user = $this->getApi()->getUserManager()->getUserById($user_id);
        } else {
            $user = $this->getApiObject()->core_usermanager_data_User();
        }
        $user->id = $user_id;
        $user->fullName = $name;
        $user->username = $email;
        $user->emailAddress = $email;
        $user->address = $this->getApiObject()->core_usermanager_data_Address();
        $user->address->city = $city;
        $user->address->address = $street;
        $user->address->postCode = $postCode;
        $user->type = $userlevel;
        $user->cellPhone = $cellPhone;
        $user->companyName = $companyName;
        $user->birthDay = $birthDay;
        if(isset($expire)) {
            $user->expireDate = date_format($expire, "M d, Y h:m:s a");
        }
        if(!$user->expireDate) {
            $user->expireDate = null;
        }
        if ($user_id) {
            $this->getApi()->getUserManager()->saveUser($user);
        } else {
            if(!isset($_POST['data']['password']) || strlen(trim($_POST['data']['password'])) == 0) {
                //Not sure what this should ever happen.
                $user->password = "anew_password322";
            } else {
                $user->password = $_POST['data']['password'];
            }
            
            $this->getApi()->getUserManager()->createUser($user);
        } 
    }

    public function updatePostPassword() {
        $userId = $_POST['data']['user_id'];
        if ($_POST['data']['new'] == $_POST['data']['new_repeat']) {
            $this->getApi()->getUserManager()->updatePassword($userId, "", $_POST['data']['new_repeat']);
        }
    }

}

?>
