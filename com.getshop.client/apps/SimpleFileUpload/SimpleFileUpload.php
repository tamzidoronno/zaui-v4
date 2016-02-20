<?php
namespace ns_994d7fed_d0cf_4a78_a5ff_4aad16b9bcab;

class SimpleFileUpload extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SimpleFileUpload";
    }

    public function render() {
        $this->includefile("uploadbutton");
        echo "<div class='filelist'>";
        $this->includefile("uploadedFiles");
        echo "</div>";
    }
    
    function reloadFileList() {
        $this->includefile("uploadedFiles");
    }
    
    function deleteFile() {
        $this->getApi()->getFileManager()->deleteFileEntry($_POST['data']['fileid']);
    }
    
    public function isImage($fileName, $type) {
        $files['image'] = "image";
        $files['jpeg'] = "image";
        $files['png'] = "image";
        $files['gif'] = "image";
        
        foreach($files as $key => $val) {
            if(stristr($fileName, $key)) {
                return true;
            }
        }
        foreach($files as $key => $val) {
            if(stristr($type, $key)) {
                return true;
            }
        }
        return false;
    }
    
    
    function humanFileSize($size,$unit="") {
        if( (!$unit && $size >= 1<<30) || $unit == "GB")
          return number_format($size/(1<<30),2)."GB";
        if( (!$unit && $size >= 1<<20) || $unit == "MB")
          return number_format($size/(1<<20),2)."MB";
        if( (!$unit && $size >= 1<<10) || $unit == "KB")
          return number_format($size/(1<<10),2)."KB";
        return number_format($size)." bytes";
    }
    
    function translateFile($fileName, $type) {
        $files = array();
        $files['pdf'] = "file-pdf-o";
        $files['xls'] = "file-excel-o";
        $files['xsd'] = "file-excel-o";
        $files['video'] = "video-camera";
        $files['mpeg'] = "video-camera";
        $files['doc'] = "file-text";
        $files['mp3'] = "music";
        $files['image'] = "image";
        
        foreach($files as $key => $val) {
            if(stristr($fileName, $key)) {
                return $val;
            }
        }
        foreach($files as $key => $val) {
            if(stristr($type, $key)) {
                return $val;
            }
        }
        
        return "question"; 
    }
}
?>
