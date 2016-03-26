<?php
namespace ns_294a8d9e_bd48_44f4_a607_b7d86d2d85fc;

class ProMeisterCandidateSearch extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterCandidateSearch";
    }

    public function render() {
        $this->includefile("favor");
        $this->includefile("searchview");
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
            $companyId = $user->companyObject ? $user->companyObject->id : "";
            $retValues[$companyId][] = $user;
        }
        
        return $retValues;
    }

    public function getUsers($mode) {
        
        if ($mode == "fav") {
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
        
        if ($mode == "search") {
            $searchValue = isset($_SESSION['ProMeisterCandidateSearch_searchword']) ? $_SESSION['ProMeisterCandidateSearch_searchword'] : "";
            return $this->getApi()->getUserManager()->findUsers($searchValue);
        }
    }

}
?>