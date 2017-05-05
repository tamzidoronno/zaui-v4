<?php
namespace ns_723546d2_c113_4c6f_b7bd_23e7f68620e8;

class EventBookingManuelAddEvent extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventBookingManuelAddEvent";
    }
        
    /**
     * @param \core_usermanager_data_User $user
     */
    public function renderUserSettings($user) {
        $this->includefile("createNewEntry");   
    }
    

    public function render() {
        
    }
    
    public function saveFileUploaded() {
        $content = strstr($_POST['fileBase64'], "base64,");
        $rawContent = $_POST['fileBase64'];
        $contentType = substr($rawContent, 0, strpos($rawContent, ";base64,"));
        if($contentType) {
            $contentType = str_replace("data:", "", $contentType);
            $contentType = trim($contentType);
        }

        $content = str_replace("base64,", "", $content);
        $content = base64_decode($content);
        $imgId = \FileUpload::storeFile($content);

        $file = new \core_usermanager_data_UploadedFiles();
        $file->contentType = $contentType;
        $file->fileId = $imgId;
        $file->fileName = $_POST['fileName'];
                
        $man = $this->getApi()->getEventBookingManager()->getManuallyAddedEventParticipant("booking", $_POST['value2']);
        $man->files[] = $file;
        $this->getApi()->getEventBookingManager()->addManuallyParticipatedEvent("booking", $man);
   }
    
    public function createNewEntry() {
        $data = new \core_eventbooking_ManuallyAddedEventParticipant();
        $data->date = $this->convertToJavaDate(strtotime($_POST['date']));
        $data->name = $_POST['eventname'];
        $data->userId = $_POST['userid'];
        $this->getApi()->getEventBookingManager()->addManuallyParticipatedEvent("booking", $data);
    }
    
    public function delete() {
        $this->getApi()->getEventBookingManager()->deleteManullyParticipatedEvent("booking", $_POST['value2']);
    }
}
?>
