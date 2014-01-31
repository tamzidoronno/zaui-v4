<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ProductHelper
 *
 * @author ktonder
 */
class ProductHelper {
    
    static function getMainProductImageData($product, $height, $width) {
        $image = \ProductHelper::getMainProductImage($product, $height, $width);
        if ($image != null)
            return $image->imageData;
    }
    
    static function findMainImageId($product) {
        if(isset($product->mainImage) && strlen($product->mainImage) > 0) {
            return $product->mainImage;
        }
        
        if(isset($product->imagesAdded) && sizeof($product->imagesAdded) > 0) {
            return $product->imagesAdded[0];
        }
    }
}

?>
