<?php
namespace ns_d612904c_8e44_4ec0_abf9_c03b62159ce4;

class Banner extends \WebshopApplication implements \Application {
    public $pageSingleton = true;
    
    public $bannerSet;
    public $images;
    public $products;
    
    public function getDescription() {
        return $this->__("This is the perfect application if you need to stash your page a bit more, simply create banners and add them to this application and then it will rotate");
    }
    
    public function saveOriginalImage() {
        $content = base64_decode(str_replace("data:image/png;base64,", "", $_POST['data']['data']));
        $imgId = \FileUpload::storeFile($content);
        echo $imgId;
    }
    
    public function getAvailablePositions() {
        return "middle";
    }
    
    public function getName() {
        return $this->__("Banner Slider");
    }
    
    public function getBanners() {
        if(isset($this->bannerSet->banners)) {
            return $this->bannerSet->banners;
        }
        return array();
    }
    
    public function getStarted() {
        echo $this->__f("When creating this application, you will be able to add banners which will slide on a time interval.");
    }
   
    public function getHeight() {
        if ($this->bannerSet->height == null || $this->bannerSet->height < 50) {
            return 50;
        }
        
        return $this->bannerSet->height;
    }
    
    public function isShowDots() {
//        echo $this->bannerSet->showDots;
//        die;
        return ($this->bannerSet->showDots == 1);
    }
    
    public function postProcess() {
    }
    
    public function preProcess() {
        
    }
    
    public function getImage($imageId) {
        if(isset($this->images)) {
            foreach ($this->images as $image) {
                if ($image->id == $imageId) {
                    return $image->imageData;
                }
            }
        }
    }
    
    public function render() {
        $this->loadBannerSet();
        $this->includefile("BannerTemplate");
    }
    
    public function showAddBanner() {
        $this->loadBannerSet();
        $this->includefile("BannerTemplateSettings");
    }
    
    /**
     * Used when app is called directly 
     */
    private function loadBannerSet() {
        $this->bannerSet = $this->getApi()->getBannerManager()->getSet($this->getConfiguration()->id);
        
        foreach($this->bannerSet->banners as $banner) {
            $ids = array();
            if(strlen(trim($banner->productId)) > 0) {
                $ids[] = $banner->productId;
            }
            if(sizeof($ids) > 0) {
                $this->products = $this->getApi()->getProductManager()->getProducts($ids);
            }
            
        }
    }
    
    private function addBanners() {
        $messages = $this->getAnswers();
        foreach ($messages as $message) {
            if ($message->originalMessage->conf->id == $this->getConfiguration()->id) {
                /* @var $message app_banner_answer_Banners  */
                $this->bannerSet = $message->bannerSet;
            }
        }
    }
    
    public function getProductName($productId) {
        $product = $this->getProduct($productId);
        return $product->name;
    }
    
    public function getProduct($productId) {
        foreach($this->products as $product) {
            if($product->id == $productId) {
                return $product;
            }
        }
    }
    
    public function DeleteBanner() {
        $imageId = $_POST['data']['imageId'];
        $this->getApi()->getBannerManager()->removeImage($this->getConfiguration()->id, $imageId);
        $this->loadBannerSet();
        $this->showAddBanner();
    }
    
    public function setBannerImages() {
        $result = array();
        foreach($_POST['data']['set'] as $index => $image) {
            $banner = new \app_bannermanager_data_Banner();
            $banner->imageId = $image;
            $banner->crop_cordinates = $_POST['data']['cordinates'][$index];
            if($_POST['data']['links'][$index] != "gs_empty") {
                $banner->link = base64_decode($_POST['data']['links'][$index]);
            }
            $banner->imagetext = array();
            foreach($_POST['data']['text'] as $id => $textarray) {
                if($textarray['imageid'] == $image) {
                    $text = new \app_bannermanager_data_BannerText();
                    $text->text = base64_decode($textarray['text']);
                    $text->x = $textarray['x'];
                    $text->y = $textarray['y'];
                    $text->size = (int)$textarray['size'];
                    $text->colour = $textarray['color'];
                    $banner->imagetext[] = $text;
                }
            }
            $result[] = $banner;
        }
        return $result;
    }
    
    public function SetSize() {
        $bannerSet = $this->getApi()->getBannerManager()->getSet($this->getConfiguration()->id);
        $bannerSet->height = $_POST['data']['height'];
        $bannerSet->interval = $_POST['data']['interval'];
        $bannerSet->showDots = $_POST['data']['showdots'] == "true" ? "true" : "false";
        $bannerSet->banners = $this->setBannerImages();
        $this->getAPi()->getBannerManager()->saveSet($bannerSet);
        $this->bannerSet = $bannerSet;
        echo "<center>" . $this->__f("This content has been updated!")."</center>";
        $this->showAddBanner();
    }
    
    public function addproducttobanner() {
        $imageId = $_POST['data']['imageId'];
        $productId = $_POST['data']['productId'];
        $bannerSetId = $this->getConfiguration()->id;
        
        $this->getApi()->getBannerManager()->linkProductToImage($bannerSetId, $imageId, $productId);
        $this->loadBannerSet();
        $this->includefile("BannerTemplateSettings");
    }
    
    public function getProductLink($productId) {
        $product = $this->getProduct($productId);
        return "?page=".$product->page->id;
    }
    
    public function removeProductFromBannerImage() {
        $imageId = $_POST['data']['imageId'];
        $bannerSetId = $this->getConfiguration()->id;
        $this->getApi()->getBannerManager()->linkProductToImage($bannerSetId, $imageId, "");
        $this->loadBannerSet();
        $this->includefile("BannerTemplateSettings");
    }
   
    public function searchForProduct() {
        $text = $_POST['data']['text'];
        $imageId = $_POST['data']['imageId'];
        $criteria = $this->getApiObject()->core_productmanager_data_ProductCriteria();
        $criteria->search = $text;
        
        $productList = $this->getApi()->getProductManager()->getProducts($text);
        if(is_array($productList)) {
            foreach($productList as $product) {
                /* @var $product core_productmanager_data_Product */
                $productId = $product->id;
                ?>
                <div gstype="form" method="addproducttobanner" output="informationbox">
                    <input type="hidden" gsname="imageId" value="<?php echo $imageId; ?>">
                    <input type="hidden" gsname="productId" value="<?php echo $productId; ?>">
                    <span gstype='submit'>
                        <?php echo $product->name; ?>
                    </span>
                </div>
                <?php
            }
        }
    }
    
    public function getInterval() {
        return $this->bannerSet->interval;
    }

}
?>