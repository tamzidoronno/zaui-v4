<?php
namespace ns_1a2cdcce_6ee0_4852_ab78_ba68a4911873;

class SmsSettings extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SmsSettings";
    }

    public function render() {
        $this->includefile("filter");
        
        if (isset($_SESSION['SmsSettings_from'])) {
            $this->showTable();
        }
        
        if (isset($_SESSION['SmsSettings_number'])) {
            $this->showTableByNumber();
        }
        
    }
    
    private function showTable() {
        $args = array();
        $args[] = $this->convertToJavaDate(strtotime("01.01.2016"));
        $args[] = $this->convertToJavaDate(strtotime("01.01.2018"));
        
        $attributes = $this->getAttributes();
        
        $table = new \GetShopModuleTable($this, 'MessageManager', 'getAllSmsMessages', $args, $attributes);
        $table->render();
    }
    
    private function showTableByNumber() {
        $args = array();
        $args[] = $_SESSION['SmsSettings_prefix'];
        $args[] = $_SESSION['SmsSettings_number'];
        $args[] = $this->convertToJavaDate(strtotime("01.01.1970"));
        $args[] = $this->convertToJavaDate(strtotime("01.01.2218"));
        
        $attributes = $this->getAttributes();
        
        $table = new \GetShopModuleTable($this, 'MessageManager', 'getSmsMessagesSentTo', $args, $attributes);
        $table->render();
    }
    
    public function setFilter() {
        $_SESSION['SmsSettings_from'] = $_POST['data']['from'];
        $_SESSION['SmsSettings_to'] = $_POST['data']['to'];
        unset($_SESSION['SmsSettings_number']);
        unset($_SESSION['SmsSettings_prefix']);
    }
    
    public function searchByNumber() {
        unset($_SESSION['SmsSettings_from']);
        unset($_SESSION['SmsSettings_to']);
        $_SESSION['SmsSettings_number'] = $_POST['data']['number'];
        $_SESSION['SmsSettings_prefix'] = $_POST['data']['prefix'];
    }

    public function formatTo($row) {
        return "+".$row->prefix." ".$row->to;
    }
    
    public function getAttributes() {
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('rowCreatedDate', 'Date', 'rowCreatedDate'),
            array('to', 'To', 'to', 'formatTo'),
            array('message', 'Message', 'message')
        );
        
        return $attributes;
    }

}
?>
 