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

    
    public function translateKey($key) {
        if (!isset($this->translation))
            return $key;
    
        $text = $this->translation->getTranslationForKey($key);
        if(!$text) {
            $text = $key;
        }
        return $text;
    }
    
    public function getWebShopTranslation() {
        if (!isset($this->translation))
            return array();
        
        return $this->translation->getTranslationMatrix();
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

    public function loadJavascriptFiles() {
        $scopid = $_POST['scopeid'];
        echo "<script>GetShop = {}; scopeid='$scopid'</script>";
//        echo '<script src="http://connect.facebook.net/en_US/all.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/jquery-1.8.3.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/getshop.Namespace.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/getshop.Ajax.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/getshop.Common.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/getshop.Skeleton.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/jquery.history.js"></script>';

        echo "\n" . '<script type="text/javascript" src="js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/jquery.ui/js/timepickeraddon.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/jquery.iframe-transport.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/jquery.fileupload.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/jquery.fileupload-ui.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/getshop.imageupload.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/PubSub.js"></script>';
//        
        echo "\n" . '<script type="text/javascript" src="js/getshop.ApplicationManager.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/getshop.Administration.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/getshop.base64.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/getshop.framework.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/datatables/jquery.dataTables.min.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/jPicker/jpicker-1.1.6.min.js"></script>';
        echo "\n" . '<script type="text/javascript" src="js/jquery.applicationPicker.js"></script>';
        echo "\n" . '<!--[if gte IE 8]><script src="js/jquery.xdr-transport.js"></script><![endif]-->';

        echo "\n" . '<script type="text/javascript" src="https://www.google.com/jsapi"></script>';
        echo "<script>google.load('visualization', '1.0', {'packages':['corechart']});</script>";
        echo "<script>$(function() { $( document ).tooltip({ 
            show : { delay : 250 },
            open: function() { setTimeout(function(){
                \$(\".tooltip\").remove();}, \"2000\"); 
                } 
            }); 
            });</script>";
        
        include 'javascripts.php';
    
        // TODO - find a better solution for this.
        if ($this->getApplicationPool() != null) {
            $app = $this->getApplicationPool()->getApplicationInstance("74ea4e90-2d5a-4290-af0c-230a66e09c78");
            if (get_class($app) == "Booking") {
                echo "\n" . '<link rel="stylesheet" type="text/css" href="js/jPicker/css/jPicker-1.1.6.min.css">';
                echo "\n" . '<script type="text/javascript" src="js/jPicker/jpicker-1.1.6.min.js"></script>';
            }
        }
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
    }

    function __construct() {
        @session_start();
        header('Content-Type: text/html; charset=UTF-8');
        $this->initialize();
        $this->applicationPool = new ApplicationPool($this);
        $this->pageManager = $this->getApi()->getPageManager();
    }
    
    public function getConfigurationFlag($flag) {
        return $this->store->configuration->configurationFlags->{$flag};
    }
    
    public function setConfigurationFlag($flag, $setting) {
        $this->store->configuration->configurationFlags->{$flag} = $setting;
        $this->getApi()->getStoreManager()->saveStore($this->store->configuration);
    }
    
    public function showNotExistsMessage() {
        if ($this->store == null) {
            echo "<br><br><center><b> The webpage that you are trying to access does not exists, please check your address or go to <a href='http://www.getshop.com'>http://www.getshop.com</a> for more information</b></center>";
            exit();
        }
    }

    public function start($loadPages = true) {
        $this->setScopeId();
        $this->errors = array();
        $this->initialize();
        $this->showNotExistsMessage();
        
        if ($loadPages) {
            $this->initPage();
            $this->read_csv_translation();
            if (!isset($this->getStoreConfiguration()->translationMatrix))
                $this->getStoreConfiguration()->translationMatrix = array();
            $this->loadLanguage($this->getStoreConfiguration()->translationMatrix);
        }
        
        $this->styleSheet = new StyleSheet();
    }
    
    public function loadLanguage($matrix) {
        $lang = $this->getSettings();
        $this->translation = new GetShopTranslation();
        if(isset($lang->language->value)) {
            $this->translation->loadTranslationFile($lang->language->value);
        } else {
            $this->translation->loadTranslationFile("en_en");
        }
        if($matrix) {
            $this->translation->overrideWithDataMap($matrix);
        }
    }
    
    private function setScopeId() {
        if (!isset($_POST['scopeid']) && !isset($_GET['scopeid'])) {
            $scopeid = "scope".rand(100000000, 99999999999).rand(100000000, 99999999999).rand(100000000, 99999999999).rand(100000000, 99999999999).rand(100000000, 99999999999);
            $_POST['scopeid'] = $scopeid;
        }
        
        if (isset($_GET['scopeid']))
            $_POST['scopeid'] = $_GET['scopeid'];
    }

    public function initPage() {
        if (isset($_GET['page'])) {
            $navigation = Navigation::getNavigation();
            $navigation->currentPageId = $_GET['page'];
            unset($navigation->currentCatId);
            $navigation->saveToSession();
        }

        if (isset($_GET['cat_id'])) {
            $navigation = Navigation::getNavigation();
            $navigation->currentCatId = $_GET['cat_id'];
            $navigation->saveToSession();
        }

        $navigation = Navigation::getNavigation();
        $homePage = @$this->getStoreConfiguration()->homePage;
        if (!$homePage) {
            $homePage = "home";
        }
        if (!isset($navigation->currentPageId)) {
            $navigation->currentPageId = $homePage;
        }

        $page = $this->pageManager->getPage($navigation->currentPageId);
        if ($page == null) {
            $page = $this->pageManager->getPage($homePage);
        }
        $this->page = new Page($page);
        $this->initApplicationsPool();
        $this->javaPage = $page;
    }
    
    public function initApplicationsPool() {
        
        $applications = $this->pageManager->getApplicationsForPage($this->page->id);
        $this->applicationPool->setApplicationInstances($applications);
    }
    
    public function setPage($page) {
        $this->page = $page;
    }

    private function runPreprocess() {
        $apps = $this->page->getApplications();
        foreach ($apps as $app) {
            $app->preProcess();
        }
    }

    private function runPostProcess() {
        foreach ($this->page->getApplications() as $app) {
            $app->postProcess();
        }
    }

    private function renderConent($json) {
        if ($json) {
            $this->page->loadJsonContent();
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
        $this->initPage();
        $this->runPreprocess();
        $this->renderConent($json);
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
            echo '<link rel="stylesheet" type="text/css" href="js/jPicker/css/jPicker-1.1.6.css" />';
        }

        echo '<link rel="stylesheet" type="text/css" href="js/datatables/demo_table.css" />';
        echo '<link rel="stylesheet" type="text/css" href="js/datatables/demo_page.css" />';
        echo "\n" . '<link rel="stylesheet" type="text/css" href="/skin/default/applicationPicker.css">';

        // LA STÅ!
        echo '<link rel="stylesheet" type="text/css" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css">';
        echo '<link id=\'mainlessstyle\' rel="stylesheet" type="text/css" media="all" href="StyleSheet.php">';
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

    public function getSettings() {
        $appPool = $this->getApplicationPool();
        $app = $appPool->getApplicationInstance("d755efca-9e02-4e88-92c2-37a3413f3f41");

        if ($app != null)
            return $app->getConfiguration()->settings;
    }

    public function getCurrency() {
        $settings = $this->getSettings();
        $currency = "";

        if (isset($settings->currencycode))
            $currency = $settings->currencycode->value;

        if ($currency == "" || !isset($currency))
            return "USD";

        return $currency;
    }

    public function getCurrencyName() {
        $settings = $this->getSettings();
        $currency = "";
        if (isset($settings->currencycode)) {
            $currency = $settings->currencycode->value;
        }

        if ($currency == "NOK")
            return "Kr";

        if ($currency == "USD")
            return "$";

        return $this->__w("Price") . ": ";
    }

    public function getTranslationForKey($app, $key) {
        $orignalkey = $key;
//        $key = $app . "." . $key;
//        $key = strtolower($key);
        if (!isset($this->translationMatrix[$key]) || !$this->translationMatrix[$key]) {
            return $orignalkey;
        }
        return $this->translationMatrix[$key];
    }

    /**
     * Replace the current store object...
     * @param type $storeObject 
     */
    public function setStore($storeObject) {
        $this->store = $storeObject;
    }

    public function read_csv_translation() {
        $lang = $this->getSettings();

        if (isset($lang->language)) {
            $lang = $lang->language->value;
            if (!file_exists("translation/f_$lang.csv")) {
                $lang = "en_en";
            }
        } else {
            $lang = "en_en";
        }
        $content = file_get_contents("translation/f_$lang.csv");
        $line = explode("\n", $content);
        $this->translationMatrix = array();
        $app = "";
        foreach ($line as $entry) {
            $cell = explode(";-;", $entry);
            if(isset($cell[1])) {
                $this->translationMatrix[$cell[0]] = $cell[1];
            }
        }
        
        //Append the english lanugage.
        $content = file_get_contents("translation/f_en_en.csv");
        $line = explode("\n", $content);
        foreach ($line as $entry) {
            $cell = explode(";-;", $entry);
            if(isset($cell[1])) {
                if(!$this->translationMatrix[$cell[0]]) {
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
        return "ns_".str_replace("-", "_", $namespace);
    }

    public function hasSelectedDesign() {
        return $this->store->configuration->hasSelectedDesign;
    }

}

?>
