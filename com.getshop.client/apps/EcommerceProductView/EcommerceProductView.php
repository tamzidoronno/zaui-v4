<?php
namespace ns_4404dc7d_e68a_4fd5_bd98_39813974a606;

class EcommerceProductView extends \MarketingApplication implements \Application {
    /**
     * @var \core_productmanager_data_Product 
     */
    private $product;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "EcommerceProductView";
    }

    public function render() {
        $this->setProduct();
        $this->includefile("productheader");
        $this->includefile("overview");
    }
    
    public function setProductId($id) {
        $_SESSION['ns_4404dc7d_e68a_4fd5_bd98_39813974a606_productid'] = $id;
    }
    
    public function isTabActive($tabName) {
        if (isset($_SESSION['ns_4404dc7d_e68a_4fd5_bd98_39813974a606_selectedTab'])) {
            return ($tabName == $_SESSION['ns_4404dc7d_e68a_4fd5_bd98_39813974a606_selectedTab']) ? "active" : false;
        }
        
        if ($tabName == "details") {
            return "active";
        }
        
        return false;
    }
    
    public function saveListToProduct() {
        foreach ($_POST['data'] as $key => $value) {
            $listIdArra = explode("_", $key);
            
            if ($listIdArra[0] != "product")
                continue;
            
            $listId = $listIdArra[2];
            
            if ($_POST['data'][$key] == "true") {
                $this->addProductToList($listId);
            } else {
                $this->removeProductToList($listId);
            }
        }
        
    }

    public static function sortByName($a, $b) {
        return strcmp($a->listName, $b->listName);
    }
    
    public function saveList() {
        $list = $this->getApi()->getProductManager()->getProductList($_POST['data']['listid']);
        $list->listName  = $_POST['data']['name'];
        $this->getApi()->getProductManager()->saveProductList($list);
    }
    
    /**
     * 
     * @return \core_productmanager_data_Product
     */
    public function getProduct() {
        if (!$this->product) {
            $this->setProduct();
        }
        return $this->product;
    }

    public function setProduct() {
        $this->product = $this->getApi()->getProductManager()->getProduct($_SESSION['ns_4404dc7d_e68a_4fd5_bd98_39813974a606_productid']);
    }
    
    public function subMenuChanged() {
        $_SESSION['ns_4404dc7d_e68a_4fd5_bd98_39813974a606_selectedTab'] = $_POST['data']['selectedTab'];
        $this->renderTab();
    }
    
    public function renderTab() {
        $tab = isset($_SESSION['ns_4404dc7d_e68a_4fd5_bd98_39813974a606_selectedTab']) ? $_SESSION['ns_4404dc7d_e68a_4fd5_bd98_39813974a606_selectedTab'] : "details";
        
        if ($tab == "details")
            $this->includefile ("details");
        
        if ($tab == "overview")
            $this->includefile ("overview");
        
        if ($tab == "lists") {
            $this->includefile ("lists");
        }
        
        if ($tab == "prices") {
            $this->includefile ("prices");
        }
        
        if ($tab == "taxes") {
            $this->includefile ("taxes");
        }
    }
    
    public function addExtraTax() {
        $this->getApi()->getProductManager()->addAdditionalTaxGroup($_POST['data']['productid'], $_POST['data']['taxgroup']);
    }
    
    public function addNewList() {
        $this->getApi()->getProductManager()->createProductList($_POST['data']['listname']);
    }
    
    public function saveDetails() {
        $product = $this->getProduct();
        $product->name = $_POST['data']['name'];
        $product->shortDescription = $_POST['data']['shortdesc'];
        $product->description = $_POST['data']['description'];
        $product->isFood = $_POST['data']['isfood'];
        $this->getApi()->getProductManager()->saveProduct($product);
    }

    public function addProductToList($listId) {
        $product = $this->getProduct();
        $productList = $this->getApi()->getProductManager()->getProductList($listId);
        if (!in_array($product->id, $productList->productIds)) {
            $productList->productIds[] = $product->id;
            $this->getApi()->getProductManager()->saveProductList($productList);
        }
    }

    public function removeProductToList($listId) {
        $product = $this->getProduct();
        $productList = $this->getApi()->getProductManager()->getProductList($listId);
        $newIds = array();
        
        foreach ($productList->productIds as $id) {
            if ($id != $product->id) {
                $newIds[] = $id;
            }
        }
        
        if ($productList) {
            $productList->productIds = $newIds;
            $this->getApi()->getProductManager()->saveProductList($productList);
        }
    }
    
    public function saveProductPrice() {
        $product = $this->getProduct();
        $product->price = $_POST['data']['price'];
        $this->getApi()->getProductManager()->saveProduct($product);
    }

    public function deleteList() {
        $this->getApi()->getProductManager()->deleteProductList($_POST['data']['listid']);
    }
    
    public function removeTax() {
        $this->getApi()->getProductManager()->removeTaxGroup($_POST['data']['productid'], $_POST['data']['taxgroupid']);
    }
    
    public function addTopLevel() {
        $product = $this->getProduct();
        if (!$product->extras) {
            $product->extras = array();
        }
        
        if (!$product->variations->nodes) {
            $product->variations->nodes = array();
        }
        
        $node = new \core_productmanager_data_ProductExtraConfig();
        $node->name = $_POST['data']['name'];
        $node->type = $_POST['data']['type'];
        
        $product->extras[] = $node;
        
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function deleteExtraOption() {
        $product = $this->getProduct();
        $newOptionList = array();
        
        foreach ($product->extras as $extra) {
            if ($extra->id != $_POST['data']['optionid']) {
                $newOptionList[] = $extra;
            }
        }
        
        $product->extras = $newOptionList;
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function saveOption() {
        $product = $this->getProduct();
        
        $i = 0;
        
        foreach ($_POST['data']['extras'] as $extra) {
            $_POST['data']['extras'][$i]['extraPriceDouble'] = str_replace(",", ".", $_POST['data']['extras'][$i]['extraPriceDouble']);
        
            if (!is_numeric($_POST['data']['extras'][$i]['extraPriceDouble'])) {
                $_POST['data']['extras'][$i]['extraPriceDouble'] = 0;
            }
        
            $i++;
        }
        
        foreach ($product->extras as $extra) {
            if ($extra->id == $_POST['data']['id']) {
                $extra->extras = $_POST['data']['extras'];
            }
        }
        
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function setDefaultTaxRate() {
        $product = $this->getProduct();
        $taxes = $this->getApi()->getProductManager()->getTaxes();
        foreach ($taxes as $taxGroup) {
            if ($taxGroup->id == $_POST['data']['gsvalue']) {
                $product->taxGroupObject = $taxGroup;
                $product->taxgroup = $taxGroup->groupNumber;
                break;
            }
        }
        $this->getApi()->getProductManager()->saveProduct($product);
    }
}
?>
