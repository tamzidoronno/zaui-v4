<?php
namespace ns_2da52bbc_a392_4125_92b6_eec1dc4879e9;

class Bring extends \ShipmentApplication implements \Application {

    public $singleton = true;
    private $additionalInfo = null;

    public function getDescription() {
        return $this->__("fraktguiden is a norwegian application for keeping track of norwegian shipment prices, shipment tracking etc.");
    }

    public function getName() {
        return "Bring";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function render() {
        
    }

    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("bring");
    }
    
    private function getOversizedPackagePrize() {
        $price = $this->getConfigurationSetting("oversizedpackageprice");
        if ($price == null) {
            $price = 0;
        }
        return $price;
    }
    
    public function getFromPostalCode() {
		throw new Exception("This part has not yet been implemented in 2.0.0 version");
    }

    public function getShippingCost($shipmentProduct = "") {
        $weight = 0;
        
        foreach ($this->getCart()->items as $cartItem) {
            $product = $cartItem->product;
            /* @var $product core_productmanager_data_Product */
            $weight += $cartItem->count * $product->weight;
        }
        
        $from = $this->getFromPostalCode();
        
        $to = false;
        
        if (isset($this->getCart()->address))
            $to = $this->getCart()->address->postCode;
        
        if(isset($_POST['data']) && isset($_POST['data']['postCode'])) {
            $to = $_POST['data']['postCode'];
        }
    
        if(!$to && isset($_SESSION['tempaddress'])) {
            $to = $_SESSION['tempaddress']['postCode'];
        }
        
        if($weight == 0) {
            $weight = 2100;
        }
        
        $url = "http://fraktguide.bring.no/fraktguide/products/all.json?weightInGrams=$weight&from=$from&to=$to";
        $content = file_get_contents($url);
        
        if ($content == "") {
            ob_clean();
            ob_start();
            header('HTTP/1.1 400 Internal Server Error');
            $_SESSION['checkoutstep'] = "address";
            unset($_SESSION['shippingproduct']);
            unset($_SESSION['shippingtype']);
            
            die("FAILED_TO_GET_SHIPMENT_BRING");
        } else {
            $this->additionalInfo = json_decode($content, true);
            if(!isset($this->additionalInfo['Product']) && $this->getOversizedPackagePrize() > 0) {
                $this->additionalInfo['Product'] = array();
                $test = array();
                $test['GuiInformation']['DisplayName'] = "Tung/stor forsendelse";
                $test['Price']['PackagePriceWithoutAdditionalServices']['AmountWithVAT'] = $this->getOversizedPackagePrize();
                $test['GuiInformation']['HelpText'] = "Wrong size.";
                $test['GuiInformation']['DescriptionText'] = "The package you are trying to send is either very heavy or oversized, that is why it will be more expensive.";
                $test['ProductId'] = "heavybring";
                $this->additionalInfo['Product'][] = $test;
            } else if(!isset($this->additionalInfo['Product']) && $this->getErrorMessageOversized() != "") {
                $this->additionalInfo['Product'] = array();
                $test = array();
                $test['GuiInformation']['DisplayName'] = $this->getErrorMessageOversized();
                $test['Price']['PackagePriceWithoutAdditionalServices']['AmountWithVAT'] = 0;
                $test['GuiInformation']['HelpText'] = "Wrong size.";
                $test['GuiInformation']['DescriptionText'] = $this->getErrorMessageOversized();
                $test['ProductId'] = "heavybring";
                $this->additionalInfo['Product'][] = $test;
            }
            
            $product = $this->additionalInfo['Product'][0];
            if(strlen($shipmentProduct) > 0) {
                foreach($this->additionalInfo['Product'] as $prod) {
                    if($prod['ProductId'] == $shipmentProduct) {
                        $product = $prod;
                        break;
                    }
                }
            }
        }
        
        return $product['Price']['PackagePriceWithoutAdditionalServices']['AmountWithVAT'];
    }
    

    public function additionalInformation() {
        
        $products = array();
        foreach ($this->additionalInfo['Product'] as $test) {
            $shipmentProduct = new \ShipmentProduct();
            $shipmentProduct->displayTitle = $test['GuiInformation']['DisplayName'];
            $shipmentProduct->price = $test['Price']['PackagePriceWithoutAdditionalServices']['AmountWithVAT'];
            $shipmentProduct->description = $test['GuiInformation']['HelpText']."<br>".$test['GuiInformation']['DescriptionText'];
            $shipmentProduct->productName = $test['ProductId'];
            
            $products[] = $shipmentProduct;
        }
        
        
        return $products;
    }
    
    public function getDefaultShipmentType() {
        return "SERVICEPAKKE";
    }
    
    public function getErrorMessageOversized() {
        $on = $this->getConfigurationSetting("displayerror");
        if ($on && $on === "true") {
            return $this->getConfigurationSetting("errormessage");
        }
    }

    public function getStoreSettings() {
        throw new Exception("This function is not yet available in 2.0.0 version");
    }
    
    public function hasSubProducts() {
        return true;
    }
}

?>
