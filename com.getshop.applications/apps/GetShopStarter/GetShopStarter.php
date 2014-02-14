<?php

namespace ns_1d8e7f34_14ee_4018_a3f4_190500dce026;

class GetShopStarter extends \ApplicationBase implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;

    function __construct() {
        
    }

    public function getDescription() {
        return "GetShopStarter";
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return "GetShopStarter";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function getStarted() {
        
    }

    private function startNewSession() {
        @session_id(session_id() . uniqid());
        @session_start();
        $this->getFactory()->updateTransportSession(session_id());
    }

    public function createStore() {
        $id = $this->getApi()->getStoreManager()->generateStoreId();
        $this->startNewSession();
        $name = $_POST['data']['name'];
        $email = $_POST['data']['email'];
        $password = $_POST['data']['password'];
        $hostname = $id.".getshop.com";

        //Creating the store
        $this->getApi()->getStoreManager()->createStore($hostname, $email, $password, true);
        
        //Initing the store.
        $this->getApi()->getStoreManager()->initializeStore($hostname, session_id());
        
        //Creating the user.
        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->emailAddress = $_POST['data']['email'];
        $user->username = $email;
        $user->password = $password;
        $user->fullName = $name;
        $this->getApi()->getUserManager()->createUser($user);
        $this->getApi()->getUserManager()->logOn($user->username, $user->password);
        
        //Adding home menu entry
        $this->getApi()->getPageManager()->getPage("home");
        $applications = $this->getApi()->getPageManager()->getApplications();
        foreach ($applications as $app) {
            if ($app->appName == "TopMenu") {
                $topMenuId = $app->id;
                break;
            }
        }
        $entry = $this->getApiObject()->core_listmanager_data_Entry();
        $entry->name = "Home";
        $entry->pageId = "home";

        $this->getApi()->getListManager()->addEntry($topMenuId, $entry, "home");
        
        //Generating logon link
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        $user->key = rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999);
        $this->getApi()->getUserManager()->saveUser($user);
        
        if(stristr($_SERVER['SERVER_NAME'], ".local.")) {
            $hostname = "http://".$id.".local.getshop.com";
        } else {
            $hostname = "https://".$hostname;
        }
        echo $hostname . "/index.php?logonwithkey=".$user->key."&showdesigns=true&prepopulate=true";
    }

    public function render() {
        $this->includefile("GetShopStarter");
    }

}

?>
