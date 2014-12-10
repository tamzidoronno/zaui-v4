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

        foreach ($layout->areas as $section) {
            $this->printCss($section);
        }

        if ($editedCellid != null) {
            $this->addCellConfigPanel();
            $this->addCellResizingPanel();
            $this->displayResizing();
            $this->printLoaderForContainers();
            $this->printEditingInfo();
            ?>
            <style>
                .dragtable { background-image: url('http://quocity.com/colresizable/img/rangeBar.png'); background-position: 10px 10px; background-repeat-y: no-repeat;}
            </style>
            <script>
                setTimeout(function () {
                    thundashop.framework.loadResizing($('.gscell[cellid="<? echo $editedCellid; ?>"]'), true);
                }, "200");
            </script>
            <script src="/js/colresize.js"/>
            <?
        }
    }

    private function printCss($areas) {
        foreach ($areas as $area) {

            if (isset($area->styles) && $area->styles) {
                $area->styles = str_replace("{incrementcellid}", $area->incrementalCellId, $area->styles);
                echo "<style cellid='" . $area->cellId . "'>" . $area->styles . "</style>" . "\n";
            }

            if (sizeof($area->cells) > 0) {
                $this->printCss($area->cells) . "\n";
            }
        }
    }

    private function addTabPanel() {
        ?>  
        <div class="tabsettingspanel">
            <div style="width:100%; position:absolute; top:0px; left: 0px; height: 20px; background-color:#bbb; text-align: center; padding-top: 10px;">
                Tab settings
            </div>
            <table>
                <tr>
                    <td>Name</td>
                    <td><input type="text" class="gstabname"></td>
                </tr>
            </table>
            <input type="button" value="Done modifying" class="gsdonemodifytab"></input>
            <div class="gsoperatecell" type="moveup" target="selectedcell"> Move tab to the left</div>
            <div class="gsoperatecell" type="movedown" target="selectedcell"> Move tab to the right</div>
            <div class="gsoperatecell" target="selectedcell" type="delete"> Remove selected tab</div>
        </div>
        <script>$('.tabsettingspanel').draggable();</script>
        <?
    }

    private function printEditingInfo() {
        echo "<div class='gseditinginfo'>";
        echo "<input type='checkbox' style='background-color:#FFF;' class='gsdisplaygridcheckbox'> Add spacing to grid";
        echo "You are now editing the selected row, <span class='gsdoneeditbutton' done='true'true'>done editing</span>";
        echo "</div>";
    }

    private function addCarouselSettingsPanel() {
        ?>  
        <div class="carouselsettingspanel">
            <div style="width:100%; position:absolute; top:0px; left: 0px; height: 20px; background-color:#bbb; text-align: center; padding-top: 10px; cursor:pointer;">
                Carousel settings
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
            Cell operations:<br>
            <div class="gsoperatecell" target="container" type="delete">Delete carousel</div>
            <div class="gsoperatecell" target="container" type="moveup">Move carousel up</div>
            <div class="gsoperatecell" target="container" type="movedown">Move carousel down</div>
            <div class="gsoperatecell" target="container" type="addbefore">Create row above</div>
            <div class="gsoperatecell" target="container" type="addafter">Create row below</div>
            <div class="gsoperatecell" type="moveup" target="selectedcell">Move slide to the left</div>
            <div class="gsoperatecell" type="movedown" target="selectedcell">Move slide to the right</div>
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

    function printCell($cell, $count, $depth, $totalcells, $edit, $parent) {

        $rowedit = "";
        $roweditouter = "";
        if ($edit) {
            $roweditouter = "gseditrowouter";
            $rowedit = "gseditrow";
        }
        $styles = "style='";
        $width = 100;
        $isColumn = false;
        $additionalinfo = "";
        if ($cell->mode == "ROTATING") {
            $additionalinfo = "height='" . $cell->carouselConfig->height . "' timer='" . $cell->carouselConfig->time . "' type='" . $cell->carouselConfig->type . "'";
            $styles .= "height: " . $cell->carouselConfig->height . "px;";
        }
        if ($cell->mode == "COLUMN" && $totalcells > 1) {
            $width = 100 / $totalcells;
            if ($cell->width > 0) {
                $width = $cell->width;
            }

            $styles = "style='width:$width%; float:left;'";
            $isColumn = true;
        }

        $styles .= "'";

        $container = "";
        $gscell = "gscell";
        $gscellinner = "gsinner";
        if ($cell->mode === "ROTATING" || $cell->mode === "TAB") {
            $container = "gscontainer";
            $gscell = "gscontainercell";
            $gscellinner = "gscontainerinner";
            $roweditouter = "";
            $rowedit = "";
        }

        $gsrotatingrow = "";

        if (!$isColumn && $parent != null && $parent->mode == "ROTATING") {
            $gsrotatingrow = "gsrotatingrow";
        }
        if (!$isColumn && $parent != null && $parent->mode == "TAB") {
            $gsrotatingrow = "gstabrow";
        }

        $mode = "gs" . strtolower($cell->mode);

        $marginsclasses = "";
        if ($totalcells > ($count + 1)) {
            $marginsclasses .= "gs_margin_right ";
        }

        if ($count > 0) {
            $marginsclasses .= " gs_margin_left";
        }
        echo "<div $additionalinfo $styles width='$width' class='gsucell $gscell $gsrotatingrow $container $marginsclasses $roweditouter gsdepth_$depth gscount_$count $mode gscell_" . $cell->incrementalCellId . "' incrementcellid='" . $cell->incrementalCellId . "' cellid='" . $cell->cellId . "'>";
        if ($parent != null && $parent->mode === "ROTATING") {
            if ($count > 0) {
                echo "<i class='fa fa-arrow-circle-left gsrotateleft gsrotatearrow'></i>";
            }
            if ($count + 1 < $totalcells) {
                echo "<i class='fa fa-arrow-circle-right gsrotateright gsrotatearrow'></i>";
            }
        }

        if ($depth === 0 && !$edit && $this->factory->isEditorMode()) {
            if ($parent == null || ($parent->mode != "TAB" && $parent->mode != "ROTATING")) {
                echo "<i title='" . $this->factory->__f("Edit row") . "' class='fa gseditrowbutton fa-pencil-square-o'></i>";
            }
        }
        echo "<div class='$gscellinner gsuicell gsdepth_$depth $container $rowedit gscount_$count gscell_" . $cell->incrementalCellId . "' totalcells='$totalcells'>";

        if ($cell->mode == "TAB") {
            $this->addTabPanel();
        }

        if ($parent != null && $parent->mode == "TAB") {
            $this->displayTabRow($parent, $edit, $cell);
        }


        if ($edit) {
            $this->printEasyModeEdit($cell);
            echo "<span class='gscellsettings'>";
            echo "<i class='fa fa-cogs'  title='Cell settings'></i>";
            echo "</span>";
        }

        if (sizeof($cell->cells) > 0) {
            $counter = 0;
            $depthprint = $depth + 1;
            if ($cell->mode == "TAB" || $cell->mode == "ROTATING") {
                $depthprint--;
            }

            if ($parent != null && $parent->mode == "TAB") {
                echo "<div class='gs_tab_conte_container'>";
            }

            foreach ($cell->cells as $innercell) {
                $this->printCell($innercell, $counter, $depthprint, sizeof($cell->cells), $edit, $cell);
                $counter++;
            }

            if ($parent != null && $parent->mode == "TAB") {
                echo "</div>";
            }

            if ($cell->mode == "ROTATING" || $cell->mode == "TAB") {
                $this->printContainerSettings((!$edit && $cell->mode == "ROTATING"), $cell, $depth);
            }


            echo "<div style='clear:both;'></div>";
        } else {
            $this->printApplicationArea($cell);
        }
        if ($parent != null && $parent->mode === "ROTATING") {
            $this->printCarouselDots($totalcells, $edit, $count, $cell->cellId);
        }
        if ($cell->mode == "ROTATING") {
            $this->addCarouselSettingsPanel();
        }

        echo "</div>";
        echo "</div>";
    }

    private function addCellConfigPanel() {
        ?>
        <span class='gscellsettingspanel'>
            <div class='gscellsettingspanelheading'>
                <i class='gs_closecelledit fa fa-times' style='float:right; margin-right:0px;'></i>
                Cell / row settings
            </div>
            <div style='margin-top:0px;' class='gscellsettingsheading'>New areas</div>
            <div class='gsinnercellsettingspanel'>
                <div class='gsoperatecell' type='addrow'><i class='fa fa-arrows-v'></i><? echo $this->factory->__w("Insert row"); ?></div>
                <div class='gsoperatecell' type='addcolumn'><i class='fa fa-arrows-h'></i><? echo $this->factory->__w("Insert column"); ?></div>
                <span class='gsrowmenu'>
                    <div class='gsoperatecell' type='addbefore'><i class='fa fa-arrow-up'></i><? echo $this->factory->__w("Create row above"); ?></div>
                    <div class='gsoperatecell' type='addafter'><i class='fa fa-arrow-down'></i><? echo $this->factory->__w("Create row below"); ?></div>
                </span>
                <span class='gscolumnmenu'>
                    <div class='gsoperatecell' type='addbefore'><i class='fa fa-arrow-left'></i><? echo $this->factory->__w("Create left column"); ?></div>
                    <div class='gsoperatecell' type='addafter'><i class='fa fa-arrow-right'></i><? echo $this->factory->__w("Create right column"); ?></div>
                </span>
            </div>
            <div class='gscellsettingsheading'>Move area</div>
            <div class='gsinnercellsettingspanel'>
                <span class='gsrowmenu'>
                    <div class='gsoperatecell' type='moveup'><i class='fa fa-arrow-up'></i><? echo $this->factory->__w("Move row up"); ?></div>
                    <div class='gsoperatecell' type='movedown'><i class='fa fa-arrow-down'></i><? echo $this->factory->__w("Move row down"); ?></div>
                </span>
                <span class='gscolumnmenu'>
                    <div class='gsoperatecell' type='moveup'><i class='fa fa-arrow-left'></i><? echo $this->factory->__w("Move cell to the left"); ?></div>
                    <div class='gsoperatecell' type='movedown'><i class='fa fa-arrow-right'></i><? echo $this->factory->__w("Move cell to the right"); ?></div>
                </span>
            </div>
            <div class='gscellsettingsheading'>Other</div>
            <div class='gsinnercellsettingspanel'>
                <div class='gs_resizing'><i class='fa fa-image'></i><? echo $this->factory->__w("Styling"); ?></div>
                <div class='gsoperatecell' type='delete'><i class='fa fa-trash-o'></i><? echo $this->factory->__w("Delete"); ?></div>

                <div class='gsoperatecell' subtype='carousel' type='setcarouselmode'><i class='fa fa-sitemap'></i><? echo $this->factory->__w("Change to carousel mode"); ?></div>
                <div class='gsoperatecell' subtype='tab' type='settabmode'><i class='fa fa-ellipsis-h'></i><? echo $this->factory->__w("Change to tab mode"); ?></div>
            </div>
        </span>
        <script>$('.gscellsettingspanel').draggable();</script>
        <?
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
                <div id="cellcsseditor" style="width:500px; height: 400px;">
                </div>

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
                <div class='gsoutercolorselectionpanel' style="display:none;">
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

    public function printContainerSettings($doCarousel, $cell, $depth) {
        $config = $cell->carouselConfig;
        ?>
        <script>
            var cellid = "<? echo $cell->cellId; ?>";
        <? if ($doCarousel) { ?>
                thundashop.framework.activateCarousel($(".gsrotating[cellid='<? echo $cell->cellId; ?>']"), <? echo $config->time; ?>);
        <? } else { ?>
                if (!thundashop.framework.lastRotatedCell[cellid]) {
                    thundashop.framework.lastRotatedCell[cellid] = '<? echo $cell->cells[0]->cellId; ?>';
                }
        <? } ?>

        </script>

        <style>
            .gsrotating[cellid='<? echo $cell->cellId; ?>'] {  width: 100%; height: <? echo $config->height; ?>px !important; }
            .gsrotating[cellid='<? echo $cell->cellId; ?>'] .gscell.gsdepth_<? echo $depth; ?> { width:100%; min-height: <? echo $config->height; ?>px !important; height: <? echo $config->height; ?>px !important; }
            .gsrotating[cellid='<? echo $cell->cellId; ?>'] .gsinner.gsdepth_<? echo $depth; ?> { height: 100%; }
            <? if (($config->type === "fade" || !$config->type) && $doCarousel) { ?>
                .gsrotating[cellid='<? echo $cell->cellId; ?>'] .gscell {
                    -webkit-transition: opacity .5s ease-in-out;
                    -moz-transition: opacity .5s ease-in-out;
                    -ms-transition: opacity .5s ease-in-out;
                    -o-transition: opacity .5s ease-in-out;
                    transition: opacity .5s ease-in-out;
                }
            <? } ?>
        </style>
        <?
    }

    public function printArea($rowsToPrint) {
        $count = 0;
        $editedCellid = null;
        foreach ($rowsToPrint as $row) {
            $isedit = false;

            $cellid = $row->cellId;

            if (isset($_SESSION['gseditcell']) && $_SESSION['gseditcell'] === $row->cellId) {
                $editedCellid = $cellid;
//                echo "<div class='gscell gsdepth_0 gseditinfo' style='height: 38px;'>";
//                echo "<div class='gsinner gsdepth_0'>";
//                echo "<div class='gseditrowheading' cellid='" . $cellid . "'>";
//                echo "<label style='float:left;'>";
//                echo "<input type='checkbox' style='background-color:#FFF;' class='gsdisplaygridcheckbox'> Add spacing to grid";
//                echo "</label>";
//                echo "You are now in edit mode for this row." . " - " . "<span class='gsdoneeditbutton' done='true'true'>done editing</span>";
//                echo "</div>";
//                echo "</div>";
//                echo "</div>";
                $isedit = true;
            }

            if ($isedit) {
                $this->printEasyRowMode($row);
            }
            $this->printCell($row, $count, 0, 0, $isedit, null);

            $count++;
        }
        return $editedCellid;
    }

    public function displayTabRow($parent, $edit, $cell) {
        echo "<div class='gstabheader'>";
        $first = true;
        foreach ($parent->cells as $innercell) {
            $active = "";
            if ($cell->cellId == $innercell->cellId) {
                $first = false;
                $active = "gsactivetab gsactivecell";
            }
            $tabName = "tab";
            if ($innercell->cellName) {
                $tabName = $innercell->cellName;
            }
            echo "<span class='gstabbtn $active' incrementid='" . $innercell->incrementalCellId . "' cellid='" . $innercell->cellId . "'>$tabName</span>";
        }
        if ($this->factory->isEditorMode() && $edit) {
            echo "<i class='fa fa-plus gsoperatecell' type='addrow' target='container' title='Add another tab'></i> ";
            echo "<i class='fa fa-cogs tabsettings' title='Tab settings'></i>";
        }
        echo "</div>";
    }

    public function printCarouselDots($totalcells, $edit, $count, $cellid) {
        echo "<div class='gscarouseldots'>";
        for ($i = 0; $i < $totalcells; $i++) {
            $activeCirle = "";
            if ($count == $i) {
                $activeCirle = "activecarousel gsactivecell";
            }
            echo "<i class='fa fa-circle gscarouseldot $activeCirle' cellid='$cellid'></i>";
        }
        if ($this->factory->isEditorMode() && $edit) {
            echo "<i class='fa fa-plus addcarouselrow gsoperatecell' type='addrow' target='container' title='Add another slider'></i>";
            echo "<i class='fa fa-cogs carouselsettings' title='Carousel settings'></i>";
        }
        echo "</div>";
    }

    public function printLoaderForContainers() {
        ?>
        <script>
            PubSub.subscribe('NAVIGATION_COMPLETED', function (a, b) {
                for (var containerid in thundashop.framework.lastRotatedCell) {
                    var lastRotatedCell = thundashop.framework.lastRotatedCell[containerid];
                    console.log("Navigating: " + containerid + " with cell: " + lastRotatedCell);
                    var cell = $('.gscell[cellid="' + lastRotatedCell + '"]');
                    var container = cell.closest('.gscontainercell');
                    if (container.hasClass('gsrotating')) {
                        container.find('.gsrotatingrow').css('opacity', '0');
                        cell.css('opacity', '1');
                        cell.css('z-index', '2');
                    }
                    if (container.hasClass('gstab')) {
                        container.find('.gstabrow').hide();
                        cell.show();
                    }

                    setTimeout(function () {
                        thundashop.framework.loadResizing(cell, true);
                    }, "200");
                }
            });
        </script>
        <?
    }

    public function printEasyModeEdit($cell) {
        if (sizeof($cell->cells) > 0) {
            return;
        }
        if ($cell->mode != "COLUMN") {
            echo "<div class='gseasymode' cellid='" . $cell->cellId . "'>";
            echo "<div class='gseasymodeinner'>";
            echo "You are now in edit mode for this row";
            echo "</div></div>";
            return;
        }

        echo "<div class='gseasymode' cellid='" . $cell->cellId . "'>";
        echo "<div class='gseasymodeinner'>";
        echo "<i class='fa fa-arrow-left gsoperatecell' type='moveup' target='gseasymode' title='Move column to the left'></i> ";
        echo "<i class='fa fa-plus gsoperatecell' type='addbefore' target='gseasymode'  title='Insert column to the left'></i> ";
        echo "<i class='fa fa-image gs_resizing' type='delete' target='gseasymode' title='Open styling'></i> ";
        echo "<i class='fa fa-trash-o gsoperatecell' type='delete' target='gseasymode' title='Delete column'></i> ";
        echo "<i class='fa fa-plus gsoperatecell' type='addafter' target='gseasymode'  title='Insert column to the right'></i> ";
        echo "<i class='fa fa-arrow-right gsoperatecell' type='movedown' target='gseasymode' title='Move column to the right'></i> ";
        echo "</div>";
        echo "</div>";
    }

    public function printEasyRowMode($row) {
        echo "<div class='gseasyrowmode' cellid='" . $row->cellId . "'>";
        echo "<div class='gseasyrowmodeinnser'>";
        if ($row->mode == "TAB") {
        } else {
        }
        if ($row->mode == "TAB" || $row->mode == "ROTATING") {
            echo "<i class='fa fa-arrow-up gsoperatecell' type='moveup' target='container' title='Move row up'></i> ";
            echo "<i class='fa fa-plus gsoperatecell' type='addbefore' target='container'  title='Create row above'></i> ";
            echo "<i class='fa fa-image gs_resizing' type='delete' target='container' title='Open styling'></i> ";
            echo "<i class='fa fa-arrows-h gsoperatecell' type='addcolumn' target='selectedcell' title='Insert column'></i> ";
            echo "<i class='fa fa-trash-o gsoperatecell' type='delete' target='container' title='Delete row'></i> ";
            echo "<i class='fa fa-plus gsoperatecell' type='addafter' target='container'  title='Create row after'></i> ";
            echo "<i class='fa fa-arrow-down gsoperatecell' type='movedown' target='container' title='Move row down'></i> ";
        } else {
            echo "<i class='fa fa-arrow-up gsoperatecell' type='moveup' target='' title='Move row up'></i> ";
            echo "<i class='fa fa-plus gsoperatecell' type='addbefore' target=''  title='Create row above'></i> ";
            echo "<i class='fa fa-image gs_resizing' type='delete' target='' title='Open styling'></i> ";
            echo "<i class='fa fa-arrows-h gsoperatecell' type='addcolumn' title='Insert column'></i> ";
            echo "<i class='fa fa-trash-o gsoperatecell' type='delete' title='Delete row'></i> ";
            echo "<i class='fa fa-plus gsoperatecell' type='addafter' title='Create row after'></i> ";
            echo "<i class='fa fa-arrow-down gsoperatecell' type='movedown' title='Move row down'></i> ";
        }
        echo "</div>";
        echo "</div>";
    }

}
