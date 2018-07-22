<?php

class ApplicationBase extends FactoryBase {
    public $pageSingleton = false;
    private $events = array();
    private $skinVariables;
    private $cell;
    private $depth;
    private $wrappedAppes;
    private $parentWrappedApp;
    private $wrapAppRefernce;
    
    /** @var core_common_AppConfiguration */
    public $configuration;
    
    /** @var core_applicationmanager_Application */
    public $applicationSettings;
    
    public function getYoutubeId() {
        return false;
    }
    
    public function sortGetShopTable() {
        
    }
    
    public function clearSessionOnIdentifierForTable() {
        $identifier = $_POST['data']['functioname'];
        unset($_SESSION['gs_moduletable_'.$identifier]);
    }
    
    public function indexList($list) {
        $list2 = array();
        
        if (!is_array($list)) {
            $list = array();
        }
        
        foreach($list as $l) {
            $list2[$l->id] = $l;
        }
        return $list2;
    }
    
    public function autosavetext() {
        $key = 'autosaved_'.$this->getConfiguration()->id . "_" . $_POST['data']['name'];
        $_SESSION[$key] = $_POST['data']['value'];
    }
    
    public function clearAutoSavedText() {
        foreach($_SESSION as $key => $value) {
            if(stristr($key, "autosaved_")) {
                unset($_SESSION[$key]);
            }
        }
    }

    public function printCell($areaname) {
        $page = new \Page($this->getPage()->javapage, $this->getFactory());
        $pageId = $this->getPage()->javapage->id;
        $cell = $this->getApi()->getPageManager()->getLooseCell($pageId, $areaname);
        
        $page->printCell($cell, 0, 1, 0, false, null, false);
    }
    
    public function getAutoSaved($name) {
        $key = 'autosaved_'.$this->getConfiguration()->id . "_" . $name;
        if(isset($_SESSION[$key])) {
            return $_SESSION[$key];
        }
        return "";
    }
    
    public function postProcess() {
    }

    public function getApplications() {
        
    }
    
    public function getAppInstanceId() {
//        print_r($this->getConfiguration());
        return $this->getConfiguration()->id;
    }
    
    public function startAdminImpersonation($managerName, $function) {
        $userToImersonate = $this->getCredentials($managerName, $function);
        if ($userToImersonate != null) {
            $this->startImpersionation($userToImersonate[4], $userToImersonate[5]);
        }
    }
    
    private function getCredentials($managerName, $function) {
        $namespace = $this->getFactory()->convertUUIDtoString($this->getApplicationSettings()->id);
        $privateFolder = "../../private/$namespace/private";
        $passwordFile = $privateFolder."/password";
        if (file_exists($passwordFile)) {
            $contents = file_get_contents($passwordFile);
            foreach (explode("\n", $contents) as $content) {
                
                $content2 = explode(";", $content);
                if (count($content2) < 3) {
                    continue;
                }
                
                if ($content2[0] == $managerName 
                        && $content2[1] == $function 
                        && $content2[2] == $this->getFactory()->getStore()->id 
                        && $content2[3] == $this->getConfiguration()->id) {
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
        $this->addToPasswordStore($managerName, $function, $saveString, $storeId, $appId);
    }
    
    private function addToPasswordStore($managerName, $function, $saveString, $storeId, $appId) {
        $namespace = $this->getFactory()->convertUUIDtoString($this->getApplicationSettings()->id);
        $privateFolder = "../../private/$namespace/private";
        
        if (!file_exists($privateFolder)) {
            $success = mkdir($privateFolder, 0777, true);
            if (!$success) {
                echo "UnExpected error 2001230919192039212451597 .. Contact support for more information: ".$privateFolder;
                die();
            }
        }
        
        $passwordFile = $privateFolder."/password";
        if (!file_exists($passwordFile)) {
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
            if ($content2[0] == $managerName && $content2[1] == $function && $appId == $content2[3] && $storeId == $content2[2]) {
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
    
    public function convertToJavaDate($time) {
        $res = date("c", $time);
        return $res;
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
    
    public function showSettings() {
        return $this->includefile("settings");
    }
    
    public function saveAppInstanceSettings() {
        foreach($_POST['data'] as $key => $val) {
            if(!stristr($key, "setting_")) {
                continue;
            }
            $key = str_replace("setting_", "", $key);
            $this->setConfigurationSetting($key, $val);
        }
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

    public function renderApplication($appNotAddedToPage=false, $fromApplication=false) {
        $changeable = '';
        $appSettingsId = $this->getApplicationSettings() ? $this->getApplicationSettings()->id : "";
        $id = isset($this->configuration) ? $this->configuration->id : "";
        $callbackInstance = "";
        $normalId = "";
        
        if ($appNotAddedToPage) {
            $id = get_class($this);
            $arrId = explode("\\", $id);
            $normalId = str_replace("_", "-", str_replace("ns_", "",$arrId[0]));
        }
        
        $fromId = "";
        if ($fromApplication) {
            $fromId = isset($fromApplication->configuration) ? $fromApplication->configuration->id : "";
            if(!$fromId) {
                $fromId = get_class($fromApplication);
            }
            $callbackInstance = " fromapplication='$fromId' ";
        }
        
        if (isset($_GET['onlyShowApp']) && isset($id) && $id != $_GET['onlyShowApp']) {
            return;
        }
        
        $className = get_class($this);
        if(strrpos($className, "\\")) {
            $className = substr($className, strrpos($className, "\\")+1);
            $_SESSION['cachedClasses'][$fromId] = get_class($this);
            $_SESSION['cachedClasses'][$id] = get_class($this);
        } else {
            $_SESSION['cachedClasses'][$fromId] = $fromId;
            $_SESSION['cachedClasses'][$id] = $id;
        }
        
        if(!isset($_SESSION['cachedClasses'])) {
            $_SESSION['cachedClasses'] = array();
        }

        echo "<div $callbackInstance appid='$id' ".$this->getExtraAttributesToAppArea()." app='" . $className . "' class='app $changeable " . $className . "' appid2='$normalId' appsettingsid='$appSettingsId'>";
        if($this->isEditorMode() && !$this->getFactory()->isMobile()) {
            echo "<div class='mask'><div class='inner'>".$this->__f("Click to delete")."</div></div>";
            echo "<div class='order_mask'>";

            echo "</div>";
        }
        
        echo "<div class='applicationinner'>";
        if($this->isEditorMode() && !$changeable && !$this->getFactory()->isMobile()) {
            if($this->hasWriteAccess()) {
                echo "<div class='application_settings inline gs_icon'><i class='fa fa-cog' style='font-size:18px;'></i></div>";
            }
        }
        $this->render();
        echo "</div>";
        echo "</div>";
    }
    
    public function renderApp($appNotAddedToPage=false, $fromApplication=false) {
        $changeable = '';
        $appSettingsId = $this->getApplicationSettings() ? $this->getApplicationSettings()->id : "";
        $id = isset($this->configuration) ? $this->configuration->id : "";
        $callbackInstance = "";
        
        if ($appNotAddedToPage) {
            $id = get_class($this);
        }
        $fromId = "";
        if ($fromApplication) {
            $fromId = isset($fromApplication->configuration) ? $fromApplication->configuration->id : "";
            if(!$fromId) {
                $fromId = get_class($fromApplication);
            }
            $callbackInstance = " fromapplication='$fromId' ";
        }
        
        if (isset($_GET['onlyShowApp']) && isset($id) && $id != $_GET['onlyShowApp']) {
            return;
        }
        
        $className = get_class($this);
        if(strrpos($className, "\\")) {
            $className = substr($className, strrpos($className, "\\")+1);
            $_SESSION['cachedClasses'][$fromId] = get_class($this);
            $_SESSION['cachedClasses'][$id] = get_class($this);
        } else {
            $_SESSION['cachedClasses'][$fromId] = $fromId;
            $_SESSION['cachedClasses'][$id] = $id;
        }
        
        if(!isset($_SESSION['cachedClasses'])) {
            $_SESSION['cachedClasses'] = array();
        }

        $hasInstance = $this->getConfiguration() ? "yes" : "no";
        echo "<div $callbackInstance appid='$id' hasinstance='$hasInstance' app='" . $className . "' class='app $changeable " . $className . "' appsettingsid='$appSettingsId'>";

        echo "<div class='applicationinner'>";
        echo "<center><i class='fa fa-spinner fa-spin'></i></center>";
        echo "</div>";
        echo "</div>";
    }
    
    public function getExtraAttributesToAppArea() {
        return "";
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
            if($this->hasWriteAccess() && !$this->getFactory()->isMobile()) {
                echo "<div class='application_settings inline gs_icon'><i class='fa fa-cog' style='font-size:20px;'></i></div>";
            }
        }
        $this->render();
        echo "</div>";
        echo "</div>";
    }
    
    public function hasWriteAccess() {
        if($this->getFactory()->getStore()->id == "f2d0c13c-a0f7-41a7-8584-3c6fa7eb68d1") {
            return $this->isEditorMode();
        }
        
        if(!$this->isEditorMode()) {
            return false;
        }
        $accesslist = @$this->getUser()->applicationAccessList;
        $type = -1;
        if(isset($this->applicationSettings) && isset($accesslist->{$this->applicationSettings->id})) {
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
    
    public function getGlobalConfigurationSetting($key) {
        if(isset($this->applicationSettings->settings->{$key})) {
            return $this->applicationSettings->settings->{$key}->value;
        }
        return null;
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

    public function setCell($cell) {
        $this->cell = $cell;
    }
    
    public function getCell() {
        return $this->cell;
    }
    
    public function setDepth($depth) {
        $this->depth = $depth;
    }
    
    public function getDepth() {
        return $this->depth;
    }

    public function gs_show_fragment() {
        $this->includefile($_POST['gss_fragment']);
    }
    
    public function formatTimeToJavaDate($time) {
        return date("M d, Y h:m:s A", $time);
    }
    
    public function formatJavaDateToTime($time) {
        return strtotime($time);
    }
    
    public function wrapApp($applicationId, $referenceName) {
        $appInstance = $this->getWrappedApp($applicationId, $referenceName);
        
        if (!$appInstance) {
            return;
        }

        $appInstance->renderApplication();
    }
    
    public function setParentWrappedApp($app, $reference) {
        $_SESSION["parent_wrapped_app".$this->getAppInstanceId()] = $app->getAppInstanceId();
        $_SESSION["parent_wrapped_app_ref".$this->getAppInstanceId()] = $reference;
    }
    /**
     * 
     * @param type $referenceName
     * @return ApplicationBase
     */
    public function getWrappedApp($applicationId, $referenceName) {
        
        $key = $this->getConfigurationSetting($referenceName);
        if ($key == null) {
            $app = $this->getApi()->getStoreApplicationInstancePool()->createNewInstance($applicationId);
            if ($app) {
                $this->setConfigurationSetting($referenceName, $app->id);
            }
        }
        
        $instanceId = $this->getConfigurationSetting($referenceName);
        if (!$instanceId) {
            return null;
        }
        
        if (!$this->wrappedAppes) {
            $this->wrappedAppes = new stdClass();
        }
        
        if (isset($this->wrappedAppes->{$instanceId})) {
            return $this->wrappedAppes->{$instanceId};
        }
        
        $app = $this->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($instanceId);
        $appInstance = $this->getFactory()->getApplicationPool()->createAppInstance($app);
        $appInstance->wrapAppRefernce = $referenceName;
        
        $appInstance->setParentWrappedApp($this, $referenceName);
                
        $this->wrappedAppes->{$instanceId} = $appInstance;
        return $appInstance;
    }
    
    /**
     * This is not an ideal solution. 
     * The reason is that if a wrapped app is printed 
     * and the user does not leave the and then tru to use the 
     * wrapped app, it might be shown as a difference instance :(
     * 
     * The id should be stored more permanantly in app configuration or something
     */
    public function getParentWrappedApplication() {
        if (isset($_SESSION["parent_wrapped_app".$this->getAppInstanceId()]) && !$this->parentWrappedApp) {
            $app = $this->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($_SESSION["parent_wrapped_app".$this->getAppInstanceId()]);
            $this->parentWrappedApp = $this->getFactory()->getApplicationPool()->createAppInstance($app);
        }
        
        return $this->parentWrappedApp;
    }
    
    public function getModalVariable($variableName) {
        if (isset($_SESSION['gs_currently_showing_rightWidget'])) {
            return @$_SESSION['modal_variable_'.$_SESSION['gs_currently_showing_rightWidget']][$variableName];
        }
        
        if (!isset($_SESSION['gs_currently_showing_modal'])) {
            return null;
        }
        
        return @$_SESSION['modal_variable_'.$_SESSION['gs_currently_showing_modal']][$variableName];
    }
    
    public function changeModalVariable($variableName, $value) {
        if (isset($_SESSION['gs_currently_showing_rightWidget'])) {
            @$_SESSION['modal_variable_'.$_SESSION['gs_currently_showing_rightWidget']][$variableName] = $value;
        }
        @$_SESSION['modal_variable_'.$_SESSION['gs_currently_showing_modal']][$variableName] = $value;
    }
    
    /**
     * 
     * Notify parent if exists and method exists
     * 
     * @param String $functionName
     * @param [] $args
     */
    public function notifyParent($method_name, $args) {
        $app = $this->getParentWrappedApplication();
        
        if (isset($_SESSION["parent_wrapped_app_ref".$this->getAppInstanceId()])) {
            $this->wrapAppRefernce = $_SESSION["parent_wrapped_app_ref".$this->getAppInstanceId()];
        }
        
        if ($app && method_exists($app, $method_name)) {
            $args[] = $this;
            call_user_func_array(array($app, $method_name), $args);
        }
    }
    
    public function wrapContentManager($referenceName, $defaultText) {
        if (!$this->getConfigurationSetting($referenceName)) {
            $app = $this->getWrappedApp("320ada5b-a53a-46d2-99b2-9b0b26a7105a", $referenceName);
            $_POST['data']['content'] = $defaultText;
            $app->saveContent();
        }
        
        $this->wrapApp("320ada5b-a53a-46d2-99b2-9b0b26a7105a", $referenceName);
    }
    
    public function getAvailableProductTemplates() {
        return ["ecommerce_product_template_1"];
    }
    
    public function getWrapAppReference() {
        return $this->wrapAppRefernce;
    }
    
    public function getStdErrorObject() {
        $obj = new \stdClass();
        $obj->fields = new stdClass();
        $obj->gsfield = new stdClass();
        
        $obj->version = 2;
        $obj->appName = @$this->getApplicationSettings()->appName;
        
        if (!isset($this->getApplicationSettings()->appName) && isset($_POST['core']['appid'])) {
            $arr = explode("\\", $_POST['core']['appid']);
            $obj->appName = $arr[1];
        }
        
        return $obj;
    }
    
    public function doError(\stdClass $obj) {
        http_response_code(400);
        echo json_encode($obj);
        die();
    }
    
    public function closeModal() {
        $appManager = new ApplicationManager();
        $appManager->closemodal();
    }
    
    public function getSelectedMultilevelDomainName() {
        if (!isset($_SESSION['selected_multilevel_name'])) {
            $_SESSION['selected_multilevel_name'] = $this->getApi()->getStoreManager()->getMyStore()->defaultMultilevelName;
        }
        
        if (!$_SESSION['selected_multilevel_name']) {
            $_SESSION['selected_multilevel_name'] = "default";
        }
        
        return $_SESSION['selected_multilevel_name'];
    }
    
    public function changeMultiLevelName($name) {
        $_SESSION['selected_multilevel_name'] = $name;
    }

    
    public function setGetShopTableRowId() {
        $_SESSION['gs_moduletable_'.$_POST['data']['functionName']] = $_POST['data'];
        die();
    }
    
    public function getCallBackApp() {
        if (!isset($_POST['core']['fromappid'])) {
            return null;
        }
        if(stristr($_POST['core']['fromappid'], "\\")) {
            $callbackApp = new $_POST['core']['fromappid']();
            return $callbackApp;
        }
        
        $callbackApp = $this->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($_POST['core']['fromappid']);
        $instance = $this->getFactory()->getApplicationPool()->createAppInstance($callbackApp);
        return $instance;
    }

    public function handleGetShopModulePaging() {
        
    }
    
    public function gsLog() {
        $appName = get_class($this);
        $appNameArr = explode("\\", $appName);
        $appName = $appNameArr[1];
        $this->getApi()->getTrackerManager()->logTracking($appName, $_POST['data']['gslog_type'], $_POST['data']['gslog_value'], $_POST['data']['gslog_description']);
    }
    
    public function gsAlsoUpdate() {
        return array();
    }
}
?>