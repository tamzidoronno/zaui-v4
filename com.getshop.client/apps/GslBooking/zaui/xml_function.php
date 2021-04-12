<?php

function sxiToArray($sxi){
    $a = array();
    for( $sxi->rewind(); $sxi->valid(); $sxi->next() ) {
        if(!array_key_exists($sxi->key(), $a)){
            $a[$sxi->key()] = array();
        }

        if($sxi->hasChildren()){
            $a[$sxi->key()][] = sxiToArray($sxi->current());
        }
        else{
            $a[$sxi->key()][] = (string) $sxi->current();
        }
    }
    return $a;
}