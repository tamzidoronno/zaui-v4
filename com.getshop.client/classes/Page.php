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

        $this->addCellConfigPanel();
        $this->addCellResizingPanel();

        $rowsToPrint = array();
        $rowsToPrint[] = $layout->header;
        $rowsToPrint = array_merge($rowsToPrint, $layout->rows);
        $rowsToPrint[] = $layout->footer;


        $count = 0;
        $beenEdited = false;
        foreach ($rowsToPrint as $row) {
            $isedit = false;

            if ($row->cellId == "footer") {
                echo "<div class='gscell  gsdepth_0' style='height:60px'>";
                echo "<div class='gsinner gsdepth_0'>";
                echo '<div class="gs_addcell" incell="" aftercell="" style="padding: 20px; text-align:center"><span style="border: solid 1px; padding: 10px; background-color:#BBB;">Add row</span></div>';
                echo "</div></div>";
            }
            
            if (isset($_SESSION['gseditcell']) && $_SESSION['gseditcell'] === $row->cellId) {
                echo "<div class='gscell gsdepth_0' style='height: 38px;'>";
                echo "<div class='gsinner gsdepth_0'>";
                echo "<div class='gseditrowheading' cellid='" . $row->cellId . "'>";
                if ($row->cellId != "footer" && $row->cellId != "header") {
                    echo "<i class='fa fa-trash-o' type='delete' title='Delete this row'></i>";
                    echo "<i class='fa fa-arrow-up' type='moveup' title='Move row up'></i>";
                    echo "<i class='fa fa-arrow-down' type='movedown' title='Move row down'></i>";
                }
                echo "You are now in edit mode for this row." . " - " . "<span class='gsdoneeditbutton' done='true'true'>done editing</span>";
                echo "</div>";
                echo "</div>";
                echo "</div>";
                $isedit = true;
                $beenEdited = true;
                ?>
                <style>
                    .dragtable { background-image: url('http://quocity.com/colresizable/img/rangeBar.png'); background-position: 10px 10px; background-repeat-y: no-repeat;}
                </style>
                <?

            }
            $this->printCell($row, $count, 0, 0, $isedit);
            $count++;
            if ($isedit) {
                echo "<div class='gscell gsdepth_0 gsendedit'>";
                echo "<div class='gsinner gsdepth_0'>";
                echo "<div class='gseditrowheading'>";
                echo "End of row to edit.";
                echo "</div>";
                echo "</div>";
                echo "</div>";
            }
        }
        
        if($beenEdited) {
            echo "<script src='/js/colresize.js'>";
        }
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
//        echo "<pre>";
//        print_r($cell);
//        echo "</pre>";

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
        $styles = "style='$cell->styles';";
        $width = 100;
        if ($cell->vertical && $totalcells > 1) {
            $width = 100 / $totalcells;
            if ($cell->width > 0) {
                $width = $cell->width;
            }

            $styles = "style='width:$width%; float:left;".$cell->styles."'";
        }
        
                    
        $innerstyles = $cell->innerStyles;


        echo "<div $styles width='$width' class='gscell $roweditouter gsdepth_$depth gscount_$count $direction' cellid='" . $cell->cellId . "'>";
        if ($depth === 0 && !$edit) {
            echo "<i class='fa gseditrowbutton fa-pencil-square-o'></i>";
        }
        echo "<div class='gsinner gsdepth_$depth $rowedit gscount_$count' totalcells='$totalcells' style='$innerstyles'>";
        if ($edit) {
            if (sizeof($cell->cells) > 1 && $cell->vertical && $cell->cells[0]->vertical) {
                $this->displayResizing();
            }
        }
        if ($edit) {
            echo "<span class='gscellsettings'></span>";
        }
        if (sizeof($cell->cells) > 0) {
            $innercount = 0;
            $innerdept = $depth + 1;
            $vertical = $cell->vertical;

            foreach ($cell->cells as $innercell) {
                $this->printCell($innercell, $innercount, $innerdept, sizeof($cell->cells), $edit);
                $innercount++;
            }
            if ($cell->vertical) {
                echo "<div style='clear:both;'></div>";
            }
        } else {
            echo "<div class='applicationarea' appid='" . $cell->appId . "' area='" . $cell->cellId . "'>";
            if (!$cell->appId) {
                echo "<span class='gsaddcontent'>";
                echo "<i class='fa fa-plus-circle gs_show_application_add_list'></i>";
                echo "</span>";
                $this->printApplicationAddCellRow($cell);
            } else {
                $this->renderApplication($cell);
            }
            echo "</div>";
        }
        echo "</div>";
        echo "</div>";
    }

    private function addCellConfigPanel() {
        echo "<span class='gscellsettingspanel'>";
        echo "<div class='gs_splithorizontally' type='addhorizontal'><i class='fa fa-chevron-circle-right'></i>" . $this->factory->__w("Add row") . "</div>";
        echo "<div class='gs_splitvertically' type='addvertical'><i class='fa fa-chevron-circle-down'></i>" . $this->factory->__w("Add column") . "</div>";
        echo "<div class='gs_resizing' type='delete'><i class='fa fa-arrows'></i>" . $this->factory->__w("Margin, padding, sizing") . "</div>";
        echo "<div class='gs_removerow' type='delete'><i class='fa fa-image'></i>" . $this->factory->__w("Background image") . "</div>";
        echo "<div class='gs_removerow' type='delete'><i class='fa fa-trash-o'></i>" . $this->factory->__w("Delete") . "</div>";
        echo "<i class='gs_closecelledit fa fa-times' style='position:absolute;right: 5px; top: 5px;'></i>";
        echo "</span>";
    }

    private function addCellResizingPanel() {
        ?>
        <span class='gsresizingpanel'>
            <div class="heading" style="cursor:pointer; text-align:center; font-size: 16px; padding: 10px;">Sizing console</div>
            <div class='gstabmenu'>
                <span class='tabbtn' target='padding' type="outer">Padding</span>
                <span class='tabbtn' target='paddinginner' type="inner">Inner padding</span>
                <span class='tabbtn' target='margins' type="outer">Margin</span>
                <span class='tabbtn' target='marginsinner' type="inner">Inner Margin</span>
            </div>
            <div class='gspage' target='margins'>
                <table width='100%'>
                    <tr>
                        <td>Left margin</td>
                        <td><input type='range'></td>
                        <td><input type='txt' data-csstype='margin-left' class='sizetxt'></td>
                    </tr>
                    <tr>
                        <td>Right margin</td>
                        <td><input type='range'></td>
                        <td><input type='txt' data-csstype='margin-right' class='sizetxt'></td>
                    </tr>
                    <tr>
                        <td>Top margin</td>
                        <td><input type='range'></td>
                        <td><input type='txt' data-csstype='margin-top' class='sizetxt'></td>
                    </tr>
                    <tr>
                        <td>Bottom margin</td>
                        <td><input type='range'></td>
                        <td><input type='txt' data-csstype='margin-bottom' class='sizetxt'></td>
                    </tr>
                </table>
            </div>
            <div class='gspage' target='marginsinner'>
                <table width='100%'>
                    <tr>
                        <td>Top margin</td>
                        <td><input type='range'></td>
                        <td><input type='txt' data-csstype='margin-top' class='sizetxt' level=".gsinner"></td>
                    </tr>
                    <tr>
                        <td>Bottom margin</td>
                        <td><input type='range'></td>
                        <td><input type='txt' data-csstype='margin-bottom' class='sizetxt' level=".gsinner"></td>
                    </tr>
                    <tr>
                        <td>Left margin</td>
                        <td><input type='range'></td>
                        <td><input type='txt' data-csstype='margin-left' class='sizetxt' level=".gsinner"></td>
                    </tr>
                    <tr>
                        <td>Right margin</td>
                        <td><input type='range'></td>
                        <td><input type='txt' data-csstype='margin-right' class='sizetxt' level=".gsinner"></td>
                    </tr>
                </table>
            </div>
            <div class='gspage' target='padding'>
                <table width='100%'>
                    <tr>
                        <td>Left padding</td>
                        <td><input type='range' ></td>
                        <td><input type='txt' data-csstype='padding-left' class='sizetxt'></td>
                    </tr>
                    <tr>
                        <td>Right padding</td>
                        <td><input type='range' ></td>
                        <td><input type='txt'  data-csstype='padding-right' class='sizetxt'></td>
                    </tr>
                    <tr>
                        <td>Top padding</td>
                        <td><input type='range' ></td>
                        <td><input type='txt' data-csstype='padding-top' class='sizetxt'></td>
                    </tr>
                    <tr>
                        <td>Bottom padding</td>
                        <td><input type='range'></td>
                        <td><input type='txt' data-csstype='padding-bottom' class='sizetxt'></td>
                    </tr>
                </table>
            </div>
            <div class='gspage' target='paddinginner'>
                <table width='100%'>
                    <tr>
                        <td>Left padding</td>
                        <td><input type='range' ></td>
                        <td><input type='txt' data-csstype='padding-left' class='sizetxt' level=".gsinner"></td>
                    </tr>
                    <tr>
                        <td>Right padding</td>
                        <td><input type='range' ></td>
                        <td><input type='txt'  data-csstype='padding-right' class='sizetxt' level=".gsinner"></td>
                    </tr>
                    <tr>
                        <td>Top padding</td>
                        <td><input type='range' ></td>
                        <td><input type='txt' data-csstype='padding-top' class='sizetxt' level=".gsinner"></td>
                    </tr>
                    <tr>
                        <td>Bottom padding</td>
                        <td><input type='range'></td>
                        <td><input type='txt' data-csstype='padding-bottom' class='sizetxt' level=".gsinner"></td>
                    </tr>
                </table>
            </div>
            <div>
                <label>
                    <input type="checkbox" class="gsshowvisualization" checked> Show visualization
                </label>
            </div>
            <div>
                <span class="modifybutton closeresizing">Undo changes</span>
                <span class="modifybutton gssavechanges" style="float:right;">Save changes</span>
            </div>
        </span>
        <script>
            $('.gsresizingpanel').draggable({handle: ".heading"});
        </script>
        <?

    }

    public function displayResizing() {
        ?>
        <style>
            .range{
                border:none;
                height:15px;
                background-image: url('http://quocity.com/colresizable/img/range.png');	
            }

            #selection{
                background-image: url('http://quocity.com/colresizable/img/range.png');
                background-position: 0px -15px;
                padding:0px;
                margin:0px;
                vertical-align:text-top;
            }


            #selection span{
                width:100%;
                height:15px;
                position:relative;
                display:block;
                content:"";
            }

            #selection span:after{
                background-image: url('http://quocity.com/colresizable/img/range.png');
                background-position: 0px -30px;
                position:absolute;
                left:0px;
                top:0px;
                display:block;
                content:"";
                height:15px;
                width:22px;
                display:block;
                content:"";
            }

            #selection span:before{
                background-image: url('http://quocity.com/colresizable/img/range.png');
                background-position: 0px -45px;
                position:absolute;
                right:0px;
                top:0px;
                display:block;
                content:"";
                height:15px;
                width:22px;
                display:block;
                content:"";
            }


            .range td{
                border:none;
            }

            .rangeGrip{
                width:14px;
                height:21px;
                background-image: url('http://quocity.com/colresizable/img/range.png');
                background-position: 0px -60px;
                position:absolute;
                left:-3px;
                top:-3px;
                z-index:8;
            }


            .rangeDrag .rangeGrip, .rangeGrip:hover{
                background-position: -14px -60px;
            }

            #text{
                color:#034a92;
                float:right;

            }                    
        </style>
        <?

    }

}
