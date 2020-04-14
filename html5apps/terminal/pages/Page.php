<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Page
 *
 * @author ktonder
 */
abstract class Page {
    /**
     *
     * @var Database
     */
    public $database;
    
    abstract function getId();
    abstract function render();
    private $api;
    public $messageToDisplay = "";
    private $state = null;
    
    function __construct() {
        $this->database =  new Database();
        $this->api = new GetShopApi(25554, "localhost", session_id());
//        $this->api = new GetShopApi(4224, "clients.getshop.com", session_id());
        $this->getApi()->getStoreManager()->initializeStore("20360.3.0.local.getshop.com", session_id());
    }

    public function __f($text) {
        
        if (isset($this->translationMatrix[$text])) {
            return $this->translationMatrix[$text];
        }
        
        return $text;
    }
    
    public function preRun() {
        
    }

    /**
     * 
     * @return GetShopApi
     */    
    function getApi() {
        return $this->api;
    }
    
    public function getBookingEngineName() {
        return $this->database->getMultiLevelName();
    }
    
    public function getTerminalId() {
        return 1;
    }
    
    public function includeFile($fileName) {
        include "pages/".  strtolower(get_class($this))."/".$fileName.".phtml";
    }
    
    public function convertToJavaDate($time) {
        $res = date("c", $time);
        return $res;
    }
    
    public function getDefaultStartDate() {
        if ($this->config->startYesterday) {
            $this->getDateToday();
        } else {
            return $this->getDateTomorrow();
        }
        
    }
    
    public function getDateTomorrow() {
        $datetime = new DateTime('tomorrow');
        return $datetime->format('d/m-Y');
    }

    public function getDateToday() {
        $datetime = new DateTime('today');
        return $datetime->format('d/m-Y');
    }

    public function setCheckoutDate() {
        $_SESSION['discountcode'] = $_POST['discountcode'];
        $_SESSION['checkoutdate'] = $_POST['checkout'];
    }
    
    public function unsetCheckoutDate() {
        unset($_SESSION['checkoutdate']);
        unset($_SESSION['checkout_page']);
    }

    public function getJavaCheckoutDate() {
        return $this->convertToJavaDate(strtotime(str_replace('/', '-', $_SESSION['checkoutdate'])));
    }

    public function getJavaStartDate() {
        if ($this->config->startYesterday) {
            $datetime = new DateTime('yesterday');
            return $this->convertToJavaDate($datetime->getTimestamp());
        } else {
            $datetime = new DateTime('today');
            return $this->convertToJavaDate($datetime->getTimestamp());
        }
    }
    
    public function read_csv_translation() {
        
        
        if (isset($_GET['lang'])) {
            $_SESSION['currentlang'] = $_GET['lang'];
            $this->getApi()->getStoreManager()->setSessionLanguage($_SESSION['currentlang']);
        }
        
        $translation = isset($_SESSION['currentlang']) ? $_SESSION['currentlang'] : "en_en";
        $content = "";

        if (file_exists("translation/$translation.csv")) {
            $content = file_get_contents("translation/$translation.csv");
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
        
//        echo "<pre>";
//        print_r($this->translationMatrix);
//        echo "</pre>";
//        die();
    }
}