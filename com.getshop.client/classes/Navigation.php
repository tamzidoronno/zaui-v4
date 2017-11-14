<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Navigation
 *
 * @author ktonder
 */
class Navigation {
    public $currentPageId;
    public $currentCatId;
    
    /**
     * @return Navigation
     */
    static function getNavigation() {
        $scope = $_POST['scopeid'];
        
        if (isset($_SESSION['getshop_current_module_id']) && $_SESSION['getshop_current_module_id'] != "cms") {
            $scope .= "_".$_SESSION['getshop_current_module_id'];
        }
        
        if (!isset($_SESSION[$scope])) {
            $nav = new Navigation();
            $nav->saveToSession();
        }
        
        return unserialize($_SESSION[$scope]);
    }
    
    public function saveToSession() {
        $scope = $_POST['scopeid'];
        
        if (isset($_SESSION['getshop_current_module_id']) && $_SESSION['getshop_current_module_id'] != "cms") {
            $scope .= "_".$_SESSION['getshop_current_module_id'];
        }
        
        $_SESSION[$scope] = serialize($this);
    }
}

?>
