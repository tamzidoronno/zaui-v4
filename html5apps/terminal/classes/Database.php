<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Database
 *
 * @author ktonder
 */
class Database {

    public function setConfig($key, $value) {
        $filePath = "etc/$key";
        $fp = fopen($filePath, "w");
        fwrite($fp, serialize($value));
        fclose($fp);
    }

    public function getConfig($key) {
        $filePath = "etc/$key";

        if (file_exists($filePath)) {
            $objData = file_get_contents($filePath);
            $obj = unserialize($objData);
            return $obj;
        }

        return null;
    }
    
    public function getMultiLevelName() {
        $multilevelname = $this->getConfig("multilevelname");
        if (!$multilevelname)
            return "default";
        
        return $multilevelname;
    }

    public function getTerminalId() {
        $terminalId = $this->getConfig("terminalid");
        if (!$terminalId)
            return 1;
        
        return $terminalId;
    }
}
