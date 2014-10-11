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
        echo "<div class='gs_splithorizontally' type='addhorizontal'><i class='fa fa-chevron-circle-right'></i>" . $this->factory->__w("Add row") . "</div>";
        echo "<div class='gs_splitvertically' type='addvertical'><i class='fa fa-chevron-circle-down'></i>" . $this->factory->__w("Add column") . "</div>";
        echo "<div class='gs_removerow' type='delete'><i class='fa fa-arrows'></i>" . $this->factory->__w("Sizing") . "</div>";
        echo "<div class='gs_removerow' type='delete'><i class='fa fa-image'></i>" . $this->factory->__w("Background image") . "</div>";
        echo "<div class='gs_removerow' type='delete'><i class='fa fa-trash-o'></i>" . $this->factory->__w("Delete") . "</div>";
        echo "<i class='gs_closecelledit fa fa-times' style='position:absolute;right: 5px; top: 5px;'></i>";
        echo "</span>";

        $rowsToPrint = array();
        $rowsToPrint[] = $layout->header;
        $rowsToPrint = array_merge($rowsToPrint, $layout->rows);
        $rowsToPrint[] = $layout->footer;
        
        
        $count = 0;
        echo "<table width='100%' cellspacing='0' cellpadding='0'>";
        foreach ($rowsToPrint as $row) {
            $isedit = false;
            
            if($row->cellId == "footer") {
                echo "<tr><td>";
                echo "<div class='gscell  gsdepth_0' style='height:60px'>";
                echo "<div class='gsinner gsdepth_0'>";
                echo '<div class="gs_addcell" incell="" aftercell="" style="padding: 20px; text-align:center"><span style="border: solid 1px; padding: 10px; background-color:#BBB;">Add row</span></div>';
                echo "</div></div>";
                echo "</td></tr>";
            }
            
            if (isset($_SESSION['gseditcell']) && $_SESSION['gseditcell'] === $row->cellId) {
                echo "<tr><td>";
                echo "<div class='gscell gsdepth_0' style='height: 38px;'>";
                echo "<div class='gsinner gsdepth_0'>";
                echo "<div class='gseditrowheading' cellid='".$row->cellId."'>";
                if($row->cellId != "footer" && $row->cellId != "header") {
                    echo "<i class='fa fa-trash-o' type='delete' title='Delete this row'></i>";
                    echo "<i class='fa fa-arrow-up' type='moveup' title='Move row up'></i>";
                    echo "<i class='fa fa-arrow-down' type='movedown' title='Move row down'></i>";
                }
                echo "You are now in edit mode for this row." . " - " . "<span class='gsdoneeditbutton' done='true'true'>done editing</span>";
                echo "</div>";
                echo "</div>";
                echo "</div>";
                echo "</td></tr>";
                $isedit = true;
            }
            echo "<tr>";
            echo "<td>";
            $this->printCell($row, $count, 0, 0, $isedit);
            $count++;
            echo "</td>";
            echo "</tr>";
            if (isset($_SESSION['gseditcell']) && $_SESSION['gseditcell'] === $row->cellId) {
                echo "<tr><td>";
                echo "<div class='gscell gsdepth_0 gsendedit'>";
                echo "<div class='gsinner gsdepth_0'>";
                echo "<div class='gseditrowheading'>";
                echo "End of row to edit.";
                echo "</div>";
                echo "</div>";
                echo "</div>";
                echo "</td></tr>";
                $isedit = true;
            }
        }
        echo "</table>";
    }

    private function printApplicationAddCellRow($cell) {
        echo "<div class='gs_add_applicationlist'>";
        $apps = $this->factory->getApi()->getStoreApplicationPool()->getApplications();
        foreach ($apps as $app) {
            $name = $app->appName;
            $id = $app->id;
            echo "<div class='gs_add_app_entry' appId='$id'>$name</div>";
        }
        echo "</div>";
    }

    private function renderApplication($cell) {
        $instance = $this->factory->getApplicationPool()->getApplicationInstance($cell->appId);
        if ($instance) {
            $instance->renderApplication();
        }
    }

    function printCell($cell, $count, $depth, $totalcells, $edit) {
        $direction = "gshorisontal";
        if ($cell->vertical) {
            $direction = "gsvertical";
        }

        $rowedit = "";
        $roweditouter = "";
        if ($edit) {
            $roweditouter = "gseditrowouter";
            $rowedit = "gseditrow";
        }

        echo "<div class='gscell $roweditouter gsdepth_$depth gscount_$count $direction' cellid='" . $cell->cellId . "' totalcells='$totalcells'>";

        if ($depth === 0 && !$edit) {
            echo "<i class='fa gseditrowbutton fa-pencil-square-o'></i>";
        }

        echo "<div class='gsinner gsdepth_$depth $rowedit gscount_$count' totalcells='$totalcells'>";
        if ($edit) {
            echo "<span class='gscellsettings'></span>";
        }
        if (sizeof($cell->cells) > 0) {
            $innercount = 0;
            $innerdept = $depth + 1;
            $vertical = $cell->vertical;
            if ($vertical) {
                echo "<table width='100%' cellspacing='0' cellpadding='0' height='100%'>";
                echo "<tr>";
                foreach ($cell->cells as $innercell) {
                    $width = 100 / sizeof($cell->cells);
                    echo "<td width='$width%'>";
                    $this->printCell($innercell, $innercount, $innerdept, 0, $edit);
                    $innercount++;
                    if ($vertical) {
                        echo "</td>";
                    }
                }
                echo "</tr>";
                echo "</table>";
            } else {
                foreach ($cell->cells as $innercell) {
                    $this->printCell($innercell, $innercount, $innerdept, sizeof($cell->cells), $edit);
                    $innercount++;
                }
            }
        } else {
            if (!$cell->appId) {
                echo "<table height='100%' width='100%' cellspacing='0' cellpadding='0'>";
                echo "<td>";
                echo "<span class='gsaddcontent'>";
                echo "<i class='fa fa-plus-circle gs_show_application_add_list'></i>";
                echo "</span>";
                $this->printApplicationAddCellRow($cell);
                echo "</td></tr></table>";
            } else {
            echo "<div class='applicationarea' appid='".$cell->appId."' area='".$cell->cellId."'>";
                $this->renderApplication($cell);
                echo "</div>";
            }
        }
        echo "</div>";
        echo "</div>";
    }

}
