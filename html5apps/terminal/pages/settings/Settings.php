<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Settings
 *
 * @author ktonder
 */
class Settings extends Page {
    

    public function getId() {
        
    }
    
    public function preRun() {
        if (!isset($_SESSION['settingsverified'])) {
            if (isset($_POST['password']) && $_POST['password'] == "getshop") {
                $_SESSION['settingsverified'] = true;
            }
            return;
        }
        
        if (isset($_POST['action'])) {
            
            if ($_POST['action'] == "saveData") {
                $this->saveData();
            }
        }
    }

    public function render() {
        if (!isset($_SESSION['settingsverified'])) {
            $this->includeFile("password");
        } else {
            $this->includeFile("settings");
        }
        
    }

    public function saveData() {
        $this->database->setConfig("multilevelname", $_POST['multilevelname']);
        $this->database->setConfig("terminalid", $_POST['terminalid']);
        unset($_SESSION['settingsverified'] );
    }
}
