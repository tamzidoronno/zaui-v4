<?php

class UserManager extends TestBase {

    var $hasResetCode = false;
    var $isNotLoggedOn = false;

    public function UserManager($api) {
        $this->api = $api;
    }

    /**
     * Sometimes it is useful to check if the user really is the boss of star trek
     * In honor of Patrick Stewart
     */
    public function test_isCaptain() {
        $userManager = $this->getApi()->getUserManager();

        //First create the user.
        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->fullName = "The best star trek captain";
        $user = $userManager->createUser($user);

        //Now verify the boss!
        $res = $userManager->isCaptain($user->id);
        if ($res) {
            echo "Captain on Deck!";
        }
    }

    /**
     * Need to create a new user?
     */
    public function test_createUser() {
        $userManager = $this->getApi()->getUserManager();
        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->fullName = "Ola Norman";
        $userManager->createUser($user);
    }

    /**
     * Sometimes it is useful to actually delete a user.
     */
    public function test_deleteUser() {
        $userManager = $this->getApi()->getUserManager();

        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->fullName = "Ola Norman";
        $user = $userManager->createUser($user);
        $userManager->deleteUser($user->id);
    }

    /**
     * Search for all users for a given search criteria.
     */
    public function test_findUsers() {
        $userManager = $this->getApi()->getUserManager();

        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->fullName = "Ola Norman";
        $user = $userManager->createUser($user);

        $userManager->deleteUser($user->id);

        //Find the user.
        $allUsers = $userManager->findUsers("Ola");
        foreach ($allUsers as $user) {
            /* @var $user core_usermanager_data_User */
        }
    }

    /**
     * Get all users registered to your webshop.
     */
    public function test_getAllUsers() {
        $userManager = $this->getApi()->getUserManager();
        $allUsers = $userManager->getAllUsers();
        foreach ($allUsers as $user) {
            /* @var $user core_usermanager_data_User */
        }
    }

    /**
     * Get myself.
     */
    public function test_getLoggedOnUser() {
        $userManager = $this->getApi()->getUserManager();
        $thisIsMe = $userManager->getLoggedOnUser();
    }

    /**
     * Fetch myself by userid.
     */
    public function test_getUserById() {
        $userManager = $this->getApi()->getUserManager();
        $thisIsMe = $userManager->getLoggedOnUser();
        $me = $userManager->getUserById($thisIsMe->id);
    }

    /**
     * Fetch a list of given users.
     */
    public function test_getUserList() {
        $userManager = $this->getApi()->getUserManager();

        //First create a user to fetch in the list.
        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->fullName = "Ola Norman";
        $user = $userManager->createUser($user);

        //Fill this list will the id of all users you want to fetch.
        $userIds = array();
        $userIds[] = $user->id;

        $allUsers = $userManager->getUserList($userIds);
        foreach ($allUsers as $user) {
            /* @var $user core_usermanager_data_User */
        }
    }

    /**
     * Check if i am logged on or not.
     */
    public function test_isLoggedIn() {
        $userManager = $this->getApi()->getUserManager();
        $loggedIn = $userManager->isLoggedIn();
        if (!$loggedIn) {
            echo "Sorry mate, you are not logged on!";
        }
    }

    /**
     * Doh, my name is really Homer Simpson!
     */
    public function test_saveUser() {
        $userManager = $this->getApi()->getUserManager();

        $me = $userManager->getLoggedOnUser();
        $me->fullName = "Homer Simpson";
        $userManager->saveUser($me);
    }

    /**
     * I am sick and tired of this, log me out please!
     */
    public function test_logout() {
        $userManager = $this->getApi()->getUserManager();
        $userManager->logout();
    }

    /**
     * Doh, i really need to log on to the system.
     */
    public function test_logOn() {
        $userManager = $this->getApi()->getUserManager();

        $username = "test@getshop.com";
        $password = "getshoptestpassword";
        $userManager->logOn($username, $password);
    }

    /**
     * Send me a rest code to use when resetting.
     */
    public function test_sendResetCode() {
        $userManager = $this->getApi()->getUserManager();

        $title = "Your new reset code";
        $desc = "This is your reset code";
        $email = "test@getshop.com";
        $userManager->sendResetCode($title, $desc, $email);
    }

    /**
     * Activate the reset code.
     */
    public function test_resetPassword() {
        $userManager = $this->getApi()->getUserManager();

        $resetCode = 123322; //The reset code sent by sendResetCode();
        $email = "test@getshop.com";
        $newPassword = "getshoptestpassword";

        //Reset the password if you got the reset code.
        if ($this->hasResetCode) {
            $userManager->resetPassword($resetCode, $email, $newPassword);
        }
    }

    /**
     * Logon using a key.
     */
    public function test_logonUsingKey() {
        $userManager = $this->getApi()->getUserManager();

        //First create a user to fetch in the list.
        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->fullName = "Ola Norman";
        $user->key = "asdfasfasdfasdf";
        $user = $userManager->createUser($user);

        //logon with they key.
        if ($this->isNotLoggedOn) {
            $userManager->logonUsingKey($user->key);
        }
    }

    /**
     * Update your own password for example.
     */
    public function test_updatePassword() {
        $userManager = $this->getApi()->getUserManager();

        $userId = $userManager->getLoggedOnUser()->id;
        $oldPassword = "getshoptestpassword";
        $newPassword = "mynewpassword";
        $userManager->updatePassword($userId, $oldPassword, $newPassword);
    }
    
    /**
     * How many administrators are connected to this webshop?
     */
    public function test_getAdministratorCount() {
        $count = $this->getApi()->getUserManager()->getAdministratorCount();
    }
    
    /**
     * How many customers are connected to this webshop?
     */
    public function test_getCustomersCount() {
        $count = $this->getApi()->getUserManager()->getCustomersCount();
    }
    
    /**
     * How many editors are connected to this webshop?
     */
    public function test_getEditorCount() {
        $count = $this->getApi()->getUserManager()->getEditorCount();
    }

}

?>
