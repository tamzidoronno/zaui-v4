<?php
namespace ns_3405a32a_c82d_4587_825a_36f10890be8e;

class Aviary extends \ThemeApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "Aviary";
    }

    public function render() {
        
    }
    
    public function saveImage() {
        \FileUpload::saveFileFromUrl($_POST['data']['url'], $_POST['data']['imageId']);
    }
    
    public function revertImage() {
        \FileUpload::revertFileFromOriginal($_POST['data']['imageId']);
    }
}
?>
