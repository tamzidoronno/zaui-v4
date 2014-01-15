<?php

class PredefinedPagesConfig {
    public static $IMAGE = "image";
    public static $MAP = "map";
    public static $MOVIE = "movie";
    public static $TEXT = "text";
    public static $CONTACT = "contact";
    
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
}

?>
