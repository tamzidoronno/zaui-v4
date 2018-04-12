<?php
namespace ns_2d26eaea_a46c_49cb_b696_d2f913182cd4;

class GetShopCmsAnalytics extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopCmsAnalytics";
    }

    public function render() {
        $this->includefile("filter");
        $this->includeTable();
    }
    
    public function setDate() {
        $_SESSION['ns_2d26eaea_a46c_49cb_b696_d2f913182cd4_startDate'] = $_POST['data']['from'];
        $_SESSION['ns_2d26eaea_a46c_49cb_b696_d2f913182cd4_endDate'] = $_POST['data']['to'];
    }

    public function includeTable() {
        
        if (isset($_SESSION['ns_2d26eaea_a46c_49cb_b696_d2f913182cd4_startDate']) && $_SESSION['ns_2d26eaea_a46c_49cb_b696_d2f913182cd4_startDate']) {
            $startDate = $this->convertToJavaDate(strtotime($_SESSION['ns_2d26eaea_a46c_49cb_b696_d2f913182cd4_startDate']));
            $endDate = $this->convertToJavaDate(strtotime($_SESSION['ns_2d26eaea_a46c_49cb_b696_d2f913182cd4_endDate']));
            $activites = $this->getApi()->getTrackerManager()->getActivities($startDate, $endDate);
        
            $data = array();
            foreach ($activites as $activity) {
                @$data[$activity->sessionId][] = $activity;
            }
            
            $i = 1;
            foreach ($data as $sessionId => $datas) {
                $this->printGuestActivity($datas, $i);
                $i++;
            }
        }
    }

    public function printGuestActivity($datas, $i) {
        $args = array();
        $attributes = array(
            array('date', 'DATE', 'date', null),
            array('type', 'TYPE', 'type', null),
            array('value', 'VALUE', 'value', null),
            array('description', 'DESCRIPTION', 'textDescription', null),
            array('sessionId', 'sessionId', 'sessionId', null)
        );
        
        echo "<h1> Visitor: ".$i."</h1>";
        $table = new \GetShopModuleTable($this, 'GetShopCmsAnalytics', "guestInfo", $args, $attributes);
        $table->setData($datas);
        $table->render();
    }

}
?>
