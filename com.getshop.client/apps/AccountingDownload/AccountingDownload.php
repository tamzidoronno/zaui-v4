<?php
namespace ns_1674e92d_feb5_4a78_9dba_1e5ba05a6a31;

class AccountingDownload extends \MarketingApplication implements \Application {
    public function getDescription() {}

    public function getName() {
        return "AccountingDownload";
    }

    public function render() {
        $this->includefile("select");
        
        if (isset($_SESSION['AccountingDownload_filesCreated'])) {
            $this->includefile("filescreated");
            return;
        }
        
        if (isset($_SESSION['AccountingDownload_endDate'])) {
            $this->includefile("overview");
            return;
        }
        
        $groups = $this->groupData();
        
        if (!$groups) {
            $this->includefile("askfortransformation");
            return;
        }
        
        foreach ($groups as $type => $group) {
            echo "<h2>$type</h2>";
            $this->printFileTable($group);
        }
    }
    
    public function formatOrderCount($file) {
        return count($file->orders);
    }

    public function formatDownload($row) {
        return '<div class="gs_shop_small_icon downloadFile dontExpand" fileid="'.$row->id.'" ><i  class="fa fa-download dontExpand"></i></div>';
    }
    
    public function GetShopAccountingManager_getOrderFiles() {
        $this->includefile("fileoverview");
        
        $file = $this->getApi()->getGetShopAccountingManager()->getOrderFile($_POST['data']['id']);
        
        $first = true;
        foreach ($file->orders as $orderId) {
            $orderPrinter = new \ns_f22e70e7_7c31_4c83_a8c1_20ae882853a7\OrderSimplePrinter();
            $orderPrinter->setOrderId($orderId);
            $orderPrinter->setCompactView();
            
            if ($first) {
                $orderPrinter->setPrintHeader();
            }
            
            $orderPrinter->renderApplication(true);
            $first = false;
        }
        
    }
    
    public function formatNumber($data, $colVal) {
        return number_format($colVal, 0, ",", ' ');
    }
    
    public function printFileTable($data) {
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('rowCreatedDate', 'Date crated', 'rowCreatedDate'),
            array('startDate', 'From', 'startDate'),
            array('endDate', 'To', 'endDate'),
            array('orders', 'Orders', 'orders', 'formatOrderCount'),
            array('amountEx', 'Total ex taxes', 'amountEx', 'formatNumber'),
            array('amountInc', 'Total inc taxes', 'amountInc', 'formatNumber'),
            array('amountExDebet', 'Total ex debet', 'amountExDebet', 'formatNumber'),
            array('amountIncDebet', 'Total ex debet', 'amountIncDebet', 'formatNumber'),
            array('subtype', 'Type', 'subtype'),
            array(null, 'Download', null, 'formatDownload')
        );
        
        $args = array(null);
        $table = new \GetShopModuleTable($this, 'GetShopAccountingManager', 'getOrderFiles', $args, $attributes);
        $table->setData($data);
        $table->avoidAutoExpanding();
        $table->render();
    }

    public function groupData() {
        $orderFiles = $this->getApi()->getGetShopAccountingManager()->getOrderFiles();
        $retarray = array();
        
        if (!is_array($orderFiles)) {
            return array();
        }
        
        foreach ($orderFiles as $file) {
            if (!isset($retarray[$file->subtype])) {
                $retarray[$file->subtype] = array();
            }
            
            $retarray[$file->subtype][] = $file;
        }
        
        return $retarray;
    }

    public function setEndDate() {
        if (!$_POST['data']['endDate']) {
            $this->clearSession();
            return;
        }
        
        $_SESSION['AccountingDownload_endDate'] = $_POST['data']['endDate'];
        unset($_SESSION['AccountingDownload_filesCreated']);
    }
    
    public function cancelOverview() {
        unset($_SESSION['AccountingDownload_endDate']);
    }
    
    public function deleteFile() {
        $this->getApi()->getGetShopAccountingManager()->deleteFile($_POST['data']['fileId'], $_POST['data']['password']);
    }
    
    public function createFiles() {
        $time = $this->convertToJavaDate(strtotime($_SESSION['AccountingDownload_endDate']));
        $_SESSION['AccountingDownload_filesCreated'] = $this->getApi()->getGetShopAccountingManager()->createNextOrderFile($time);

        if (!$_SESSION['AccountingDownload_filesCreated']) {
            $_SESSION['AccountingDownload_filesCreated'] = "null";
        }
    }
    
    public function clearSession() {
        unset($_SESSION['AccountingDownload_endDate']);
        unset($_SESSION['AccountingDownload_filesCreated']);
    }
    
    public function transferFromOldSystem() {
        $this->getApi()->getAccountingManager()->transferAllToNewSystem();
    }
}
?>

