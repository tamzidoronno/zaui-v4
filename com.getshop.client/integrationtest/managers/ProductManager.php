<?php

class ProductManager extends TestBase {
    public $api;
    
    /**
     * @param GetShopApi $api
     */
    public function __construct($api) {
        $this->api = $api;
    }
    
    /**
     * Change name of product 
     * @createProduct
     */
    public function test_createProduct() {
        $productManager = $this->api->getProductManager();
        $product = $productManager->createProduct();
    }
    
    /**
     * This will create a product 
     * then change nume and price
     * and finally saves it back
     * to productManager.
     * 
     * @saveProduct
     */
    public function test_saveProduct() {
        $productManager = $this->api->getProductManager();
        $product = $productManager->createProduct();
        $product->name = "Awsome product that you sell alot of";
        $product->price = 102301023.23;
        $productManager->saveProduct($product);
    }
    
    /**
     * You can change the stock quantity in two ways, 
     * either by using this covential method or 
     * change it to the product before you
     * save it. 
     */
    public function test_changeStockQuantity() {
        $productManager = $this->api->getProductManager();
        
        // Need product to demostrate with.
        $product = $productManager->createProduct();
                
        /* 1 positibily - change it directly with the save function. */
        $product->stockQuantity = 99;
        $productManager->saveProduct($product);
        /* end posibility 1 */
        
        /* 2. Using the convential method. */
        $productManager->changeStockQuantity($product->id, 99);
        /* end posibility 2 */
    }
    
    /**
     * Convential method for adding
     * a image to a product.
     */
    public function test_addImage() {
        $productManager = $this->api->getProductManager();
        
        // Need product to demostrate with.
        $product = $productManager->createProduct();
        
        $productManager->addImage($product->id, 
                "fileid_you_got_from_filemanager1", 
                "description");
    }
    
    /**
     * Retreive a product from the productManager
     * This is just a convenient method for retreiving 
     * a product by id.
     */
    public function test_getProduct() {
        $productManager = $this->api->getProductManager();
        
        // Need product to demostrate with.
        $product = $productManager->createProduct();
        
        $fetchedProduct = $productManager->getProductPages($product->id);
    }
    
    /**
     * Retreive products by criteria. 
     */
    public function test_getProducts() {
        $productManager = $this->api->getProductManager();
        
        // Need products to demostrate with.
        $product1 = $productManager->createProduct();
        $product2 = $productManager->createProduct();
        
        $productCriteria = $this->getApiObject()->core_productmanager_data_ProductCriteria();
        $productCriteria->pageIds[] = $product1->page->id;
        $productCriteria->pageIds[] = $product2->page->id;
        
        $products = $productManager->getProducts($productCriteria);
        /*
         * $products is an array that contains two products.
         */
    }
    
    /**
     * Get a random set of products.
     */
    public function test_getRandomProducts() {
        $productManager = $this->api->getProductManager();
        
        // Need products to demostrate with.
        $product1 = $productManager->createProduct();
        $product2 = $productManager->createProduct();
        $product3 = $productManager->createProduct();
        
        $randomProducts = $productManager->getRandomProducts(2, $product1->id);
        /**
         * The $randomProducts list will contain $product2 and $product3 
         * in a random order.
         */
    }
    
    /**
     * This is how you delete a product. 
     */
    public function test_removeProduct() {
        $productManager = $this->api->getProductManager();
        
        // Need products to demostrate with.
        $product1 = $productManager->createProduct();
        
        $productManager->removeProduct($product1->id);
        /**
         * Product1 will now not be available anymore from productmanager. 
         */
    }
    
    /**
     * Sets a image to be main.
     */
    public function test_setMainImage() {
        $productManager = $this->api->getProductManager();
        
        // Need products to demostrate with.
        $product1 = $productManager->createProduct();
        
        $productManager->setMainImage($product1->id, "imageid_from_filemanager");
    }
    
    /**
     * Fetch a list of all the latest / newest products.
     */
    public function test_getLatestProducts() {
        $productManager = $this->api->getProductManager();
        //Get the 10 latest products.
        $latestProducts = $productManager->getLatestProducts(10);
        
    }
    
    
    /**
     * Internal usage only 
     */
    public function test_translateEntries() {}
}

?>
