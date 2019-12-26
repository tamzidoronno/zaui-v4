<?php
namespace ns_0c6398b0_c301_481a_b4e7_faea0376e822;

class ProductList extends \MarketingApplication implements \Application {
    public $createProductError;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductList";
    }

    public function render() {
        $this->printTable();
        $this->includefile("createproduct");
    }
    
    public function formatTaxGroup($row) {
        return $row->taxGroupObject->taxRate."%";
    }
    
    public function ProductManager_findProducts() {
        $app = new \ns_4404dc7d_e68a_4fd5_bd98_39813974a606\EcommerceProductView();
        $app->setProductId($_POST['data']['id']);
        $app->renderApplication(true, $this);
    }
    
    public function printTable() {
        $filterOptions = new \core_common_FilterOptions();
        $filterOptions->searchWord = $this->getSearchWord();
        $args = array($filterOptions);
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('name', 'NAME', 'name'),
            array('listText', 'LIST', 'listText'),
            array('price', 'PRICE', 'price'),
            array('tax', 'TAX', '', 'formatTaxGroup')
            
        );
        
        $products = $this->getApi()->getProductManager()->getAllProducts();
        $lists = $this->getApi()->getProductManager()->getProductLists();
        
        foreach($products as $idx => $prod) {
            $names = array();
            foreach($lists as $list) {
                if(in_array($prod->id, $list->productIds)) {
                    $names[] = $list->listName;
                }
            }
            $products[$idx]->listText = join(",", $names);
        }
        
        $table = new \GetShopModuleTable($this, 'ProductManager', 'findProducts', $args, $attributes);
        $table->setData($products);
        $table->loadContentInOverlay = true;
        $table->render();
    }

    public function getSearchWord() {
        if (isset($_SESSION['ns_c282cfba_2873_46fd_876b_c44269eb0dfb_searchword'])) {
            return $_SESSION['ns_c282cfba_2873_46fd_876b_c44269eb0dfb_searchword'];
        }
        
        return "";
    }
    
    public function createNewProduct() {
        $account = 0;
        if($_POST['data']['account']) {
            $account = $_POST['data']['account'];
        }
        $product = $this->getApi()->getProductManager()->createProductWithAccount($account);
        if(!$product) {
            echo "<div style='background-color:red; font-size:20px; text-align:center;margin: 10px;padding: 10px; color:#fff;'>Failed to create a new product, make sure the account you are trying to create a product on is correct set up.</div>";
            return;
        }
        $product->name = $_POST['data']['name'];
        $this->getApi()->getProductManager()->saveProduct($product);
        $_SESSION['ns_c282cfba_2873_46fd_876b_c44269eb0dfb_searchword'] = $product->name;
        
        $app = new \ns_4404dc7d_e68a_4fd5_bd98_39813974a606\EcommerceProductView();
        $app->setProductId($product->id);
        $app->renderApplication(true, $this);
    }

}
?>
