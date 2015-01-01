<?php

class ApplicationBase extends FactoryBase {
    public $pageSingleton = false;
    private $events = array();
    private $skinVariables;
    
    /** @var core_common_AppConfiguration */
    public $configuration;
    
    /** @var core_applicationmanager_Application */
    public $applicationSettings;
    
    public function getYoutubeId() {
        return false;
    }
    
    public function postProcess() {
    }

    public function getApplications() {
        
    }
    
    public function startAdminImpersonation($managerName, $function) {
        $userToImersonate = $this->getCredentials($managerName, $function);
        if ($userToImersonate != null) {
            $this->startImpersionation($userToImersonate[4], $userToImersonate[5]);
        }
    }
    
    private function getCredentials($managerName, $function) {
        $namespace = $this->getFactory()->convertUUIDtoString($this->getApplicationSettings()->id);
        $privateFolder = "../app/$namespace/private";
        $passwordFile = $privateFolder."/password";
        if (file_exists($passwordFile)) {
            $contents = file_get_contents($passwordFile);
            foreach (explode("\n", $contents) as $content) {
                
                $content2 = explode(";", $content);
                if (count($content2) < 3) {
                    continue;
                }
                
                if ($content2[0] == $managerName && $content2[1] == $function) {
                    return $content2;
                }
            }
        }
        
        return null;
    }
    
    public function requestAdminRight($managerName, $function, $descriptions) {
        $user = $this->getApi()->getUserManager()->requestAdminRight($managerName, $function, $this->getConfiguration()->id);
        $username = $user->username;
        $password = $user->password;
        $storeId = $this->getConfiguration()->storeId;
        $appId = $this->getConfiguration()->id;
        $saveString = "$managerName;$function;$storeId;$appId;$username;$password";
        $this->addToPasswordStore($managerName, $function, $saveString);
    }
    
    private function addToPasswordStore($managerName, $function, $saveString) {
        $namespace = $this->getFactory()->convertUUIDtoString($this->getApplicationSettings()->id);
        $privateFolder = "../app/$namespace/private";
        
        if (!file_exists($privateFolder)) {
            $success = @mkdir($privateFolder);
            if (!$success) {
                echo "UnExpected error 2001230919192039212451597 .. Contact support for more information";
                die();
            }
        }
        
        $passwordFile = $privateFolder."/password";
        if (!file($passwordFile)) {
            $success = touch($passwordFile);
            if (!$success) {
                echo "UnExpected error 2001230919192039212451598 .. Contact support for more information";
                die();
            }
        }
        
        $addContent = "";
        $contents = file_get_contents($passwordFile);
        $result = "";
        foreach (explode("\n", $contents) as $content) {
            $content2 = explode(";", $content);
            if (count($content2) < 3) {
                continue;
            }
            if ($content2[0] == $managerName && $content2[1] == $function) {
                continue;
            }
            $addContent .= $content."\n";
        }
        
        $addContent .= $saveString;
        file_put_contents($passwordFile, $addContent."\n");
    }
    
    /**
     * Returns a array with the following information
     * 
     * @return ['font-awesome-icon', 'javascript.function.to.ChartDrawer', 'title'];
     */
    public function getDashboardChart($year=false) {
        return false;
    }
    
    public function renderDashBoardWidget() {
        return false;
    }
    
    /**
     * This function should be overridden when 
     * an application need to ask for admin rights.
     * 
     * This function is executed after application has been added.
     */
    public function requestAdminRights() {
        
    }
    
    
    public function setSkinVariable($variableName, $defaultValue, $description) {
        $this->skinVariables[$variableName] = $defaultValue;
    }
    
    public function getSkinVariable($variableName) {
        return $this->skinVariables[$variableName];
    }
    
    public function setSkinVariables($skinVaribles) {
        $this->skinVariables = $skinVaribles;
    }
    
    public function getSkinVariables() {
        return $this->skinVariables;
    }
    
    public function preProcess() {
    }
    
    public function showDescription() {
        $this->includefile('applicationdescription', 'Common');
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
        if($this->isEditorMode() && !$this->getFactory()->isMobile()) {
            echo "<div class='mask'><div class='inner'>".$this->__f("Click to delete")."</div></div>";
            echo "<div class='order_mask'>";

            echo "<div class='inner'>";
            echo $this->__f("Drag to reorder");
            echo "</div>";
            echo "</div>";
        }
        
        echo "<div class='applicationinner'>";
        if($this->isEditorMode() && !$changeable) {
            if($this->hasWriteAccess()) {
                echo "<div class='application_settings inline gs_icon'><i class='fa fa-cog' style='font-size:18px;'></i></div>";
            }
        }
        $this->render();
        echo "</div>";
        echo "</div>";
    }
    
    public function renderBottomArea() {
        $changeable = !isset($this->applicationSettings) || $this->getApplicationSettings()->type == "SystemApplication" ? 'systemapplication' : '';
        $appSettingsId = $this->getApplicationSettings() ? $this->getApplicationSettings()->id : "";
        $id = isset($this->configuration) ? $this->configuration->id : "";
        $className = get_class($this);
        if(strrpos($className, "\\")) {
            $className = substr($className, strrpos($className, "\\")+1);
        }
        
        echo "<div appid='$id' app='" . $className . "' class='app bottom_app $changeable " . $className . "' appsettingsid='$appSettingsId'>";
        echo "<div class='applicationinner'>";
        if($this->isEditorMode()) {
            if($this->hasWriteAccess()) {
                echo "<div class='application_settings inline gs_icon'><i class='fa fa-cog' style='font-size:20px;'></i></div>";
            }
        }
        $this->render();
        echo "</div>";
        echo "</div>";
    }
    
    public function hasWriteAccess() {
        if(!$this->isEditorMode()) {
            return false;
        }
        $accesslist = $this->getUser()->applicationAccessList;
        $type = -1;
        if(isset($accesslist->{$this->applicationSettings->id})) {
            $type = $accesslist->{$this->applicationSettings->id};
        }
        if(sizeof($accesslist) == 0 || $type == 0 || $type == 2) {
            return true;
        }
        if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            return true;
        }
        return false;
    }
    
    public function hasReadAccess() {
        if(!$this->isEditorMode()) {
            return false;
        }
        $accesslist = $this->getUser()->applicationAccessList;
        $type = -1;
        if(isset($accesslist->{$this->applicationSettings->id})) {
            $type = $accesslist->{$this->applicationSettings->id};
        }
        if(sizeof($accesslist) == 0  || $type == 0 || $type == 1) {
            return true;
        }
        if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            return true;
        }
        return false;
    }
    
    public function getEvents() {
        return $this->events;
    }

    public final function getConfiguration() {
        return $this->configuration;
    }
   
    public function setConfiguration($configuration) {
        if(isset($configuration->settings)) {
            foreach ($configuration->settings as $config) {
                if (isset($config->type) && $config->type == "table") {
                    $config->value = json_decode($config->value, true);
                }
            }
        }

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
        if (isset($this->configuration)) {
            if(isset($this->configuration->settings->{$key}->value)) {
                return $this->configuration->settings->{$key}->value;
            }
        } else {
            if(isset($this->applicationSettings->settings->{$key}->value)) {
                return $this->applicationSettings->settings->{$key}->value;
            }
        }
        
        return null;
    }

    public function getSettings() {
        return $this->getConfiguration()->settings;
    }
    
    public function getAnswersImmediatly() {
        $answers = IocContainer::getFactorySingelton()->getEventHandler()->sendRequests($this->getEvents());
        $this->events = array();
        return $answers;
    }

    public function setConfigurationSetting($key, $value, $secure = false) {
        $setting = new core_common_Setting();
        $setting->id = $key;
        $setting->value = $value;
        $setting->name = $key;
        $setting->secure = $secure;
        
        if ($this->configuration) {
            $newSettings = array();
            $newSettings[] = $setting;
            $sendCore = $this->getApiObject()->core_common_Settings();
            $sendCore->settings = $newSettings; 
            $sendCore->appId = $this->getConfiguration()->id;
            $this->getApi()->getStoreApplicationInstancePool()->setApplicationSettings($sendCore);
            $this->configuration->settings->{$key} = $setting;
        } else if($this->applicationSettings) {
            $this->applicationSettings->settings->{$key} = $setting;
            $this->getApi()->getStoreApplicationPool()->setSetting($this->applicationSettings->id, $setting);
        }
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

    /**
     * @return core_usermanager_data_User
     */
    public function getUser() {
        return \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
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
//        if (!$success) {
            echo $this->__f("Just click the button below");
//        }
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


    public function gs_show_fragment() {
        $this->includefile($_POST['gss_fragment']);
    }
}
?>
