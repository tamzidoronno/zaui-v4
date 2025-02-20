<?php
namespace ns_d612904c_8e44_4ec0_abf9_c03b62159ce4;

class Banner extends \WebshopApplication implements \Application {
    public $pageSingleton = true;
    
    public $bannerSet;
    public $images;
    public $products;
    
    public function getDescription() {
        return $this->__("This application enables you to upload images and display them as a gallery with thumbnails.");
    }
    
    public function toggleThumbs() {
        $toggled = $this->getConfigurationSetting("toggleThumbs");
        if(!$toggled) {
            $this->setConfigurationSetting("toggleThumbs", "on");
        } else {
            $this->setConfigurationSetting("toggleThumbs", "");
        }
    }
    
    public function toggleCompressMainImage() {
        $toggled = $this->getConfigurationSetting("toggleCompressMainImage");
        if(!$toggled) {
            $this->setConfigurationSetting("toggleCompressMainImage", "on");
        } else {
            $this->setConfigurationSetting("toggleCompressMainImage", "");
        }
    }
    
    public function getName() {
        return $this->__w("Gallery");
    }
    
    public function isShowDots() {
        if ($this->bannerSet->showDots == 1) {
            return true;
        }
        
        return $this->getConfigurationSetting("showDots") == true;
    }
    
    public function hasMouseOver() {
        return $this->getConfigurationSetting("mouseover") == "true";
    }
    
    public function toggleMouseOver() {
        if($this->hasMouseOver()) {
            $this->setConfigurationSetting("mouseover", "false");
        } else {
            $this->setConfigurationSetting("mouseover", "true");
        }
    }

    public function getBanners() {
        $this->loadBannerSet();
        return $this->bannerSet->banners;
    }
    
    public function getHeight() {
        $this->loadBannerSet();
        return $this->bannerSet->height;
    }
    
    public function render() {
        $this->loadBannerSet();
        $this->includefile("BannerTemplate");
    }
    
    public function showAddBanner() {
        $this->loadBannerSet();
        $this->includefile("BannerTemplateSettings");
    }
    
    public function getInterval() {
        $this->loadBannerSet();
        return $this->bannerSet->interval;
    }
    
    public function setValidBanners() {
        $allImages = $_POST['data']['images'];
        $this->loadBannerSet();
        foreach ($this->bannerSet->banners as $banner) {
            $imageId = $banner->imageId;
            if (!in_array($imageId, $allImages)) {
                $this->getApi()->getBannerManager()->removeImage($this->bannerSet->id, $imageId);
            }
        }
        
        if ($this->bannerSet->id) {
            $this->bannerSet = $this->getApi()->getBannerManager()->getSet($this->bannerSet->id);
        }
        
        foreach ($this->bannerSet->banners as $banner) {
            /* @var $banner \app_bannermanager_data_Banner */
            $banner->isDefault = false;
            $imageId = $banner->imageId;
            if($imageId == $_POST['data']['defaultImg']) {
                $banner->isDefault = true;
            }
        }
        
        $this->bannerSet->interval = $_POST['data']['interval'];
        $this->bannerSet->height = $_POST['data']['height'];
        $this->getApi()->getBannerManager()->saveSet($this->bannerSet);
    }
    
    public function toggleDots() {
        $this->loadBannerSet();
        $value = !$this->isShowDots();
        $this->bannerSet->showDots = "false";
        $this->getApi()->getBannerManager()->saveSet($this->bannerSet);
        $this->setConfigurationSetting("showDots", $value ? true : false);
    }
    
    private function getTexts() {
        $this->loadBannerSet();
        $imagetext = array();
        
        if (isset($_POST['data']['textFields'])) {
            $textFields = $_POST['data']['textFields'];
            
            foreach ($textFields as $textFieldPost) {
                $textField = new \app_bannermanager_data_BannerText();
                $textField->colour = $textFieldPost['color'];
                $textField->x = round($textFieldPost['x']);
                $textField->y = round($textFieldPost['y']);
                $textField->size = round($textFieldPost['fontSize']);
                $textField->text = $textFieldPost['text'];
                $imagetext[] = $textField;
            }
        }
        
        return $imagetext;
    }
    
    public function updateCordinates() {
        $crops = $this->getCropsFromPost();
        $imageId = $_POST['data']['imageId'];
        $rotation = $_POST['data']['rotation'];
        $this->attachImageToBannerSet($imageId, $crops, $rotation);
    }
    
    public function saveOriginalImage() {
        $content = base64_decode(str_replace("data:image/png;base64,", "", $_POST['data']['data']));
        $imgId = \FileUpload::storeFile($content);
        
        $crop = $this->getCropsFromPost();
        $rotation = $_POST['data']['rotation'];
        $this->attachImageToBannerSet($imgId, $crop, $rotation);
        echo $imgId;
    }
    
    private function getCropsFromPost() {
        $c = $_POST['data']['cords'];
        
        $crop = (object) array(
            "x"  => $c[0],
            "x2" => $c[2], 
            "y"  => $c[1],
            "y2" => $c[3]
        );
        
        return $crop;
    }
    
    private function attachImageToBannerSet($imageId, $cords, $rotation) {
        $this->loadBannerSet();
        $banner = $this->getBannerById($imageId);
        
        if (!$banner) {
            $banner = new \app_bannermanager_data_Banner();
            $this->bannerSet->banners[] = $banner;
        }
        
        $banner->imageId = $imageId;
        $banner->crop_cordinates = json_encode($cords);
        $banner->rotation = $rotation;
        $banner->imagetext = $this->getTexts();
        
        $this->getApi()->getBannerManager()->saveSet($this->bannerSet);
    }
    
    private function loadBannerSet() {
        if(!isset($this->bannerSet->banners)) {
            $this->bannerSet = $this->getApi()->getBannerManager()->getSet($this->getConfiguration()->id);
        }
    }
    
    private function getBannerById($imageId) {
        $this->loadBannerSet();
        foreach ($this->bannerSet->banners as $banner) {
            if ($banner->imageId == $imageId) {
                return $banner;
            }
        }
        
        return null;
    }
}
?>