<?php

/**
 * Description of FactoryBase
 *
 * @author ktonder
 */
class FactoryBase {
    private static $api;

    /**
     * @return Factory
     */
    public function getFactory() {
        return IocContainer::$factory;
    }

    /**
     * Gets the GetShopApi to backend
     * 
     * @return GetShopApi
     */
    public function getApi() {
        if (!isset(FactoryBase::$api)) {
            $config = new ConfigReader();
            $port = $config->getConfig("port");
            $host = $config->getConfig("backenddb");
            FactoryBase::$api = new GetShopApi($port, $host);
        }
        return FactoryBase::$api;
    }

    /**
     * 
     * @return GetShopApi
     */
    public function getApiObject() {
        return new Api();
    }

    /**
     * @return Page
     */
    public function getPage() {
        $factory = $this->getFactory();
        return $factory->getPage();
    }

    public function includefile($filename, $overrideappname = null, $printError = true) {
        $appname = get_class($this);
        if ($overrideappname != null) {
            $appname = $this->getFactory()->getApplicationPool()->getNamespaceByApplicationName($overrideappname);
            $appname = ($appname == null) ? $overrideappname : $appname;
        }

        if (strpos($appname, "\\")) {
            $appname = substr($appname, 0, strpos($appname, "\\"));
        }

        $file = '../template/default/' . $appname . '/' . $filename . ".phtml";
        if (file_exists($file)) {
            include $file;
        } else {
            $appTemplateFile = '../app/' . $appname . '/template/' . $filename . ".phtml";
            if (file_exists($appTemplateFile)) {
                include $appTemplateFile;
            } else {
                if(!$this->getApplications()) {
                    echo "Is the template file: " . $filename . " missing?";
                    return;
                }
                
                foreach ($this->getApplications() as $application) {
                    $application->renderApplication();
                }
            }
        }

        return true;
    }

    public function isEditorMode() {
        return \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isEditor();
    }

    public function __f($string) {
        return $this->__($string);
    }

    public function __o($string) {
        return $string;
    }

    /**
     * Translate the text into correct language.
     * 
     * @param string $string
     * @return string
     */
    public function __($string, $app = null) {
        if (!$app)
            $app = get_class($this);

        return trim($this->getFactory()->getTranslationForKey($app, $string));
    }

    public function __w($string, $app = null) {
        return $this->getFactory()->translateKey($string);
    }

    /**
     * Logging function
     * @param type $logstring 
     */
    public function ___($logstring) {
        
    }

    public function getAnswers($className = "") {
        $answers = $this->getFactory()->getBackendAnswers();

        if ($className != "") {
            foreach ($answers as $answer) {
                if (EventHandler::isMessage($className, $answer)) {
                    return $answer;
                }
            }
            return null;
        }
        return $answers;
    }

    /**
     * @return Navigation
     */
    public function getNavigation() {
        return Navigation::getNavigation();
    }

    public function dump($object) {
        if (is_object($object)) {
            $object = $this->convertObjectToPHP($object);
        } else if (is_array($object)) {
            $object = $this->convertArrayToPHP($object);
        }
        echo "<pre>";
        print_r($object);
        echo "</pre>";
    }

    private function convertObjectToPHP($obj) {
        $obj_name = get_class($obj);
        if ($obj_name == "java.lang.Class") {
            return;
        }
        $obj_name = str_replace(".", "_", $obj_name);
        $obj_name = str_replace("com_thundashop_", "", $obj_name);
        $return_obj = new $obj_name();

        foreach ($return_obj as $key => $val) {
            $tmp_val = $obj->$key;
            if (gettype($tmp_val) == "object") {
                $tmp_val = $this->convertObjectToPHP($tmp_val);
            }
            if (gettype($tmp_val) == "array") {
                $tmp_val = $this->convertArrayToPHP($tmp_val);
            }

            $return_obj->$key = $tmp_val;
        }
        return $return_obj;
    }

    private function convertArrayToPHP($array) {
        $newArray = array();
        foreach ($array as $id => $val) {
//            echo "Array: " . $key . " : " . $val . "<br>";
            if (gettype($val) == "object") {
                $tmp_val = $this->convertObjectToPHP($val);
            } else if (gettype($val) == "array") {
                $tmp_val = $this->convertArrayToPHP($val);
            } else {
                $tmp_val = $val;
            }
            $newArray[$id] = $tmp_val;
        }
        return $newArray;
    }

}

?>
