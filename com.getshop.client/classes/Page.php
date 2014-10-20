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
            if(isset($_GET['gseditcell']) && $_GET['gseditcell']== $row->cellId) {
                $_SESSION['gseditcell'] = $_GET['gseditcell'];
            }
        }
        
        foreach ($rowsToPrint as $row) {
            $isedit = false;

            if ($row->cellId == "footer") {
                echo "<div class='gscell  gsdepth_0 gseditinfo' style='height:55px'>";
                echo "<div class='gsinner gsdepth_0'>";
                echo '<div class="gs_addcell" incell="" aftercell="" style="padding: 20px; text-align:center"><span style="border: solid 1px; padding: 10px; background-color:#BBB;">Add row</span></div>';
                echo "</div></div>";
            }

            if (isset($_SESSION['gseditcell']) && $_SESSION['gseditcell'] === $row->cellId) {
                echo "<div class='gscell gsdepth_0 gseditinfo' style='height: 38px;'>";
                echo "<div class='gsinner gsdepth_0'>";
                echo "<div class='gseditrowheading' cellid='" . $row->cellId . "'>";
                if ($row->cellId != "footer" && $row->cellId != "header") {
                    echo "<i class='fa fa-trash-o' type='delete' title='Delete this row'></i>";
                }
                echo "<i class='fa fa-cogs' type='settings' title='Rows settings'></i>";
                echo "<i class='fa fa-plus' type='addvertical' title='Add column'></i>";
                echo "<label style='float:left;'>";
                echo "<input type='checkbox' style='background-color:#FFF;' class='gsdisplaygridcheckbox'> Add spacing to grid";
                echo "</label>";
                echo "You are now in edit mode for this row." . " - " . "<span class='gsdoneeditbutton' done='true'true'>done editing</span>";
                echo "</div>";
                echo "</div>";
                echo "</div>";
                $isedit = true;
                $beenEdited = true;
            }
            $this->printCell($row, $count, 0, 0, $isedit);
            $count++;
            if ($isedit) {
                echo "<div class='gscell gsdepth_0 gsendedit gseditinfo'>";
                echo "<div class='gsinner gsdepth_0'>";
                echo "<div class='gseditrowheading'>";
                echo "End of row to edit.";
                echo "</div>";
                echo "</div>";
                echo "</div>";
            }
        }

        if ($beenEdited) {
            ?>
            <style>
                .dragtable { background-image: url('http://quocity.com/colresizable/img/rangeBar.png'); background-position: 10px 10px; background-repeat-y: no-repeat;}
            </style>
            <?

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
        $isColumn = false;
        if ($cell->vertical && $totalcells > 1) {
            $width = 100 / $totalcells;
            if ($cell->width > 0) {
                $width = $cell->width;
            }

            $styles = "style='width:$width%; float:left;" . $cell->styles . "'";
            $direction .= " gscolumn";
            $isColumn = true;
        } else {
            $direction .= " gsrow";
        }


        $innerstyles = $cell->innerStyles;


        echo "<div $styles width='$width' class='gscell $roweditouter gsdepth_$depth gscount_$count $direction' cellid='" . $cell->cellId . "'>";
        if ($depth === 0 && !$edit) {
            echo "<i title='" . $this->factory->__f("Edit row") . "' class='fa gseditrowbutton fa-pencil-square-o'></i>";
        }
        echo "<div class='gsinner gsdepth_$depth $rowedit gscount_$count' totalcells='$totalcells' style='$innerstyles'>";
        if ($edit) {
            if (sizeof($cell->cells) > 1 && $cell->vertical && $cell->cells[0]->vertical) {
                $this->displayResizing();
            }
        }
        if ($edit && $depth != 0) {
            echo "<span class='gscellsettings'>";
            echo "<i class='fa fa-cogs'  title='Cell settings'></i>";
            echo "</span>";
        }
        if (sizeof($cell->cells) > 0) {
            $innercount = 0;
            $innerdept = $depth + 1;
            $vertical = $cell->vertical;

            foreach ($cell->cells as $innercell) {
                $this->printCell($innercell, $innercount, $innerdept, sizeof($cell->cells), $edit);
                $innercount++;
            }
//            if ($cell->vertical) {
                echo "<div style='clear:both;'></div>";
//            }
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
        echo "<div class='gscellsettingsheading'>New areas</div>";
        echo "<span class='gscolumnmenu'>";
        echo "<div class='gs_splithorizontally' type='addhorizontal'><i class='fa fa-arrows-v'></i>" . $this->factory->__w("Insert row") . "</div>";
        echo "</span>";
        echo "<div class='gs_splitvertically' type='addvertical'><i class='fa fa-arrows-h'></i>" . $this->factory->__w("Insert column") . "</div>";
        echo "<span class='gsrowmenu'>";
        echo "<div class='gs_splithorizontally' type='addbefore'><i class='fa fa-long-arrow-up'></i>" . $this->factory->__w("Create row above") . "</div>";
        echo "<div class='gs_splithorizontally' type='addafter'><i class='fa fa-long-arrow-down'></i>" . $this->factory->__w("Create row below") . "</div>";
        echo "</span>";
        echo "<span class='gscolumnmenu'>";
        echo "<div class='gs_splithorizontally' type='addbefore'><i class='fa fa-long-arrow-left'></i>" . $this->factory->__w("Create left column") . "</div>";
        echo "<div class='gs_splithorizontally' type='addafter'><i class='fa fa-long-arrow-right'></i>" . $this->factory->__w("Create right column") . "</div>";
        echo "</span>";
        echo "<div class='gscellsettingsheading'>Move area</div>";
        echo "<span class='gsrowmenu'>";
        echo "<div class='gs_splithorizontally' type='moveup'><i class='fa fa-long-arrow-up'></i>" . $this->factory->__w("Move row up") . "</div>";
        echo "<div class='gs_splithorizontally' type='movedown'><i class='fa fa-long-arrow-down'></i>" . $this->factory->__w("Move row down") . "</div>";
        echo "</span>";
        echo "<span class='gscolumnmenu'>";
        echo "<div class='gs_splithorizontally' type='moveup'><i class='fa fa-long-arrow-left'></i>" . $this->factory->__w("Move cell to the left") . "</div>";
        echo "<div class='gs_splithorizontally' type='movedown'><i class='fa fa-long-arrow-right'></i>" . $this->factory->__w("Move cell to the right") . "</div>";
        echo "</span>";
        echo "<div class='gscellsettingsheading'>Other</div>";
        echo "<div class='gs_resizing'><i class='fa fa-image'></i>" . $this->factory->__w("Styling") . "</div>";
        echo "<div class='gs_removerow' type='delete'><i class='fa fa-trash-o'></i>" . $this->factory->__w("Delete") . "</div>";
        echo "<i class='gs_closecelledit fa fa-times' style='position:absolute;right: 5px; top: 5px; cursor:pointer;'></i>";
        echo "</span>";
    }

    private function addCellResizingPanel() {
        ?>
        <span class='gsresizingpanel'>
            <div class="heading" style="cursor:pointer; text-align:center; font-size: 16px; padding: 10px;">Sizing console</div>
            <div class='gstabmenu'>
                <span class='tabbtn' target='padding'>Padding</span>
                <span class='tabbtn' target='paddinginner'>Inner padding</span>
                <span class='tabbtn' target='margins'>Margin</span>
                <span class='tabbtn' target='marginsinner'>Inner Margin</span>
                <span class='tabbtn' target='background'>Background</span>
            </div>
            <div class='gspage' target='background'>
                <div class='gsoutercolorselectionpanel'>
                    <div class='gsheading'>Outer background</div>
                    <div class='gscolorselectionpanel' level=''>
                        <table width='100%'>
                            <tr>
                                <td><i class='fa fa-trash-o gsremovebgcolor'></i> Color</td>
                                <td align='right'><input type='color' value='Select color' class='gsbgcolorinput gsbgcouter'></td>
                            </tr>
                            <tr>
                                <td><i class='fa fa-trash-o gsremoveopacity'></i> Opacity</td>
                                <td align='right'><input type='range' min='0' max='10' class='gsbgopacityinput'></td>
                            </tr>
                            <tr>
                                <td><i class='fa fa-trash-o gsremovebgimage'></i> Background image</td>
                                <td align='right'>
                                    <div class="inputWrapper">
                                        <span class='gsuploadimage' style='display:none;'><i class='fa fa-spin fa-spinner'></i></span>
                                        <input type='button' value='Choose' class='gschoosebgimagebutton'>
                                        <input class="fileInput gsbgimageselection" type="file" />
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div class='gsoutercolorselectionpanel'>
                    <div class='gsheading'>Inner background</div>
                    <div class='gscolorselectionpanel' level='.gsinner'>
                        <table width='100%'>
                            <tr>
                                <td><i class='fa fa-trash-o gsremovebgcolor'></i> Color</td>
                                <td align='right'><input type='color' value='Select color' class='gsbgcolorinput gsbginner'></td>
                            </tr>
                            <tr>
                                <td><i class='fa fa-trash-o gsremoveopacity'></i> Opacity</td>
                                <td align='right'><input type='range' min='0' max='10' class='gsbgopacityinput'></td>
                            </tr>
                            <tr>
                                <td><i class='fa fa-trash-o gsremovebgimage'></i> Background image</td>
                                <td align='right'>
                                    <div class="inputWrapper">
                                        <span class='gsuploadimage' style='display:none;'><i class='fa fa-spin fa-spinner'></i></span>
                                        <input type='button' value='Choose' class='gschoosebgimagebutton'>
                                        <input class="fileInput gsbgimageselection" type="file" />
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
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
