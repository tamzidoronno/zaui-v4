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
        
        // Two rows
        $res[] = array(array($text,$image_slider),array($text,$text,$text),array($text,$text,$text));
        $res[] = array(array($text,$image_slider),array($productwidget,$productwidget,$productwidget),array($productwidget,$productwidget, $text));
        $res[] = array(array($text,$image_slider),array($image,$text,$text),array($image,$text,$text));
        
        $res[] = array(array($text,$image_slider),array($image,$image),array($text,$text),array($productlist_row));
        $res[] = array(array($text,$image_slider),array($movie,$text), array($movie,$text), array($productlist_row));
        $res[] = array(array($text,$image_slider),array($image,$text), array($movie,$text), array($productlist_row));
        
        $res[] = array(array($text,$image_slider),array($text,$text,$text),array($image,$text,$text),array($image,$text,$text));
        $res[] = array(array($text,$image_slider),array($text,$text,$movie),array($image,$text,$text),array($image,$text,$text));
        $res[] = array(array($text,$image_slider),array($text,$text,$image),array($movie,$text,$text),array($movie,$text,$text));
        
        
        $res[] = array(array($text,$image_slider),array($image,$text),array($image,$text),array($image,$text),array($productlist_row));
        $res[] = array(array($text,$image_slider),array($text,$image),array($text,$image),array($text,$image),array($productlist_row));
        $res[] = array(array($text,$image_slider),array($text),array($image,$image),array($text,$text),array($productlist_row));
        
        // Dont displayed correctly
        $res[] = array(array($text,$image_slider),array($image,$image),array($text,$text,$text,$text),array($productlist_row));
        $res[] = array(array($text,$image_slider),array($image,$image,$image,$image),array($text,$text,$text,$text),array($productlist_row));
        $res[] = array(array($text,$image_slider),array($text,$text,$text,$text),array($productlist_row));
        
        return $res;
    }

    public function getText($pageTag, $index, $pageTagGroup) {
        if ($index == 0 && $pageTagGroup == "frontpage") {
            $changeInstruction = $this->__f("To change the text, hover the mouse over the text edior and a gear will appear. Happy hunting!<br><br>");
            return '<span style="font-size:18px;"><span style="color:#FFFFFF;"><strong>'.$changeInstruction.'</strong></span></span><br>&nbsp;<div style="margin-left: 40px;"><span style="font-size:16px;"><span style="color:#FFFFFF;"><span style="font-family: \'Open Sans\', HelveticaNeue, \'Helvetica Neue\', Helvetica, Arial, sans-serif;"><b>Hi There!</b><br> I am a "text area". Its my job to bring your customers the message you want to provide to them. Yeah, I know that a picture says more then 1000 words, but Im not jalouse!<br><br><span style="font-size:14px;">I goes perfectly along with any pictures that you add to the page, or any other applications for that matter.</span></span></span></span></div>';
        }
        
        if ($pageTagGroup == "frontpage") {
            return '<h1>Text area</h1><div style="margin-left: 40px;"><span style="font-size:20px; color: #555">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</span></div>';
        }
        
        return null;
    }
    
    public function getProductId($pageTag, $pageTagGroup, $index) {
        $products = $this->getApi()->getProductManager()->getLatestProducts(1);
        if ($products && count($products)) {
            return $products[0]->id;
        }
        return null;
    }
    
    public function addBannerSlider($pageTag, $index, $pageTagGroup, $pageId, $area) {
        if ($pageTagGroup == "frontpage") {
            $appconf = $this->getApi()->getPageManager()->addApplicationToPage($pageId, "d612904c-8e44-4ec0-abf9-c03b62159ce4", $area);
            $this->getApi()->getBannerManager()->addImage($appconf->id, "4dfd5fb0-6c0f-4342-bbb2-a1a0ccd03dbd");
            $this->getApi()->getBannerManager()->addImage($appconf->id, "87a62af8-372c-4e94-8d94-9a8ab61af278");
            $set = $this->getApi()->getBannerManager()->getSet($appconf->id);
            $set->height = 250;
            $this->getApi()->getBannerManager()->saveSet($set);
            return;
        } 
        
        parent::addBannerSlider($pageTag, $index, $pageTagGroup, $pageId, $area);
    }
    
}
?>