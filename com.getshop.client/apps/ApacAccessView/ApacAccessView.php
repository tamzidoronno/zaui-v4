<?php
namespace ns_0cf90108_6e9f_49fd_abfe_7541d1526ba2;

class ApacAccessView extends \MarketingApplication implements \Application {
    
    /**
     * @var \core_getshoplocksystem_AccessGroupUserAccess
     */
    private $user;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ApacAccessView";
    }

    public function render() {
        $this->setData();
        $this->includefile("view");
    }

    public function setUserId($id) {
        $_SESSION['ns_0cf90108_6e9f_49fd_abfe_7541d1526ba2_userid'] = $id;
    }

    public function setData() {
        if ($this->getModalVariable("userid")) {
            $_SESSION['ns_0cf90108_6e9f_49fd_abfe_7541d1526ba2_userid'] = $this->getModalVariable("userid");
        }
        
        $this->user = $this->getApi()->getGetShopLockSystemManager()->getAccess($_SESSION['ns_0cf90108_6e9f_49fd_abfe_7541d1526ba2_userid']);
    }

    /**
     * 
     * @return \core_getshoplocksystem_AccessGroupUserAccess
     */
    function getUser() {
        return $this->user;
    }

    public function sendMessage() {
        $this->setData();
        
        if ($_POST['data']['submit'] == "sms") {
            $this->getApi()->getGetShopLockSystemManager()->sendSmsToCustomer($this->user->id, $_POST['data']['message']);
        } 
        
        if ($_POST['data']['submit'] == "email") {
            $this->getApi()->getGetShopLockSystemManager()->sendEmailToCustomer($this->user->id, $_POST['data']['subject'], $_POST['data']['message']);
        } 
        
        $this->setData();
        $this->render();
    }

    public function getSentMessages() {
        $user = $this->getUser();
        $retArray = array();
        
        foreach ($user->emailMessages as $msgId) {
            $retArray[] = $this->getApi()->getMessageManager()->getMailMessage($msgId);
        }
        
        foreach ($user->smsMessages as $msgId) {
            $retArray[] = $this->getApi()->getMessageManager()->getSmsMessage($msgId);
        }
        
        return $retArray;
    }

    public function removeAccess() {
        $this->setData();
        $this->getApi()->getGetShopLockSystemManager()->removeAccess($this->user->id);
    }
    
    public function saveData() {
        $this->setData();
        $this->user->fullName = $_POST['data']['fullName'];
        $this->user->prefix = $_POST['data']['prefix'];
        $this->user->email = $_POST['data']['email'];
        $this->user->phonenumber = $_POST['data']['phonenumber'];
        $this->getApi()->getGetShopLockSystemManager()->saveUser($this->user);
        $this->setData();
    }
}
?>
