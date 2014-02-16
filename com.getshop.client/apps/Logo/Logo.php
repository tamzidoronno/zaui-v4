<?php
namespace ns_974beda7_eb6e_4474_b991_5dbc9d24db8e;

class Logo extends \SystemApplication implements \Application {
    var $imageId;
    var $sticky = true;
    
    function __construct() {
        $this->setSkinVariable("height", 120, "The height of the logo");
        $this->setSkinVariable("width", 120, "The width of the logo");
    }

    public function getDescription() {
    }
    
    public function getName() {
        
    }

    public function postProcess() {
        
    }

    public function preProcess() {
    }

    public function render() {
        $this->imageId = $this->getApi()->getLogoManager()->getLogo()->LogoId;
        $this->includefile("LogoTemplate");
    }
    
    public function uploadImage() {
        $content = $_POST['data']['data'];
        $content = base64_decode(str_replace("data:image/png;base64,", "",$content));
        $imgId = \FileUpload::storeFile($content);
        $this->getApi()->getLogoManager()->setLogo($imgId);
    }
    
    public function removeLogo() {
        $this->getApi()->getLogoManager()->deleteLogo();
    }
    
    public function getLogo() {
        if(!isset($this->imageId)) {
            return null;
        }
        return $this->imageId;
    }
    
    public function reloadLogo() {
        
    }
}

?>