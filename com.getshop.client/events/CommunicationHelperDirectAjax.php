<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class CommunicationHelperDirectAjax {
    private $getshopAddress = "";
    
    function __construct($getshopAddress) {
        $this->getshopAddress = $getshopAddress;
    }

    public function sendMessage($event, $debug=false) {
        $ch = curl_init();
    
        curl_setopt($ch, CURLOPT_URL, $this->getshopAddress."/scripts/api.php");
        curl_setopt($ch, CURLOPT_POST, 1);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
        curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($event));
        
        // Receive server response ...
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

        $res = curl_exec($ch);
        return json_decode($res);
    }
    
    public function object_unset_nulls($obj) {
        if (!is_array($obj) && !is_object($obj))
            return $obj;

        $arrObj = is_object($obj) ? get_object_vars($obj) : $obj;
        foreach ($arrObj as $key => $val) {
            $val = (is_array($val) || is_object($val)) ? $this->object_unset_nulls($val) : $val;

            if (is_array($obj))
                $obj[$key] = $val;
            else
                $obj->$key = $val;

            if ($val === null || $val === "")
                unset($obj->$key);
        }
        return $obj;
    }

    private function getClass($array) {
        if (!is_array($array) || !isset($array['className'])) {
            return null;
        }

        $className = $array['className'];
        $className = str_replace("com.thundashop.", "", $className);
        $className = str_replace(".", "_", $className);
        $class = new $className();

        return $class;
    }

    private function createThundashopObject($array) {
        $class = $this->getClass($array);

        if ($class == null && !is_array($array)) {
            return $array;
        }

        foreach (array_keys($array) as $key) {
            if (!is_object($class)) {
                $class[$key] = $this->createThundashopObject($array[$key]);
            } else {
                $class->$key = $this->createThundashopObject($array[$key]);
            }
        }

        return $class;
    }

    function cast($destination, $sourceObject) {
        if ($sourceObject == null)
            return;

        if (is_string($destination)) {
            $destination = new $destination();
        }
        $sourceReflection = new ReflectionObject($sourceObject);
        $destinationReflection = new ReflectionObject($destination);
        $sourceProperties = $sourceReflection->getProperties();
        foreach ($sourceProperties as $sourceProperty) {
            $sourceProperty->setAccessible(true);
            $name = $sourceProperty->getName();
            $value = $sourceProperty->getValue($sourceObject);
            if ($destinationReflection->hasProperty($name)) {
                $propDest = $destinationReflection->getProperty($name);
                $propDest->setAccessible(true);
                $propDest->setValue($destination, $value);
            } else {
                $destination->$name = $value;
            }
        }
        return $destination;
    }
}