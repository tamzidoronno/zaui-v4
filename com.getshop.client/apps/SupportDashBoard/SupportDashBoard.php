<?php
namespace ns_84268253_6c1e_4859_86e3_66c7fb157ea1;

class SupportDashBoard extends \WebshopApplication implements \Application {

    private $currentFeatureList;

    public function getDescription() {
        
    }

    public function isGetShop() {
        $storeId = $this->getFactory()->getStore()->id;
        return $storeId == "13442b34-31e5-424c-bb23-a396b7aeb8ca";
    }
    
    public function saveRequest() {
        $content = $_POST['data']['content'];
        $requesttype = $_POST['data']['type'];
        $requesttitle = $_POST['data']['title'];
        
        $case = new \core_support_SupportCase();
        $case->title = $requesttitle;
        $case->type = $this->convertRequestType($requesttype);
        $case = $this->getApi()->getSupportManager()->createSupportCase($case);
        
        $history = new \core_support_SupportCaseHistory();
        $history->content = $content;
        $this->getApi()->getSupportManager()->addToSupportCase($case->id, $history);
    }
    
    public function getName() {
        return "SupportDashBoard";
    }

    public function addReply() {
        $caseid = $_POST['data']['caseid'];
        $history = new \core_support_SupportCaseHistory();
        $history->content = $_POST['data']['content'];
        $minutes = (int)$_POST['data']['timeused'];
        if($minutes <= 0) { $minutes = 1; }
        $history->minutesUsed = $minutes;
        $rawcontent = trim(strip_tags($history->content));
        if($rawcontent && $rawcontent != "&nbsp;") {
            $this->getApi()->getSupportManager()->addToSupportCase($caseid, $history);
        }
        
        if(isset($_POST['data']['title'])) {
            $this->getApi()->getSupportManager()->changeSupportCaseType($caseid, $_POST['data']['type']);
            $this->getApi()->getSupportManager()->changeStateForCase($caseid, $_POST['data']['state']);
            $this->getApi()->getSupportManager()->changeTitleOnCase($caseid, $_POST['data']['title']);
            if($_POST['data']['assignedto']) {
                $this->getApi()->getSupportManager()->assignCareTakerForCase($caseid, $_POST['data']['assignedto']);
            }
        }
        
    }
    
    public function lazyLoadOverviewData() {
        $res = array();
        
        $stats = $this->getApi()->getSupportManager()->getSupportStatistics();
        switch($_POST['data']['view']) {
            case "Bugs":
                $res['today'] = $stats->bugs;
                $res['tomorrow'] = $stats->bugsTotal;
                break;
            case "Features":
                $res['today'] = $stats->features;
                $res['tomorrow'] = $stats->featuresTotal;
                break;
            case "Time_spent":
                $res['today'] = $stats->timeSpent;
                $res['tomorrow'] = $stats->timeSpentTotal;
                break;
            case "Questions":
                $res['today'] = $stats->questions;
                $res['tomorrow'] = $stats->questionsTotal;
                break;
            default:
                $res['today'] = 0;
                $res['tomorrow'] = 0;
                break;
        }
        
        echo json_encode($res);
    }
    
    public function render() {
       if($this->page->getId() == "getshopdevcenter") {
            $this->includefile("devcenter"); 
       } else if($this->page->getId() ==  "getshopusermanual") {
            $this->includefile("usermanuals"); 
       } else {
            $this->includefile("requestform"); 
            $this->includefile("overview"); 
       }
    }

    public function loadDialog() {
        $this->includefile("suppportcasedialog");
    }
    
    public function convertRequestType($requesttype) {
        $requesttype = strtolower($requesttype);
        switch($requesttype) {
            case "bugs":
                return 1;
            case "questions":
                return 0;
            case "features":
                return 2;
            case "time_spent":
                return 3;
        }
        return 0;
    }

    public function getTypes() {
        $types = array();
        $types[0] = "SUPPORT";
        $types[1] = "BUG";
        $types[2] = "FEATURE";
        $types[3] = "MEETINGREQUEST";
        return $types;
    }
    
    public function dofilter() {
        $_SESSION['supportfiltertype'] = $_POST['data']['type'];
    }
    
    public function getFilter() {
        if(!$this->isGetShop()) {
            return new \core_support_SupportCaseFilter();
        }
        $type = $this->getfilterType();
        $filter = new \core_support_SupportCaseFilter();
        if($type == "assignedtomeneedfollowup") {
            $filter->state = 0;
            $filter->userId = $this->getApi()->getUserManager()->getLoggedOnUser()->id;
        } else if($type == "assignedtome") {
            $filter->state = -1;
            $filter->userId = $this->getApi()->getUserManager()->getLoggedOnUser()->id;
        } else if($type == "unassigned") {
            $filter->state = 0;
        } else if($type == "assigned") {
            $filter->state = 9;
        } else if($type == "all") {
            $filter->state = -1;
        } else {
            $filter->state = -1;
        }
        return $filter;
    }
    
    public function translateTypeToText($type) {
        $types = $this->getTypes();
        return $types[$type];
    }
    
    
    public function getStates() {
        $state = array();
        $state[0] = "SENT";
        $state[1] = "APRROVED";
        $state[2] = "BACKLOGGED";
        $state[3] = "REJECTED";
        $state[4] = "WAITING_RESPONSE";
        $state[5] = "SOLVED";
        $state[6] = "DELEGATED";
        $state[7] = "CREATED";
        $state[8] = "REPLIED";
        $state[9] = "ASSIGNED";
        $state[10] = "INPROGRESS";
        $state[11] = "SOLVED BY DEVELOPER";
        return $state;
    }
    
    public function startDevelopement() {
        $caseid = $_POST['data']['caseid'];
        $this->getApi()->getSupportManager()->changeStateForCase($caseid, 10);
    }
    
    public function solvedCase() {
        $caseid = $_POST['data']['caseid'];
        $this->getApi()->getSupportManager()->changeStateForCase($caseid, 11);
    }

    public function translateStateToText($stateId) {
        $state = $this->getStates();
        return $state[$stateId];
    }

    public function translateModule($moduleId) {
        if(!$moduleId) {
            return "N/A";
        }
        $modules[] = "PMS";
        $modules[0] = "PMS";
        $modules[1] = "SRS";
        $modules[2] = "APAC";
        
        return $modules[$moduleId];
    }
    
    /**
     * 
     * @return \core_usermanager_data_User[]
     */
    public function getAdmins() {
        if(!$this->isGetShop()) {
            return array();
        }
        $users = $this->getApi()->getUserManager()->getAllUsers();
        $admins = array();
        foreach($users as $usr) {
            if($usr->type == 100 && $usr->emailAddress && $usr->emailAddress != "post@getshop.com") {
                $admins[$usr->id] = $usr;
            }
        }
        return $admins;
    }

    public function getfilterType() {
        $type = "assignedtomeneedfollowup";
        if(isset($_SESSION['supportfiltertype'])) {
            $type = $_SESSION['supportfiltertype'];
        }
        return $type;
    }

    public function getBackLog() {
        $filter = new \core_support_SupportCaseFilter();
        $filter->state = 2;
        $cases = (array)$this->getApi()->getSupportManager()->getSupportCases($filter);
        return $cases;
    }

    public function getInProgress() {
        $filter = new \core_support_SupportCaseFilter();
        $filter->state = 10;
        $cases = (array)$this->getApi()->getSupportManager()->getSupportCases($filter);
        return $cases;
    }

    public function getSolvedCases() {
        $filter = new \core_support_SupportCaseFilter();
        $filter->state = 11;
        $cases = (array)$this->getApi()->getSupportManager()->getSupportCases($filter);
        return $cases;
    }

    public function savefeaturelist() {
        foreach($_POST['data']['three'] as $module) {
            $moduleid = $module['id'];
            if(isset($module['children'])) {
                $this->currentFeatureList = $this->getApi()->getSupportManager()->getFeatureThree($moduleid);
                $children = $this->createModuleFeatureList($module['children'], $moduleid);
                $featurelist = new \core_support_FeatureList();
                $featurelist->entries = $children;
                $featurelist->module = $moduleid;
                $featurelist->id = $moduleid;
                $this->getApi()->getSupportManager()->saveFeatureThree($featurelist->id, $featurelist);
            }
        }
        
    }

    public function createModuleFeatureList($children, $parentId) {
        $childs = array();
        foreach($children as $child) {
            $childobject = $this->getExistingChild($child['id']);
            if(!$childobject) {
                $childobject = new \core_support_FeatureListEntry();
            }
            $childobject->text = new \stdClass();
            $childobject->text->{'en'} = $child['text'];
            $childobject->parentId = $parentId;
            if(strlen($child['id']) < 8) {
                $childobject->id = uniqid();
            } else {
                $childobject->id = $child['id'];
            }
            if(isset($child['children'])) {
                $childobject->entries = $this->createModuleFeatureList($child['children'], $childobject->id);
            }
            $childs[] = $childobject;
        }
        return $childs;
    }

    public function createFlatList($children, $parent) {
        $retval = array();
        foreach($children as $child) {
            $res = new \stdClass();
            $res->id = $child->id;
            $res->text = $child->text->{'en'};
            $res->parent = $parent;
            $retval[] = $res;
            if(isset($child->entries) && sizeof($child->entries) > 0) {
                $tmparray = $this->createFlatList($child->entries, $res->id);
                $retval = array_merge($retval, $tmparray);
            }
        }
        return $retval;
    }

    public function getExistingChild($childId) {
        return $this->searchChildren($this->currentFeatureList->entries, $childId);
    }

    public function searchChildren($children, $childId) {
        foreach($children as $child) {
            if($child->id == $childId) {
                return $child;
            }
            if(isset($child->children)) {
                return $this->searchChildren($child->children, $childId);
            }
        }
        return null;
    }

}
?>
