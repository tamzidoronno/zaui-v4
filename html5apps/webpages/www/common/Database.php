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
    public function get($id, $database) {
        $db = $this->getDb($database);
        return $db->where('id', '=', $id)->fetch();
    }
    
    private function getDb($database) {
        $dataDir = "../../databases/".Config::$domain;
        $database = \SleekDB\SleekDB::store($database, $dataDir);
        return $database;
    }
    
    public function delete($id, $database) {
        $db = $this->getDb($database);
        $db->where('id', '=', $id)->delete();
    }
    
    public function save($data, $database, $commonPage=false) {
        if (!isset($data['id']) || !$data['id']) {
            throw new RuntimeException("please set a id before saving");
        }
        
        $this->checkSecurity();
        
        $router = new PageRouter();
        $pagename = $router->getCurrentPageName();
        $data['gs_page_id'] = $pagename;
        
        if ($commonPage) {
            $data['gs_page_id'] = "gs_common_area";
        }
        
        $db = $this->getDb($database);
        $existingData = $db->where('id', '=', $data['id'])->fetch();
        
        if (count($existingData)) {
            $db->where('id', '=', $data['id'])->update( $data ); 
        } else {
            $db->insert( $data ); 
        }
    }

    public function checkSecurity() {
//        throw new RuntimeException("Access Denied");
    }

    public function getAllKeysForPage($pageId, $database) {
        $db = $this->getDb($database);
        $res = $db->where('gs_page_id', '=', $pageId)->fetch();
        
        $returnRes = array();
        
        foreach ($res as $a) {
            $returnRes[] = $a['id'];
        }
        
        return $returnRes;
    }

}
