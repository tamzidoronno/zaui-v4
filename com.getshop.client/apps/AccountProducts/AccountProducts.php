<?php
namespace ns_80fe9499_b4f5_41b4_8ce0_89c872b5bd87;

class AccountProducts extends \MarketingApplication implements \Application {
    private $products = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountProducts";
    }

    public function render() {
        if ($this->getApi()->getGetShopAccountingManager()->isCurrentSelectedAccountingSystemPrimitive()) {
            $this->includefile("primitivproductconfig");
        } else {
            $this->includefile("productsview");
            $this->includefile("accountingcodes");
        }
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
                
                foreach ($product->accountingConfig as $accountingData) {
                    if ($accountingData->taxGroupNumber === $product->taxGroupObject->groupNumber) {
                        $accountingData->accountingNumber = $value;
                    }
                }
                
                $this->getApi()->getProductManager()->saveProduct($product);
            }
        }
    }
    
    public function saveAccountingDescription() {
        
    }

    /**
     * 
     * @param \core_productmanager_data_Product $product
     * @param type $taxCodeId
     * @return \core_productmanager_data_ProductAccountingInformation
     */
    public function getAccountinProductInfoForTaxCode($product, $taxGroupNumber) {
        
        foreach ($product->accountingConfig as $config) {
            if ($config->taxGroupNumber == $taxGroupNumber) {
                return $config;
            }
        }
        
        return null;
    }

    public function checkForProductsWithoutTax() {
        $allOk = true;
        
        $products = $this->getProducts();
        
        foreach ($products as $product) {
            if (!$product->taxGroupObject) {
                echo "<div>";
                    echo $this->__f("Product with name " . $product->name . " does not have any tax code set, please set this first");
                echo "</div>";
                $allOk = false;
            }
        }
        
        return $allOk;
    }

    /**
     * 
     * @return \core_productmanager_data_Product[]
     */
    public function getProducts() {
        if (!$this->products) {
            $this->products = $this->getApi()->getProductManager()->getAllProductsIncDeleted();
        }
        return $this->products;   
    }

    /**
     * 
     * @param type $groupNumber
     * @param \core_productmanager_data_TaxGroup[] $taxGroups
     * @return \core_productmanager_data_TaxGroup
     */
    public function getTaxGroup($groupNumber, $taxGroups) {
        foreach ($taxGroups as $group) {
            if ($group->groupNumber == $groupNumber) {
                return $group;
            }
        }
        
        return null;    
    }
    
    public function savePrimitiveAccounting() {
        foreach ($_POST['data'] as $productId => $accountingInformations) {
            $this->getApi()->getProductManager()->saveAccountingInformation($productId, $accountingInformations);    
        }
    }

}
?>