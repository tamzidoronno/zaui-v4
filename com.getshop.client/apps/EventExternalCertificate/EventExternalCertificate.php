<?php
namespace ns_2156b189_72f2_4827_81ca_196777193083;

class EventExternalCertificate extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventExternalCertificate";
    }

    public function render() {
        $app = $this->getWrappedApp("994d7fed-d0cf-4a78-a5ff-4aad16b9bcab", "file");
        $this->wrapApp("994d7fed-d0cf-4a78-a5ff-4aad16b9bcab", "file");
    }
    
    public function fileDeleted($fileId) {
        
    }
    
    public function fileUplaoded($fileId) {
        
    }
    
    /**
     * This function is used from the wrapped app
     */
    public function getFileuploadListId() {
        return $this->getModalVariable("userid")."_".$this->getModalVariable("eventid");
    }
}
?>
