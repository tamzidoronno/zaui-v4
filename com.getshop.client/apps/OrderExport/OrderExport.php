<?php
namespace ns_13270e94_258d_408d_b9a1_0ed3bbb1f6c9;

class OrderExport extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "Replaces the old accountingtransfer application. This is more flexible";
    }

    public function getTransferTypes() {
        $transfertypes = array();
        $transfertypes['creditor'] = "Creditor";
        $transfertypes['bookingcomratemanager'] = "Booking.com ratemanager";
        return $transfertypes;
    }

    public function getName() {
        return "OrderExport";
    }

    public function renderConfig() {
        $this->includefile("orderexportconfig");
    }
    public function removeTransferConfig() {
        $this->getApi()->getAccountingManager()->removeTransferConfig($_POST['data']['configid']);
    }
    
    public function printStatistics() {
        $configId = "";
        if(isset($_POST['data']['configId'])) {
            $configId = $_POST['data']['configId'];
        }
        $stats = $this->getApi()->getAccountingManager()->getStats($configId); 
        $resEx = array();
        $resInc = array();
        $totalEx = 0;
        $totalInc = 0;
        $totalExProducts = array();
        $totalIncProducts = array();
        $products = $this->getApi()->getProductManager()->getAllProducts();
        foreach($stats->entries as $stat) {
            $time = date("y-M", strtotime($stat->day));

            $mnd =  array();
            $ex = 0.0;
            $inc = 0.0;
            
            foreach($stat->priceEx as $prodId => $tmpEx) { @$totalExProducts[$prodId][$time] += $tmpEx; }
            foreach($stat->priceInc as $prodId => $tmpInc) { @$totalIncProducts[$prodId][$time] += $tmpInc;  }
            
            foreach($stat->priceEx as $prodId => $tmpEx) { $ex += $tmpEx; }
            foreach($stat->priceInc as $prodId => $tmpInc) { $inc += $tmpInc; }
            
            if(!isset($resEx[$time])) { $resEx[$time] = 0; }
            if(!isset($resInc[$time])) { $resInc[$time] = 0; }
            $resEx[$time] += $ex;
            $resInc[$time] += $inc;

            $totalEx += $ex;
            $totalInc += $inc;
        }
        echo "<table border='1' cellspacing='0' cellpadding='5'>";
        echo "<tr>";
        echo "<th>Month</th>";
        echo "<th>Total</th>";
        foreach($products as $product) {
            echo "<th>" . $product->name . "</th>";
        }
        echo "</tr>";
        foreach($resEx as $mnd => $val) {
            if(!$val && !$resInc[$mnd]) {
                continue;
            }
            echo "<tr>";
            echo "<td>" . $mnd . "</td><td>" . round($val) . "<br><span style='color:#aaa;'>" . round($resInc[$mnd]) . "</span></td>";
            foreach($products as $product) {
                $ex = @$totalExProducts[$product->id][$mnd] ? $totalExProducts[$product->id][$mnd] : "0" ;
                $inc = @$totalIncProducts[$product->id][$mnd] ? $totalIncProducts[$product->id][$mnd] : "0" ;
                echo "<td>" . round($ex). "<br>";
                echo "<span style='color:#aaa;'>". round($inc). "</span></td>";
            }
            echo "</tr>";
        }
        echo "<tr>";
        echo "<td></td>";
        echo "<td>" . round($totalEx) . "<br>";
        echo "<span style='color:#aaa;'>" . round($totalInc) . "</span></td>";
        
        foreach($products as $product) {
            $ex = 0;
            $inc = 0;
            foreach($totalExProducts[$product->id] as $tmp) { $ex += $tmp; }
            foreach($totalIncProducts[$product->id] as $tmp) { $inc += $tmp; }
            echo "<td>".round($ex) . "<br><span style='color:#aaa;'>" . round($inc)."</span></td>";
        }
        echo "</tr>";
        echo "</table>";
        
    }
    
    public function saveAccountingConfig() {
        $configs = $this->getApi()->getAccountingManager()->getAllConfigs();
        foreach($configs as $conf) {
            if($conf->id == $_POST['configid']) {
                $conf->includeUsers = $_POST['includeUsers'];
                $conf->orderFilterPeriode = $_POST['orderFilterPeriode'];
                
                $conf->paymentTypeCustomerIds = array();
                foreach($_POST as $key => $val) {
                    if(stristr($key, "customeridforpaymentmethod_")) {
                        $paymentType = str_replace("customeridforpaymentmethod_", "", $key);
                        $conf->paymentTypeCustomerIds[$paymentType] = $val;
                    }
                }
                
                $conf->username = $_POST['username'];
                $conf->password = $_POST['password'];
                $conf->subType = $_POST['subtype'];
                $conf->startCustomerCodeOffset = $_POST['startCustomerCodeOffset'];
                
                $this->getApi()->getAccountingManager()->saveConfig($conf);
                break;
            }
        }
    }
    
    public function addPaymentTypeToConfig() {
        $configs = $this->getApi()->getAccountingManager()->getAllConfigs();
        foreach($configs as $conf) {
            if($conf->id == $_POST['data']['configid']) {
                $type = new \core_accountingmanager_AccountingTransferConfigTypes();
                $type->paymentType = $_POST['data']['paymentmethod'];
                $type->status = $_POST['data']['paymentstatus'];
                $conf->paymentTypes[] = $type;
                print_r($conf->paymentTypes);
                $this->getApi()->getAccountingManager()->saveConfig($conf);
                break;
            }
        }
    }
    
    public function removePaymentTypeToConfig() {
        $configs = $this->getApi()->getAccountingManager()->getAllConfigs();
        foreach($configs as $conf) {
            $toRemove = null;
            if($conf->id == $_POST['data']['configid']) {
                $newArray = array();
                foreach($conf->paymentTypes as $key => $val) {
                    if($val->paymentType == $_POST['data']['type'] && $val->status == $_POST['data']['status']) {
                        $toRemove = $key;
                    } else {
                        $newArray[] = $val;
                    }
                }
                $conf->paymentTypes = $newArray;
                print_r($newArray);
                $this->getApi()->getAccountingManager()->saveConfig($conf);
            }
        }
    }
    
    public function render() {
        
    }
    
    public function addNewConfig() {
        $config = new \core_accountingmanager_AccountingTransferConfig();
        $config->transferType = $_POST['data']['newtype'];
        $this->getApi()->getAccountingManager()->saveConfig($config);
    }
}
?>
