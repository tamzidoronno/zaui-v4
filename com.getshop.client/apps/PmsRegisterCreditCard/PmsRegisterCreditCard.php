<?php
namespace ns_96b4d05e_68d9_4c89_9654_6edbe7548318;

class PmsRegisterCreditCard extends \WebshopApplication implements \Application {
    var $error;
    var $redirectToPayment;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsRegsiterCreditCard";
    }
    
    public function registercard() {
        $item = $_POST['data']['item'];
        
        $orderId = $this->getApi()->getPmsInvoiceManager()->createRegisterCardOrder($this->getSelectedName(), $item);
        if($orderId == "-1" || !$orderId) {
            $this->error = "Kunne ikke registrere kortet ditt opp mot boden, kontakt oss.";
        } else {
            $this->redirectToPayment = $orderId;
        }
    }
    
    public function selectEngine() {
        $this->setConfigurationSetting("selectedkey", $_POST['data']['name']);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("selectedkey");
    }
    
    public function render() {
        if($this->error) {
            echo "<div>" . $this->error . "</div>";
        }
        
        if($this->redirectToPayment) {
            ?>
            <script>
                thundashop.common.goToPageLink("?page=cart&payorder=<?php echo $this->redirectToPayment; ?>");
            </script>
            <?php
        }
        
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first. <br>";
            $engns = $this->getApi()->getStoreManager()->getMultiLevelNames();

            foreach($engns as $engine) {
                echo '<div gstype="clicksubmit" style="font-size: 16px; cursor:pointer; margin-top: 10px;" method="selectEngine" gsname="name" gsvalue="'.$engine.'">' . $engine . "</div>";
            }
            return;
        }
        $this->includefile("printitemsandsubmit");
    }
}
?>
