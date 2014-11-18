<?php
namespace ns_06f9d235_9dd3_4971_9b91_88231ae0436b;

class Product extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    var $attributesList = array();
    private $product;
    
    function __construct() {
    }

    public function getDescription() {
        return "Product";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    
    public function loadPicker() {
        $this->includefile("productpicker");
    }
    
    
    public function getProduct() {
        return $this->product;
    }
    
    public function getName() {
        return "Product";
    }

    public function postProcess() {
        
    }
    
    public function loadEditProduct() {
        $this->preProcess();
        $this->includefile("productedit");
    }

    public function preProcess() {
        $this->product = $this->getApi()->getProductManager()->getProductByPage($this->getPage()->id);
    }
    
    public function getStarted() {
    }

    
    private function createVariation($config) {
        $prodctVariation = new \core_productmanager_data_ProductVariation();
        $prodctVariation->title = $config['title'];
        $prodctVariation->priceDifference = isset($config['price']) ? $config['price'] : 0;
        $prodctVariation->children = array();
        if (isset($config['children'])) {
            foreach ($config['children'] as $config2) {
                $prodctVariation->children[] = $this->createVariation($config2);
            }
        }
        return $prodctVariation;
    }

    
    public function render() {
        $this->includefile("product");
    }
    
    public function updateAttributePool($allattr) {
        $attrgroups = array();
        foreach($allattr as $attr) {
            $value = new \core_productmanager_data_AttributeValue();
            $value->value = $attr['value'];
            $value->groupName = $attr['group'];
            if(isset( $attr['id'])) {
                $value->id = $attr['id'];
            }
            $attrgroups[] = $value;
        }
        $this->getApi()->getProductManager()->updateAttributePool($attrgroups);
    }
    
    public function saveProduct() {
        $this->preProcess();
        $product = $this->getProduct();
        
        if($_POST['data']['attributes_modified'] == "true") {
            if(isset($_POST['data']['all_attributes'])) {
                $allattr=$_POST['data']['all_attributes'];
            } else {
                $allattr = array();
            }
            $this->updateAttributePool($allattr);
        }
        
        //Saving product data.
        $product->name = $_POST['data']['title'];
        $product->price = floatval(str_replace(",", ".", $_POST['data']['price']));
        if(isset($_POST['data']['taxgroup'])) {
            $product->taxgroup = $_POST['data']['taxgroup'];
        } else {
            $product->taxgroup = -1;
        }
        
        //Saving variations
        $product->variations = array();
        if (isset($_POST['data']) && isset($_POST['data']['variations'])) {
            $configs = $_POST['data']['variations'];
            foreach ($configs as $config) {
                $object = $this->createVariation($config);
                $product->variations[] = $object;
            }
        }
        
        //Saving attributes.
        $product->attributesAdded = array();
        if(isset($_POST['data']['attr_to_prod'])) {
            $added = $_POST['data']['attr_to_prod'];
            foreach($added as $arr) {
                $product->attributesAdded[$arr['name']] = $arr['value'];
            }
        }
        
        //saving advanced data.
        $product->sku = $_POST['data']['skui'];
        $product->vismaId = $_POST['data']['vismaId'];
        $product->freeShipping = $_POST['data']['shipping'];
        $product->promoted = $_POST['data']['promoted'];
        $product->hideShippingPrice = $_POST['data']['hideShippingPrice'];
        $product->weight = floatval(str_replace(",", ".", $_POST['data']['weight']));
        $product->stockQuantity = (int)$_POST['data']['quantity'];
        $product->campaign_price = $_POST['data']['campaign_price'];
        $product->campaing_start_date = strtotime($_POST['data']['start_date']);
        $product->campaing_end_date = strtotime($_POST['data']['end_date']);
        $product->privateExcluded = $_POST['data']['privateExcluded'];
//        print_r($product);
        $this->getApi()->getProductManager()->saveProduct($product);
        
        $page = $this->getPage()->backendPage;
        $page->title = $product->name;
        $this->getApi()->getPageManager()->savePage($page);
    }
    
    public function addProductToCart() {
        $productId = $_POST['data']['productid'];

        $variations = array();
        if (isset($_POST['data']['variations'])) {
            $variations = $_POST['data']['variations'];
        }

        $this->getApi()->getCartManager()->addProduct($productId, 1, $variations);
    }

    /**
     * @return \core_productmanager_data_Product[]
     */
    public function getAllProducts() {
        $products = $this->getApi()->getProductManager()->getAllProductsLight();
        if(!$products) {
            $products = array();
        }
        return $products;
    }
    
}
?>
