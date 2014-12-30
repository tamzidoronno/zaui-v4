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
            $userLoggedIn = $this->getApi()->getUserManager()->logOn($_POST['username'], $_POST['password']);
            
            if ($userLoggedIn != null && isset($userLoggedIn)) {
                unset($_SESSION['tempaddress']);
                $_SESSION['loggedin'] = serialize($userLoggedIn);
                // Need to refresh the page in order to make sure that all data is loaded.
                // This login is after data has been initialized to factory.
                header("location: index.php?page=".$this->getFactory()->getStoreConfiguration()->homePage);
            } 
        }
    }
    
    public function render() {
        $this->setUp();
        $this->includefile('login');
    }
    
    public function sendConfirmation() {
        $email = $_POST['data']['email'];
        
        $this->getApi()->getUserManager()->sendResetCode($this->__f("Your reset code"), $this->__f("Reset code is"), $email);
        if(stristr($answer->className, "ResetCodeSent")) {
            $result['error'] = "0";
        }
        echo json_encode($result);
    }
    
    public function sendResetPassword() {
        $email = $_POST['data']['email'];
        $password = $_POST['data']['newPassword'];
        $confirmCode = $_POST['data']['confirmCode'];
        
        $resetCode = $confirmCode;
        $email = $email;
        $newPassword = $password;
        $this->getApi()->getUserManager()->resetPassword($resetCode, $email, $newPassword);
        $result['error'] = 0;
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
