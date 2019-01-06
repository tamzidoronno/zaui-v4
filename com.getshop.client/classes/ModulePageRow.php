<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ModulePageRow
 *
 * @author ktonder
 */
class ModulePageRow {
    private $columns = array();
    private $page;
    public $ignoreTopRow = false;
    
    function __construct($page) {
        $this->page = $page;
    }

    public function addColumn($appId, $appInstanceId) {
        $column = new ModulePageColumn($appId, $appInstanceId, $this->page);
        $this->columns[] = $column;
    }
    
    public function addText($text) {
        $column = new ModulePageColumn(null, null, $this->page);
        $column->text = $text;
        $this->columns[] = $column;
    }
    
    /**
     * 
     * @return ModulePageColumn[]
     */
    function getColumns() {
        return $this->columns;
    }
}