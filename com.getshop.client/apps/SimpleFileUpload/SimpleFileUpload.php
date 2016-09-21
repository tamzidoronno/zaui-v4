<?php
namespace ns_994d7fed_d0cf_4a78_a5ff_4aad16b9bcab;

class SimpleFileUpload extends \MarketingApplication implements \Application {
    var $imagePrinted = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SimpleFileUpload";
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }
    
    public function render() {
        $withWriteAccess = "";
        if($this->hasWriteAccess()) {
            $this->includefile("uploadbutton");
            $withWriteAccess = "withwacces";
        }
        echo "<div class='filelist $withWriteAccess'>";
        $this->includefile("uploadedFiles");
        echo "</div>";
        echo "<div style='clear:both;'></div>";
    }
    
    function updateFileName() {
        $oldfile = $this->getApi()->getFileManager()->getFile($_POST['data']['fileId']);
        
        $extention = $this->getExtention($oldfile->name);
        $newFileName = $_POST['data']['newFileName'];
        
        if ($extention) {
            $newFileName .= ".".$extention;
        }
        
        $this->getApi()->getFileManager()->renameFileEntry($_POST['data']['fileId'], $newFileName);
        
        $arrayres = array();
        $arrayres[] = $_POST['data']['fileId'];
        $this->notifyParent("fileRenamed", $arrayres);
    }
    
    function reloadFileList() {
        $this->includefile("uploadedFiles");
    }
    
    public function displayImage() {
        $uploadedImg = false;
        foreach($this->getAllFiles() as $file) {
            if($this->isImage($file)) {
                echo "<div class='image'>";
                echo "<img src='displayImage.php?id=".$file->id."&maxWidth=500'>";
                $uploadedImg = true;
                $this->imagePrinted = $file->id;
                echo "</div>";
                break;
            }
        }
    }
    
    public function getImageId() {
        foreach($this->getAllFiles() as $file) {
            if($this->isImage($file)) {
                return $file->id;
            }
        }
        return "";
    }
    
    function deleteFile() {
        $this->getApi()->getFileManager()->deleteFileEntry($_POST['data']['fileid']);
        $arrayres = array();
        $arrayres[] = $_POST['data']['fileid'];
        $this->notifyParent("fileDeleted", $arrayres);
    }
    
    /**
     * @param \core_filemanager_FileEntry $file
     * @return boolean
     */
    public function isImage($file) {
        $files['image'] = "image";
        $files['jpeg'] = "image";
        $files['png'] = "image";
        $files['gif'] = "image";
        
        foreach($files as $key => $val) {
            if(stristr($file->name, $key)) {
                return true;
            }
        }
        foreach($files as $key => $val) {
            if(stristr($file->type, $key)) {
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

    
    public function fileUplaoded($fileId) {
        $arrayres = array();
        $arrayres[] = $fileId;
        $this->notifyParent("fileUplaoded", $arrayres);
    }
    
    public function setSortingAlphaDesc() {
        $this->setConfigurationSetting("isInvertedAlpaSortingActivatedOverride", "true");
    }
    
    public static function sortAlphaDescending($a, $b) {
        return strcmp($b->name, $a->name);
    }
    
    /**
     * @return \core_filemanager_FileEntry[]
     */
    public function getAllFiles() {
        $files = $this->getApi()->getFileManager()->getFiles($this->getFileListId());
        if(!$files) {
            $files = array();
        }
        
        if ($this->getConfigurationSetting("isInvertedAlpaSortingActivatedOverride") === "true" || $this->getConfigurationSetting("isInvertedAlpaSortingActivated") === "true") {
            usort($files, array("ns_994d7fed_d0cf_4a78_a5ff_4aad16b9bcab\SimpleFileUpload", "sortAlphaDescending"));
        }
        
        return $files;
    }

    public function displayAdditionalFiles() {
        $this->includefile("uploadedFiles");
    }

    public function getFileListId() {
        if ($this->getParentWrappedApplication() !== null && method_exists($this->getParentWrappedApplication(), "getFileuploadListId")) {
            return $this->getParentWrappedApplication()->getFileuploadListId();
        }
        
        return $this->getAppInstanceId();
    }

    public function getExtention($fileName) {
        $fileName = explode(".", $fileName);
        if (count($fileName) > 1) {
            return $fileName[count($fileName)-1];
        }
        
        return "";
    }

    public function getFileName($fileEntry) {
        $fileName = explode(".", $fileEntry->name);
        
        if (count($fileName) > 1) {
            array_pop($fileName);
        }
        
        return implode(".", $fileName);
    }

    public function saveSettings() {
        $this->setConfigurationSetting("isInvertedAlpaSortingActivated", $_POST['isInvertedAlpaSortingActivated']);
    }
}
?>
