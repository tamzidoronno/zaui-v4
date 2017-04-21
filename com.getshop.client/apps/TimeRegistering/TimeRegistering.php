<?php
namespace ns_49f47d6c_9663_4063_b239_9ea77ec7ed14;

class TimeRegistering extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "TimeRegistering";
    }

    public function render() {
        $this->includefile("overview");
    }
    
    
    public function downloadRegisteredHours() {
        $userId = $_POST['data']['userid'];
        $start = strtotime($_POST['data']['startdate'] . "00:00");
        $end = strtotime($_POST['data']['enddate'] . "23:59");
        $hours = $this->getApi()->
                getTimeRegisteringManager()->
                getRegisteredHoursForUser($userId, $this->convertToJavaDate($start), $this->convertToJavaDate($end));
        
        $rows = array();
        $header = array();
        $header[] = "Start date";
        $header[] = "End date";
        $header[] = "Hours";
        $header[] = "Minutes";
        $header[] = "Comment";
        $rows[] = $header;
        
        foreach($hours as $hour) {
            $row = array();
            $row[] = date("d.m.Y H:i", strtotime($hour->start));
            $row[] = date("d.m.Y H:i", strtotime($hour->end));
            $row[] = $hour->hours;
            $row[] = $hour->minutes;
            $row[] = $hour->comment;
            $rows[] = $row;
        }
        
        echo json_encode($rows);
    }
}
?>
