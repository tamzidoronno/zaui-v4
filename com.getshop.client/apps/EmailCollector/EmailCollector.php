<?php
namespace ns_34981104_5a56_436f_b897_f9d7a9b80427;

class EmailCollector extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }
    
    public function addEmail() {
        $email = $_POST['data']['email'];
        $this->getApi()->getMessageManager()->collectEmail($email);
    }

    public function getName() {
        return "EmailCollector";
    }

    public function downloadList() {
        $excel = array();
        $list = $this->getApi()->getMessageManager()->getCollectedEmails();
        
        $res = array();
        
        foreach($list as $email) {
            if(!stristr($email, "@")) {
                continue;
            }
            $res[$email] = "a";
        }
        
        foreach($res as $r => $t) {
            $emailRow = array();
            $emailRow[] = $r;
            $excel[] = $emailRow;
        }
        
        echo json_encode($excel);
    }
    
    public function render() {
        $this->includefile("collectorform");
        if($this->isEditorMode()) {
            echo "<div class='shop_button' style='width: 138px; line-height: 25px; height: 25px;' gs_downloadexcelreport='downloadList' gs_filename='emails'>Download list</div>";
//            $this->printEmailList();
        }
    }

    public function printEmailList() {
        $list = $this->getApi()->getMessageManager()->getCollectedEmails();
        $res = array();
        foreach($list as $email) {
            if(!stristr($email, "@")) {
                continue;
            }
            $res[$email] = "a";
        }
        
        foreach($res as $r => $t) {
            echo $r . "<br>";
        }
    }

    public function getEmailList() {
        $list = $this->getConfigurationSetting("emaillist");
        if($list) {
            $list = unserialize($list);
        } else {
            $list = array();
        }
        return $list;
    }

}
?>
