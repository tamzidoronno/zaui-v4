<?php
namespace ns_ba6f5e74_87c7_4825_9606_f2d3c93d292f;

class Users extends \ns_27716a58_0749_4601_a1bc_051a43a16d14\GSTableCommon implements \Application {
    private $users;
    /* @var $selectedUser \core_usermanager_data_User */
    private $selectedUser;
    
    public function getDescription() {
        
    }
    
    public function getName() {
        return $this->__f("Users");
    }
    
    public function renderDashBoardWidget() {
        $this->includefile("dashboardwidget");
    }
    
    public function renderConfig() {
        $this->includefile("overview");
    }

    public function postProcess() {
    }
    
    public function removeComment() {
        $commentid = $_POST['data']['commentid'];
        $userid = $_POST['data']['userid'];
        $this->getApi()->getUserManager()->removeComment($userid, $commentid);
    }
    
    public function addCommentOnUser() {
        $comment = $_POST['data']['comment_text'];
        $userid = $_POST['data']['userid'];
        $user = $this->getApi()->getUserManager()->getUserById($userid);
        $commentobject = new \core_usermanager_data_Comment();
        $commentobject->comment = $comment;
        $this->getApi()->getUserManager()->addComment($userid, $commentobject);
    }
    
    public function updateApplicationPermissions() {
        $_GET['userid'] = $_POST['data']['userid'];
        $this->setSelectedUser();
         /* @var $user core_usermanager_data_User */
        $user = $this->selectedUser;
        $user->applicationAccessList = array();
        if(isset($_POST['data']['toSave'])) {
            $user->applicationAccessList = $_POST['data']['toSave'];
        }
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function updatePassword() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['userid']);
        $this->getApi()->getUserManager()->updatePassword($user->id, "", $_POST['password1']);
    }
    
    public function updateAddress() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['userid']);
        if (!$user->address) {
            $user->address = new \core_usermanager_data_Address();
        }
        
        $user->address->address = $_POST['address_street'];
        $user->address->postCode = $_POST['address_postcode'];
        $user->address->city = $_POST['address_city'];
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function updateGeneralInformation() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['userid']);
        $user->emailAddress = $_POST['email'];
        $user->fullName = $_POST['name'];
        $user->cellPhone = $_POST['phone_number'];
        $user->showHiddenFields = $_POST['showHiddenFields'];
        $user->canChangeLayout = $_POST['canChangeLayout'];
        $user->showLoguotCounter = $_POST['showLogoutCounter'];
        $user->prefix = $_POST['prefix'];
        $user->type = 10;
        $user->sessionTimeOut = $_POST['sessionTimeout'];
        
        if ($_POST['iseditor'] == "true") {
            $user->type = 50;
        }
        
        if ($_POST['isadmin'] == "true") {
            $user->type = 100;
        }
        
        $user->discount = $_POST['discount'];
        
        $user = $this->getApi()->getUserManager()->saveUser($user);
        
        \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::refresh();
    }
    
    public function createUser() {
        $user = new \core_usermanager_data_User();
        $user->emailAddress = $_POST['email'];
        $user->fullName = $_POST['name'];
        $user->cellPhone = $_POST['phone_number'];
        $user->password = $_POST['password1'];
        
        if ($_POST['iseditor'] == "true") {
            $user->type = 50;
        }
        
        if ($_POST['isadmin'] == "true") {
            $user->type = 100;
        }
        
        $user = $this->getApi()->getUserManager()->createUser($user);
        $this->getApi()->getUserManager()->updatePassword($user->id, "", $_POST['password1']);
        $_POST['value'] = $user->id;
    }
    
    public function preProcess() {
        if (isset($_GET['removegroup'])) {
            $this->removeGroup();
        }
    }
    
    public function deleteAccount() {
        $this->getApi()->getUserManager()->deleteUser($_POST['userid']);
    }
    
    private function removeGroup() {
        $groupId = $_GET['removegroup'];
        $this->getApi()->getUserManager()->removeGroup($groupId);
    }
    
    public function addGroup() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $user->groups[] = $_POST['data']['groupId'];
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function removeUserFromGroup() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $groups = array();
        foreach($user->groups as $groupId) {
            if ($groupId != $_POST['data']['groupId']) {
                $groups[] = $groupId;
            }
        }
        
        $user->groups = $groups;
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    private function setUsers() {
        $this->users = $this->getApi()->getUserManager()->getAllUsers();
    }
    
    public function deleteUser() {
        $this->getApi()->getUserManager()->deleteUser($_POST['data']['userid']);
        $_GET['userid'] = null;
    }
    
    public function render() {
        if (isset($_POST['data']['userid'])) {
            $_GET['userid'] = $_POST['data']['userid'];
        }
       
        if (isset($_POST['data']['user_id'])) {
            $_GET['userid'] = $_POST['data']['user_id'];
        }
        
        if(isset($_GET['userid'])) {
            $this->setSelectedUser();
            $this->includeFile("EditUserTemplate");
        } elseif ($this->getPage()->id == "users") {
            $this->includeFile("overview");
        } elseif($this->getPage()->id == "users_all_users") {
            $this->setUsers();
            $this->includeFile("users");
        }
    }
    
    public function getAllUsers() {
        return $this->users;
    }
    
    public function registerUser() {
        $account = new \ns_6c245631_effb_4fe2_abf7_f44c57cb6c5b\Account();
        $account->updatePostData();
    }

    /*
     * @return core_usermanager_data_User
     */
    public function getUserData() {
        return $this->selectedUser;
    }

    public function setSelectedUser() {
        if(isset($_GET['userid'])) {
            $this->selectedUser = $this->getApi()->getUserManager()->getUserById($_GET['userid']);
        }
    }
    
    public function sendEmail() {
        $from = $this->getFactory()->getStoreConfiguration()->emailAdress;
        $title = $_POST['data']['title'];
        $content = $_POST['data']['msg'];
        $to = $_POST['data']['email'];
        $this->getApi()->getMessageManager()->sendMail($to, "", $title, $content, "", "");
    }
    
    public function checkEmail() {
        
    }
    
    public function loadData() {
        if (!$this->filteredData)
            $this->filteredData = $this->getApi()->getUserManager()->getAllUsersFiltered($this->createFilter());
    }
    
    public function searchForUser() {
        $this->setSearchUsers($_POST['data']['text']);
        $this->includeFile("searchResult");
    }

    public function setSearchUsers($searchCriteria) {
        $this->users = $this->getApi()->getUserManager()->findUsers($searchCriteria);
    }

    public function createGroup() {
        $title = $_POST['data']['title'];
        $group = new \core_usermanager_data_Group();
        $group->groupName = $title;
        $this->getApi()->getUserManager()->saveGroup($group);
    }
    
    public function uploadImage() {
        $groupId = $_POST['data']['extra']['groupId'];
        $content = $_POST['data']['data'];
        $content = base64_decode(str_replace("data:image/png;base64,", "",$content));
        $imgId = \FileUpload::storeFile($content);
        
        $groups = $this->getApi()->getUserManager()->getAllGroups();
        foreach ($groups as $group) {
            if ($group->id == $groupId) {
                $group->imageId = $imgId;
                $this->getApi()->getUserManager()->saveGroup($group);
            }
        }
    }
    
    public function showCreateNewUser() {
        $_SESSION['navigation_user_app'] = "userlist";
    }

    public function getUserGroups($user) {
        $groups = [];
        
        if ($user->groups) {
            foreach ($user->groups as $groupId) {
                $inGroup = $this->getApi()->getUserManager()->getGroup($groupId);
                if ($inGroup) {
                    $groups[] = $inGroup;
                }
            }
        }
        
        return $groups;
    }

    
    public function gsEmailSetup($model) {
        if (!$model) {
            $this->includefile("emailsettings");
            return;
        } 
        
        $this->setConfigurationSetting("ordersubject", $_POST['ordersubject']);
        $this->setConfigurationSetting("orderemail", $_POST['emailconfig']);
        $this->setConfigurationSetting("shouldSendEmail", $_POST['shouldSendEmail']);
    }

    public function getAppsWithPrintingToUser() {
        $apps = $this->getApi()->getStoreApplicationPool()->getApplications();
        $retApps = [];
        foreach ($apps as $appSetting) {
            $instance = $this->getFactory()->getApplicationPool()->createInstace($appSetting);
            if (method_exists($instance, "renderUserSettings")) {
                $retApps[] = $instance;
            }
        }
        
        return $retApps;
    }

}
?>