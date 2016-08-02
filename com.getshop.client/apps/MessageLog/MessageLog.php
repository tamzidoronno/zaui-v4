<?php
namespace ns_03e0641c_d42f_496b_9496_365affe7bce9;

class MessageLog extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "MessageLog";
    }

    public function render() {
        $this->includefile("messages");
    }

    public function getMessages() {
        if (!isset($_POST['data']['searchtext']))
            return array();
        
        return $this->getApi()->getMessageManager()->getMailSent(null, null, $_POST['data']['searchtext']);
    }
    
    public function doSearch() {
        
    }
}

?>