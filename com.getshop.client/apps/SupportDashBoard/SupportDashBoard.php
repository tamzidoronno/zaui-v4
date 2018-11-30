<?php
namespace ns_84268253_6c1e_4859_86e3_66c7fb157ea1;

class SupportDashBoard extends \WebshopApplication implements \Application {
    public function getDescription() {
        
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

    public function lazyLoadOverviewData() {
        $res = array();
        
        switch($_POST['data']['view']) {
            case "Bugs":
                $res['today'] = 0;
                $res['tomorrow'] = 0;
                break;
            default:
                $res['today'] = 0;
                $res['tomorrow'] = 0;
                break;
        }
        
        echo json_encode($res);
    }
    
    public function render() {
       $this->includefile("requestform"); 
       $this->includefile("overview"); 
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

    public function translateTypeToText($type) {
        $types = array();
        $types[0] = "SUPPORT";
        $types[1] = "BUG";
        $types[2] = "FEATURE";
        $types[3] = "MEETINGREQUEST";
        return $types[$type];
    }

    public function translateStateToText($stateId) {
        $state = array();
        $types[0] = "SENT";
        $types[1] = "APRROVED";
        $types[2] = "MOVEDTOBACKLOG";
        $types[3] = "REJECTED";
        $types[4] = "WAITING_RESPONSE";
        $types[5] = "SOLVED";
        $types[6] = "DELEGATED";
        $types[7] = "CREATED";
        
        return $types[$stateId];
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

}
?>
