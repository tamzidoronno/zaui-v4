<?php
namespace ns_ed0c5af9_32a9_426a_9908_c7ac67e7b2f4;

class UserFileUpload extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function renderUserSettings($user) {
        $this->includefile("userfileupload");   
    }
    
    public function getName() { 
        return "Upload File";
    }

    public function render() {
    }
}
?>
