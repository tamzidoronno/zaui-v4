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
        $core_eventbooking_ReminderTemplate->subject = $_POST['data']['subject'];
        $this->getApi()->getEventBookingManager()->saveReminderTemplate($this->getBookingEngineName(), $core_eventbooking_ReminderTemplate);
    }
    
    public function sendDiplomas() {
        $reminder = new \core_eventbooking_Reminder();
        $reminder->userIds = json_decode($_POST['data']['userids']);
        $reminder->content = $_POST['data']['content'];
        $reminder->type = "diplom";
        $reminder->subject = $_POST['data']['subject'];
        $reminder->eventId = $this->getEvent()->id;
        
        foreach ($reminder->userIds as $userid) {
            
            $generator = "createEventDiplomas";

            if ($this->getFactory()->getStore()->id == "17f52f76-2775-4165-87b4-279a860ee92c") {
                $generator = "createEventDiplomasNo";
            }
            
            $sessionId = session_id();
            session_write_close();

            $url = "http://$_SERVER[HTTP_HOST]/scripts/promeister/$generator.php?id=$sessionId&eventId=".$this->getEvent()->id."&userId=$userid";
            $base64 = $this->getApi()->getGetShop()->getBase64EncodedPDFWebPage($url);
            
            session_id($sessionId);
            session_start();
            session_id($sessionId);

            $this->getApi()->getEventBookingManager()->sendDiplomas($this->getBookingEngineName(), $reminder, $userid, $base64);
        }
    }
    
    public function sendReminder() {
        $reminder = new \core_eventbooking_Reminder();
        $reminder->userIds = json_decode($_POST['data']['userids']);
        $reminder->content = $_POST['data']['content'];
        $reminder->type = $_POST['data']['type'];
        $reminder->subject = $_POST['data']['subject'];
        $reminder->eventId = $this->getEvent()->id;
        
        $this->getApi()->getEventBookingManager()->sendReminder($this->getBookingEngineName(), $reminder);
    }
    
    public function updateIndicator() {
        $this->includefile("indicator");
    }
    
    public function deleteTemplate() {
        $this->getApi()->getEventBookingManager()->deleteReminderTemplate($this->getBookingEngineName(), $_POST['data']['templateId']);
    }
    
}
?>
