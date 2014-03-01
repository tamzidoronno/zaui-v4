<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
namespace ns_8b060db0_a0a3_11e3_a5e2_0800200c9a66;

class SnicklesTheme extends \ThemeApplication implements \Application {
    

    public function getDescription() {
        return $this->__w("Nice modern theme. Could be used for clothing, skateboarding, snowboarding etc.");
    }

    public function render() {
        
    }
    
    public function getName() {
        return $this->__f("Snickles");
    }
    
    public function getHomePages() {
        $image = \PredefinedPagesConfig::$IMAGE;
        $image_slider = \PredefinedPagesConfig::$IMAGESLIDER;
        $map = \PredefinedPagesConfig::$MAP;
        $text = \PredefinedPagesConfig::$TEXT;
        $contact = \PredefinedPagesConfig::$CONTACT;
        $movie = \PredefinedPagesConfig::$MOVIE;
        $productwidget = \PredefinedPagesConfig::$PRODUCTWIDGET;
        $productlist_standard = \PredefinedPagesConfig::$PRODUCTLIST_STANDARD;
        $productlist_boxed = \PredefinedPagesConfig::$PRODUCTLIST_BOXED;
        $productlist_row = \PredefinedPagesConfig::$PRODUCTLIST_ROW;
        
        $res = array();
        $res[] = array(array($text,$image_slider),array($text,$text,$text),array($productlist_row));
        $res[] = array(array($text,$image_slider),array($text,$text,$text),array($text,$text,$text));
        return $res;
    }

//    public function getText($pageTag, $index, $pageTagGroup) {
//        if ($index == 0 && $pageTagGroup == "frontpage") {
//            return "YO YO YO";
//        }
//    }
}
?>