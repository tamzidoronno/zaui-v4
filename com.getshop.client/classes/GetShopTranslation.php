<?php

/**
 * Description of Translation
 *
 * @author boggi
 */
class StoreTranslationLine {
    var $key;
    var $text;
    var $userLevel;
    var $app;
    
    
    function loadData($line) {
        $line = explode(";-;", $line);
        $this->userLevel = $line[0];
        $this->app = $line[1];
        $this->key = $line[2];
        $this->text = $line[3];

    }
    
    function toString() {
        return $this->userLevel . ";-;". $this->app . ";-;" . $this->key . ";-;" .  $this->text;
    }

    public function populate($key, $text, $userLevel, $app) {
        $this->key = $key;
        $this->text = $text;
        $this->userLevel = $userLevel;
        $this->app = $app;
        if($this->key == $this->text) {
            $this->text = "";
        }
    }

}

class GetShopTranslation {
    var $base;
    var $translationMatrix;
    var $language;
    
    
    public function GetShopTranslation() {
        $base = array();
        $this->loadBase();
    }
    
    
    public function logTranslationEntry($key, $text, $storeId, $userLevel, $app, $lang) {
        $this->language = $lang;
        $addr = $this->getStoreTranslationPath($storeId);
        $storeTranslations = $this->getStoreTranslations($storeId);
        if(stristr($app, "\\")) {
            $app = substr($app, strpos($app, "\\")+1);
        }
        if(!isset($storeTranslations[$key])) {
            $entry = new StoreTranslationLine();
            $entry->populate($key, $text, $userLevel, $app);
            $storeTranslations[$key] = $entry;
        } else {
            $entry = $storeTranslations[$key];
            if($entry->userLevel > $userLevel) {
                $entry->userLevel = $userLevel;
            } else {
                return;
            }
        }
        
        $this->saveStoreTranslations($storeTranslations, $storeId);
        
    }
    
    public function loadBase() {
        $content = file_get_contents("translation/w_base.csv");
        $contentLines = explode("\n", $content);
        foreach($contentLines as $line) {
            $cells = explode(";-;", $line);
            if($cells[0]) {
                $this->base[] = $cells[0];
            }
        }
    }
    
    /**
     * If the language exists, then we will translate it.
     */
    
    public function loadTranslationFile($language) {
        foreach($this->base as $key) {
            $this->translationMatrix[$key] = $key;
        }
        if(file_exists("translation/w_".$language.".csv")) {
            $content = file_get_contents("translation/w_".$language.".csv");
            $lines = explode("\n", $content);
            foreach($lines as $line) {
                $cells = explode(";-;", $line);
                if(sizeof($cells) == 2) {
                    $this->translationMatrix[$cells[0]] = $cells[1];
                }
            }
        }
    }
    
    public function overrideWithDataMap($map) {
        if(!isset($map) || !is_array($map)) { 
           return;
        }
        foreach($map as $translationObject) {
            /* @var $translationObject core_storemanager_data_TranslationObject */
            $this->translationMatrix[$translationObject->key] = $translationObject->value;
        }
    }
    
    public function getTranslationForKey($key) {
        if (isset($this->translationMatrix[$key]))
            return $this->translationMatrix[$key];
        
        return $key;
    }
 
    public function getTranslationMatrix() {
        return $this->translationMatrix;
    }

    public function getStoreTranslations($storeId) {
        $path = $this->getStoreTranslationPath($storeId);
        if(!file_exists($path)) {
            return array();
        }
        $content = file_get_contents($path);
        $lines = explode("\n", $content);
        $res = array();
        foreach($lines as $line) {
            if($line) {
                $data = new StoreTranslationLine();
                $data->loadData($line);
                $res[$data->key] = $data;
            }
        }
        return $res;
    }

    public function getStoreTranslationPath($storeId) {
        $storePth = getcwd() . "/translation/store/";
        if(!file_exists($storePth)) {
            mkdir($storePth);
        }
        return $storePth . $storeId . "_" . $this->language;
    }

    public function saveStoreTranslations($storeTranslations, $storeId) {
        $path = $this->getStoreTranslationPath($storeId);
        $content = "";
        foreach($storeTranslations as $trans) {
            /* @ $trans StoreTranslationLine */
            $content .= $trans->toString() . "\n";
        }
        file_put_contents($path, $content);
    }

}

?>
