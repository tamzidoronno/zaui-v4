<?php

class ApplicationBase extends FactoryBase {
    public $pageSingleton = false;
    private $events = array();
    private $skinVariables;
    
    /** @var core_common_AppConfiguration */
    public $configuration;
    
    /** @var core_applicationmanager_ApplicationSettings */
    public $applicationSettings;
    
    public function getYoutubeId() {
        return false;
    }

    public function postProcess() {
    }

    public function setSkinVariable($variableName, $defaultValue, $description) {
        $this->skinVariables[$variableName] = $defaultValue;
    }
    
    public function getSkinVariable($variableName) {
        return $this->skinVariables[$variableName];
    }
    
    public function preProcess() {
    }
    
    public function showDescription() {
        $this->includefile('applicationdescription', 'Common');
    }

    public function setSticky() {
        $this->getApi()->getPageManager()->setApplicationSticky($this->getConfiguration()->id, $_POST['data']['sticky']);
    }

    public function renderApplication() {
        
        $changeable = !isset($this->applicationSettings) || $this->getApplicationSettings()->type == "SystemApplication" ? 'systemapplication' : '';
        $appSettingsId = $this->getApplicationSettings() ? $this->getApplicationSettings()->id : "";
        $id = isset($this->configuration) ? $this->configuration->id : "";
        
        if (isset($_GET['onlyShowApp']) && isset($id) && $id != $_GET['onlyShowApp']) {
            return;
        }
        
        $className = get_class($this);
        if(strrpos($className, "\\")) {
            $className = substr($className, strrpos($className, "\\")+1);
        }
        
        echo "<div appid='$id' app='" . $className . "' class='app $changeable " . $className . "' appsettingsid='$appSettingsId'>";
        if($this->isEditorMode()) {
            echo "<div class='mask'><div class='inner'>".$this->__("click_delete", "common")."</div></div>";
            echo "<div class='order_mask'>";

            echo "<div class='inner'>";
            echo "<span class='reorder_up'></span>";
            echo $this->__("click_order", "common");
            echo "<span class='reorder_down'></span>";
            echo "</div>";
            echo "</div>";
        }
        
        echo "<div class='applicationinner'>";
        $this->render();
        echo "</div>";
        echo "</div>";
    }

    public function getEvents() {
        return $this->events;
    }

    public final function getConfiguration() {
        return $this->configuration;
    }

    public function setConfiguration($configuration) {
        $this->configuration = $configuration;
    }

    /**
     * Gets the pagearea that this application
     * is added to.
     * 
     * @return PageArea
     */
    public function getPageArea() {
        $pageArea = null;
        if ($this->configuration) {
            $id = $this->configuration->id;
            $pageArea = $this->getPage()->getApplicationAreaByAppId($id);
        }
        return $pageArea;
    }
    
    public function getConfigurationSetting($key) {
        if(isset($this->configuration->settings->{$key}->value)) {
            return $this->configuration->settings->{$key}->value;
        }
        return null;
    }

    public function getAnswersImmediatly() {
        $answers = IocContainer::getFactorySingelton()->getEventHandler()->sendRequests($this->getEvents());
        $this->events = array();
        return $answers;
    }

    public function setConfigurationSetting($key, $value, $secure = false) {
        $newSettings = array();
        
        $setting = new core_common_Setting();
        $setting->id = $key;
        $setting->value = $value;
        $setting->secure = $secure;
        $newSettings[] = $setting;
        
        $sendCore = $this->getApiObject()->core_common_Settings();
        $sendCore->settings = $newSettings; 
        $sendCore->appId = $this->getConfiguration()->id;
        $this->getApi()->getPageManager()->setApplicationSettings($sendCore);
    }
    
    public function showConfigurationMenu() {
        $this->includefile('ApplicationConfiguration', 'Common');
        $this->includefile('ConfigurationMenu');
    }

    public function createUploadImageForm($event, $id, $title, $searchFrom, $appendImageToElementWithClass, $width = 100, $height = 100) {
        
        $scopeid = $_POST['scopeid'];
        //For production
        $cssInput = "position: absolute; right: 0px; top: 0px; z-index: 1; font-size: 460px; margin-top: 0px; margin-right: 0px; margin-bottom: 0px; margin-left: 0px; padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px; cursor: pointer; opacity:0;";
        $widthpx = $width."px;";
        $cssText = "overflow:hidden;position:relative; width:$widthpx";
        
        echo '
            <form style="' . $cssText . '" name="fileuploadform" id="fileupload" height="' . $height . '" width="' . $width . '" searchFrom="' . $searchFrom . '" imageTo="' . $appendImageToElementWithClass . '" class="imageUploader" style="" action="handler.php" method="POST" enctype="multipart/form-data">
                <div class="upload_image_text" >
                    <input type="file" id="file_selection" name="files[]" class="file_upload_descriptor" style="'.$cssInput.'"></input>
                    <input type="hidden" name="id" value="' . $id . '">
                    <input type="hidden" name="scopeid" value="' . $scopeid . '">';
                    if(isset($this->getConfiguration()->id)) {
                        echo '<input type="hidden" name="core[appid]" value="' . $this->getConfiguration()->id . '">';
                    }
                    echo '<input type="hidden" name="event" value="' . $event . '">
                    <input type="hidden" name="synchron" value="true">' . $title . '
                </div>
            </form>
        ';
    }

    
    
    public function isAvailable() {
        
        $hasImage = file_exists("../app/" . strtolower(get_class($this)) . "/".$this->getName().".png");
        $hasName = ($this->getName() != null);

        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() && \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->username == "aaa")
            return true;

        return ($hasImage && $hasName);
    }

    public function getAvailablePositions() {
        if (isset($this->singleton) && $this->singleton)
            return "";
        
        return "right middle left header footer";
    }
    
    /**
     * @Event 
     * 
     * This event will be invoked before application is deleted.
     */
    public function applicationDeleted() {}
    
    /**
     * @Event 
     * 
     * This event will be invoked before application is deleted.
     */
    public function applicationAdded() {}
    
    /**
     * @Event
     * 
     * This function is called if the application is a singleton
     * and it is added to the page.
     * 
     * It will only be fired on page load, and when singleton is being added/removed.
     */
    public function renderBottom() {
    }
    
    public function getStarted() {
        $success = $this->includefile("getstarted", null, false);
        if (!$success) {
            echo $this->__f("Just click the button below");
        }
    }
    
    /**
     * 
     * @return core_applicationmanager_ApplicationSettings
     */
    public function getApplicationSettings() {
        return $this->applicationSettings;
    }

    public function setApplicationSettings($applicationSettings) {
        $this->applicationSettings = $applicationSettings;
    }
}
?>
