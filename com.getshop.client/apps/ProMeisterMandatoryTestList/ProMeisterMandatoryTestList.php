<?php
namespace ns_009cd387_0ca4_4b05_98b4_d6dae4ad6a4a;

class ProMeisterMandatoryTestList extends \WebshopApplication implements \Application {
    private $currentUser = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterMandatoryTestList";
    }

    public function render() {
        $this->includefile("courses");
    }

    public function getUserId() {
        return isset($_SESSION['ProMeisterSpiderDiager_current_user_id_toshow']) ? $_SESSION['ProMeisterSpiderDiager_current_user_id_toshow'] : \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
    }

    public function renderUserSettings($user) {
        $this->currentUser = $user;
        $this->includefile("usersettings");
    }
    
    public function saveMandatoryCoursesForUser() {
        $bookingItemIds = array();
        
        foreach ($_POST as $key => $value) {
            $id  = "";
            if (strstr($key, "course_")) {
                $tmp = explode("_", $key);
                $id = $tmp[1];
            }
            
            if ($id && $value === "true") {
                $bookingItemIds[] = $id;
            }
        }
        
        $this->getApi()->getEventBookingManager()->setForcedMandatoryAccess("booking", $_POST['userid'], $bookingItemIds);
    }
    
    public function getCurrentUser() {
        return $this->currentUser;
    }
}
?>
