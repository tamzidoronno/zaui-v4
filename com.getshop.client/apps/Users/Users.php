<?php
namespace ns_ba6f5e74_87c7_4825_9606_f2d3c93d292f;

class Users extends \SystemApplication implements \Application {
    private $users;
    /* @var $selectedUser \core_usermanager_data_User */
    private $selectedUser;
    
    public function getDescription() {
        
    }
    
    public function getName() {
        
    }

    public function postProcess() {
    }
    
    public function isProMeisterLoginAppAdded() {
        $appExists = false;
        $singletons = $this->getFactory()->getApplicationPool()->getSingletonInstances();

        foreach ($singletons as $app) {
            if ($app->getConfiguration()->appSettingsId == "2f98236f-b36d-4d5c-93c6-0ad99e5b3dc6") {
                $appExists = true;
            }
        }
        
        return $appExists;
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
    
    
    public function createUser() {
        $email = $_POST['data']['email'];
        $user = new \core_usermanager_data_User();
        $user->emailAddress = $email;
        $user = $this->getApi()->getUserManager()->createUser($user);
        if(isset($user->id)) {
            $_GET['userid'] = $user->id;
        } else {
            return null;
        }
    }
    
    public function preProcess() {
        if (isset($_GET['removegroup'])) {
            $this->removeGroup();
        }
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
    
    public function updatePassword() {
        $newpw = $_POST['data']['new'];
        $newpw2 = $_POST['data']['new_repeat'];
        if($newpw == $newpw2) {
            $account = new \ns_6c245631_effb_4fe2_abf7_f44c57cb6c5b\Account();
            $account->updatePostPassword();
        }
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
    
    public function showGroupStatistic() {
        
        $startDate = date('M d, Y h:m:s A',  strtotime($_POST['data']['startDate']));
        $endDate = date('M d, Y h:m:s A',  strtotime($_POST['data']['endDate']));
        
        $result = $this->getApi()->getCalendarManager()->getStatistic($startDate, $endDate);
        $countSum = 0;
        $waitingListSum = 0;
        
        echo "<table>";
        echo "<th style='width: 100px;'>Group</th><th style='width: 100px;'>Signed on</th><th style='width: 100px;'>Waitinglist</th>";
        foreach ($result as $res) {
            $groupName = $res->group != null ? $res->group->groupName : "Unassigned"; 
            $count = $res->signedOn; 
            $waiting = $res->waitingList; 
            $countSum += $res->signedOn;
            $waitingListSum += $res->waitingList;
            echo "<tr ><td style='border-bottom: dashed 1px #BBB;'>$groupName</td><td style='text-align: center; border-bottom: dashed 1px #BBB;'>$count</td><td style='text-align: center; border-bottom: dashed 1px #BBB;'>$waiting</td></tr>";
        }
        
        echo "<tr><td></td><td style='text-align: center;'>$countSum</td><td style='text-align: center;'>$waitingListSum</td></tr>";
        
        echo "</table>";
    }
    
    public function isPromeister() {
        return $this->isProMeisterLoginAppAdded();
    }
        
    public function saveProMeisterSettings() {
        $settings = $this->getApi()->getUserManager()->getProMeisterScoreType();
        
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userId']);
        $user->proMeisterScoreSettings = new \core_usermanager_data_ProMeisterScoreSettings();
        foreach ($settings->categories as $cat) {
            $user->proMeisterScoreSettings->scores->{$cat} = $_POST['data'][$cat];
        }
        
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function showEditCompany() {
        $this->includefile("editCompany");
    }
    
    public function saveCompanyInformation() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userId']);
        $user->company->name = $_POST['data']['companyNameToSave'];
        $user->company->streetAddress = $_POST['data']['streetaddress'];
        $user->company->postnumber = $_POST['data']['postnumber'];
        $user->company->city = $_POST['data']['city'];
        $user->company->country = $_POST['data']['country'];
        
        $this->getApi()->getUserManager()->saveUser($user);
    }
    
    public function showAdminAddSubUserAccount() {
        $this->includefile("addSubAccountToUserAdmin");
    }
    
    public function createSubAccountAdmin() {
        $this->getApi()->getUserManager()->createSubAccountEditor($_POST["data"]["name"], $_POST["data"]["phone"], $_POST["data"]["leaderId"]);
    }
}
?>