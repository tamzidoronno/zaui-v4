<?php

class Page {
    var $javapage;
    /* @var $factory Factory */
    var $factory;
    
    /**
     * 
     * @param type $javapage
     * @param Factory $factory
     */
    function __construct($javapage, $factory) {
        $this->javapage = $javapage;
        $this->factory = $factory;
    }
    
    function getApplications() {
        return array();
    }
    
    function getId() {
        return $this->javapage->id;
    }

    function loadSkeleton() {
        /* @var $layout core_pagemanager_data_PageLayout */
        $layout = $this->javapage->layout;
        
        if($layout->header) {
            $this->printRow($layout->header);
        }
        
        foreach($layout->rows as $row) {
            $this->printRow($row);
        }
        echo '<span class="gs_addcell" incell="" aftercell="">Add row</span>';
        
        if($layout->footer) {
            $this->printRow($layout->footer);
        }
    }
    
    function printRow($row) {
        print_r($row);
    }
    
    
}
