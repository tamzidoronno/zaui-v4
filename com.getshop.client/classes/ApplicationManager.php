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

    function dndAddCell() {
        $egde = $_POST['data']['edge'];
        $pageId = $this->getPage()->javapage->id;
        $cellid = $_POST['data']['cellid'];
        $area = "body";
        $type = strtoupper($_POST['data']['type']);
        $this->getApi()->getPageManager()->addLayoutCellDragAndDrop($pageId, $cellid, $type, $egde, $area);
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
            
    function swapAppIds() {
        $fromCell = $_POST['data']['fromCellId'];
        $toCell = $_POST['data']['toCellId'];
        $pageId = $this->getFactory()->getPage()->javapage->id;
        $this->getApi()->getPageManager()->swapAppWithCell($pageId, $fromCell, $toCell);
    }
    
    function resetMobileLayout() {
        $this->getApi()->getPageManager()->resetMobileLayout($this->getPage()->javapage->id);
    }
    
    function flattenMobileLayout() {
        $this->getApi()->getPageManager()->flattenMobileLayout($this->getPage()->javapage->id);
    }
    
    function setLayoutOnCell() {
        $layout = $_POST['data']['layout'];
        $cellid = $_POST['data']['cellid'];
        $pageId = $this->getPage()->javapage->id;
        $this->getApi()->getPageManager()->updateCellLayout($layout, $pageId, $cellid);
    }
    
    
    function createFooter() {
        $this->getApi()->getPageManager()->createHeaderFooter($_POST['data']['type']);
    }
    
    function doLinkCell() {
        $cellid = $_POST['data']['cellid'];
        $url = $_POST['data']['url'];
        $this->getApi()->getPageManager()->linkPageCell($this->getPage()->javapage->id, $cellid, $url);
    }
    
    function saveContainerPosition() {
        $cellid = $_POST['data']['cellid'];
        $containerid = $_POST['data']['containerid'];
        
        $_SESSION['gscontainerposition'][$containerid] = $cellid;
    }
    
    function simpleAddRow() {
        $pageManager = $this->getFactory()->getApi()->getPageManager();
        
        $cellid = "";
        if(isset($_POST['data']['cellid'])) {
            $cellid = $_POST['data']['cellid'];
        }
        $area = $_POST['data']['area'];
        $pageId = $this->getPage()->javapage->id;
        $mode = $_POST['data']["metaData"]["mode"];
        
        //First create the row.
        $id = $pageManager->addLayoutCell($pageId, "", $cellid, "ROW", $area);
        if($mode != "ROW") {
            $pageManager->setCellMode($pageId, $id, $mode);
        } else {
            $config = $_POST['data']['metaData']['rowconfig'];
            for($i = 0; $i < sizeof($config)-1; $i++) {
                $newId = $pageManager->addLayoutCell($pageId, $id, "", "COLUMN", $area);
            }
            $cell = $pageManager->getCell($pageId, $id);
            $i = 0;
            foreach($cell->cells as $tmpCell) {
                $this->getApi()->getPageManager()->setStylesOnCell($pageId, $tmpCell->cellId, "notset", "notset", $config[$i]);
                $i++;
            }
        }
        
    }    
    
    function setSlideMode() {
        if(!isset($_SESSION['gsrotatingmodemobile'])) {
            $_SESSION['gsrotatingmodemobile'] = true;
            return;
        }
        if($_SESSION['gsrotatingmodemobile']) {
            unset($_SESSION['gsrotatingmodemobile']);
        }
    }
    
    function updateCarouselConfig() {
        $cellId = $_POST['data']['cellid'];
        $outerWidth = $_POST['data']['outerWidth'];
        $outerWidthWithMargins = $_POST['data']['outerWidthWithMargins'];
        
        $pageId = $this->getPage()->javapage->id;
        
        $config = new core_pagemanager_data_CarouselConfig();
        $config->height = $_POST['data']['height'];
        $config->time = $_POST['data']['timer'];
        $config->type = $_POST['data']['type'];
        $config->heightMobile = $_POST['data']['heightMobile'];
        $config->displayNumbersOnDots = false;
        $config->keepAspect = $_POST['data']['keepAspect'];
        $config->windowWidth = $_POST['data']['windowWidth'];
        $config->innerWidth = $_POST['data']['innerWidth'];
        
        if($_POST['data']['carouselnumber'] == "true") {
            $config->displayNumbersOnDots = true;
        }
        $config->avoidRotate = false;
        if($_POST['data']['avoidrotate'] == "true") {
            $config->avoidRotate = true;
        }
        $config->navigateOnMouseOver = false;
        if($_POST['data']['gsnavonmouseover'] == "true") {
            $config->navigateOnMouseOver = true;
        }
        $this->getApi()->getPageManager()->setCarouselConfig($pageId, $cellId, $config);
        $this->getApi()->getPageManager()->setWidth($pageId, $cellId, $outerWidth, $outerWidthWithMargins);
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
        
        $cell = $this->getApi()->getPageManager()->getCell($pageid, $cellid);
        $cell->keepOriginalLayoutOnMobile = ($_POST['data']['keepOriginalLayout'] == "true");
        $cell->anchor = $_POST['data']['anchor'];
        $cell->link = $_POST['data']['settings']['link'];
        $cell->hideOnMobile = ($_POST['data']['settings']['hideOnMobile'] == "true");

        $this->getApi()->getPageManager()->saveCell($pageid, $cell);

        if(isset($_POST['data']['colsizes'])) {
            $colsizes = $_POST['data']['colsizes'];
            foreach($colsizes as $cellid => $width) {
                $this->getApi()->getPageManager()->setStylesOnCell($pageid, $cellid, "notset", "notset", $width);
            }
        }
        
        if(isset($_POST['data']['settings'])) {
            $settings = $_POST['data']['settings'];
            $this->getApi()->getPageManager()->savePageCellSettings($pageid, $cellid, $settings);
        }
        
    }
    
    function saveBackgroundImage() {
        $data = $_POST['data']['data'];
        $data = substr($data, strrpos($data, ";base64,")+8);
        $content = base64_decode($data);
        $imgId = \FileUpload::storeFile($content);
        
        if(isset($_POST['data']['type'])) {
            $store = $this->getFactory()->getStore();
            if($_POST['data']['type'] == "mobile_portrait") {
                $store->configuration->mobileImagePortrait = $imgId;
            }
            if($_POST['data']['type'] == "mobile_landscape") {
                $store->configuration->mobileImageLandscape = $imgId;
            }
            $this->getApi()->getStoreManager()->saveStore($store->configuration);
        }
        
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
        $mode = "ROW";
        
        if(isset($_POST['data']['area'])) {
            $area = $_POST['data']['area'];
        }
        if(isset($_POST['data']['mode'])) {
            $mode = $_POST['data']['mode'];
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
            case "addbefore":
            case "addafter":
                $cellId = $this->getApi()->getPageManager()->addLayoutCell($this->getPage()->javapage->id, $cellId, $before, $mode, $area);
                $_GET['gseditcell'] = $cellId;
                break;
            case "addcolumn":
                $this->getApi()->getPageManager()->addLayoutCell($this->getPage()->javapage->id, $cellId, $before, "COLUMN", $area);
                break;
            case "addrow":
                $this->getApi()->getPageManager()->addLayoutCell($this->getPage()->javapage->id, $cellId, $before, $mode, $area);
                break;
            case "setcarouselmode":
                $this->getApi()->getPageManager()->setCellMode($this->getPage()->javapage->id, $cellId, "ROTATING");
                break;
            case "settabmode":
                $this->getApi()->getPageManager()->setCellMode($this->getPage()->javapage->id, $cellId, "TAB");
                break;
            case "setnormalmode":
                $this->getApi()->getPageManager()->setCellMode($this->getPage()->javapage->id, $cellId, "ROW");
                break;
            case "addfloating":
                $this->getApi()->getPageManager()->addLayoutCell($this->getPage()->javapage->id, $cellId, $before, "FLOATING", $area);
                break;
            case "mobilemovedown":
                $this->getApi()->getPageManager()->moveCellMobile($this->getPage()->javapage->id, $cellId, false);
                break;
            case "mobilemoveup":
                $this->getApi()->getPageManager()->moveCellMobile($this->getPage()->javapage->id, $cellId, true);
                break;
            case "mobilehideon":
                $this->getApi()->getPageManager()->toggleHiddenOnMobile($this->getPage()->javapage->id, $cellId, true);
                break;
            case "mobilehideoff":
                $this->getApi()->getPageManager()->toggleHiddenOnMobile($this->getPage()->javapage->id, $cellId, false);
                break;
            case "pinarea":
                $this->getApi()->getPageManager()->togglePinArea($this->getPage()->javapage->id, $cellId);
                break;
        }
    }
 
   function saveFloatingPosition() {
        $data = new core_pagemanager_data_FloatingData();
        $data->height = $_POST['data']['height'];
        $data->width = $_POST['data']['width'];
        $data->top = $_POST['data']['top'];
        $data->left = $_POST['data']['left'];
        $pageId = $this->getPage()->javapage->id;
        $cellid = $_POST['data']['cellid'];
        $this->getApi()->getPageManager()->saveCellPosition($pageId, $cellid, $data);
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
        $global_css = $_POST['data']['global_css'];
        $page_text = $_POST['data']['page_text'];
        $selected_color_template = $_POST['data']['selected_color_template'];
        $color_templates = $_POST['data']['color_templates'];
        $store = $this->getFactory()->getStore();
        $store->configuration->customCss = $global_css;
        if($color_templates) {
            $store->configuration->colorTemplates = $color_templates;
        }
        $store->configuration->selectedColorTemplate = $selected_color_template;
        
        unset($_SESSION['gscolorselection']);
        
        $this->getApi()->getStoreManager()->saveStore($store->configuration);
        
        /* @var $javapage core_pagemanager_data_Page */
        $javapage = $this->getFactory()->getPage()->javapage;
        $javapage->metaKeywords = $_POST['data']['metaKeywords'];
        $javapage->metaTitle = $_POST['data']['metaTitle'];
        $javapage->overridePageTitle = $_POST['data']['pageTitle'];
        $javapage->description = $_POST['data']['metaDesc'];
        $this->getApi()->getPageManager()->savePage($javapage);
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

    function randomPassword() {
        $alphabet = "abcdefghijklmnopqrstuwxyzABCDEFGHIJKLMNOPQRSTUWXYZ0123456789";
        $pass = array(); //remember to declare $pass as an array
        $alphaLength = strlen($alphabet) - 1; //put the length -1 in cache
        for ($i = 0; $i < 8; $i++) {
            $n = rand(0, $alphaLength);
            $pass[] = $alphabet[$n];
        }
        return implode($pass); //turn the array into a string
    }
    
    public function startStore() {
        $startData = new core_getshop_data_StartData();
        $startData->storeId = $_POST['data']['storeId'];
        $startData->name = "";
        $startData->email = $_POST['data']['gs_start_store_email'];
        $startData->phoneNumber = "";
        $startData->shopName = "";
        $startData->password =  $this->randomPassword();
        $startData->language =  $this->getFactory()->getMainLanguage();
        $startData->color =  isset($_SESSION['gscolorselection']) ? $_SESSION['gscolorselection'] : "";
        
        $newAddress = $this->getApi()->getGetShop()->startStoreFromStore($startData);
        
        echo 'https://'.$newAddress."/login.php?username=" . $startData->email . "&password=" . $startData->password . "&autologin=true";
        die();
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
        $cellid = @$_POST['data']['cellid'];
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
        $pageId = $this->getPage()->javapage->id;
       
        $application = $this->getFactory()->getApi()->getPageManager()->addApplication($appId, $cellId, $pageId);
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

    public function loadpaymentinfo() {
        $this->includefile("applicationpayment");
    }

    public function removeAllInstances() {
        $appSettingsId = $_POST['data']['id'];
        $this->getApi()->getPageManager()->removeAllApplications($appSettingsId);
        $this->includefile("applicationpayment");
    }

    /*
     * Dont remove this. it is used for ping!
     */

    public function ping() {
        
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

    public function setShowingSettings() {
        $_SESSION['showadmin'] = true;
    }
    
    public function unsetShowingSettings() {
        $_SESSION['showadmin'] = false;
    }
    
    public function upgradeToGetShopAdmin() {
        $this->getApi()->getUserManager()->upgradeUserToGetShopAdmin($_POST['data']['password']);
        ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::refresh();
    }
    
    public function toggleSideBar() {
        $pageId = $this->getFactory()->getPage()->getId();
        $this->getApi()->getPageManager()->toggleLeftSideBar($pageId);
    }
}
?>
