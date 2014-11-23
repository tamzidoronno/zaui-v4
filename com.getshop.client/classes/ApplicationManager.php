<?php

/**
 * Description of ApplicationManager
 *
 * @author ktonder
 */
class ApplicationManager extends FactoryBase {

    var $app;
    var $port;
    var $subscriptions;

    function setCurrentApp($app) {
        $this->app = $app;
    }

    function switchArea() {
        $pageId = $this->getPage()->id;
        $fromArea = $_POST['data']['area'];
        $toArea = $_POST['data']['newarea'];
        $this->getApi()->getPageManager()->switchApplicationAreas($pageId, $fromArea, $toArea);
    }

    function showThemeSelection() {
        include("initdata/themeselection.phtml");
    }

    function updateCarouselConfig() {
        $cellId = $_POST['data']['cellid'];
        $pageId = $this->getPage()->javapage->id;
        
        $config = new core_pagemanager_data_CarouselConfig();
        $config->height = $_POST['data']['height'];
        $config->time = $_POST['data']['timer'];
        $config->type = $_POST['data']['type'];
        $this->getApi()->getPageManager()->setCarouselConfig($pageId, $cellId, $config);
    }
    
    function saveColChanges() {
        $cellid = $_POST['data']['cellid'];
        $pageid = $this->getPage()->javapage->id;
        $styles = "notset";
        $stylesInner = "notset";
        if(isset($_POST['data']['styles'])) {
            $styles = $_POST['data']['styles'];
        }
        
        $this->getApi()->getPageManager()->setStylesOnCell($pageid, $cellid, $styles, "", -1);
        
        if(isset($_POST['data']['colsizes'])) {
            $colsizes = $_POST['data']['colsizes'];
            foreach($colsizes as $cellid => $width) {
                $this->getApi()->getPageManager()->setStylesOnCell($pageid, $cellid, "notset", "notset", $width);
            }
        }
    }
    
    function saveBackgroundImage() {
        $data = $_POST['data']['data'];
        $data = substr($data, strrpos($data, ";base64,")+8);
        $content = base64_decode($data);
        $imgId = \FileUpload::storeFile($content);
        echo $imgId;
    }
    
    function updateCellName() {
        $id = $_POST['data']['cellid'];
        $name = $_POST['data']['name'];
        $page = $this->getPage()->javapage->id;
        $this->getApi()->getPageManager()->setCellName($page, $id, $name);
    }
    
    function operateCell() {
        $type = $_POST['data']['type'];
        $cellId = $_POST['data']['cellid'];
        $before = null;
        $area = "";
        if(isset($_POST['data']['area'])) {
            $area = $_POST['data']['area'];
        }
        if(isset($_POST['data']['before'])) {
            $before = $_POST['data']['before'];
        }
        switch ($type) {
            case "movedown":
                $this->getApi()->getPageManager()->moveCell($this->getPage()->javapage->id, $cellId, false);
                break;
            case "moveup":
                $this->getApi()->getPageManager()->moveCell($this->getPage()->javapage->id, $cellId, true);
                break;
            case "delete":
                $this->getApi()->getPageManager()->dropCell($this->getPage()->javapage->id, $cellId);
                break;
            case "addrotate":
                $this->getApi()->getPageManager()->addLayoutCell($this->getPage()->javapage->id, $cellId, $before, "ROTATING", $area);
                break;
            case "addtab":
                $this->getApi()->getPageManager()->addLayoutCell($this->getPage()->javapage->id, $cellId, $before, "TAB", $area);
                break;
            case "addbefore":
            case "addafter":
                $cellId = $this->getApi()->getPageManager()->addLayoutCell($this->getPage()->javapage->id, $cellId, $before, "HORIZONTAL", $area);
                $_GET['gseditcell'] = $cellId;
                break;
            case "addvertical":
                $this->getApi()->getPageManager()->addLayoutCell($this->getPage()->javapage->id, $cellId, $before, "VERTICAL", $area);
                break;
            case "addhorizontal":
                $this->getApi()->getPageManager()->addLayoutCell($this->getPage()->javapage->id, $cellId, $before, "HORIZONTAL", $area);
                break;
        }
    }

    function setProductFromProductPicker() {
        $productIds = $_POST['data']['productids'];
        if (isset($_POST['data']['config']['type']) && $_POST['data']['config']['type'] == "delete") {
            foreach ($productIds as $productId) {
                $this->getApi()->getProductManager()->removeProduct($productId);
            }
        } else {
            $productId = $productIds[0];
            $settingsid = "b741283d-920d-460b-8c08-fad5ef4294cb";
            $pageId = $this->getFactory()->getPage()->getId();
            $area = $_POST['data']['config']['area'];
            $application = $this->getFactory()->getApi()->getPageManager()->addApplicationToPage($pageId, $settingsid, $area);
            $app = new ns_b741283d_920d_460b_8c08_fad5ef4294cb\ProductWidget();
            $app->setConfiguration($application);
            $app->setConfigurationSetting("productid", $productId);
            $this->getFactory()->clearCachedPageData();
            $this->getFactory()->initPage();
        }
    }

    function savePageDetails() {
        $page_css = $_POST['data']['page_css'];
        $global_css = $_POST['data']['global_css'];
        $page_text = $_POST['data']['page_text'];

        $store = $this->getFactory()->getStore();
        $store->configuration->customCss = $global_css;
        $this->getApi()->getStoreManager()->saveStore($store->configuration);

        $page = $this->getFactory()->getPage()->backendPage;
        $page->customCss = $page_css;
        $page->description = $page_text;
        $this->getApi()->getPageManager()->savePage($page);
    }

    function validateArea($areas, $area, $size, $type, $app = null) {
        if (!in_array($size, $areas) && $size != "xlarge" || sizeof($areas) == 0) {
            return false;
        }
        if (in_array("right", $areas) || in_array("left", $areas)) {
            if (in_array("right", $areas)) {
                if (!stristr("right", $area)) {
                    return false;
                }
            } else {
                if (!stristr("left", $area)) {
                    return false;
                }
            }
        }
        if ($type != "standard" && !in_array($type, $areas)) {
            return false;
        }

        return true;
    }

    function getCurrentApp() {
        return $this->app;
    }

    function saveStyling() {
        $layout = $this->getPage()->getLayout();
        /* @var $row core_pagemanager_data_RowLayout */
        $row = ((int) $_POST['data']['row']) - 1;
        $outer = "";
        if (isset($_POST['data']['gs_outer'])) {
            $outer = $_POST['data']['gs_outer'];
        }
        $inner = "";
        if (isset($_POST['data']['gs_inner'])) {
            $inner = $_POST['data']['gs_inner'];
        }

        $layout->rows[$row]->innercss = $inner;
        $layout->rows[$row]->outercss = $outer;
        $this->getPage()->setLayout($layout);
    }

    public function getSubscriptions() {
        return $this->subscriptions;
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

    public function CookieAccepted() {
        $_SESSION['getshop_cookie_accepted'] = true;
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

    public function saveProductImage() {
        $content = $_POST['data']['data'];
        $content = base64_decode(str_replace("data:image/png;base64,", "", $content));
        $imgId = \FileUpload::storeFile($content);
        echo $imgId;
    }

    public function saveColorAttribute() {
        $color = $_POST['data']['color'];
        $path = $_POST['data']['path'];
        $type = $_POST['data']['type'];

        if ($path == "body" && $type == "background-color") {
            $this->getFactory()->setConfigurationFlag("bgimage", false);
        }


        $config = json_decode($this->getFactory()->getConfigurationFlag("getshop_colors"), true);
        if (!$config) {
            $config = array();
        } else {
            foreach ($config as $index => $entry) {
                if ($entry['path'] == $path && $entry['type'] == $type) {
                    unset($config[$index]);
                }
            }
        }

        $entry = array();
        $entry['color'] = $color;
        $entry['path'] = $path;
        $entry['type'] = $type;

        $config[] = $entry;
        $this->getFactory()->setConfigurationFlag("getshop_colors", json_encode($config));
        $this->loadColorAttributes();
    }

    public function loadColorAttributes() {
        $config = json_decode($this->getFactory()->getConfigurationFlag("getshop_colors"), true);
        echo "<style id='set_colors'>";
        foreach ($config as $name => $entry) {
            echo $entry['path'] . " {" . $entry['type'] . " : #" . $entry['color'] . " }\n";
        }
        echo "</style>";
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
    
    public function startEditRow() {
        $cellid = $_POST['data']['cellid'];
        $_SESSION['gseditcell'] = $cellid;
    }

    public function updatePageUserLevel() {
        $pageId = $this->getPage()->getId();
        $userLevel = $_POST['data']['userlevel'];
        $this->getFactory()->getApi()->getPageManager()->changePageUserLevel($pageId, $userLevel);
    }

    public function removeApplicationFromArea() {
        $id = $_POST['data']['cellid'];
        $this->getFactory()->getApi()->getPageManager()->removeAppFromCell($this->getPage()->javapage->id, $id);
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
        echo $this->__o("This application has been marked for synchronization");
        $appMan->display();
    }

    public function getStartedExtended() {
        $name = $_POST['data']['applicationName'];
        $area = $_POST['data']['applicationArea'];
        $appId = $_POST['data']['appSettingsId'];
        $extrainfo = $_POST['data']['extrainfo'];
        ?>
        <div class="content_holder">
            <div class="application">
                <div class="descriptionholder" gstype="form" method="addApplicationToArea" id="applicationform">
                    <input type="hidden" gsname="applicationName" value="<? echo $name; ?>">
                    <input type="hidden" gsname="applicationArea" value="<? echo $area; ?>">
                    <input type="hidden" gsname="appSettingsId" value="<? echo $appId; ?>">
                    <input type="hidden" gsname="extrainfo" value="<? echo $extrainfo; ?>">
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

    public function loadProductPicker() {
        $product = new ns_06f9d235_9dd3_4971_9b91_88231ae0436b\Product();
        $product->loadPicker();
    }

    public function addApplicationToCell() {
        $cellId = $_POST['data']['cellId'];
        $appId = $_POST['data']['appId'];

        $application = $this->getFactory()->getApi()->getPageManager()->addApplication($appId, $cellId);
        $this->invokeApplicationAdded($application);
    }

    private function invokeApplicationAdded($application) {
        $app = $this->getFactory()->getApplicationPool()->getApplicationInstance($application->id);
        if(method_exists($app, "requestAdminRights")) {
            $app->requestAdminRights();
        }

        if ($app instanceof \ShipmentApplication || $app instanceof \PaymentApplication) {
            \HelperCart::clearSession(false);
        }

        $app->applicationAdded();
    }

    private function callApplicationDeleted($id) {
        $app = $this->getFactory()->getApplicationPool()->getApplicationInstance($id);
        $app->applicationDeleted();
    }

    public function setRowLayoutSortOrder() {
        if (isset($_POST['data']['layoutmode'])) {
            $pb = $this->getPage()->loadPageBuilder();
            $pb->activateBuildLayoutMode();
            $pb->reorderRows($_POST['data']['sortorder']);
            $pb->saveBuildLayout($pb->getLayout());
            echo "Need to sort for layout mode";
        }
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

    public function showPageLayoutSelection() {
        echo "<div class='gs_showPageLayoutSelection'>";
        $this->includefile("applicationSelectionLayout");
        echo "</div>";
    }

    public function showApplications() {
        $this->subscriptions = $this->getFactory()->getApi()->getAppManager()->getAllApplicationSubscriptions(false);
        $this->includefile('applicationSet');
    }

    public function addCell() {
        $incell = $_POST['data']['incell'];
        $aftercell = $_POST['data']['aftercell'];
        $cellId = $this->getApi()->getPageManager()->addLayoutCell($this->getFactory()->getPage()->getId(), $incell, $aftercell, true, "");
        $_SESSION['gseditcell'] = $cellId;
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
        if ($app instanceof ThemeApplication) {
            $this->getFactory()->setConfigurationFlag("color", false);
            $this->getFactory()->setConfigurationFlag("bgimage", false);
            $this->getFactory()->setConfigurationFlag("getshop_colors", json_encode(array()));
        }

        if (method_exists($app, "renderStandalone")) {
            $pageManager = $this->getFactory()->getApi()->getPageManager();
            $pageId = $appConfiguration->id . "_standalone";
            $page = $pageManager->createPageWithId(5, "", $pageId);
            $page->hideHeader = true;
            $page->hideFooter = true;
            $pageManager->savePage($page);
            $pageManager->addExistingApplicationToPageArea($pageId, $appConfiguration->id, "main_1");
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

    public function toggleDeepfreeze() {
        $password = $_POST['data']['password'];
        $store = $this->getFactory()->getStore();
        if ($store != null && $store->isDeepFreezed) {
            $this->getFactory()->getApi()->getStoreManager()->setDeepFreeze(false, $password);
        } elseif ($store != null && !$store->isDeepFreezed) {
            $this->getFactory()->getApi()->getStoreManager()->setDeepFreeze(true, $password);
        }
    }

    public function toggleApplicationSticky() {
        $appInstanceId = $_POST['data']['appInstanceId'];
        $toggle = $_POST['data']['toggle'];
        $this->getApi()->getPageManager()->setApplicationSticky($appInstanceId, $toggle);
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

        $this->callApplicationDeleted($appId);
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

    public function selectPredefinedData() {
        $this->getFactory()->reloadStoreObject();
        if ($_POST['data']['target'] !== "default") {
            $page = $this->getApi()->getPageManager()->createPage(-1, "");
        } else {
            $page = $this->getPage()->backendPage;
        }
        $pb = new PageBuilder(null, null, $page);
        $page->type = -1;
        $page->layout = $pb->buildPredefinedPage(json_decode($_POST['data']['config'], true));
        $page->pageTag = $_POST['data']['pagetype'] . "_" . $_POST['data']['index'];
        $page->pageTagGroup = $_POST['data']['pagetype'];
        $page->pageType = 1;
        if ($_POST['data']['pagetype'] == "product") {
            $page->pageType = 2;
        }
        $this->getApi()->getPageManager()->savePage($page);
        $pb->addPredefinedContent($_POST['data']['pagetype'], json_decode($_POST['data']['config']));

        $this->addProductData($page);

        $this->getFactory()->clearCachedPageData();
        $this->getFactory()->reloadStoreObject();
        $this->getFactory()->initPage();
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
        if (isset($_POST['data']['bg'])) {
            $bg = $_POST['data']['bg'];
            $config = json_decode($this->getFactory()->getConfigurationFlag("getshop_colors"), true);
            if ($config) {
                foreach ($config as $index => $entry) {
                    if ($entry['path'] == 'body') {
                        unset($config[$index]);
                    }
                }
                $this->getFactory()->setConfigurationFlag("getshop_colors", json_encode($config));
            }
            if ($bg == "none") {
                $this->getFactory()->setConfigurationFlag("bgimage", false);
            } else {
                $this->getFactory()->setConfigurationFlag("bgimage", $bg);
            }
        } else {
            $this->getFactory()->setConfigurationFlag("getshop_colors", json_encode(array()));
            $bg = $_POST['data']['color'];
            $this->getFactory()->setConfigurationFlag("color", $bg);
        }
    }

    public function showDeepFreeze() {
        $this->includefile("deepfreeze");
    }

    public function searchForPages() {
        $this->includefile("pageSearchResult");
    }

    public function buyBigstockImage() {
        $imageId = $_POST['data']['imageId'];
        $sizeCode = $_POST['data']['sizeCode'];
        $downloadUrl = $this->getApi()->getBigStock()->purchaseImage($imageId, $sizeCode);

        if ($downloadUrl != null) {
            $imageData = file_get_contents($downloadUrl);
            $content = base64_decode(str_replace("data:image/png;base64,", "", base64_encode($imageData)));
            $imgId = \FileUpload::storeFile($content);

            $this->getApi()->getBigStock()->addGetShopImageIdToBigStockOrder($downloadUrl, $imgId);
            echo $imgId;
        }
    }

    public function setBigStockCreditAccount() {
        $password = $_POST['data']['password'];
        $credits = $_POST['data']['credit'];

        $this->getApi()->getBigStock()->setCreditAccount($credits, $password);
    }

    public function printAvailableBigStockCredit() {
        echo $this->getApi()->getBigStock()->getAvailableCredits();
    }

    public function addProductData($page) {

        if ($_POST['data']['target'] == "productwidget") {
            $subtype = $_POST['data']['pageSubType'];
            $product = $this->getApi()->getProductManager()->getProductByPage($page->id);
            /* @var $app ns_b741283d_920d_460b_8c08_fad5ef4294cb\ProductWidget */
            $_POST['data']['productid'] = $product->id;

            $app = $this->getFactory()->getApplicationPool()->getApplicationInstance($subtype);
            $app->setProductId();
        }
        if ($_POST['data']['target'] == "productlist") {
            $subtype = $_POST['data']['pageSubType'];
            $product = $this->getApi()->getProductManager()->getProductByPage($page->id);
            $_POST['data']['productid'] = $product->id;
            $app = $this->getFactory()->getApplicationPool()->getApplicationInstance($subtype);
            $app->addProduct();
        }
        if ($_POST['data']['pageSubType'] == "area") {
            //We need to add a productwidget to this area.
            $siteBuilder = new SiteBuilder($this->getPage()->backendPage);
            $product = $this->getApi()->getProductManager()->getProductByPage($page->id);
            $siteBuilder->addProductData($_POST['data']['target'], $product->id);
        }

        if ($page->pageType == 2 && $_POST['data']['pagemode'] == "new") {
            $sitebuilder = new SiteBuilder($page);
            $sitebuilder->addProduct();
        }
        echo $page->id;
    }

    public function activateAppArea() {
        $this->getApi()->getPageManager()->toggleBottomApplicationArea($this->getPage()->id, $_POST['data']['appArea']);
    }

    public function removeWidthFromCss($styles) {
        
        if($styles == "reset" || $styles == "notset") {
            return $styles;
        }
        
        $newstyles = "";
        foreach(explode(";", $styles) as $style) {
            if($style && !stristr($style, "width") && 
                    !stristr($style, "float") && 
                    !stristr($style, "border") && 
                    !stristr($style, "display:") && 
                    !stristr($style, "z-index:") && 
                    !stristr($style, "opacity:")) {
                $newstyles .= $style . ";";
            }
        }
        return $newstyles;
    }

}
?>
