<?php
namespace ns_9d94feed_f4fd_4e6d_8be8_81aa89f58dcf;

class EventPartitipationData extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "EventPartitipationData";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "EventPartitipationData";
    }

    public function postProcess() {
        
    }

    public function saveData() {
        $eventpart = new \core_calendarmanager_data_EventPartitipated();
        $eventpart->title = $_POST['data']['title'];
        $eventpart->title2 = $_POST['data']['title2'];
        $eventpart->heading = $_POST['data']['heading'];
        $eventpart->body = $_POST['data']['text'];
        $eventpart->pageId = $this->getPage()->id;
        $this->getApi()->getCalendarManager()->setEventPartitipatedData($eventpart);
    }
    
    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        if($this->hasWriteAccess()) {
            $this->includefile("EventPartitipationData");
        }
    }
}
?>
