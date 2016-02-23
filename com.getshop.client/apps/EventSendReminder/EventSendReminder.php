<?php
namespace ns_f61a60d1_d9e4_491e_a487_5dae01495771;

class EventSendReminder extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventSendReminder";
    }

    public function render() {
        $this->includefile("sendreminder");
    }
    
    public function saveTemplate() {
        $core_eventbooking_ReminderTemplate = new \core_eventbooking_ReminderTemplate();
        
        if ($_POST['data']['id']) {
            $core_eventbooking_ReminderTemplate = $this->getApi()->getEventBookingManager()->getReminderTemplate($this->getBookingEngineName(), $_POST['data']['id']);
        } else {
            $core_eventbooking_ReminderTemplate->name = $_POST['data']['name'];
        }
        
        $core_eventbooking_ReminderTemplate->content = $_POST['data']['content'];
        $this->getApi()->getEventBookingManager()->saveReminderTemplate($this->getBookingEngineName(), $core_eventbooking_ReminderTemplate);
    }
    
    public function sendReminder() {
        $reminder = new \core_eventbooking_Reminder();
        $reminder->userIds = json_decode($_POST['data']['userids']);
        $reminder->content = $_POST['data']['content'];
        $reminder->type = $_POST['data']['type'];
        $reminder->eventId = $this->getEvent()->id;
        
        $this->getApi()->getEventBookingManager()->sendReminder($this->getBookingEngineName(), $reminder);
    }
    
    public function updateIndicator() {
        $this->includefile("indicator");
    }
}
?>
