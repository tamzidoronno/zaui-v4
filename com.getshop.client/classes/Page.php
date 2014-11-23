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

    function getId() {
        return $this->javapage->id;
    }

    function loadSkeleton() {
        /* @var $layout core_pagemanager_data_PageLayout */
        $layout = $this->javapage->layout;

        $beenEdited = false;
        foreach ($layout->areas as $area => $rowsToPrint) {
            foreach ($rowsToPrint as $row) {
                if (isset($_GET['gseditcell']) && $_GET['gseditcell'] == $row->cellId) {
                    $_SESSION['gseditcell'] = $_GET['gseditcell'];
                }
            }
        }

        $editedCellid = null;
        echo "<div class='gsarea' area='header'>";
        $edited = $this->printArea($layout->areas->{'header'});
        if ($edited) {
            $editedCellid = $edited;
        }
        echo "</div>";

        echo "<div class='gsarea' area='body'>";
        if (isset($layout->areas->{'body'})) {
            $edited = $this->printArea($layout->areas->{'body'});
            if ($edited) {
                $editedCellid = $edited;
            }
        }
        echo "</div>";


        echo "<div class='gsarea' area='footer'>";
        $edited = $this->printArea($layout->areas->{'footer'});
        if ($edited) {
            $editedCellid = $edited;
        }
        echo "</div>";

        echo "<style id='csseditedbyuser'>";
        foreach ($layout->areas as $section) {
            $this->printCss($section);
        }
        echo "</style>";

        if ($editedCellid != null) {
            $this->addCellConfigPanel();
            $this->addCellResizingPanel();
            $this->addCarouselSettingsPanel();
            $this->addTabPanel();
            $this->displayResizing();
            ?>
            <style>
                .dragtable { background-image: url('http://quocity.com/colresizable/img/rangeBar.png'); background-position: 10px 10px; background-repeat-y: no-repeat;}
            </style>
            <script>
                setTimeout(function () {
                    thundashop.framework.loadResizing($('.gscell[cellid="<? echo $editedCellid; ?>"]'), true);
                }, "200");
                PubSub.subscribe('NAVIGATION_COMPLETED', function (a, b) {
                    if (thundashop.framework.lastRotatedCell) {
                        var cell = $('.gscell[cellid="' + thundashop.framework.lastRotatedCell + '"]');
                        if (!cell.hasClass('gseditrowouter')) {
                            return;
                        }
                        var container = cell.closest('.gscontainer');
                        if (container.hasClass('rotatingcontainer')) {
                            container.find('.gsrotating').css('opacity', '0');
                            cell.css('opacity', '1');
                            cell.css('z-index', '2');
                        }
                        if (container.hasClass('tabcontainer')) {
                            container.find('.gstab').hide();
                            cell.show();
                            container.find('.gsactivetab').removeClass('gsactivetab');
                            container.find('.gstabbtn[cellid="'+thundashop.framework.lastRotatedCell+'"]').addClass('gsactivetab');
                        }

                        $('.gseditrowheading').attr('cellid', thundashop.framework.lastRotatedCell);
                        setTimeout(function () {
                            thundashop.framework.loadResizing(cell, true);
                        }, "200");
                        console.log('need to reset to cell: ');
                    }
                })
            </script>
            <script src="/js/colresize.js"/>
            <?
        }
    }

    private function printCss($areas) {
        foreach ($areas as $area) {


            if (isset($area->styles) && $area->styles) {
                echo "/*start " . $area->cellId . "*/\n";
                echo $area->styles;
                echo "/*end " . $area->cellId . "*/\n";
            }

            if (sizeof($area->cells) > 0) {
                $this->printCss($area->cells);
            }
        }
    }

    private function addTabPanel() {
        ?>  
        <div class="tabsettingspanel">
            <div style="width:100%; position:absolute; top:0px; left: 0px; height: 20px; background-color:#bbb; text-align: center; padding-top: 10px;">
                <i class="fa fa-arrow-left gsoperatecell" type="moveup" target="selectedcell" style="position:absolute; top: 10px; left: 10px;" title="Move tab to the left"></i>
                <i class="fa fa-arrow-right gsoperatecell" type="movedown" target="selectedcell" style="position:absolute; top: 10px; right: 10px;" title="Move tab to the right"></i>
                <i class="fa fa-trash-o gsoperatecell" type="delete" target="selectedcell" title="Delete current cell"></i>
            </div>
            <table>
                <tr>
                    <td>Name</td>
                    <td><input type="text" class="gstabname"></td>
                </tr>
            </table>
            <input type="button" value="Done modifying" class="gsdonemodifytab"></input>
        </div>
        <?
    }
    
    private function addCarouselSettingsPanel() {
        ?>  
        <div class="carouselsettingspanel">
            <div style="width:100%; position:absolute; top:0px; left: 0px; height: 20px; background-color:#bbb; text-align: center; padding-top: 10px;">
                <i class="fa fa-arrow-left gsoperatecell" type="moveup" target="selectedcell" style="position:absolute; top: 10px; left: 10px;" title="Move cell to the left"></i>
                <i class="fa fa-arrow-right gsoperatecell" type="movedown" target="selectedcell" style="position:absolute; top: 10px; right: 10px;" title="Move cell to the right"></i>
                <i class="fa fa-trash-o gsoperatecell" type="delete" target="selectedcell" title="Delete current cell"></i>
            </div>
            <table>
                <tr>
                    <td>Height</td>
                    <td><input type="text" class="gscarouselheight">px</td>
                </tr>
                <tr>
                    <td>Timer</td>
                    <td><input type="text" class="gscarouseltimer"> milliseconds</td>
                </tr>
                <tr>
                    <td>Carousel type</td>
                    <td>
                        <select style="width: 100px;" class="gscarouseltype">
                            <option value='slideleft'>Slide left</option>
                            <option value='slideright'>Slide right</option>
                            <option value='fade'>Fade</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="button" value="Close" class="gs_closecarouselsettings">
                        
                    </td>
                    <td>
                        <input style="width: 100px;" class="savecarouselconfig" type="button" value="Save settings">
                    </td>
                </tr>
            </table>
        </div>
        <?
    }

    private function printApplicationAddCellRow($cell) {
        $this->factory->includefile("applicationlist", 'Common');
    }

    private function renderApplication($cell) {
        $instance = $this->factory->getApplicationPool()->getApplicationInstance($cell->appId);
        if ($instance) {
            $instance->renderApplication();
        }
    }

    function printCell($cell, $count, $depth, $totalcells, $edit) {
        $rowedit = "";
        $roweditouter = "";
        if ($edit) {
            $roweditouter = "gseditrowouter";
            $rowedit = "gseditrow";
        }
        $styles = "style='";
        $width = 100;
        $isColumn = false;
        if ($cell->mode == "VERTICAL" && $totalcells > 1) {
            $width = 100 / $totalcells;
            if ($cell->width > 0) {
                $width = $cell->width;
            }

            $styles = "style='width:$width%; float:left;" . $cell->styles . "'";
            $isColumn = true;
        }
        if ($cell->mode == "ROTATING") {
            if ($count === 0) {
                $styles .= " opacity:1;z-index:2;";
            }
        }

        $styles .= "'";

        $direction = "gs" . strtolower($cell->mode);

        echo "<div $styles width='$width' class='gscell $roweditouter gsdepth_$depth gscount_$count $direction gscell_" . $cell->incrementalCellId . "' incrementcellid='" . $cell->incrementalCellId . "' cellid='" . $cell->cellId . "'>";
        if ($cell->mode === "ROTATING") {
            if ($count > 0) {
                echo "<i class='fa fa-arrow-circle-left gsrotateleft gsrotatearrow'></i>";
            }
            if ($count + 1 < $totalcells) {
                echo "<i class='fa fa-arrow-circle-right gsrotateright gsrotatearrow'></i>";
            }
        }

        if ($depth === 0 && !$edit && $this->factory->isEditorMode()) {
            echo "<i title='" . $this->factory->__f("Edit row") . "' class='fa gseditrowbutton fa-pencil-square-o'></i>";
        }
        echo "<div class='gsinner gsdepth_$depth $rowedit gscount_$count gscell_" . $cell->incrementalCellId . "' totalcells='$totalcells'>";

        if ($edit && $depth != 0) {
            echo "<span class='gscellsettings'>";
            echo "<i class='fa fa-image'  title='Styling' style='display:none;'></i> ";
            echo "<i class='fa fa-cogs'  title='Cell settings'></i>";
            echo "</span>";
        }

        if (sizeof($cell->cells) > 0) {
            $this->printSubCells($cell, $edit, $depth);
            echo "<div style='clear:both;'></div>";
        } else {
            $this->printApplicationArea($cell);
        }
        if ($cell->mode === "ROTATING") {
            $this->printCarouselDots($totalcells, $edit, $count);
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
        echo "<span class='gsrowmenu'>";
        echo "<div class='gs_splitvertically' type='addvertical'><i class='fa fa-arrows-h'></i>" . $this->factory->__w("Insert column") . "</div>";
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

        echo "<span class='modesettings'>";
        echo "<div class='gs_addrotating' subtype='carousel' type='addrotate'><i class='fa fa-sitemap'></i>" . $this->factory->__w("Insert carousel cell") . "</div>";
        echo "<div class='gs_addtab' subtype='tab' type='addtab'><i class='fa fa-sitemap'></i>" . $this->factory->__w("Insert tab") . "</div>";
        echo "</span>";
        echo "</span>";
    }

    private function addCellResizingPanel() {
        ?>
        <span class='gsresizingpanel'>
            <div class="heading" style="cursor:pointer; text-align:center; font-size: 16px; padding: 10px;">Sizing console</div>
            <div class='gstabmenu'>
                <span class='tabbtn' target='css'>Css</span>
                <span class='tabbtn' target='background'>Background image</span>
            </div>
            <div class='gspage' target='css'>
                <div id="cellcsseditor" style="width:500px; height: 400px;">test</div>

            </div>
            <div class='gspage' target='background' style="padding: 10px;">
                <div class='gsoutercolorselectionpanel'>
                    <div class='gsheading'>Outer background</div>
                    <div class='gscolorselectionpanel' level=''>
                        <table width='100%'>
                            <tr>
                                <td valign="top">
                                    <select>
                                        <option>Cover</option>
                                        <option>Center</option>
                                        <option>100% width</option>
                                        <option>Normal</option>
                                    </select>
                                </td>
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
                                <td valign="top">
                                    <select>
                                        <option>Cover</option>
                                        <option>Center</option>
                                        <option>100% width</option>
                                        <option>Normal</option>
                                    </select>
                                </td>                                
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

            <div style="border-top: solid 1px #bbb;">
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

    public function printApplicationArea($cell) {
        echo "<div class='applicationarea' appid='" . $cell->appId . "' area='" . $cell->cellId . "'>";
        if (!$cell->appId && $this->factory->isEditorMode()) {
            echo "<span class='gsaddcontent'>";
            echo "<i class='fa fa-plus-circle gs_show_application_add_list'></i>";
            echo "</span>";
            $this->printApplicationAddCellRow($cell);
        } else {
            $this->renderApplication($cell);
        }
        echo "</div>";
    }

    public function printSubCells($cell, $isedit, $depth) {
        $innercount = 0;
        $innerdept = $depth + 1;

        $mode = $cell->cells[0]->mode;
        if ($mode == "TAB" || $mode == "ROTATING") {
            $config = $cell->carouselConfig;
            $containertype = strtolower($mode) . "container";
            $editClass = "";
            if ($isedit) {
                $editClass = "editcontainer";
            }
            echo "<div class='gscontainer $containertype $editClass' height='" . $config->height . "' timer='" . $config->time . "' type='" . $config->type . "' cellid='" . $cell->cellId . "'>";
            if ($cell->cells[0]->mode == "TAB") {
                $this->displayTabRow($cell, $innerdept, $isedit);
            }
        }

        foreach ($cell->cells as $innercell) {
            $this->printCell($innercell, $innercount, $innerdept, sizeof($cell->cells), $isedit);
            $innercount++;
        }
        $doCarousel = !$isedit;
        if ($cell->cells[0]->mode == "TAB" || $cell->cells[0]->mode == "ROTATING") {
            echo "</div>";
        }

        if ($cell->cells[0]->mode == "ROTATING") {
            ?>
            <style>
                .rotatingcontainer[cellid='<? echo $cell->cellId; ?>'] {  width: 100%; height: <? echo $config->height; ?>px !important; }
                .rotatingcontainer[cellid='<? echo $cell->cellId; ?>'] .gscell.gsdepth_<? echo $innerdept; ?> { width:100%; min-height: <? echo $config->height; ?>px !important; height: <? echo $config->height; ?>px !important; }
                .rotatingcontainer[cellid='<? echo $cell->cellId; ?>'] .gsinner.gsdepth_<? echo $innerdept; ?> { height: 100%; }
                <? if (($config->type === "fade" || !$config->type) && $doCarousel) { ?>
                    .rotatingcontainer[cellid='<? echo $cell->cellId; ?>'] .gsrotating {
                        -webkit-transition: opacity .5s ease-in-out;
                        -moz-transition: opacity .5s ease-in-out;
                        -ms-transition: opacity .5s ease-in-out;
                        -o-transition: opacity .5s ease-in-out;
                        transition: opacity .5s ease-in-out;
                    }
                <? } ?>
            </style>
            <? if ($doCarousel) { ?>
                <script>thundashop.framework.activateCarousel($(".rotatingcontainer[cellid='<? echo $cell->cellId; ?>']"), <? echo $config->time; ?>);</script>
            <? } else { ?>
                <script>
                    if (!thundashop.framework.lastRotatedCell) {
                        thundashop.framework.lastRotatedCell = '<? echo $cell->cells[0]->cellId; ?>';
                    }
                </script>
                <?
            }
        }
    }

    public function printArea($rowsToPrint) {
        $count = 0;
        $editedCellid = null;
        foreach ($rowsToPrint as $row) {
            $isedit = false;

            $cellid = $row->cellId;
            if (sizeof($row->cells) > 0 && $row->cells[0]->mode === "ROTATING") {
                $cellid = $row->cells[0]->cellId;
            }
            if (sizeof($row->cells) > 0 && $row->cells[0]->mode === "TAB") {
                $cellid = $row->cells[0]->cellId;
            }

            if (isset($_SESSION['gseditcell']) && $_SESSION['gseditcell'] === $row->cellId) {
                $editedCellid = $cellid;
                echo "<div class='gscell gsdepth_0 gseditinfo' style='height: 38px;'>";
                echo "<div class='gsinner gsdepth_0'>";
                echo "<div class='gseditrowheading' cellid='" . $cellid . "'>";
                echo "<i class='fa fa-cogs' type='settings' title='Rows settings'></i>";
                echo "<label style='float:left;'>";
                echo "<input type='checkbox' style='background-color:#FFF;' class='gsdisplaygridcheckbox'> Add spacing to grid";
                echo "</label>";
                echo "You are now in edit mode for this row." . " - " . "<span class='gsdoneeditbutton' done='true'true'>done editing</span>";
                echo "</div>";
                echo "</div>";
                echo "</div>";
                $isedit = true;
            }

            if (sizeof($row->cells) > 0 && $row->cells[0]->mode === "ROTATING") {
                $this->printSubCells($row, $isedit, -1);
            } else if (sizeof($row->cells) > 0 && $row->cells[0]->mode === "TAB") {
                $this->printSubCells($row, $isedit, -1);
            } else {
                $this->printCell($row, $count, 0, 0, $isedit);
            }
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
        return $editedCellid;
    }

    public function displayTabRow($cell, $depth, $edit) {
        echo "<div class='gscell'>";
        echo "<div class='gsinner gsdepth_$depth'>";
        echo "<div class='gstabrow'>";
        $first = true;
        foreach ($cell->cells as $innercell) {
            $active = "";
            if($first) {
                $first = false;
                $active = "gsactivetab";
            }
            $tabName="tab";
            if($innercell->cellName) {
                $tabName = $innercell->cellName;
            }
            echo "<span class='gstabbtn $active' incrementid='" . $innercell->incrementalCellId . "' cellid='".$innercell->cellId."'>$tabName</span>";
        }
        if ($this->factory->isEditorMode() && $edit) {
            echo "<i class='fa fa-plus gsoperatecell' type='addtab' target='container' title='Add another tab'></i> ";
            echo "<i class='fa fa-cogs tabsettings' title='Tab settings'></i>";
        }
        echo "</div>";
        echo "</div>";
        echo "</div>";
    }

    public function printCarouselDots($totalcells, $edit, $count) {
        echo "<div class='gscarouseldots'>";
        for ($i = 0; $i < $totalcells; $i++) {
            $activeCirle = "";
            if ($count == $i) {
                $activeCirle = "activecarousel";
            }
            echo "<i class='fa fa-circle gscarouseldot $activeCirle'></i>";
        }
        if ($this->factory->isEditorMode() && $edit) {
            echo "<i class='fa fa-plus addcarouselrow gsoperatecell' type='addrotate' target='container' title='Add another slider'></i>";
            echo "<i class='fa fa-cogs carouselsettings' title='Carousel settings'></i>";
        }
        echo "</div>";
    }

}
