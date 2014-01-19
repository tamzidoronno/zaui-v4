<?php

class PredefinedPagesConfig {
    public static $IMAGE = "image";
    public static $MAP = "map";
    public static $MOVIE = "movie";
    public static $TEXT = "text";
    public static $CONTACT = "contact";
    public static $PRODUCT = "product";
    public static $PRODUCTLIST_STANDARD = "productlist_standard";
    public static $PRODUCTLIST_BOXED = "productlist_boxed";
    public static $PRODUCTWIDGET = "productwidget";
    
    function getStandardPages() {
        $image = PredefinedPagesConfig::$IMAGE;
        $map = PredefinedPagesConfig::$MAP;
        $movie = PredefinedPagesConfig::$MOVIE;
        $text = PredefinedPagesConfig::$TEXT;
        
        $res = array();
        //One row
        $res[] = array(array($text));
        $res[] = array(array($image));
        $res[] = array(array($map));
        $res[] = array(array($movie));
        
        //Two rows
        $res[] = array(array($text), array($map, $image));
        $res[] = array(array($text, $image), array($movie, $image));
        $res[] = array(array($image), array($text));
        $res[] = array(array($image), array($movie, $text));
        $res[] = array(array($image), array($map, $image));
        $res[] = array(array($text), array($image, $text));
        $res[] = array(array($text), array($movie, $text));
        $res[] = array(array($text), array($map, $image));
        $res[] = array(array($map, $image), array($text));
        $res[] = array(array($image), array($movie, $movie));
        $res[] = array(array($text), array($movie, $movie));
        $res[] = array(array($movie, $movie), array($movie, $movie));
        $res[] = array(array($image, $image), array($image, $image));
        $res[] = array(array($text, $text), array($text, $text));
        
        //Three rows
        $res[] = array(array($image), array($text, $text), array($text, $text));
        $res[] = array(array($image), array($text, $text), array($image, $image));
        $res[] = array(array($image), array($text, $text, $text), array($image, $image));
        $res[] = array(array($image), array($text, $text, $text), array($text));
        $res[] = array(array($image), array($movie, $movie, $movie), array($text, $text, $text));
        $res[] = array(array($image), array($text, $text, $text), array($movie, $movie, $movie));
        $res[] = array(array($image), array($movie, $movie, $movie), array($movie, $movie, $movie));
        $res[] = array(array($image), array($text, $text, $text), array($text, $text, $text));
        $res[] = array(array($image), array($image, $image, $image), array($image, $image, $image));
        return $res;
    }

    public function getContactPages() {
        $image = PredefinedPagesConfig::$IMAGE;
        $map = PredefinedPagesConfig::$MAP;
        $text = PredefinedPagesConfig::$TEXT;
        $contact = PredefinedPagesConfig::$CONTACT;
        
        $res = array();
        $res[] = array(array($text, $image), array($contact));
        $res[] = array(array($image, $text), array($contact));
        $res[] = array(array($contact), array($text, $image));
        $res[] = array(array($contact, $image), array($text));
        $res[] = array(array($image, $contact), array($text));
        $res[] = array(array($image, $contact), array($text, $map));
        $res[] = array(array($map, $image), array($text, $contact));
        $res[] = array(array($text,$contact), array($map, $image));
        $res[] = array(array($contact,$map), array($image,$text));
        $res[] = array(array($text), array($contact));
        $res[] = array(array($text), array($contact,$image));
        $res[] = array(array($image), array($text,$contact));
        $res[] = array(array($image), array($map, $contact));
        $res[] = array(array($contact), array($image));
        $res[] = array(array($text, $contact), array($image));
        $res[] = array(array($map,$contact), array($image));
        $res[] = array(array($image), array($contact), array($text));
        return $res;
    }

    public function getMapPages() {
        $image = PredefinedPagesConfig::$IMAGE;
        $map = PredefinedPagesConfig::$MAP;
        $text = PredefinedPagesConfig::$TEXT;
        $contact = PredefinedPagesConfig::$CONTACT;
        
        $res = array();
        $res[] = array(array($map));
        $res[] = array(array($image), array($map));
        $res[] = array(array($text), array($map));
        $res[] = array(array($text), array($map, $image));
        $res[] = array(array($image), array($map, $text));
        $res[] = array(array($map, $text), array($image));
        $res[] = array(array($map), array($text));
        $res[] = array(array($map, $text), array($image, $map));
        $res[] = array(array($image, $map), array($map, $text));
        return $res;
    }

    public function getProductPages() {
        $image = PredefinedPagesConfig::$IMAGE;
        $map = PredefinedPagesConfig::$MAP;
        $text = PredefinedPagesConfig::$TEXT;
        $contact = PredefinedPagesConfig::$CONTACT;
        $movie = PredefinedPagesConfig::$MOVIE;
        $product = PredefinedPagesConfig::$PRODUCT;
        
        $res = array();
        $res[] = array(array($image, $product), array($text));
        
        $res[] = array(array($image, $product), array($text, $movie));
        $res[] = array(array($text, $product), array($image, $movie));
        $res[] = array(array($image, $product), array($movie, $text));
        $res[] = array(array($image, $product), array($movie, $text));
        $res[] = array(array($image, $product), array($text, $image));
        $res[] = array(array($text, $product), array($text, $image));
        $res[] = array(array($image), array($text, $product));
        $res[] = array(array($text), array($text, $product));
       
        $res[] = array(array($image), array($text, $product), array($image), array($text));
        $res[] = array(array($image), array($text, $product), array($image, $text), array($text));
        $res[] = array(array($image), array($movie, $product), array($text, $image), array($text));
        $res[] = array(array($image), array($text, $product), array($image, $movie), array($text));
        $res[] = array(array($image), array($text, $product), array($movie, $image), array($text));
        $res[] = array(array($image), array($text), array($image, $movie), array($text));
        $res[] = array(array($image, $text), array($text, $product), array($image, $movie), array($text));
        $res[] = array(array($text, $image), array($text, $product), array($movie, $image), array($text));
        $res[] = array(array($image, $product), array($text), array($image, $movie), array($movie, $text));
        
        return $res;
    }
    
    public function getProductListPages() {
        $image = PredefinedPagesConfig::$IMAGE;
        $map = PredefinedPagesConfig::$MAP;
        $text = PredefinedPagesConfig::$TEXT;
        $contact = PredefinedPagesConfig::$CONTACT;
        $movie = PredefinedPagesConfig::$MOVIE;
        $productwidget = PredefinedPagesConfig::$PRODUCTWIDGET;
        $productlist_standard = PredefinedPagesConfig::$PRODUCTLIST_STANDARD;
        $productlist_boxed = PredefinedPagesConfig::$PRODUCTLIST_BOXED;
        
        $res = array();
        $res[] = array(array($productlist_standard));
        $res[] = array(array($productlist_boxed));
        
        
        $res[] = array(array($text), array($productlist_standard));
        $res[] = array(array($text, $contact), array($productlist_standard));
        $res[] = array(array($image, $contact), array($productlist_standard));
        $res[] = array(array($image, $image), array($productlist_standard));
        $res[] = array(array($image, $image, $image), array($productlist_standard));
        $res[] = array(array($productwidget, $productwidget, $productwidget), array($productlist_standard));
        
        $res[] = array(array($image), array($productlist_boxed));
        $res[] = array(array($text), array($productlist_boxed));
        $res[] = array(array($text, $contact), array($productlist_boxed));
        $res[] = array(array($image, $contact), array($productlist_boxed));
        $res[] = array(array($image, $image), array($productlist_boxed));
        $res[] = array(array($image, $image, $image), array($productlist_boxed));
        $res[] = array(array($productwidget, $productwidget, $productwidget), array($productlist_boxed));
        
        
        return $res;
    }
}

?>
