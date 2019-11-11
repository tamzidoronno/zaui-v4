<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of InvoiceTemplateTranslator
 *
 * @author ktonder
 */
class InvoiceTemplateTranslator {
    private $matrix;
    private $currency = "NOK";
    private $language = "nb_NO";
    
    function __construct($language, $currency) {
        // Using if statements like this to avoid injection to read file system!!
        $data = "";
        
        if ($currency) {
            $this->currency = $currency;
        }
        
        if ($language) {
            $this->language = $language;
        }
        
        $prefix = strstr(getcwd(), "invoicetemplates") ? "" : "scripts/invoicetemplates/";
        if ($this->language === "nb_NO" || $this->language === "nb_no") {
            $data = file_get_contents($prefix.'lang_nb_NO.json');
        } else {
            $defaultlanguagefile = $prefix."lang_". $this->language.'.json';
            if(file_exists($defaultlanguagefile)) {
                $data = file_get_contents($defaultlanguagefile);
            } else {
                $data = file_get_contents($prefix.'lang_default.json');
            }
        }
        
        $this->matrix = json_decode($data);
    }

    public function translate($string) {
        return $this->matrix->{$string};
    }
    
    
    public function formatPrice($price) {
        if ($this->currency == "NOK") {
            return "Kr ".round($price, 2);
        }
        
        if (strtolower($this->currency) == "usd" || strtolower($this->currency) == "cad") {
            return "$ ".sprintf('%0.2f', $price);
        }
        
        if (strtolower($this->currency) == "eur") {
            return "€ ".sprintf('%0.2f', $price);
        }
        
        if ($this->currency == "GBP") {
            return "£ ".sprintf('%0.2f', $price);
        }
        
        return $price;   
    }
    
    public function getCurrencyDisplayText() {
        if ($this->currency == "NOK") {
            return "NOK";
        }
        
        if ($this->currency == "USD") {
            return "USD";
        }
        
        if ( strtolower($this->currency) == "eur") {
            return "EURO";
        }
        
        if ($this->currency == "GBP") {
            return "GBP";
        }
        
        if (strtolower($this->currency) == "cad") {
            return "CAD";
        }
        
        return "";
    }
    
}
