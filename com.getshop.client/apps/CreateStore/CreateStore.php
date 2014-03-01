<?php

namespace ns_e2554f70_ecdb_47a6_ba37_79497ea65986;

class CreateStore extends \SystemApplication implements \Application {

    public $standAlone = false;

    private $partnerMode = true;
    
    public function isPartnerMode() {
        return \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null;
    }
    
    public function setStandAlone() {
        $this->standAlone = true;
    }

    public function getDescription() {
        return "Internal usage for create webshops";
    }

    public function getName() {
        return "Create Store";
    }

    public function postProcess() {
        
    }

    public function checkExists() {
        $address = $_POST['data']['address'];
        $result = $this->getApi()->getStoreManager()->isAddressTaken($address);
        echo $result;
    }

    public function preProcess() {
        
    }

    public function render() {
        echo '<script type="text/javascript" src="https://www.googleadservices.com/pagead/conversion_async.js"></script>';
        $loggedOn = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        if ($loggedOn) {
            $partner = $this->getUser()->partnerid;
            if (!$partner) {
                echo "<h1>" . $this->__f("Warning. you are not connected as a partner yet, please contact getshop support!") . "</h1>";
                return;
            }
        }

        $this->includefile("createstoreschema");
    }

    public function playMovie() {
        $this->includefile("playmovie");
    }

    public function startCreateStore() {
        $this->startNewSession();

        $email = $_POST['data']['email'];
        $name = $_POST['data']['shopname'];
        $password = $_POST['data']['password'];
        
        $loggedOn = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        $notify = true;
        if($loggedOn) {
            $notify = false;
        }
        
        $store = $this->getApi()->getStoreManager()->createStore($name, $email, $password, $notify);
    }

    public function initStore() {
        $this->startNewSession();
        echo "Initing store";
        $this->getApi()->getStoreManager()->initializeStore($_POST['data']['shopname'], session_id());
        print_r($this->getApi()->transport);
    }

    public function addUser() {
        $loggedOn = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        if ($loggedOn) {
            $partner = $this->getUser()->partnerid;
        }

        $this->startNewSession();
        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->emailAddress = $_POST['data']['email'];
        $user->username = $_POST['data']['email'];
        $user->password = $_POST['data']['password'];
        $user->fullName = $_POST['data']['username'];
        $this->getApi()->getUserManager()->createUser($user);
        $this->getApi()->getUserManager()->logOn($user->username, $user->password);

        if ($loggedOn) {
            $this->getApi()->getStoreManager()->connectStoreToPartner($partner);
        }
    }

    public function addPages() {
        $this->startNewSession();
        $this->getApi()->getPageManager()->getPage("home");
        $applications = $this->getApi()->getPageManager()->getApplications();

        foreach ($applications as $app) {
            if ($app->appName == "TopMenu") {
                $id = $app->id;
            }
        }

        $entry = $this->getApiObject()->core_listmanager_data_Entry();
        $entry->name = "Home";
        $entry->pageId = "home";

        $this->getApi()->getListManager()->addEntry($id, $entry, "home");
    }

    public function sendMail() {
        
    }

    private function startNewSession() {
        $session = preg_replace('/[^A-Za-z0-9\-]/', '', $_POST['data']['name']);
        @session_id(session_id() . $session . "_" . rand(0,100000000));
        @session_start();
        $this->getFactory()->updateTransportSession(session_id());
    }


    public function createLink() {
        $this->startNewSession();
        $user = $this->getApi()->getUserManager()->getLoggedOnUser();
        $user->key = rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999);

        $this->getApi()->getUserManager()->saveUser($user);
        $webAddress = $this->getApi()->getStoreManager()->getMyStore()->webAddress;

        echo "http://" . $webAddress . "/index.php?logonwithkey=" . $user->key . "&showdesigns=true&prepopulate=true";
    }


}

?>