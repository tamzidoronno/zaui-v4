<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PageRouter
 *
 * @author ktonder
 */
class PageRouter {
    public function renderCurrentPage() {
        $currentPageName = $this->getCurrentPageName();
        
        $filename = Config::$domain.'/'.$currentPageName.'/'.$currentPageName.'.phtml';
        
        include $filename;
    }

    public function getCurrentPageName() {
        if (isset($_GET['page'])) {
            $pages = $this->getAllPages();
            
            foreach ($pages as $page) {
                if (get_class($page) == $_GET['page']) {
                    return $_GET['page'];
                }
            }
        }
        
        return "home";
    }
    
    public function renderCurrentPageCss() {
        $currentPageName = $this->getCurrentPageName();
        $cssFile = Config::$domain.'/'.$currentPageName.'/'.$currentPageName.'.css';
        
        if (file_exists($cssFile)) {
            echo '<link rel="stylesheet" href="'.$cssFile.'">';
        }
        
        $cssFile = Config::$domain."/".Config::$domain.".css";
        if (file_exists($cssFile)) {
            echo '<link rel="stylesheet" href="'.$cssFile.'">';
        }
        
        $pagejsfile = Config::$domain."/".$currentPageName."/".$currentPageName.".js";

        if (file_exists($pagejsfile)) {
            echo '<script src="'.$pagejsfile.'"></script>';
        }
    }
    
   
    public function getAllPages() {
        $di = new RecursiveDirectoryIterator(Config::$domain);
        $pages = array();
        
        foreach (new RecursiveIteratorIterator($di) as $filename => $file) {
            if (strstr($filename, '..')) {
                $arr = explode("/", $filename);
                
                if (count($arr) == 3) {
                    if (class_exists($arr[1]))
                        $pages[] = new $arr[1]();
                }
            }
        }
        
        return $pages;
    }

    public function getCurrentPage() {
        $pageName = $this->getCurrentPageName();
        return new $pageName();
    }

}
