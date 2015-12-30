<?php
namespace ns_df435931_9364_4b6a_b4b2_951c90cc0d70;

class Login extends \SystemApplication implements \Application {

    public $errorMessage = "";

    public function getDescription() {
        return $this->__w("Add this to the header or to the body to make a new login form, can be added anywhere on the page.");
    }

    public function getName() {
        return $this->__f("Login");
    }

    public function postProcess() {
        
    }

    public function logout() {
        //Send logout event to backend.
        $this->getApi()->getUserManager()->logout();
        session_destroy();
    }
    
    public function preProcess() {
        if (isset($_POST['loginbutton'])) {
            $loggedIn = Login::getUserObject();
            if ($loggedIn) {
                return;
            }
            
            if ($this->isDoubleAuthRequired() || isset($_GET['doubleauth'])) {
                $username = $_POST['username'];
                $password = $_POST['password'];
                $pincode = $_POST['pincode'];
                $this->doLogin($username, $password, $pincode);
            } else {
                $username = $_POST['username'];
                $password = $_POST['password'];
                $this->doLogin($username, $password);
            }
            
        }
    }
    
    private function isDoubleAuthRequired() {
        $settings = $this->getFactory()->getApplicationPool()->getApplicationSetting("d755efca-9e02-4e88-92c2-37a3413f3f41");
        $settingsInstance = $this->getFactory()->getApplicationPool()->createInstace($settings);
        return $settingsInstance->getConfigurationSetting("doubleauthentication") == "true";
    }
    
    private function doLogin($username, $password, $pincode=false) {
        if ($pincode) {
            $userLoggedIn = $this->getApi()->getUserManager()->loginWithPincode($username, $password, $pincode);
        } else {
            $userLoggedIn = $this->getApi()->getUserManager()->logOn($username, $password);
        }

        
        if ($userLoggedIn != null && isset($userLoggedIn)) {
            unset($_SESSION['tempaddress']);
            $_SESSION['loggedin'] = serialize($userLoggedIn);
            // Need to refresh the page in order to make sure that all data is loaded.
            // This login is after data has been initialized to factory.
            $page = $_SERVER['PHP_SELF'];
            header("Refresh:0");
            exit(0);
        }         
    }
    
    public function render() {
        $this->setUp();
        $this->includefile('login');
    }
    
    public function sendConfirmation() {
        $email = $_POST['data']['email'];
        $result = $this->getApi()->getUserManager()->sendResetCode($this->__f("Your reset code"), $this->__f("Reset code is"), $email);
        
        $retval = array();
        $retval['code'] = $result;
        if($result != 0) {
            $retval['msg'] = $this->__w("Sorry, this user does not exists, are you sure this is the correct username / email?");
        } else {
            $retval['msg'] = $this->__w("An email has been sent with a code to use in step 2.");
        }
        echo json_encode($retval);
    }
    
    public function sendResetPassword() {
        $email = $_POST['data']['email'];
        $password = $_POST['data']['newPassword'];
        $confirmCode = $_POST['data']['confirmCode'];
        
        $resetCode = $confirmCode;
        $email = $email;
        $newPassword = $password;
        $code = $this->getApi()->getUserManager()->resetPassword($resetCode, $email, $newPassword);
        $result['code'] = $code;
        if($code == 1) {
            $result['msg'] = $this->__w("Email is incorrect");
        }
        if($code == 2) {
            $result['msg'] = $this->__w("Invalid reset code.");
        }
        
        if($code == 0) {
            $this->doLogin($email, $password);
        }
        
        echo json_encode($result);
    }
    
    public function recoverPassword() {
        $this->includefile("recoverpassword");
    }
    
    /**
     * @return core_usermanager_data_User
     */
    public static function getUserObject() {
        if (!isset($_SESSION['loggedin'])) {
            return false;
        }
        
        return unserialize($_SESSION['loggedin']);
    }
    
    public static function isEditor() {
        $user = Login::getUserObject();
        return ($user && (int)$user->type > 10);
    }
    
    public static function isAdministrator() {
        $user = Login::getUserObject();
        return ($user && $user->type > 50);
    }

    public static function setLoggedOn($user) {
        $_SESSION['loggedin'] = serialize($user);
    }
    
    public function isLoggedIn() {
        return isset($_SESSION['loggedin']);
    }

    public static function refresh() {
        $factory = \IocContainer::getFactorySingelton();
        $user = $factory->getApi()->getUserManager()->getLoggedOnUser();
        Login::setLoggedOn($user);
    }

    public function setUp() {
    }
}

?>
