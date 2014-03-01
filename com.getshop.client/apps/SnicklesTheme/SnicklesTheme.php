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

    public function getText($pageTag, $index, $pageTagGroup) {
        if ($index == 0 && $pageTagGroup == "frontpage") {
            $changeInstruction = $this->__f("To change the text, hover the mouse over the text edior and a gear will appear. Happy hunting!<br><br>");
            return '<div class="content" style="min-height: 40px; position: relative;" id="85a6f8a2-531f-3519-7236-274ae849fede" haspdf="0" contenteditable="false"><span style="font-size:18px;"><span style="color:#FFFFFF;"><strong>'.$changeInstruction.'</strong></span></span><br>&nbsp;<div style="margin-left: 40px;"><span style="font-size:16px;"><span style="color:#FFFFFF;"><span style="font-family: \'Open Sans\', HelveticaNeue, \'Helvetica Neue\', Helvetica, Arial, sans-serif;">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam<br><br><span style="font-size:14px;">Quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</span></span></span></span></div></div>';
        }
        
        return parent::getText($pageTag, $index, $pageTagGroup);
    }
}
?>