<?php

/**
 * Description of Translation
 *
 * @author boggi
 */
class GetShopTranslation {
    var $base;
    var $translationMatrix;
    
    public function GetShopTranslation() {
        $base = array();
        $this->loadBase();
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
    
}

?>
