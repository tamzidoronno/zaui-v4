<?php

namespace ns_e72f97ad_aa1f_4e67_bcfd_e64607f05f93;

class Gallery extends \MarketingApplication implements \Application {

    private $allImages;
    private $imageData;
    private $currentEntry;

    public function getDescription() {
        return $this->__("This gallery gives you the ability to display images into a gallery, and even connect the images to products, categorise them by combining a category displayer, or by using a left menu.");
    }

    public function getAvailablePositions() {
        return "middle";
    }

    public function getAllImages() {
        if (!is_array($this->allImages) || sizeof($this->allImages) == 0) {
            $this->allImages = array();
            for($i = 0; $i < 9; $i++) {
                $this->allImages[$i] = new \app_gallerymanager_data_ImageEntry();
                $this->allImages[$i]->demo = true;
            }
            $this->allImages[0]->imageId = "6cb2ad0d-1441-4f66-90b3-bb6dae4fb78e";
            $this->allImages[1]->imageId = "d2af2175-e990-416e-ae61-d7f6999d87d3";
            $this->allImages[2]->imageId = "633b9eae-a8a2-4389-9998-2c65c7d3d4a9";
            $this->allImages[3]->imageId = "237f7192-eed4-416b-9dbe-08c9c25700ef";
            $this->allImages[4]->imageId = "e38bd458-11b7-4757-a157-b3efb9931e0a";
            $this->allImages[5]->imageId = "4950c06e-e54e-48cc-a583-bb218cf858ee";
            $this->allImages[6]->imageId = "feda4884-b151-4521-b321-8c12c29e7598";
            $this->allImages[7]->imageId = "1c561b58-906b-4a16-8613-fc5cf764b109";
            $this->allImages[8]->imageId = "caf81fa0-f83a-4a70-807b-e8606b067065";
        }
            

        return $this->allImages;
    }

    public function editEntry() {
        $this->currentEntry = $this->getApi()->getGalleryManager()->getEntry($_POST['data']['id']);
        $this->includefile("GalleryTemplateEditEntry");
    }

    public function getCurrentEntry() {
        return $this->currentEntry;
    }

    public function getName() {
        return $this->__("Gallery");
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function getStarted() {
        echo $this->__f("Start adding pictures to your webshop, create this application and start uploading images.");
    }

    public function render() {
        $this->setup();
        $this->includefile("GalleryTemplateView");
    }

    public function updateImage() {
        $desc = $_POST['data']['desc'];
        $title = $_POST['data']['title'];
        $id = $_POST['data']['id'];
        $imgId = $_POST['data']['imgId'];
        if ($id) {
            $entry = $this->getApi()->getGalleryManager()->getEntry($id);
            $entry->title = $title;
            $entry->description = $desc;
            $this->getApi()->getGalleryManager()->saveEntry($entry);
        } else {
            $this->getApi()->getGalleryManager()->addImageToGallery($this->getConfiguration()->id, $imgId, $desc, $title);
        }
    }

    public function uploadImage() {
        $content = base64_decode(str_replace("data:image/png;base64,", "",  $_POST['data']['data']));
        $imgId = \FileUpload::storeFile($content);
        
        $id = $this->getConfiguration()->id;
        $this->getApi()->getGalleryManager()->addImageToGallery($id, $imgId, "", "");
        echo $imgId;
    }

    public function showLargeImage() {
        $this->includefile("largeimagetemplate", "ns_dcd22afc_79ba_4463_bb5c_38925468ae26\ProductManager");
    }

    public function setup() {
        $this->allImages = $this->getApi()->getGalleryManager()->getAllImages($this->getConfiguration()->id);
        if (!is_array($this->allImages))
            $this->allImages = array();
    }

    public function reprint() {
        
    }

    public function deleteImage() {
        $id = $_POST['data']['id'];
        $this->setup();
        foreach($this->allImages as $img) {
            if($img->imageId == $id) {
                $this->getApi()->getGalleryManager()->deleteImage($img->id);
            }
        }
    }

    public function newImage() {
        $this->currentEntry = null;
        $this->includefile("GalleryTemplateEditEntry");
    }

    public function getImageData($imageId) {
        foreach ($this->imageData as $data) {
            /* @var $data core_filemanager_data_Image */
            if ($data->id == $imageId) {
                return $data;
            }
        }
    }

}

?>
