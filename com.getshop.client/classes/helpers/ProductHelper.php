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
    
    static function getCombinations($base,$n){
        $baselen = count($base);
        if($baselen == 0){
            return;
        }
        
        if($n == 1){
            $return = array();
        
            foreach($base as $b){
                $return[] = array($b);
            }
            
            return $return;
        } else {
            //get one level lower combinations
            $oneLevelLower = ProductHelper::getCombinations($base,$n-1);
            //for every one level lower combinations add one element to them that the last element of a combination is preceeded by the element which follows it in base array if there is none, does not add
            $newCombs = array();

            foreach($oneLevelLower as $oll){
                $lastEl = $oll[$n-2];
                $found = false;
    
                foreach($base as  $key => $b){
                    if($b == $lastEl){
                        $found = true;
                        continue;
                        //last element found
                    }
                    
                    if($found == true){
                        //add to combinations with last element
                        if($key < $baselen){
                            $tmp = $oll;
                            $newCombination = array_slice($tmp,0);
                            $newCombination[]=$b;
                            $newCombs[] = array_slice($newCombination,0);
                        }
                    }
                }
            }
        }
        
        return $newCombs;
    }
    
    static function getVariationCombinations($api, $productId) {
        $nodelist = $api->getListManager()->getJsTree("variationslist_product_" . $productId);
        $optionList = array();
        foreach($nodelist->nodes[0]->children as $variation) {
            foreach($variation->children as $variationValue) {
                $option = array("name" => $variation, "value" => $variationValue);
//                array_push($optionList, $variation->text . ":" . $variationValue->text);
                array_push($optionList, $option);
            }
        }
                
        $allCombinations = ProductHelper::getCombinations($optionList,  count($nodelist->nodes[0]->children));
        $variationCombinations = array();
                
        foreach($allCombinations as $possibleCombination) {
            
            $combination = array();
                    
            foreach($possibleCombination as $variation) {
                array_push($combination, $variation);
            }
                    
            $validCombination = true;
                    
            foreach($nodelist->nodes[0]->children as $variation) {
                $count = 0;
                
                foreach($combination as $value) {
                    if($value["name"]->id == $variation->id) {
                        $count++;
                    }
                }
                
                if($count > 1) {
                    $validCombination = false;
                }
            }
                    
            if($validCombination) {
                array_push($variationCombinations, $possibleCombination);
            }
        }
        
        return $variationCombinations;
    }
}

?>
