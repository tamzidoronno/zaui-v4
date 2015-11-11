<?php

/*
 * Main factory class.
 */

class Factory extends FactoryBase {

    /** @var Page */
    private $page;
    private $answers;
    private $errors;
    private $eventHandler;

    /** @var core_storemanager_data_Store */
    private $store;
    private $applicationPool;
    private $translationMatrix;
    private $styleSheet;
    public $javaPage;
    private $storeSettings;
    private $language;
    
    private $isProductionMode = false;
    
    public $productionMode = true;
    
    /** @var $translation GetShopTranslation */
    public $translation;

    /**
     * @var APIPageManager
     */
    private $pageManager;

    /**
     * @return EventHandler 
     */
    public function getEventHandler() {
        if (!isset($this->eventHandler))
            $this->eventHandler = new EventHandler($this);

        return $this->eventHandler;
    }

    public function getSelectedLanguage() {
        return $this->getMainLanguage();
    }

    public function isMobile() {
        if(stristr($_SERVER['HTTP_HOST'], "gsmobile")) {
            return true;
        }
        
        $useragent=$_SERVER['HTTP_USER_AGENT'];
        if(preg_match('/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i',$useragent)||preg_match('/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i',substr($useragent,0,4)))
            return true;
        
        return false;
    }


    public function getMainLanguage() {
        if (!isset($this->language)) {
            $app = $this->getApi()->getStoreApplicationPool()->getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
            $instance = $this->applicationPool->createInstace($app);
            $this->language = $instance->getConfigurationSetting("language");
        }

        if (!$this->language) {
            $this->language = "en_en";
        }

        return $this->language;
    }

    public function translateKey($key) {
        if (!isset($this->translation))
            return $key;

        $text = $this->translation->getTranslationForKey($key);
        if (!$text) {
            $text = $key;
        }
        return $text;
    }

    public function getTerritoriesList() {
        $lang = "en";
        if (isset($this->getSettings()->language)) {
            $lang = $this->getSettings()->language->value;
        }
        $lang = substr($lang, 0, 2);
        $countries = json_decode(file_get_contents("translation/countries/" . $lang . "_territories.json"), true);
        return $countries['main'][$lang]['localeDisplayNames']['territories'];
    }

    public function getWebShopTranslation() {
        if (!isset($this->translation))
            return array();

        return $this->translation->getTranslationMatrix();
    }

    public function reloadStoreObject() {
        $this->store = $this->getApi()->getStoreManager()->getMyStore();
    }

    public function getTranslationMatrix() {
        return $this->translationMatrix;
    }

    public function addErrors($errors) {
        $this->errors = array_merge($this->errors, $errors);
    }

    public function addError(core_common_answer_ErrorMessage $error) {
        $this->errors[] = $error;
    }

    public function removeError($answer) {
        $errors = array();
        foreach ($this->errors as $error) {
            if ($error->errorCode == $answer->errorCode) {
                continue;
            }
            $errors[] = $error;
        }
        $this->errors = $errors;
    }

    private function addJavascriptFile($file) {
        if ($this->isProductionMode) {
            $fileName = "javascripts/".$this->getStore()->id."_framework_".$this->startupCount.".js";
            $fileContent = file_get_contents($file)."\n";
            file_put_contents($fileName, $fileContent, FILE_APPEND);
        } else {
            echo "\n" . '<script type="text/javascript" src="'.$file.'"></script>';
        }
    }
    
    public function loadJavascriptFiles($includetoolbox = true) {
        $scopid = $_POST['scopeid'];
        echo "<script>GetShop = {}; scopeid='$scopid'</script>";
        
        $this->isProductionMode = $this->getApi()->getUtilManager()->isInProductionMode();
        $this->startupCount = $this->getApi()->getUtilManager()->getStartupCount();
        if ($this->isProductionMode) {
            $fileName = "javascripts/".$this->getStore()->id."_framework_".$this->startupCount.".js";
            file_put_contents($fileName, "");
        }
        
        $this->addJavascriptFile("js/jquery-1.9.0.js");
        $this->addJavascriptFile("js/watch.js");
        $this->addJavascriptFile("js/jquery-migrate-1.2.1.js");
        $this->addJavascriptFile("js/jquery.placeholder.js");
        $this->addJavascriptFile("js/getshop.Namespace.js");
        $this->addJavascriptFile("js/getshop.Ajax.js");
        $this->addJavascriptFile("js/getshop.Common.js");
        $this->addJavascriptFile("js/getshop.Skeleton.js");
        $this->addJavascriptFile("js/getshop.ImageEditor.js");
        $this->addJavascriptFile("js/getshop.BigStock.js");
        $this->addJavascriptFile("js/getshop.Model.js");
        $this->addJavascriptFile("js/jquery.history.js");
        $this->addJavascriptFile("js/jquery.form.js");
        $this->addJavascriptFile("js/jcrop/js/jquery.Jcrop.min.js");
        $this->addJavascriptFile("js/colresize.js");
        $this->addJavascriptFile("js/moments.js");
        $this->addJavascriptFile("js/photoswipe/photoswipe.min.js");
        $this->addJavascriptFile("js/photoswipe/photoswipe-ui-default.min.js");
        $this->addJavascriptFile("js/getshop.photoswipe.js");
        $this->addJavascriptFile("js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js");
        $this->addJavascriptFile("js/jquery.ui/js/timepickeraddon.js");
        $this->addJavascriptFile("js/jquery.iframe-transport.js");
        $this->addJavascriptFile("js/jquery.fileupload.js");
        $this->addJavascriptFile("js/jquery.fileupload-ui.js");
        $this->addJavascriptFile("js/getshop.imageupload.js");
        $this->addJavascriptFile("js/PubSub.js");
        $this->addJavascriptFile("js/mutate.events.js");
        $this->addJavascriptFile("js/mutate.min.js");
        $this->addJavascriptFile("js/getshop.ApplicationManager.js");
        $this->addJavascriptFile("js/getshop.Administration.js");
        $this->addJavascriptFile("js/getshop.base64.js");
        $this->addJavascriptFile("js/getshop.framework.js");
        $this->addJavascriptFile("js/getshop.ImportApplication.js");
        $this->addJavascriptFile("js/getshop.Toolbox.js");
        $this->addJavascriptFile("js/datatables/jquery.dataTables.min.js");
        $this->addJavascriptFile("js/colorpicker/js/colorpicker.js");
        $this->addJavascriptFile("js/jquery.applicationPicker.js");
        $this->addJavascriptFile("js/imagesloaded.pkgd.min.js");
        $this->addJavascriptFile("js/getshop.doImageUpload.js");
        $this->addJavascriptFile("js/getshop.rotate.js");
        $this->addJavascriptFile("js/getshop.PagePicker.js");
        $this->addJavascriptFile("js/getshop.Settings.js");
        
        if ($this->isEffectsEnabled()) {
            echo '<script '.$this->includeSeo().' src="//cdnjs.cloudflare.com/ajax/libs/gsap/1.14.2/TweenMax.min.js"></script>';
            echo '<script '.$this->includeSeo().' src="//cdnjs.cloudflare.com/ajax/libs/ScrollMagic/2.0.5/ScrollMagic.min.js"></script>';
            echo '<script '.$this->includeSeo().' src="//cdnjs.cloudflare.com/ajax/libs/ScrollMagic/2.0.5/plugins/debug.addIndicators.min.js"></script>';
            echo '<script '.$this->includeSeo().' src="js/scrollmagic.Velocity.js"></script>';
            echo '<script '.$this->includeSeo().' src="js/scrollmagic.js"></script>';
            echo '<script '.$this->includeSeo().' src="js/getshop.ScrollMangic.js"></script>';
        }
        
        // JS TREE
        $this->addJavascriptFile("js/jstree/jstree.min.js");
        
        if ($this->isProductionMode) {
            echo "\n" . '<script type="text/javascript" src="'.$fileName.'"></script>';
        }
//        echo '<script src="http://connect.facebook.net/en_US/all.js"></script>';


        echo "\n" . '<!--[if gte IE 8]><script src="js/jquery.xdr-transport.js"></script><![endif]-->';

        if (preg_match('/(?i)msie[1-8]/', $_SERVER['HTTP_USER_AGENT'])) {
            echo "\n" . '<script type="text/javascript" src="js/getshopwebsocketapi/GetShopApiWebSocket.js"></script>';
        }

        $appTheme = $this->getApplicationPool()->getSelectedThemeApp();
        $themeApp = $this->getApplicationPool()->createInstace($appTheme);
        $apps = $this->getApi()->getStoreApplicationPool()->getApplications();
        foreach($apps as $app) {
            $appInstance = $this->getApplicationPool()->createInstace($app);
            if (method_exists($appInstance, "renderOnStartup")) {
                $appInstance->renderOnStartup();
            }
        }
        if (method_exists($themeApp, "addScripts")) {
            $themeApp->addScripts();
        }
        ?>
        <script>
            $(document).tooltip({
                show: {delay: 250 }
            });
        </script>
        <?

        include 'javascripts.php';
    }

    public function loadJavascriptFilesEditorMode() {
        if ($this->isEditorMode()) {
            echo "\n" . '<script type="text/javascript" src="js/ckeditor/ckeditor.js"></script>';
            echo "\n" . '<script type="text/javascript" src="js/ckeditor/adapters/jquery.js"></script>';

            //Load app files.
            foreach (AppRegister::$register as $app) {
                if (file_exists("js/app/" . $app . "_editormode.js"))
                    echo "\n" . '<script type="text/javascript" src="js/app/' . $app . '_editormode.js"></script>';
            }
        }
    }

    public function initialize() {
        $this->store = $this->getApi()->getStoreManager()->initializeStore($_SERVER['HTTP_HOST'], session_id());
        if(!$this->store) {
            $this->store = $this->getApi()->getStoreManager()->getMyStore();
        }

        if (!$this->store) {
            include("createinstance/createinstance.phtml");
            exit(0);
        }
    }

    function __construct() {
        @session_start();
        header('Content-Type: text/html; charset=UTF-8');
        $this->initialize();
        $this->applicationPool = new ApplicationPool($this);
        $this->pageManager = $this->getApi()->getPageManager();
    }

    public function getConfigurationFlags() {
        return $this->store->configuration->configurationFlags;
    }

    public function getConfigurationFlag($flag) {
        if (isset($this->store->configuration->configurationFlags->{$flag}))
            return $this->store->configuration->configurationFlags->{$flag};
        return null;
    }

    public function setConfigurationFlag($flag, $setting) {
        $this->store->configuration->configurationFlags->{$flag} = $setting;
        $this->getApi()->getStoreManager()->saveStore($this->store->configuration);
    }

    public function start($loadPages = true) {
        $this->setScopeId();
        $this->errors = array();

        if ($loadPages) {
            $this->initPage();
            $this->read_csv_translation();
            if (!isset($this->getStoreConfiguration()->translationMatrix)) {
                $this->getStoreConfiguration()->translationMatrix = array();
            }
            $this->loadLanguage();
        }

        if (!$loadPages) {
            $this->styleSheet = new StyleSheet();
        }

        $this->displayCookieWarning();
    }

    public function loadLanguage() {
        $matrix = $this->getStoreConfiguration()->translationMatrix;
        $translation = $this->getSelectedTranslation();
        $this->translation->loadTranslationFile($translation);
        if ($matrix) {
            $this->translation->overrideWithDataMap($matrix);
        }
    }

    private function setScopeId() {
        if (!isset($_POST['scopeid']) && !isset($_GET['scopeid'])) {
            $scopeid = "scope" . rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999) . rand(100000000, 99999999999);
            $_POST['scopeid'] = $scopeid;
        }

        if (isset($_GET['scopeid']))
            $_POST['scopeid'] = $_GET['scopeid'];
    }

    private function checkRewrite() {
        if (isset($_GET['rewrite'])) {
            $name = str_replace("+", "%2b", $_GET['rewrite']);
            $name = urldecode($name);
            $name = str_replace("_''", "\"", $name);
            $pageId = $this->getApi()->getListManager()->getPageIdByName($name);
            if ($pageId != "") {
                $_GET['page'] = $pageId;
                return;
            }

            $pageId = $this->getApi()->getProductManager()->getPageIdByName($name);
            if ($pageId != "") {
                $_GET['page'] = $pageId;
            }
        }
    }

    public function initPage() {
        $this->checkRewrite();
        if (isset($_GET['page'])) {
            if ($_GET['page'] == "clear_page" || $_GET['page'] == "{HOMEPAGE}") {
                $navigation = Navigation::getNavigation();
                $navigation->currentPageId = null;
            } else {
                $navigation = Navigation::getNavigation();
                $navigation->currentPageId = $_GET['page'];
                unset($navigation->currentCatId);
            }
            $navigation->saveToSession();
        }

        $navigation = Navigation::getNavigation();

        $homePageId = $this->getHomePageName();

        if (!isset($navigation->currentPageId)) {
            $navigation->currentPageId = $homePageId;
        }

        $pageId = isset($_POST['data']['getShopPageId']) ? $_POST['data']['getShopPageId'] : $navigation->currentPageId;


        $javaPage = $this->pageManager->getPage($pageId);

        if ($javaPage == null) {
            $javaPage = $this->pageManager->getPage($homePageId);
        }

        if ($javaPage == null) {
            echo "<center><h1>Page not found: $homePageId</h1></center>";
            http_response_code(404);
            exit(0);
        }

        $this->page = new Page($javaPage, $this);
        $this->getApplicationPool()->initForPage($this->pageManager, $this->page);

        $this->javaPage = $javaPage;
        $this->styleSheet = new StyleSheet();
    }

    private function getHomePageName() {
        $homePage = @$this->getStoreConfiguration()->homePage;
        if (!$homePage) {
            $homePage = "home";
        }
        return $homePage;
    }

    public function setPage($page) {
        $this->page = $page;
    }

    private function runPreprocess() {
        $apps = $this->getApplicationPool()->getAllAddedInstances();
        foreach ($apps as $app) {
            if (method_exists($app, "preProcess"))
                $app->preProcess();
        }
    }

    private function runPostProcess() {
        foreach ($this->getApplicationPool()->getAllAddedInstances() as $app) {
            if (method_exists($app, "postProcess"))
                $app->postProcess();
        }
    }

    private function renderContent($json) {
        if ($json) {
            ob_start();
            $this->page->loadSkeleton();
            $content = ob_get_contents();
            ob_end_clean();
            $data = array();
            $data['content'] = $content;
            echo json_encode($data);
        } else {
            $this->page->loadSkeleton();
        }
    }

    public function getBottomHtml() {
        ob_start();
        $this->includefile("bottomFileContent");
        $html = ob_get_contents();
        ob_end_clean();
        return $html;
    }

    public function run($json = false) {
        $this->sendCurrentLanguage();
        $this->runPreprocess();
        $this->renderContent($json);
        $this->runPostProcess();
    }

    public function isExtendedMode() {
        return $this->store->isExtendedMode;
    }

    /**
     * @return StyleSheet
     */
    public function getStyleSheet() {
        return $this->styleSheet;
    }
    
    public function showCssFiles() {
        if ($this->isEditorMode()) {
            echo '<link rel="stylesheet" type="text/css" href="skin/default/ckeditor.css" />';
            echo '<link rel="stylesheet" type="text/css" href="js/colorpicker/css/colorpicker.css" />';
        }

        echo "<link href='/fonts.css' rel='stylesheet' type='text/css'>";
        echo '<link rel="stylesheet" type="text/css" href="js/datatables/demo_table.css" />';
        echo '<link rel="stylesheet" type="text/css" href="js/datatables/demo_page.css" />';
        echo '<link rel="stylesheet" type="text/css" href="/skin/default/applicationPicker.css">';
        echo '<link rel="stylesheet" href="skin/default/fontawesome/css/font-awesome.min.css">';

        echo "\n" . '<link rel="stylesheet" type="text/css" href="js/jcrop/css/jquery.Jcrop.css">';
        echo "\n" . '<link rel="stylesheet" type="text/css" href="js/jstree/themes/default/style.min.css">';
        echo "\n" . '<link rel="stylesheet" href="/js/photoswipe/photoswipe.css">';
        echo "\n" . '<link rel="stylesheet" href="/js/photoswipe/default-skin/default-skin.css">';
        
        // LA STÅ!
        echo '<link rel="stylesheet" type="text/css" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css">';
        echo '<link rel="stylesheet" type="text/css" href="/skin/default/skeletons.css">';
        echo '<link rel="stylesheet" type="text/css" href="/skin/default/PagePicker.css">';
        echo '<link rel="stylesheet" type="text/css" href="/skin/default/getshop.ImageEditor.css">';

        $styleSheet = new StyleSheet();
        $styleSheet->render(false);

        $config = json_decode($this->getFactory()->getConfigurationFlag("getshop_colors"), true);
        if ($config) {
            echo "<style id='set_colors'>";
            foreach ($config as $name => $entry) {
                echo $entry['path'] . " {" . $entry['type'] . " : #" . $entry['color'] . " }\n";
            }
            echo "</style>";
        }
    }

    /*
     * @return Page
     */

    public function getPage() {
        return $this->page;
    }

    public function getBackendAnswers() {
        if (!is_array($this->answers)) {
            return array();
        }
        return $this->answers;
    }

    public function getApps() {
        return $this->page->getApplications();
    }

    public function getErrorsHtml() {
        if (count($this->getApi()->transport->errors) == 0) {
            return "";
        }
        ob_start();
        $this->includefile('errors', 'Common');
        $errors = ob_get_contents();
        ob_end_clean();
        return $errors;
    }

    public function getErrorCodes() {
        if (count($this->getApi()->transport->errorCodes) == 0) {
            return "";
        }

        return $this->getApi()->transport->errorCodes;
    }

    /**
     * @return core_storemanager_data_StoreConfiguration 
     */
    public function getStoreConfiguration() {
        return $this->store->configuration;
    }

    public function runStartupGuide() {
        
    }

    /**
     * @return ApplicationPool
     */
    public function getApplicationPool() {
        return $this->applicationPool;
    }

    /**
     * @return core_storemanager_data_Store 
     */
    public function getStore() {
        return $this->store;
    }

    public function getJsonTranslationMatrix() {
        return json_encode($this->translationMatrix);
    }

    public function getSettings() {
        if ($this->storeSettings) {
            return $this->storeSettings;
        }
        $this->storeSettings = $this->getApi()->getStoreApplicationPool()->getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
        return $this->storeSettings;
    }

    public function clearCachedPageData() {
        
    }

    public function getTranslationForKey($app, $key) {
        if (!count($this->translationMatrix)) {
            $this->read_csv_translation();
        }

        if (!isset($this->translationMatrix[trim($key)]) || !$this->translationMatrix[trim($key)]) {
            return $key;
        }
        return $this->translationMatrix[trim($key)];
    }

    /**
     * Replace the current store object...
     * @param type $storeObject 
     */
    public function setStore($storeObject) {
        $this->store = $storeObject;
    }

    public function read_csv_translation() {
        $translation = $this->getSelectedTranslation();
        $content = "";

        if (file_exists("translation/f_$translation.csv")) {
            $content = file_get_contents("translation/f_$translation.csv");
        }
        $line = explode("\n", $content);
        $this->translationMatrix = array();
        $app = "";
        foreach ($line as $entry) {
            if (strpos($entry, "###### ") === 0) {
                continue;
            }
            $cell = explode(";-;", $entry);
            if (isset($cell[1])) {
                $this->translationMatrix[$cell[0]] = $cell[1];
            }
        }

        //Add customer related translation to the matrix.
        if (file_exists("translation/w_$translation.csv"))
            $content = file_get_contents("translation/w_$translation.csv");

        $line = explode("\n", $content);
        $app = "";
        foreach ($line as $entry) {
            if (strpos($entry, "###### ") === 0) {
                continue;
            }
            $cell = explode(";-;", $entry);
            if (isset($cell[1])) {
                $this->translationMatrix[$cell[0]] = $cell[1];
            }
        }


        //Append the english lanugage.
        $content = file_get_contents("translation/f_en_en.csv");
        $line = explode("\n", $content);
        foreach ($line as $entry) {
            if (strpos($entry, "###### ") === 0) {
                continue;
            }
            $cell = explode(";-;", $entry);
            if (isset($cell[1])) {
                if (!isset($this->translationMatrix[$cell[0]])) {
                    $this->translationMatrix[$cell[0]] = $cell[1];
                }
            }
        }
    }

    public function dumpOtherTranslation() {
        $oldTranslation = $this->tmpTrans;
        $matrix = $this->translationMatrix;
        $newTrans = array();
        echo "<pre>";
        $this->dump($matrix);
        echo "</pre>";
        foreach ($matrix as $key => $text) {
            foreach ($oldTranslation as $app) {
                if (isset($newTrans[$key])) {
                    continue;
                }
                foreach ($app as $trans) {
                    /* @var $trans core_translationmanager_data_SingleTranslation */
                    if (strtolower(trim($trans->key)) == strtolower(trim($text))) {
                        $trans->translation = utf8_decode($trans->translation);
                        $newTrans[$key] = $trans->translation;
                        $newTrans[$key . ":old"] = $text;
                        break;
                    }
                }
                if (!isset($newTrans[$key])) {
                    $newTrans[$key] = "";
                    $newTrans[$key . ":old"] = $text;
                }
            }
        }
        $this->translationMatrix = $newTrans;
        $curApp = $key[0];
        echo "<table>";
        foreach ($this->translationMatrix as $key => $text) {
            if (stristr($key, ":old")) {
                continue;
            }
            $key2 = $key;
            $key = explode(".", $key);
            if ($key[0] != $curApp) {
                $curApp = $key[0];
                echo "<tr></tr><tr></tr>";
                echo "<tr><td>_application_</td><td>$curApp</td></tr>";
            }
            echo "<tr><td>" . $key[1] . "</td><td>" . $this->translationMatrix[$key2 . ":old"] . "</td><td>" . $text . "</td></tr>";
        }
        echo "</table>";
    }

    public function convertUUIDtoString($namespace) {
        return "ns_" . str_replace("-", "_", $namespace);
    }

    public function hasSelectedDesign() {
        return $this->store->configuration->hasSelectedDesign;
    }

    public function checkUserAgentAndUpdate() {
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        $save = false;
        $agent = false;

        $ua = strtolower($_SERVER['HTTP_USER_AGENT']);

        if (strstr($ua, "chrome")) {
            $agent = "chrome";
        }
        if (strstr($ua, "firefox")) {
            $agent = "firefox";
        }
        if (!$user->userAgent) {
            $user->userAgent = $_SERVER['HTTP_USER_AGENT'];
            $save = true;
        }
        if (!$user->hasChrome && strstr($ua, "chrome")) {
            $user->hasChrome = true;
            $save = true;
        }

        if ($save) {
            $this->getApi()->getUserManager()->saveUser($user);
            \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::setLoggedOn($user);
        }

        return $agent;
    }

    public function displayCookieWarning() {
        $settings = $this->getSettings();

        if (isset($settings->cookiewarning) && $settings->cookiewarning->value === "true") {
            if (isset($_SESSION['getshop_cookie_accepted'])) {
                return;
            }
            echo "<div style='display:none;' id='cookiewarning_overlay'>";
            echo "<span class='textbox'>";
            echo "<div class='title'>" . $this->__w("Cookies are being stored") . "</div>";
            echo "<div class='text'>" . $this->__w("To be able to serve you we need to store a tiny amount of data about your browser. An identification id is being generated and added to your web browser. This allows us to track you for a two hours periode while you are navigating this site. This data is anonymous and are only being used to identify your web browser and not you as a person.") . "</div>";
            echo "<input type='button' class='continue' value='" . $this->__w("Continue") . "'>";
            echo "</span>";
            echo "</div>";
        }
    }

    public function getSelectedTranslation() {
        $translation = $this->getSelectedLanguage();
        $this->translation = new GetShopTranslation();

        if (isset($_GET['setLanguage'])) {
            $translation = $_GET['setLanguage'];
            $_SESSION['language_selected'] = $translation;
        } else if (isset($_SESSION['language_selected'])) {
            $translation = $_SESSION['language_selected'];
        }
        return $translation;
    }

    public function printTemplateFunctions() {
        if (!$this->isExtendedMode()) {
            return;
        }
       
        if ($this->store && $this->store->isTemplate && !$this->isMobile() && !$this->getFactory()->isMobile()) {
            $this->includefile("templatefunctions", 'Common');
        }
        
//        if ($this->store && $this->store->expiryDate && !$this->store->isTemplate && !$this->getFactory()->isMobile()) {
//            $this->includefile("expirywarning", 'Common');
//        }
        
        if ($this->isEditorMode()) {
            $this->includefile("rowpicker", 'Common');
        }
    }

    public function getLanguageCodes() {
        if (!isset($this->languageCodes)) {
            $app = $this->getApi()->getStoreApplicationPool()->getApplication("d755efca-9e02-4e88-92c2-37a3413f3f41");
            $instance = $this->applicationPool->createInstace($app);
            $this->languageCodes = [];
            $codesInJson = $instance->getConfigurationSetting("languages");
            if ($codesInJson) {
                $this->languageCodes = json_decode($codesInJson);
            }
        }

        if (!$this->languageCodes) {
            $this->languageCodes = [];
        }

        return $this->languageCodes;
    }

    public function getPageTitle() {
        $javapage = $this->getPage()->javapage;
        $settings = $this->getSettings();
        $title = isset($settings->settings) && isset($settings->settings->title) ? $settings->settings->title->value : "";
        

        if($javapage->title) {
            $title = $this->getPage()->javapage->title . " - " . $title;
        }
        if($javapage->overridePageTitle) {
            $title = $javapage->overridePageTitle;
        }
        return $title;
    }

    public function renderBottom() {
        $apps = $this->getApi()->getStoreApplicationPool()->getApplications();
        foreach ($apps as $app) {
            $appInstance = $this->getApplicationPool()->createInstace($app);
            if ($appInstance) {
                $appInstance->renderBottom();
            }
        }
    }

    public function sendCurrentLanguage() {
        if (isset($_SESSION['language_selected'])) {
            $this->getApi()->getStoreManager()->setSessionLanguage($_SESSION['language_selected']);
        } else {
            $this->getApi()->getStoreManager()->setSessionLanguage(null);
        }
    }

    public function isEffectsEnabled() {
        return true;
    }

    public function includeSeo() {
        $settings = $this->getApplicationPool()->getApplicationSetting("d755efca-9e02-4e88-92c2-37a3413f3f41");
        $instance = $this->getApplicationPool()->createInstace($settings);
        
        $singleOnGroup = $instance->getConfigurationSetting("seo");

        if($singleOnGroup && $singleOnGroup == "true") {
            return "async";
        }
        return "";
    }

}
?>