<?php
namespace ns_80fe9499_b4f5_41b4_8ce0_89c872b5bd87;

class AccountProducts extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountProducts";
    }

    public function render() {
        $this->includefile("productsview");
    }
    
    public function saveTaxGroups() {
        foreach ($_POST['data'] as $key => $value) {
            if (strpos($key, "taxcode_") > -1) {
                $x = explode("_", $key);
                $productId = $x[1];
                $product = $this->getApi()->getProductManager()->getProduct($productId);
                $product->sku = $value;
                $this->getApi()->getProductManager()->saveProduct($product);
            }
            
            if (strpos($key, "product_") > -1) {
                $x = explode("_", $key);
                $productId = $x[1];
                $product = $this->getApi()->getProductManager()->getProduct($productId);
                $product->accountingSystemId = $value;
                $this->getApi()->getProductManager()->saveProduct($product);
            }
            
            if (strpos($key, "accounting_") > -1) {
                $x = explode("_", $key);
                $productId = $x[1];
                $product = $this->getApi()->getProductManager()->getProduct($productId);
                $product->accountingAccount = $value;
                $this->getApi()->getProductManager()->saveProduct($product);
            }
        }
    }
    
    public function saveAccountingDescription() {
        foreach ($_POST['data'] as $key => $value) {
            $exp = explode("_", $key);
            if ($exp[0] == "description") {
                $accountDetail = new \core_productmanager_data_AccountingDetail();
                $accountDetail->accountNumber = $exp[1];
                $accountDetail->description = $value;
                $accountDetail->taxgroup = $_POST['data']['taxcode_'.$exp[1]];
                $this->getApi()->getProductManager()->saveAccountingDetail($accountDetail);
            }
        }
    }
}
?>
