<?php
namespace ns_9ab6923e_3d6b_4b7c_b94e_c14e5ebe5364;

class PmsMonthlyBilling extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsMonthlyBilling";
    }

    public function render() {
        $this->includefile("instructions");
    }
    
    public function activatePluginModule() {
        $store = $this->getApi()->getStoreManager()->getMyStore();
        $store->configuration->additionalPlugins[] = "monthlypaymentlinks";
        $this->getApi()->getStoreManager()->saveStore($store->configuration);
    }
    
    public function deActivatePluginModule() {
        $store = $this->getApi()->getStoreManager()->getMyStore();
        $tmp = array();
        foreach($store->configuration->additionalPlugins as $plugin) {
            if($plugin == "monthlypaymentlinks") {
                continue;
            }
            $tmp[] = $plugin;
        }
        $store->configuration->additionalPlugins=$tmp;
        $this->getApi()->getStoreManager()->saveStore($store->configuration);
        
    }
    
    public function updateDateRange() {
        $_SESSION['monthlyselecteddaterangestart'] = $_POST['data']['startdate'];
        $_SESSION['monthlyselecteddaterangeend'] = $_POST['data']['enddate'];
    }
    
    public function getStartDate() {
        if(isset($_SESSION['monthlyselecteddaterangestart'])) {
            return $_SESSION['monthlyselecteddaterangestart'];
        }
        return  date("01.m.Y", time());
    }
    
    public function getEndDate() {
        if(isset($_SESSION['monthlyselecteddaterangeend'])) {
            return $_SESSION['monthlyselecteddaterangeend'];
        }
        return date("t.m.Y", time());
    }
    
    public function chooseCandidates() {
        $message = $_POST['data']['message'];
        $start = $this->getStartDate();
        $end = $this->getEndDate();
        $link = $this->getApi()->getPmsInvoiceManager()->getPaymentLinkConfig("default")->webAdress;
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "chosen_") && $val == "true") {
                $id = str_replace("chosen_", "", $key);
                $prefix = $_POST['data']['prefix_'.$id];
                $phone = $_POST['data']['phone_'.$id];
                $email = $_POST['data']['email_'.$id];
                $message = str_replace("{start}", $start, $message);
                $message = str_replace("{end}", $end, $message);
                $message = str_replace("{paymentlink}", $link . "/pr.php?start=" . $start . "&end=" . $end . "&id=" . $id,$message);
                
                $this->getApi()->getPmsManager()->sendPaymentRequest("default", $id, $email, $prefix, $phone, $message);
            }
        }
    }
    
}
?>
