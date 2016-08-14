<?php
namespace ns_1c48b89f_2279_40af_8bc1_470c8360fef8;

class UserMerge extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "UserMerge";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("mergeview");
    }
    
    public function mergeUsers() {
        $this->getApi()->getUserManager()->mergeUsers($_POST["data"]["userIds"], $_POST["data"]["properties"]);
    }
}
?>
