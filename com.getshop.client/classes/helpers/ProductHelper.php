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
        $mainImage = null;
        if(isset($product->images)) {
            foreach($product->images as $img) {
                if($mainImage == null || $img->type == 0) {
                    $mainImage = $img;
                }
            }
        }
        if(isset($mainImage->fileId))
            return $mainImage->fileId;
    }
}

?>
