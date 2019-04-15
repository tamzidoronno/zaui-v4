<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PluginBase
 *
 * @author ktonder
 */
class PluginBase {
    public function checkSecurity() {
        if (!$this->isLoggedIn()) {
            throw RuntimeException("Access denied");
        }
        
        return true;
    }
    
    public function isLoggedIn() {
        if (isset($_SESSION['tokenpass']) && $_SESSION['tokenpass'] == DomainConfig::$password) {
            return true;
        }
        
        return false;
    }
}
