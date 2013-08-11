<?php

/**
 * Description of ApplicationManager
 *
 * @author ktonder
 */
class ApplicationManager extends FactoryBase {
    var $app;
    var $port;

    function setCurrentApp($app) {
        $this->app = $app;
    }
    
    function getCurrentApp() {
        return $this->app;
    }
    
    function __construct() {
        $configReader = new ConfigReader();
        $this->port = $configReader->getConfig("port");
    }

    public function getApplications() {
        return $this->getFactory()->getApplicationPool()->getAllApplicationSettings();
    }

    public function systemReloadPage() {
        
    }

    public function moveApplication() {
        $pageId = $this->getPage()->getId();
        $appId = $_POST['data']['appid'];
        $moveUp = $_POST['data']['direction'] == "up";

        $this->getFactory()->getApi()->getPageManager()->reorderApplication($pageId, $appId, $moveUp);
    }

    public function uploadImage() {
        foreach ($_FILES as $file) {
            $content = file_get_contents($file['tmp_name'][0]);
            $result['imageId'] = \FileUpload::storeFile($content);
            echo json_encode($result);
            return;
        }
    }

    public function previewApplication() {
        $appId = $_POST['data']['appId'];
        $api = IocContainer::getFactorySingelton()->getApi();
        $apps = $api->getPageManager()->getApplications();
        foreach ($apps as $app) {
            /* @var $app core_common_AppConfiguration */
            if ($app->id == $appId) {
                $toPrint = new $app->{'appName'}();
                /* @var $toPrint ApplicationBase */
                $toPrint->setConfiguration($app);
                $toPrint->preProcess();
                $this->isEditorMode();

                $tmp = $_SESSION['loggedin'];
                $_SESSION['loggedin'] = null;

                echo "<div class='app " . $app->{'appName'} . "'>";
                $toPrint->render();
                echo "</div>";
                $toPrint->postProcess();

                $_SESSION['loggedin'] = $tmp;
                break;
            }
        }
    }

    public function updatePageUserLevel() {
        $pageId = $this->getPage()->getId();
        $userLevel = $_POST['data']['userlevel'];
        $this->getFactory()->getApi()->getPageManager()->changePageUserLevel($pageId, $userLevel);
    }

    public function removeApplicationFromArea() {
        $id = $_POST['data']['appid'];
        $this->getFactory()->getApi()->getPageManager()->removeApplication($id, $this->getPage()->id);
        $this->callApplicationDeleted($id);
    }

    private function handleResponses($response) {
        foreach ($response as $resp) {
            if (EventHandler::isMessage("core_common_answer_ErrorMessage", $resp)) {
                return;
            }
        }
    }

    public function cloneapplication() {
        $appToCloneId = $_POST['data']['id'];
        $appMan = new ApplicationManagement();
        $settings = $appMan->cloneApplication($appToCloneId);
        $this->getFactory()->getApi()->getAppManager()->setSyncApplication($settings->id);
    }

    public function syncApp() {
        $appMan = new ApplicationManagement();
        $appMan->setApplicationSettingsId($_POST['data']['id']);
        $appMan->syncApplication();
        echo $this->__f("This application has been marked for synchronization");
        $appMan->display();
    }

    public function getStartedExtended() {
        $name = $_POST['data']['applicationName'];
        $area = $_POST['data']['applicationArea'];
        $appId = $_POST['data']['appSettingsId'];
        ?>
        <div class="content_holder">
            <div class="application">
                <div class="descriptionholder" gstype="form" method="addApplicationToArea" id="applicationform">
                    <input type="hidden" gsname="applicationName" value="<? echo $name; ?>">
                    <input type="hidden" gsname="applicationArea" value="<? echo $area; ?>">
                    <input type="hidden" gsname="appSettingsId" value="<? echo $appId; ?>">
                    <?
                    $app = new $name();
                    $app->getStartedExtended();
                    ?>
                    <span class="allFieldsNeedToBeFilled">* <? echo $this->__f("All fields need to be filled before you continue"); ?></span> 
                </div>
                <?
                $app->includefile("add_app_button", 'ApplicationManager');
                ?>
            </div>
        </div>
        <?
    }

    public function addApplicationToArea() {
        $area = $_POST['data']['applicationArea'];
        $settingsId = $_POST['data']['appSettingsId'];
        $pageId = $this->getFactory()->getPage()->getId();

        $pool = $this->getFactory()->getApplicationPool();
        $app = $pool->getApplicationSetting($settingsId);

        if ($app == null || $app->isSingleton) {
            return;
        }

        $application = $this->getFactory()->getApi()->getPageManager()->addApplicationToPage($pageId, $settingsId, $area);
        $this->invokeApplicationAdded($application);
    }

    private function invokeApplicationAdded($application) {
        $applications = array();
        $applications[] = $application;
        $this->getFactory()->getApplicationPool()->setApplicationInstances($applications);
        $app = $this->getFactory()->getApplicationPool()->getApplicationInstance($application->id);
        
        if ($app instanceof \ShipmentApplication || $app instanceof \PaymentApplication) {
            \HelperCart::clearSession(false);
        }
        
        $app->applicationAdded();
    }

    private function callApplicationDeleted($id) {
        $app = $this->getFactory()->getApplicationPool()->getApplicationInstance($id);
        $app->applicationDeleted();
    }

    public function setHelpRead() {
        $api = IocContainer::getFactorySingelton()->getApi();
        $api->getStoreManager()->setIntroductionRead();
    }

    public function sendFeedback() {
        $text = nl2br($_POST['data']['text']);
        $text .= "<hr>";
        $text .= "Shop name: " . $this->getFactory()->getStoreConfiguration()->shopName . "<br>";
        $text .= "Email: " . $this->getFactory()->getStoreConfiguration()->emailAdress . "<br>";

        $title = "Feedback from customer";
        $to = "post@getshop.com";
        $toName = "Getshop Support";
        $from = "system@getshop.com";
        $fromName = "Getshop system";

        $api = IocContainer::getFactorySingelton()->getApi();
        $api->getMessageManager()->sendMail($to, $toName, $title, $text, $from, $fromName);
    }

    public function showApplications() {
        $this->includefile('applicationSet');
    }

    public function displayAllTheemes() {
        $theme = new TheemeDisplayer();
        $theme->printDesigns();
    }

    public function loadApplicationPicker() {
        $this->includefile("applicationPicker");
    }

    public function displayAllUsers() {
        $userManager = new UserManager();
        $userManager->displayAllUsers();
    }

    public function addApplicationDirect() {
        if (!isset($_POST['data']['appId']))
            return;

        if ($this->getFactory()->getApplicationPool()->getApplicationInstance($_POST['data']['appId']) != null)
            return;

        $appConfiguration = $this->getFactory()->getApi()->getPageManager()->addApplication($_POST['data']['appId']);

        
        $namespace = $this->getFactory()->convertUUIDtoString($appConfiguration->appSettingsId);
        $appName = $namespace . "\\" . $appConfiguration->appName;

        $app = new $appName();
        if($app instanceof ThemeApplication) {
            $this->getFactory()->setConfigurationFlag("color", false);
            $this->getFactory()->setConfigurationFlag("bgimage", false);
        }
        
        if (method_exists($app, "renderStandalone")) {
            $pageManager = $this->getFactory()->getApi()->getPageManager();
            $pageManager->createPageWithId(5, "home", $appConfiguration->id . "_standalone");
            $pageManager->addExistingApplicationToPageArea($appConfiguration->id . "_standalone", $appConfiguration->id, "middle");
        }

        $this->invokeApplicationAdded($appConfiguration);

        die($appConfiguration->id);
    }

    public function displayIntroductionMovie() {
        echo '<iframe border="0" frameborder="0" height="567" scrolling="no" src="http://www.getshop.com/flowplayer/example/index.html" width="790"></iframe>';
    }

    public function enableSMS() {
        $password = $_POST['data']['password'];
        $toggle = $_POST['data']['toggle'];

        if ($toggle == "true") {
            $toggle = true;
        } else {
            $toggle = false;
        }
        $api = IocContainer::getFactorySingelton()->getApi();
        $api->getStoreManager()->enableSMSAccess($toggle, $password);
    }

    public function enableExtendedMode() {
        $password = $_POST['data']['password'];
        $toggle = $_POST['data']['toggle'];
        if ($toggle == "true") {
            $toggle = true;
        } else {
            $toggle = false;
        }
        $api = IocContainer::getFactorySingelton()->getApi();
        $api->getStoreManager()->enableExtendedMode($toggle, $password);
    }

    public function connectUserToPartner() {
        $password = $_POST['data']['password'];
        $userId = $_POST['data']['userId'];
        $partner = $_POST['data']['partner'];

        $api = IocContainer::getFactorySingelton()->getApi();
        $api->getGetShop()->addUserToPartner($userId, $partner, $password);
    }

    public function setVIS() {
        $password = $_POST['data']['password'];
        $toggle = $_POST['data']['toggle'];
        $this->getApi()->getStoreManager()->setVis($toggle, $password);
    }
    
    public function importApplication() {
        if (!isset($_POST['data']['list'])) {
            return;
        }
        $list = $_POST['data']['list'];
        $area = $_POST['data']['apparea'];

        $api = IocContainer::getFactorySingelton()->getApi();
        $pageId = $this->getPage()->id;
        
        foreach ($list as $appId) {
            $api->getPageManager()->addExistingApplicationToPageArea($pageId, $appId, $area);
        }
        
        $this->getFactory()->initPage();
    }

    public function displayColorPickers() {
        $this->includefile("GetShopColorPicker");
    }

    public function deleteApplication() {
        $appId = $_POST['data']['appId'];
        $app = $this->getFactory()->getApplicationPool()->getApplicationInstance($appId);

        if ($app instanceof \ShipmentApplication || $app instanceof \PaymentApplication) {
            \HelperCart::clearSession(false);
        }
        
        $this->getFactory()->getApi()->getPageManager()->deleteApplication($appId);

        if (method_exists($app, "renderStandalone"))
            $this->getFactory()->getApi()->getPageManager()->deletePage($appId);
    }

    public function getAllAddedApplications($appName) {
        $api = IocContainer::getFactorySingelton();
        $apps = $api->getApi()->getPageManager()->getApplications();
        $retval = array();
        foreach ($apps as $app) {
            if ($app->{'appName'} == $appName) {
                $retval[] = $app;
            }
        }

        return $retval;
    }
    
    public function syncapplication() {
        $id = $_POST['data']['id'];
        $this->getApi()->getAppManager()->setSyncApplication($id);
        echo "synching application with id: " . $id;
        $this->displayApplicationManagement();
    }

    public function displayApplicationManagement() {
        $appMan = new ApplicationManagement();
        if (isset($_POST['data']['id'])) {
            $appMan->setApplicationSettingsId($_POST['data']['id']);
        }
        $appMan->display();
    }

    public function deleteMyApplication() {
        $id = $_POST['data']['id'];
        $appMan = new ApplicationManagement();
        $appMan->deleteApp($id);
        $appMan->display();
    }

    public function saveApplication() {
        $appMan = new ApplicationManagement();
        $appName = $_POST['data']['appname'];
        if (!$appName) {
            $appMan->setErrorMessage($this->__f("Please specify a name"));
            $appMan->setShowCreatePage();
        } else {
            if (isset($_POST['data']['id'])) {
                $appMan->saveSettingsConfiguration($_POST['data']);
            } else {
                $appMan->createApplication($appName);
            }
        }
        $appMan->display();
    }

    public function loadpaymentinfo() {
        $this->includefile("applicationpayment");
    }

    public function removeAllInstances() {
        $appSettingsId = $_POST['data']['id'];
        $this->getApi()->getPageManager()->removeAllApplications($appSettingsId);
        $this->includefile("applicationpayment");
    }

    public function importExistingApplication() {
        $appSettingsId = $_POST['data']['appSettingsId'];
        $area = $_POST['data']['area'];
        $import = new ImportApplication($appSettingsId, $area);
        echo $import->getControlPanel();
    }
    
    /*
     * Dont remove this. it is used for ping!
     */
    public function ping() {
        
    }
    
    public function deleteStore() {
        $this->getFactory()->getApi()->getStoreManager()->delete();
    }
    
    public function displayPageSettings() {
        $this->includefile("pagesettings");
    }
    
    public function savePageDescription() {
        $desc = $_POST['data']['description'];
        $pageid = $this->getPage()->id;
        $this->getApi()->getPageManager()->setPageDescription($pageid, $desc);
    }
    
    public function updateSmallCart() {
        $small = new \ns_900e5f6b_4113_46ad_82df_8dafe7872c99\CartManager();
        $small->renderSmallCartView();
    }
    
    public function setDesignVariation() {
        if(isset($_POST['data']['bg'])) {
            $bg = $_POST['data']['bg'];
            $this->getFactory()->setConfigurationFlag("bgimage", $bg);
        } else {
            $bg = $_POST['data']['color'];
            $this->getFactory()->setConfigurationFlag("color", $bg);
        }
    }
}
?>