<?php

class Page {

    var $javapage;
    /*  @var $factory Factory */
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

        echo "<span class='gscellsettingspanel'>";
        echo "<div class='gs_splithorizontally' type='split_horizontal'><i class='fa fa-chevron-circle-right'> " . $this->factory->__w("Split horizontally") . "</i></div>";
        echo "<div class='gs_splitvertically' type='split_vertical'><i class='fa fa-chevron-circle-down'> " . $this->factory->__w("Split vertically") . "</i></div>";
        echo "<div class='gs_removerow' type='delete'><i class='fa fa-trash-o'> " . $this->factory->__w("Delete") . "</i></div>";
        echo "</span>";
        
        if ($layout->header) {
            echo "<div class='gsheader'>";
            echo "<div class='gsinner'>";
            $this->printCell($layout->header);
            echo "</div>";
            echo "</div>";
        }
        $count = 0;
        foreach ($layout->rows as $row) {
            $this->printCell($row,$count, 0, 0);
            $count++;
        }
        echo '<span class="gs_addcell" incell="" aftercell="">Add row</span>';

        
        if ($layout->footer) {
            $this->printCell($layout->footer);
        }
    }

    function printCell($cell, $count, $depth, $totalcells) {
        $direction = "gshorisontal";
        if($cell->vertical) {
            $direction = "gsvertical";
        }
        
        echo "<div class='gscell gsdepth_$depth gscount_$count $direction' cellid='".$cell->cellId."' totalcells='$totalcells'>";
        echo "<div class='gsinner gsdepth_$depth gscount_$count' totalcells='$totalcells'>";
        
        echo "<i class='fa fa-tags gscellsettings'></i>";
        if(sizeof($cell->cells) > 0) {
            $innercount=0;
            $innerdept = $depth+1;
            foreach($cell->cells as $innercell) {
                $this->printCell($innercell, $innercount, $innerdept, sizeof($cell->cells));
                $innercount++;
            }
        } else {
            if(!$cell->appId) {
                echo "<span class='gsaddcontent'>";
                echo "<i class='fa fa-plus-circle'></i>";
                echo "</span>";
            }
        }
        echo "</div>";
        echo "</div>";
    }

}
