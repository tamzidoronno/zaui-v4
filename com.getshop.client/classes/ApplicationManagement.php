<?php

class ApplicationManagement extends ApplicationBase {

    var $showPage;
    var $errorMessage = "";
    var $settingsId;
    var $settings;

    public function display() {
        $this->includefile("applicationmanagement", "ApplicationManager");
    }

    public function getMyAppCount() {
        $settings =  $this->getFactory()->getApplicationPool()->getAllApplicationSettings();
        $count = 0;
        $storeId = $this->getFactory()->getStore()->id;
        foreach($settings as $setting) {
            if($setting->ownerStoreId == $storeId) {
                $count++;
            }
        }
        return $count;
    }

    public function isSynchToolConnected() {
        return $this->getFactory()->getApi()->getAppManager()->isSyncToolConnected();
    }

    public function getLastCreatedApp() {
        $settings =  $this->getFactory()->getApplicationPool()->getAllApplicationSettings();
        $retval = null;
        $storeId = $this->getFactory()->getStore()->id;
        
        foreach($settings as $setting) {
            if($setting->ownerStoreId == $storeId) {
                if($retval == null || $setting->rowCreatedDate > $retval->rowCreatedDate) {
                    $retval = $setting;
                }
            }
        }
        return $retval;
    }
    
    public function getPublicApplicationCount() {
        $settings =  $this->getFactory()->getApplicationPool()->getAllApplicationSettings();
        $retval = null;
        $storeId = $this->getFactory()->getStore()->id;
        
        $count = 0;
        foreach($settings as $setting) {
            if($setting->ownerStoreId == $storeId) {
                if($retval == null || $setting->rowCreatedDate > $retval->rowCreatedDate) {
                    $count++;
                }
            }
        }
        return $count;
    }

    public function getAppAddedToPageCount() {
        return 0;
    }

    public function setShowCreatePage() {
        $this->showPage = "createPage";
    }

    public function setShowOverViewPage() {
        $this->showPage = "overview";
    }

    public function showOverViewPage() {
        if ((!$this->showPage || $this->showPage == "overview") && !$this->applicationSettings) {
            return true;
        }
        return false;
    }

    public function showCreatePage() {
        if ($this->showPage == "createPage") {
            return true;
        }
        return false;
    }

    public function getErrorMessage() {
        return $this->errorMessage;
    }

    public function setErrorMessage($message) {
        $this->errorMessage = $message;
    }

    public function getMyApplications() {
        return $this->getFactory()->getApplicationPool()->getAllApplicationSettings();
    }

    /**
     * 
     * @return core_appmanager_data_ApplicationSettings
     */
    public function getApplicationSettings() {
        return $this->settings;
    }

    public function getSettingsId() {
        if (!$this->settingsId) {
            return "overview";
        }
        return $this->settingsId;
    }

    public function setApplicationSettingsId($id) {
        $this->settingsId = $id;
        $this->settings = $this->getFactory()->getApi()->getAppManager()->getApplication($this->settingsId);
    }

    public function hasArea($checkArea) {
        $settings = $this->getApplicationSettings();
        if (isset($settings->allowedAreas) && is_array($settings->allowedAreas)) {
            foreach ($settings->allowedAreas as $area) {
                if ($area == $checkArea) {
                    return true;
                }
            }
        }
        return false;
    }

    public function isAppType($type) {
        $settings = $this->getApplicationSettings();
        if ($settings->type == $type) {
            return true;
        }
        return false;
    }

    public function saveSettingsConfiguration($data) {
        $id = $data['id'];

        $this->setApplicationSettingsId($id);
        $settings = $this->getApplicationSettings();

        $settings->appName = $data['appname'];
        $settings->type = $data['apptype'];
        $settings->isPublic = $data['public'];
        $settings->isSingleton = $data['singleton'];
        $settings->renderStandalone = $data['standalone'];
        $settings->price = $data['price'];
        $settings->allowedAreas = array();
        if ($data['apparea_left'] == "true") {
            $settings->allowedAreas[] = "left";
        }
        if ($data['apparea_middle'] == "true") {
            $settings->allowedAreas[] = "middle";
        }
        if ($data['apparea_right'] == "true") {
            $settings->allowedAreas[] = "right";
        }
        $settings->pageSingelton = $data['pagesingelton'];
        $settings->trialPeriode = $data['trialPeriode'];
        
        
        $this->getFactory()->getApi()->getAppManager()->saveApplication($settings);
        $this->settings = $settings;
        

        echo "<script>";
        echo "thundashop.common.Alert('Configuration save','The configuration has been saved');";
        echo "</script>";
    }

    public function syncApplication() {
        $this->getFactory()->getApi()->getAppManager()->setSyncApplication($this->settingsId);
    }

    /**
     * 
     * @param type $appToCloneId
     * @return core_appmanager_data_ApplicationSettings
     */
    public function cloneApplication($appToCloneId, $setAsCloned = true) {
        $settings = $this->getFactory()->getApplicationPool()->getApplicationSetting($appToCloneId);
        $cloneId = $settings->id;
        $newApplication = $this->getFactory()->getApi()->getAppManager()->createApplication($settings->appName);
        $settings->id = $newApplication->id;
        if($setAsCloned) {
            $settings->clonedFrom = $cloneId;
        }
        $settings->ownerStoreId = $newApplication->ownerStoreId;
        $this->getFactory()->getApi()->getAppManager()->saveApplication($settings);
        $this->setApplicationSettingsId($settings->id);

        $namespaceSource = $this->getFactory()->convertUUIDtoString($cloneId);
        $dest = $this->getFactory()->convertUUIDtoString($settings->id);
        //Copy and synchronize the folder.
        $this->copyDirectory("../app/" . $namespaceSource, "../app/" . $dest, $namespaceSource, $dest);
        $this->getFactory()->getApi()->getPageManager()->swapApplication($cloneId, $settings->id);
        return $settings;
    }

    function copyDirectory($source, $destination, $oldNameSpace, $newNameSpace) {
        if (is_dir($source)) {
            //Paths which contains private is not to be cloned.
            if(stristr($source, "private")) {
                return;
            }
            
            @mkdir($destination);
            $directory = dir($source);
            while (FALSE !== ( $readdirectory = $directory->read() )) {
                if ($readdirectory == '.' || $readdirectory == '..') {
                    continue;
                }
                $PathDir = $source . '/' . $readdirectory;
                if (is_dir($PathDir)) {
                    $this->copyDirectory($PathDir, $destination . '/' . $readdirectory, $oldNameSpace, $newNameSpace);
                    continue;
                }
                copy($PathDir, $destination . '/' . $readdirectory);
                $this->changeNameSpace($destination . '/' . $readdirectory, $oldNameSpace, $newNameSpace);
            }

            $directory->close();
        } else {
            copy($source, $destination);
        }
    }

    public function changeNameSpace($pathDir, $oldNameSpace, $newNameSpace) {
        if (stristr($pathDir, ".php") || stristr($pathDir, ".phtml") || stristr($pathDir, ".js")) {
            $readedFile = file_get_contents($pathDir);
            $content = str_replace($oldNameSpace, $newNameSpace, $readedFile);
            file_put_contents($pathDir, $content);
        }
    }

    public function deleteApp($id) {
        $toDelete = $this->getFactory()->getApplicationPool()->getApplicationSetting($id);
        if($toDelete->clonedFrom) {
            $this->getFactory()->getApi()->getPageManager()->swapApplication($toDelete->id, $toDelete->clonedFrom);
        }
        $this->getFactory()->getApi()->getAppManager()->deleteApplication($id);
    }

    public function createApplication($appName) {
        //Clone the hello world application.
        $settings = $this->cloneApplication("b0db6ad0-8cc2-11e2-9e96-0800200c9a66", false);
        $settings->appName = $appName;
        $this->getFactory()->getApi()->getAppManager()->saveApplication($settings);
        
        $path = "../app/" . $this->getFactory()->convertUUIDtoString($settings->id);
        rename($path."/HelloWorld.php", $path."/".$appName.".php");
        rename($path."/javascript/HelloWorld.js", $path."/javascript/".$appName.".js");
        rename($path."/HelloWorld.png", $path."/".$appName.".png");
        
        $fileContent = file_get_contents($path."/".$appName.".php");
        $content = str_replace("class HelloWorld extends", "class ".$appName." extends", $fileContent);
        $content = str_replace("return \"My application\";", "return \"$appName\";", $content);
        file_put_contents($path."/".$appName.".php", $content);
        
        $this->getFactory()->getApi()->getAppManager()->setSyncApplication($settings->id);
        
        $this->settings = $settings;
    }

}

?>
