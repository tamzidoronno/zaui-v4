<?php
namespace ns_294a8d9e_bd48_44f4_a607_b7d86d2d85fc;

class ProMeisterCandidateSearch extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public $currentlyLoading = "";
    
    public function getDescription() {
        
    }

    public function getName() {
        return "Course";
    }

    
    public function renderUserSettings($user) {
        $names = $this->getApi()->getStoreManager()->getMultiLevelNames();

        foreach($names as $name) {
            $events = $this->getApi()->getEventBookingManager()->getEventsForUser($name, $user->id);
            if($events) {
                $oldEvents = $this->getOldEvents($events);
                $newEvents = $this->getNewEvents($events);
                echo "<div class='GSPromeisterCandidateSearch'>";
                $this->printEventRows($oldEvents, false);
                $this->printEventRows($newEvents, true);
                echo "</div>";
            }
        }
    }
    
    public function render() {
        $this->currentlyLoading = "favor";
        $this->includefile("searchresult");
        
        $this->includefile("searchview");
        
        $this->currentlyLoading = "searchview";
        $this->includefile("searchresult");
    }
    
    public function searchForUsers() {
        $_SESSION['ProMeisterCandidateSearch_searchword'] = $_POST['data']['txt'];
    }

    public function getFavList() {
        $me = $this->getApi()->getUserManager()->getLoggedOnUser();
        $metaData = @$me->metaData->{'promeister_users_fav_list'};
        $list = [];
        
        
        if ($metaData) {
            
            $list = json_decode($metaData, true);
            
        }
        
        if (!$list) {
            $list = [];
        }
        
        return $list;
    }
    
    public function addToFav() {
        $me = $this->getApi()->getUserManager()->getLoggedOnUser();
        $list = $this->getFavList();
        $key = array_search($_POST['data']['userid'], $list);

        if ($key > -1) {
            unset($list[$key]);
        } else {
            $list[] = $_POST['data']['userid'];
        }
        
        $list = json_encode($list);
        
        $this->getApi()->getUserManager()->addMetaData($me->id, "promeister_users_fav_list", $list);
    }
    
    /**
     * 
     * @param \core_usermanager_data_User[] $users
     */
    public function groupByCompany($users) {
        $retValues = [];
        foreach ($users as $user) {
            foreach ($user->company as $company) {
                $retValues[$company][] = $user;
            }
        }
        
        
        if ($this->currentlyLoading == "searchview") {
            $searchValue = isset($_POST['data']['txt']) ? $_POST['data']['txt'] : false;
            echo $searchValue;
            if ($searchValue) {
                $companies = $this->getApi()->getUserManager()->searchForCompanies($searchWord);
                foreach ($companies as $company) {
                    if (!isset($retValues[$company->id])) {
                        $retValues[$company->id] = array();
                    }
                }
            }
        }
        
        return $retValues;
    }
   
    public function getUsers($mode) {
        
        if ($this->currentlyLoading == "favor") {
            $favList = $this->getFavList();
            $retUsers = [];
            
            foreach ($favList as $fav) {
                $user =  $this->getApi()->getUserManager()->getUserById($fav);
                if ($user != null) {
                    $retUsers[] = $user;
                } else {
                    $comusers = $this->getApi()->getUserManager()->getUsersByCompanyId($fav);
                    if (is_array($comusers)) {
                        $retUsers = array_merge($comusers, $retUsers);
                    }
                }
            }
            
            $containsArray = [];
            $retUser2 = [];
            foreach ($retUsers as $retUser) {
                if (!in_array($retUser->id, $containsArray)) {
                    $retUser2[] = $retUser;
                    $containsArray[] = $retUser->id;
                }
            }
            
            return $retUser2;
        }
        
        if ($this->currentlyLoading == "searchview") {
            $searchValue = isset($_SESSION['ProMeisterCandidateSearch_searchword']) ? $_SESSION['ProMeisterCandidateSearch_searchword'] : "";
            if ($searchValue) {
                return $this->getApi()->getUserManager()->findUsers($searchValue);
            }
            
            return [];
        }
    }

    /**
     * 
     * @param \core_eventbooking_Event[] $events
     */
    public function getOldEvents($events) {
        $ret = [];
        
        foreach ($events as $event) {
            if (!$event->isInFuture) {
                $ret[] = $event;
            }
        }
        
        return $ret;
    }

    /**
     * 
     * @param \core_eventbooking_Event[] $events
     */
    public function getNewEvents($events) {
        $ret = [];
        
        foreach ($events as $event) {
            if ($event->isInFuture) {
                $ret[] = $event;
            }
        }
        
        return $ret;
    }

    public function printEventRows($oldEvents, $new) {
        if (count($oldEvents)) {
            if ($new) {
                echo "<div class='event_list_title'><span>".$this->__w("Upcoming events")."</span></div>";
            } else {
                echo "<div class='event_list_title old_events'><span>".$this->__w("Old events")."</span></div>";
            }
            
            $i = 0;
            foreach ($oldEvents as $event) {
                $i++;
                $start = date("d.m.Y H:i", strtotime($event->mainStartDate));
                $end = date("d.m.Y H:i", strtotime($event->mainEndDate));
                $lastInRow = count($oldEvents) == $i ? "lastInRow" : "";
                echo "<div class='event_row $lastInRow'>$start - $end: ".$event->bookingItemType->name."</div>";
            }
        }

    }

    public function toggleVisibility() {
        $key = 'ProMeisterCandidateSearch_visible_'.$_POST['data']['companyId'];
        $_SESSION[$key] = $_POST['data']['visible'];
    }
    
    public function isCompanyVisible($companyId) {
        $key = 'ProMeisterCandidateSearch_visible_'.$companyId;
        return isset($_SESSION[$key]) && $_SESSION[$key] == "true";
    }
    
    public function toggleCompanyOwner() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['data']['userid']);
        $this->getApi()->getUserManager()->forceCompanyOwner($_POST['data']['userid'], !$user->isCompanyOwner);
    }

}
?>