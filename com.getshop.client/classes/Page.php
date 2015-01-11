<?php

class Page {

    var $javapage;
    /*  @var $factory Factory */
    var $factory;
    var $flatCellList = array();

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
//                    $_SESSION['gseditcell'] = $_GET['gseditcell'];
                }
            }
        }

        $editedCellid = null;

        if (!$this->factory->isMobile()) {
            echo "<div class='gsarea' area='header'>";
            $edited = $this->printArea($layout->areas->{'header'});
            $editingHeader = false;
            if ($edited) {
                $editingHeader = true;
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

            if ($this->factory->isEditorMode()) {

                if ($this->editCarouselForMobile()) {
                    echo "<style>";
                    echo ".gscontainer.gsrotating .gscell.gsdepth_0 { background-color:#bbb; padding-top: 20px; padding-bottom: 20px; }";
                    echo ".gscontainer.gsrotating .gsinner.gsdepth_0 { max-width: 500px !important; background-color:#fff; }";
                    echo "</style>";
                }

                $this->displayResizing();
                $this->printApplicationAddCellRow();
                $this->addCellConfigPanel();
                $this->addCellResizingPanel();
                if (isset($editingHeader)) {
                    $this->printEditingInfo($editingHeader);
                }
                $this->makeCarouselMenuDraggable();
            }

            if ($editedCellid == null) {
                echo "<script>$('.gsiseditingprepend').remove();</script>";
            }
        } else {
            echo "<div class='gsbody'>";

            $cells = array();
            $this->flattenCells($layout->areas->{'body'});

            foreach ($layout->mobileList as $id) {
                $cells[] = $this->flatCellList[$id];
            }

            $this->printMobileHeader($layout->areas->{'header'});
            $this->printArea($cells, 0, null);
            $this->printCss($layout->areas->{'body'});
            $this->printMobileMenu($layout->areas->{'header'});
            echo "</div>";
        }
    }

    private function printMobileHeader($headerCells) {
        echo "<div class='gsmobileheader'>";
        $config = $this->factory->getStore()->configuration;

        echo "<div class='gsportraitimage'>";
        if ($config->mobileImagePortrait) {
            echo "<img src='displayImage.php?id=" . $config->mobileImagePortrait . "' style='width:100%'>";
        } else {
            echo $this->factory->__w("No header uploaded for mobile potrait mode yet, please upload one.");
        }
        echo "</div>";

        echo "<div class='gslandscapeimage'>";
        if ($config->mobileImageLandscape) {
            echo "<img src='displayImage.php?id=" . $config->mobileImageLandscape . "' style='width:100%'>";
        } else {
            echo $this->factory->__w("No header uploaded for mobile landscape mode yet, please upload one.");
        }
        echo "</div>";

        echo "</div>";
    }

    private function printMobileMenu($headerCells) {
        $topMenu = $this->findInstance($headerCells, "Menu");

        if ($topMenu) {
            echo "<span class='gsmobilemenuinstance'>";
            $topMenu->renderApplication();
            echo "</span>";
        }

        echo "<span class='gsmobilesearchbox'>";
        echo "<input type='text' class='gsmobilsearchfield' placeholder='Enter the name of the product to search for'></input><i class='fa fa-search'></i>";
        echo "</span>";
        echo "<div class='gsmobilemenu'>";
        echo "<span class='gsmobilemenuentry gsslideleft'>";
        echo "<i class='fa fa-caret-left'></i>";
        echo "Menu";
        echo "</span>";
        echo "<span class='gsmobilemenuentry gsmobilemenubox gsmobiletopmenu'>";
        echo "<i class='fa fa-navicon'></i>";
        echo "Menu";
        echo "</span>";
        echo "<a href='?page=cart'>";
        echo "<span class='gsmobilemenuentry gsmobilemenubox gsmobilemenucart'>";

        $count = 0;
        $cart = $this->factory->getApi()->getCartManager()->getCart();
        foreach ($cart->items as $cartItem) {
            $count += $cartItem->count;
        }


        echo "<span class='gsshopcartcount'>" . $count . "</span>";
        echo "<i class='fa fa-shopping-cart'></i>";
        echo "Cart";
        echo "</span>";
        echo "</a>";
        echo "<span class='gsmobilemenuentry gsmobilemenubox gsmobilesearch'>";
        echo "<i class='fa fa-search'></i>";
        echo "Search";
        echo "</span>";
        echo "<span class='gsmobilemenuentry gsmobilemenubox gsslideright'>";
        echo "<i class='fa fa-caret-right'></i>";
        echo "Hide";
        echo "</span>";
        echo "</div>";
    }

    private function printCss($areas) {
        foreach ($areas as $area) {

            $styles = $area->styles;

            if (isset($area->styles) && $area->styles) {
                $area->styles = str_replace("{incrementcellid}", $area->incrementalCellId, $area->styles);
                echo "<style cellid='" . $area->cellId . "'>" . $styles . "</style>" . "\n";
            }

            if (sizeof($area->cells) > 0) {
                $this->printCss($area->cells) . "\n";
            }
        }
    }

    private function addTabPanel() {
        ?>  
        <div class="gsframeworkstandard tabsettingspanel">
            <div class="tabsettingspanelheading">
                <? echo $this->factory->__w("Tab settings"); ?>
                <i class="fa fa-close gsclosetabsettings"></i>
            </div>
            <b><? echo $this->factory->__w("Tab name"); ?></b><br>
            <input type="text" class="gstabname"><br>
            <bR>
            <b><? echo $this->factory->__w("Other operations"); ?></b><br>
            <div class="gsoperatecell" type="moveup" target="selectedcell"><i class="fa fa-arrow-left"></i>&nbsp;&nbsp;&nbsp;<? echo $this->factory->__w("Move tab to the left"); ?></div>
            <div class="gsoperatecell" type="movedown" target="selectedcell"><i class="fa fa-arrow-right"></i>&nbsp;&nbsp;&nbsp;<? echo $this->factory->__w("Move tab to the right"); ?></div>
            <div class="gsoperatecell" target="selectedcell" type="delete"><i class="fa fa-trash-o"></i>&nbsp;&nbsp;&nbsp;<? echo $this->factory->__w("Remove selected tab"); ?></div>
            <br>
            <input type="button" value="Done modifying" class="gsdonemodifytab" style="width:100%;"></input>
        </div>
        <script>$('.tabsettingspanel').draggable();</script>
        <?
    }

    private function printEditingInfo($editingHeader) {
        echo "<div class='gseditinginfo gsframeworkstandard'>";
        echo "</div>";
        echo "<script>$('.gsiseditingprepend').remove();</script>";
        if ($editingHeader) {
            echo "<script>$('body').prepend(\"<div class='gsiseditingprepend'></div>\");</script>";
        }
    }

    private function addCarouselSettingsPanel($cell) {
        ?>  
        <div class="carouselsettingspanel gsframeworkstandard">
            <div class="carouselsettingsheading">
                <? echo $this->factory->__w("Carousel settings"); ?>
                <i class="fa fa-close gs_closecarouselsettings"></i>
            </div>
            <b><? echo $this->factory->__w("Container operations"); ?></b><br>
            <select style="width: 100%;" class="gscarouseltype">
                <option value='slideleft'><? echo $this->factory->__w("Slide left"); ?></option>
                <option value='slideright'><? echo $this->factory->__w("Slide right"); ?></option>
                <option value='fade'><? echo $this->factory->__w("Fade"); ?></option>
            </select>
            <table width="100%">
                <tr>
                    <td><? echo $this->factory->__w("Height (px)"); ?></td>
                    <td align="right"><input type="text" class="gscarouselheight" value='<? echo $cell->carouselConfig->height; ?>'></td>
                </tr>
                <tr>
                    <td><? echo $this->factory->__w("Height (px) mobile"); ?></td>
                    <td align="right"><input type="text" class="gscarouselheightmobile" value='<? echo $cell->carouselConfig->heightMobile; ?>'></td>
                </tr>
                <tr>
                    <td><? echo $this->factory->__w("Timer (ms)"); ?></td>
                    <td align="right"><input type="text" class="gscarouseltimer" value='<? echo $cell->carouselConfig->time; ?>'></td>
                </tr>
            </table>
            <br>
            <input style="width: 100%;" class="savecarouselconfig" type="button" value="<? echo $this->factory->__w("Save settings"); ?>">
        </div>
        <script>
            $(function () {
                $('.carouselsettingspanel').draggable();
            });
        </script>
        <?
    }

    private function printApplicationAddCellRow() {
        $this->factory->includefile("applicationlist", 'Common');
    }

    private function renderApplication($cell) {
        $instance = $this->factory->getApplicationPool()->getApplicationInstance($cell->appId);
        if ($instance) {
            $instance->renderApplication();
        }
    }

    function printCell($cell, $count, $depth, $totalcells, $edit, $parent) {

        if ($this->factory->isMobile()) {
            if ($cell->hideOnMobile && !$this->factory->isEditorMode()) {
                return false;
            }
        }

        if ($cell->isHidden == true) {
            return false;
        }

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
            if($this->editCarouselForMobile() || $this->factory->isMobile()) {
                $styles .= "height: " . $cell->carouselConfig->heightMobile . "px;";
            } else {
                $styles .= "height: " . $cell->carouselConfig->height . "px;";
            }
        }
        if ($cell->mode == "COLUMN" && $totalcells > 1) {
            $width = 100 / $totalcells;
            if ($cell->width > 0) {
                $width = $cell->width;
            }

            $styles = "style='width:$width%; float:left;'";
            $isColumn = true;
        }

        $innerstyles = "";
        $floatData = false;
        if ($cell->mode == "FLOATING") {
            $floatData = $cell->floatingData;
            $innerstyles = "style='min-height:inherit; height:100%;'";
            $styles .= "height: 100%; min-height:inherit; overflow-y: hidden; overflow-x: hidden;";
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

        $gsrowmode = "";
        if ($parent != null && $parent->mode == "ROTATING") {
            $gsrowmode = "gsrotatingrow";
        }
        if ($parent != null && $parent->mode == "TAB") {
            $gsrowmode = "gstabrow";
        }

        $mode = "gs" . strtolower($cell->mode);

        $marginsclasses = "";
        if ($isColumn && ($totalcells > ($count + 1))) {
            $marginsclasses .= "gs_margin_right ";
        }

        if ($isColumn && ($count > 0)) {
            $marginsclasses .= " gs_margin_left";
        }
        $this->printFloatingHeader($cell, $floatData, $parent);

        if($cell->mode == "ROTATING" && $this->editCarouselForMobile() && !$this->factory->isMobile()) {
            echo "<div class='gsmobilecarouseleditdesc'>".$this->factory->__w("Add slides for carousel to mobile view")."</div>";
        }
        
        
        echo "<div $additionalinfo $styles width='$width' class='gsucell $gscell $gsrowmode $container $marginsclasses $roweditouter gsdepth_$depth gscount_$count $mode gscell_" . $cell->incrementalCellId . "' incrementcellid='" . $cell->incrementalCellId . "' cellid='" . $cell->cellId . "' outerwidth='" . $cell->outerWidth . "' outerWidthWithMargins='" . $cell->outerWidthWithMargins . "'>";

        if ($this->factory->isMobile() && $gsrowmode == "") {
            $this->printMobileAdminMenu($depth, $cell);
        }

        if ($parent != null && $parent->mode === "ROTATING") {
            if ($count > 0) {
                echo "<i class='fa fa-arrow-circle-left gsrotateleft gsrotatearrow'></i>";
            }
            if ($count + 1 < $totalcells) {
                echo "<i class='fa fa-arrow-circle-right gsrotateright gsrotatearrow'></i>";
            }
        }

        $this->printRowEditButtons($depth, $edit, $parent);
        $this->printEasyModeLayer($edit, $cell, $parent);

        echo "<div $innerstyles class='$gscellinner gsuicell gsdepth_$depth $container $rowedit gscount_$count gscell_" . $cell->incrementalCellId . "' totalcells='$totalcells'>";

        $this->printCellBox($edit, $cell, $parent);
        $this->printCellContent($cell, $parent, $edit, $totalcells, $count, $depth);

        echo "</div>";
        echo "</div>";

        $this->printFloatingEnd($cell);
        if ($cell->mode == "ROTATING" && $this->factory->isMobile()) {
            $this->resizeContainer($cell);
        }
        return true;
    }

    private function addCellConfigPanel() {
        ?>
        <span class='gscellsettingspanel gsframeworkstandard'>
            <div class='gscellsettingspanelheading'>
                <i class='gs_closecelledit fa fa-times'></i>
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
                <div class='gsoperatecell' subtype='row' type='setrowmode'><i class='fa fa-ellipsis-h'></i><? echo $this->factory->__w("Change to row mode"); ?></div>
            </div>
        </span>
        <script>$('.gscellsettingspanel').draggable();</script>
        <?
    }

    private function addCellResizingPanel() {
        ?>
        <span class='gsresizingpanel gsframeworkstandard'>
            <i class="fa fa-close gsclosecsseditor"></i>
            <div class="heading gsresizingheading"></div>
            <div class='gstabmenu'>
                <span class='tabbtn' target='css'><? echo $this->factory->__w("Css"); ?></span>
                <span class='tabbtn' target='background'><? echo $this->factory->__w("Styling"); ?></span>
            </div>
            <div class='gspage' target='css'>
                <div id="cellcsseditor" style="width:500px; height: 400px;">
                </div>

            </div>
            <div class='gspage' target='background' style="padding: 10px;">
                <div class='gsoutercolorselectionpanel gsoutercolorselectionbg'>
                    <div class='gsheading'><? echo $this->factory->__w("Outer background"); ?></div>
                    <div class='gscolorselectionpanel' level=''>
                        <table width='100%'>
                            <tr>
                                <td valign="top">
                                    <select>
                                        <option><? echo $this->factory->__w("Cover"); ?></option>
                                        <option><? echo $this->factory->__w("Center"); ?></option>
                                        <option><? echo $this->factory->__w("100% width"); ?></option>
                                        <option><? echo $this->factory->__w("Normal"); ?></option>
                                    </select>
                                </td>
                                <td align='right'>
                                    <div class="inputWrapper">
                                        <span class='gsuploadimage' style='display:none;'><i class='fa fa-spin fa-spinner'></i></span>
                                        <input type='button' value='<? echo $this->factory->__w("Choose"); ?>' class='gschoosebgimagebutton'>
                                        <input class="fileInput gsbgimageselection" type="file" />
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div class='gsoutercolorselectionpanel' >
                    <div class='gsheading'><? echo $this->factory->__w("Inner background"); ?></div>
                    <div class='gscolorselectionpanel' level='.gsuicell'>
                        <table width='100%'>
                            <tr>
                                <td valign="top">
                                    <select>
                                        <option><? echo $this->factory->__w("Cover"); ?></option>
                                        <option><? echo $this->factory->__w("Center"); ?></option>
                                        <option><? echo $this->factory->__w("100% width"); ?></option>
                                        <option><? echo $this->factory->__w("Normal"); ?></option>
                                    </select>
                                </td>                                
                                <td align='right'>
                                    <div class="inputWrapper">
                                        <span class='gsuploadimage' style='display:none;'><i class='fa fa-spin fa-spinner'></i></span>
                                        <input type='button' value='<? echo $this->factory->__w("Choose"); ?>' class='gschoosebgimagebutton'>
                                        <input class="fileInput gsbgimageselection" type="file" />
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div> 
                </div>

                <div class='gsheading'><? echo $this->factory->__w("Spacing"); ?></div>

                <div class='gscssattributes'>
                    <div class="gscssrow">
                        <? echo $this->factory->__w("Left spacing"); ?> <span class="gscssinput"><input type='text' data-attr="padding-left" data-prefix="px">px</span>
                    </div>
                    <div class="gscssrow">
                        <? echo $this->factory->__w("Top spacing"); ?> <span class="gscssinput"><input type='text' data-attr="padding-top" data-prefix="px">px</span>
                    </div>
                    <div class="gscssrow">
                        <? echo $this->factory->__w("Bottom spacing"); ?> <span class="gscssinput"><input type='text' data-attr="padding-bottom" data-prefix="px">px</span>
                    </div>
                    <div class="gscssrow">
                        <? echo $this->factory->__w("Right spacing"); ?> <span class="gscssinput"><input type='text' data-attr="padding-right" data-prefix="px">px</span>
                    </div>
                </div>
            </div>

            <div style="border-top: solid 1px #bbb;">
                <input type="button" value="<? echo $this->factory->__w("Save changes"); ?>" class="modifybutton gssavechanges" style="float:right;">
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
                background-image: url('/skin/default/range.png');	
            }

            #selection{
                background-image: url('/skin/default/range.png');
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
                background-image: url('/skin/default/range.png');
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
                background-image: url('/skin/default/range.png');
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
                background-image: url('/skin/default/range.png');
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
        if ($cell->type == "FLOATING") {
            return;
        }
        echo "<div class='applicationarea' appid='" . $cell->appId . "' area='" . $cell->cellId . "'>";
        if (!$cell->appId) {
            echo "<span class='gsaddcontent'>";
            $show = "";
            if (!$this->factory->isEditorMode()) {
                $show = "style='opacity:0;'";
            }
            echo "<i class='fa fa-plus-circle gs_show_application_add_list' $show></i>";
            echo "</span>";
        } else {
            $this->renderApplication($cell);
        }

        echo "</div>";
    }

    public function printContainerSettings($doCarousel, $cell, $depth) {
        $config = $cell->carouselConfig;
        $height = $config->height;
        if($this->factory->isMobile() || $this->editCarouselForMobile()) {
            $height = $config->heightMobile;
        }
        ?>
        <script>
            var cellid = "<? echo $cell->cellId; ?>";
        <? if ($doCarousel) { ?>
                thundashop.framework.activateCarousel($(".gsrotating[cellid='<? echo $cell->cellId; ?>']"), <? echo $config->time; ?>);
        <? } else { ?>
                if (!thundashop.framework.activeContainerCellId[cellid]) {
                    thundashop.framework.setActiveContainerCellId('<? echo $cell->cells[0]->cellId; ?>', cellid);
                }
        <? } ?>
        </script>

        <style>
            .gsrotating[cellid='<? echo $cell->cellId; ?>'] {  width: 100%; height: <? echo $height; ?>px; }
            .gsrotating[cellid='<? echo $cell->cellId; ?>'] .gscell.gsdepth_<? echo $depth; ?> { width:100%; min-height: <? echo $height; ?>px; height: <? echo $height; ?>px; }
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
        $printed = false;
        foreach ($rowsToPrint as $row) {
            $isedit = false;

            $cellid = $row->cellId;

            if (isset($_SESSION['gseditcell']) && $_SESSION['gseditcell'] === $row->cellId) {
                $editedCellid = $cellid;
                $isedit = true;
            }

            if ($isedit) {
                echo "<div class='gseditrowseperator'><div class='gseditrowseperatorinnertop'>";
                echo "<span style='float:left;'><input type='checkbox' style='background-color:#FFF;' class='gsdisplaygridcheckbox'> " . $this->factory->__w("Add spacing to grid") . "</span>";
                echo $this->factory->__w("You are now in edit mode for this row, from this manipulate your row by adding more columns or add a row above / below, and much more.") . "<input  type='button' class='gsdoneeditbutton' value='" . $this->factory->__w("Done editing") . "'><br>";

                echo "</div></div>";
                $this->printEasyRowMode($row);
            }
            $this->printCell($row, $count, 0, 0, $isedit, null);
            if ($isedit) {
                echo "<div class='gseditrowseperator'><div class='gseditrowseperatorinnerbottom'></div></div>";
            }
            $count++;
        }

        return $editedCellid;
    }

    public function displayTabRow($parent, $edit, $cell) {

        $names = array();
        $names['info'] = $this->factory->__w("Information");
        $names['relatedProducts'] = $this->factory->__w("Related products");
        $names['comments'] = $this->factory->__w("Comments");
        $names['attributes'] = $this->factory->__w("Attributes");

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

            if ($innercell->isHidden) {
                continue;
            }

            if (isset($names[$tabName])) {
                $tabName = $names[$tabName];
            }

            echo "<span class='gstabbtn $active' incrementid='" . $innercell->incrementalCellId . "' cellid='" . $innercell->cellId . "'>$tabName</span>";
        }
        if ($this->factory->isEditorMode() && !$this->factory->isMobile()) {
            echo "<i class='fa fa-plus gsoperatecell' type='addrow' target='container' title='" . $this->factory->__w("Add another tab") . "'></i> ";
            echo "<i class='fa fa-cogs tabsettings' title='" . $this->factory->__w("Tab settings") . "' style='cursor:pointer;'></i>";
        }
        echo "</div>";
    }

    public function printCarourselMenu() {
        if (!$this->factory->isEditorMode() || $this->factory->isMobile()) {
            return;
        }
        ?>
        <span class='gscaraouselmenu'>
            <div class='gscaraouselmenuheader'><? echo $this->factory->__w("Carousel menu"); ?></div>
            <i class="gsoperatecell fa fa-arrow-left" type="moveup" target="selectedcell" title='<? echo $this->factory->__w("Move slide to the left"); ?>'></i>
            <i class='fa fa-plus-circle gsoperatecell' type='addfloating' title='<? echo $this->factory->__w("Add content to slider"); ?>'></i>
            <i class="fa fa-image gs_resizing" data-hideouterbg='true' type="delete" title="<? echo $this->factory->__w("Background image / styling"); ?>"></i>
            <i class='fa fa-cogs carouselsettings' title='<? echo $this->factory->__w("Carousel settings"); ?>' style='cursor:pointer;'></i>
            <i class="gsoperatecell fa fa-trash-o" target="selectedcell" type="delete" title='<? echo $this->factory->__w("Delete selected slide"); ?>'></i>
            <i class="gssetslidemodemobile fa fa-mobile" title='<? echo $this->factory->__w("Configure mobile slides"); ?>'></i>
            <i class="gsoperatecell fa fa-arrow-right" type="movedown" target="selectedcell" title='<? echo $this->factory->__w("Move slide to the right"); ?>'></i>
        </span>
        <?
    }

    public function printCarouselDots($totalcells, $count, $cellid) {
        $editdots = "";
        if ($this->factory->isEditorMode() && !$this->factory->isMobile()) {
            $editdots = "gscarouseldotseditmode";
        }
        echo "<div class='gscarouseldots $editdots'>";
        for ($i = 0; $i < $totalcells; $i++) {
            $activeCirle = "";
            if ($count == $i) {
                $activeCirle = "activecarousel gsactivecell";
            }
            echo "<i class='fa fa-circle gscarouseldot $activeCirle' cellid='$cellid'></i>";
        }
        if ($this->factory->isEditorMode() && !$this->factory->isMobile()) {
            if($this->editCarouselForMobile()) {
                echo "<i class='fa fa-plus addcarouselrow gsoperatecell' type='addrow' target='container' mode='rowmobile' title='" . $this->factory->__w("Add another slider") . "'></i>";
            } else {
                echo "<i class='fa fa-plus addcarouselrow gsoperatecell' type='addrow' target='container' title='" . $this->factory->__w("Add another slider") . "'></i>";
            }
            echo "<i class='fa fa-warning' title='" . $this->factory->__w("The carousel is not rotating while logged in as administrator.") . "' style='cursor:pointer;'></i>";
        }
        echo "</div>";
    }

    public function printEasyModeEdit($cell, $parent, $simple = false) {

        if (sizeof($cell->cells) > 0) {
            return;
        }

        $leftClass = "";
        $rightClass = "";
        if ($simple) {
            $leftClass = "gsleftheading";
            $rightClass = "gsrightheading";
        }

        if ($cell->mode == "FLOATING") {
            echo "<i class='fa fa-image gs_resizing' type='delete' title='" . $this->factory->__w("Open styling") . "'></i> ";
            echo "<i class='fa fa-trash-o gsoperatecell' type='delete' title='" . $this->factory->__w("Delete area") . "' target='this' cellid='" . $cell->cellId . "'></i> ";
            if (!$cell->floatingData->pinned) {
                echo "<i class='fa fa-eyedropper gsoperatecell' type='pinarea' title='" . $this->factory->__w("Pin area") . "' target='this' cellid='" . $cell->cellId . "'></i> ";
            } else {
                echo "<i class='fa fa-eyedropper gsoperatecell' style='color:#bbb'; type='pinarea' title='" . $this->factory->__w("Unpin area") . "' target='this' cellid='" . $cell->cellId . "'></i> ";
            }
        } else if ($cell->mode == "ROW") {
            if ($parent && sizeof($parent->cells) > 1 && $parent->mode != "ROTATING" && $parent->cells[0]->cellId != $cell->cellId && !$simple) {
                echo "<i class='fa fa-arrow-up gsoperatecell' type='moveup' title='" . $this->factory->__w("Move row up") . "'></i> ";
            }
            echo "<i class='fa fa-plus gsoperatecell $leftClass' type='addcolbefore' mode='COLUMN' title='" . $this->factory->__w("Insert column to the left") . "'></i> ";
            echo "<i class='fa fa-image gs_resizing' type='delete' title='" . $this->factory->__w("Open styling") . "'></i> ";
            if ($parent != null && !$simple) {
                echo "<i class='fa fa-arrow-down gsoperatecell' type='addrow' title='" . $this->factory->__w("Insert row") . "'></i> ";
            }
            echo "<i class='fa fa-trash-o gsoperatecell' type='delete' title='" . $this->factory->__w("Delete row") . "'></i> ";
            echo "<i class='fa fa-plus gsoperatecell $rightClass' type='addcolumn' title='" . $this->factory->__w("Insert column to the right") . "'></i> ";
            if ($parent && (sizeof($parent->cells) > 1) && $parent->mode != "ROTATING" && $parent->cells[sizeof($parent->cells) - 1]->cellId != $cell->cellId && !$simple) {
                echo "<i class='fa fa-arrow-down gsoperatecell' type='movedown' title='" . $this->factory->__w("Move row down") . "'></i> ";
            }
        } else {
            if ($parent != null && $parent->cells[0]->cellId != $cell->cellId && !$simple) {
                echo "<i class='fa fa-arrow-left gsoperatecell' type='moveup' title='" . $this->factory->__w("Move column to the left") . "'></i> ";
            }
            echo "<i class='fa fa-plus gsoperatecell $leftClass' type='addbefore' title='" . $this->factory->__w("Insert column to the left") . "'></i> ";
            echo "<i class='fa fa-image gs_resizing' type='delete' title='" . $this->factory->__w("Open styling") . "'></i> ";
            if ($parent != null && !$simple) {
                echo "<i class='fa fa-arrow-down gsoperatecell' type='addrow' title='" . $this->factory->__w("Insert row") . "'></i> ";
            }
            echo "<i class='fa fa-trash-o gsoperatecell' type='delete' title='" . $this->factory->__w("Delete column") . "'></i> ";
            if ($cell->mode == "COLUMN") {
                echo "<i class='fa fa-arrows-h gsresizecolumn' title='" . $this->factory->__w("Resize column") . "'></i> ";
            }
            echo "<i class='fa fa-plus gsoperatecell $rightClass' type='addafter'  title='" . $this->factory->__w("Insert column to the right") . "'></i> ";
            if ($parent != null && $parent->cells[sizeof($parent->cells) - 1]->cellId != $cell->cellId && !$simple) {
                echo "<i class='fa fa-arrow-right gsoperatecell' type='movedown' title='" . $this->factory->__w("Move column to the right") . "'></i> ";
            }
        }
    }

    public function printEasyRowMode($row) {
        echo "<div class='gseasyrowmode gsframeworkstandard' cellid='" . $row->cellId . "'>";
        echo "<div class='gseasyrowmodeinnser'>";
        if ($row->mode == "TAB" || $row->mode == "ROTATING") {
            echo "<i class='fa fa-arrow-up gsoperatecell' type='moveup' target='container' title='" . $this->factory->__w("Move row up") . "'></i> ";
            echo "<i class='fa fa-plus gsoperatecell' type='addbefore' target='container'  title='" . $this->factory->__w("Create row above") . "'></i> ";
            if ($row->mode != "ROTATING") {
                echo "<i class='fa fa-image gs_resizing' type='delete' target='container' title='" . $this->factory->__w("Open styling") . "'></i> ";
            }
            echo "<i class='fa fa-trash-o gsoperatecell' type='delete' target='container' title='" . $this->factory->__w("Delete row") . "'></i> ";
            echo "<i class='fa fa-plus gsoperatecell' type='addafter' target='container'  title='" . $this->factory->__w("Create row after") . "'></i> ";
            echo "<i class='fa fa-arrow-down gsoperatecell' type='movedown' target='container' title='" . $this->factory->__w("Move row down") . "'></i> ";
        } else {
            echo "<i class='fa fa-arrow-up gsoperatecell' type='moveup' target='' title='" . $this->factory->__w("Move row up") . "'></i> ";
            echo "<i class='fa fa-plus gsoperatecell' type='addbefore' mode='INIT' target=''  title='" . $this->factory->__w("Create row above") . "'></i> ";
            echo "<i class='fa fa-image gs_resizing' type='delete' target='' title='" . $this->factory->__w("Open styling") . "'></i> ";
            echo "<i class='fa fa-arrows-h gsoperatecell' type='addcolumn' title='" . $this->factory->__w("Insert column") . "'></i> ";
            echo "<i class='fa fa-trash-o gsoperatecell' type='delete' title='" . $this->factory->__w("Delete row") . "'></i> ";
            echo "<i class='fa fa-plus gsoperatecell' type='addafter' mode='INIT'  title='" . $this->factory->__w("Create row after") . "'></i> ";
            echo "<i class='fa fa-arrow-down gsoperatecell' type='movedown' title='" . $this->factory->__w("Move row down") . "'></i> ";
        }
        echo "</div>";
        echo "</div>";
    }

    public function makeDraggable($cell) {
        ?>
        <script>
            $('.gsfloatingbox[cellid="<? echo $cell->cellId; ?>"]').draggable(
                    {
                        handle: '.gsfloatingheader',
                        containment: 'parent',
                        stop: function (e, ui) {
                            var app = $(this);
                            thundashop.framework.saveFloating($(this));
                        }
                    }).resizable(
                    {
                        containment: 'parent',
                        stop: function (e, ui) {
                            thundashop.framework.saveFloating($(this));
                        }
                    });
        </script>
        <?
    }

    public function printCellContent($cell, $parent, $edit, $totalcells, $count, $depth) {

        if ($cell->mode == "INIT") {
            echo "<div class='gsinitrow'>";
            echo "<div class='gsselectcelltype'>Select a type for this row</div>";
            echo "<span class='gsmodeselectbox gsoperatecell' type='setnormalmode'>";
            echo "<i class='fa fa-arrows-h'></i>";
            echo "Row mode";
            echo "</span>";
            echo "<span class='gsmodeselectbox gsoperatecell' type='setcarouselmode'>";
            echo "<i class='fa fa-th'></i>";
            echo "Carousel mode";
            echo "</span>";
            echo "<span class='gsmodeselectbox gsoperatecell' type='settabmode'>";
            echo "<i class='fa fa-ellipsis-h'></i>";
            echo "Tab mode";
            echo "</span>";
            echo "</div>";
            return;
        }


        if ($cell->mode == "TAB") {
            $this->addTabPanel();
        }

        if ($parent != null && $parent->mode == "TAB") {
            $this->displayTabRow($parent, $edit, $cell);
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
            $cellsToPrint = $this->getCellsToPrint($cell->cells, $cell->mode);
            foreach ($cellsToPrint as $innercell) {
                $this->printCell($innercell, $counter, $depthprint, sizeof($cellsToPrint), $edit, $cell);
                $counter++;
            }

            if (sizeof($cellsToPrint) == 0) {
                echo "<div class='gsinner' style='text-align:center;clear:both;'>";
                echo "<br>";
                echo "You do not have any slides created yet..";
                echo "<br><br>";
                echo "<i class='fa fa-plus gsoperatecell shop_button' type='addrow' mode='rowmobile' target='container' title='" . $this->factory->__w("Add another slide") . "'> Create a new slide</i> ";
                echo "</div>";
            }

            if ($parent != null && $parent->mode == "TAB") {
                echo "</div>";
            }

            if ($cell->mode == "ROTATING" || $cell->mode == "TAB") {
                $doCarousel = (!$edit && $cell->mode == "ROTATING");
                if ($this->factory->isEditorMode()) {
                    $doCarousel = false;
                }
                $this->printContainerSettings($doCarousel, $cell, $depth);
            }
            echo "<div style='clear:both;'></div>";
        } else {
            $this->printApplicationArea($cell);
        }
        if ($cell->mode == "ROTATING") {
            $this->addCarouselSettingsPanel($cell);
        }
        if ($parent != null && $parent->mode === "ROTATING") {
            $this->printCarourselMenu();
            $this->printCarouselDots($totalcells, $count, $cell->cellId);
        }
    }

    public function printCellBox($edit, $cell, $parent) {
        if ($this->factory->isMobile()) {
            return false;
        }
        if ($parent && $parent->mode == "ROTATING") {
            return false;
        }
        if ($parent && $parent->mode == "TAB") {
            return false;
        }

        $result = !$edit && sizeof($cell->cells) == 0 && $cell->mode != "INIT" && $cell->mode != "FLOATING" && $this->factory->isEditorMode();

        if ($result) {
            $style = "position:absolute;width:100%; bottom: -1px;";
            echo "<div style='$style' class='gscellbox' cellid='" . $cell->cellId . "'>";
            echo "<div class='gscellheadermin'><i class='fa fa-external-link-square'></i></div>";
            echo "<div class='gscellboxheader'>";
            echo "<span style='float:left;'>" . $this->printEasyModeEdit($cell, $parent, true) . "</span>";
            echo "</div></div>";
        }
    }

    private function flattenCells($cells) {
        foreach ($cells as $cell) {
            $this->flatCellList[$cell->cellId] = $cell;
            if (sizeof($cell->cells) > 0) {
                $this->flattenCells($cell->cells);
            }
        }
    }

    public function findInstance($headerCells, $type) {
        foreach ($headerCells as $cell) {
            if (sizeof($cell->cells) > 0) {
                $app = $this->findInstance($cell->cells, $type);
                if ($app) {
                    return $app;
                }
            } else {
                $instance = $this->factory->getApplicationPool()->getApplicationInstance($cell->appId);
                if ($type == "Menu") {
                    if ($instance && $instance instanceof \ns_a11ac190_4f9a_11e3_8f96_0800200c9a66\Menu) {
                        return $instance;
                    }
                }
                if ($type == "ImageDisplayer") {
                    if ($instance && $instance instanceof ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer) {
                        return $instance;
                    }
                }
            }
        }
    }

    public function makeCarouselMenuDraggable() {
        ?>
        <script>
            $('.gscaraouselmenu').draggable({
                handle: '.gscaraouselmenuheader',
                containment: 'parent'
            });
        </script>
        <?
    }

    public function printMobileAdminMenu($depth, $cell) {
        if ($depth == 0 && $this->factory->isEditorMode()) {
            echo "<span class='gsmobileoptions'>";
            echo "<span style='position:absolute; left: 5px; top: 2px;'>" . $this->factory->__w("Row options") . "</span>";
            echo "<i class='fa fa-caret-down gscaretleft gscaret'></i>";
            echo "<i class='fa fa-arrow-up gsoperatecell' cellid='" . $cell->cellId . "' type='mobilemoveup' title='" . $this->factory->__w("Move up on mobile") . "' target='this'></i>";
            if ($cell->hideOnMobile) {
                echo "<i class='fa fa-trash-o gsoperatecell gshiddenonmobile' cellid='" . $cell->cellId . "' type='mobilehideoff' title='" . $this->factory->__w("Row is not displayed on mobile, click for make it reappear") . "' target='this'></i>";
            } else {
                echo "<i class='fa fa-trash-o gsoperatecell' cellid='" . $cell->cellId . "' type='mobilehideon' title='" . $this->factory->__w("Hide on mobile") . "' target='this'></i>";
            }
            echo "<i class='fa fa-arrow-down gsoperatecell' cellid='" . $cell->cellId . "' type='mobilemovedown' title='" . $this->factory->__w("Move down on mobile") . "' target='this'></i>";
            echo "<i class='fa fa-caret-down gscaretright gscaret'></i>";
            echo "</span>";
        }
    }

    public function printRowEditButtons($depth, $edit, $parent) {
        if ($depth === 0 && !$edit && $this->factory->isEditorMode()) {
            if ($parent == null || ($parent->mode != "TAB" && $parent->mode != "ROTATING")) {
                echo "<span class='gseditrowbuttons'>";
                echo "<i title='" . $this->factory->__w("Add row below") . "' class='fa fa-plus gsoperatecell' type='addbefore' mode='INIT'></i>";
                echo "<i title='" . $this->factory->__w("Edit row") . "' class='fa gseditrowbutton fa-pencil-square-o'></i>";
                echo "<i title='" . $this->factory->__w("Add row below") . "' class='fa fa-plus gsoperatecell' type='addafter' mode='INIT'></i>";
                echo "</span>";
            } else if ($parent && $parent->mode == "TAB") {
                echo "<span class='gseditrowbuttons'>";
                echo "<i title='" . $this->factory->__w("Add row below") . "' class='fa fa-plus gsoperatecell' type='addbefore' mode='INIT' target='container'></i>";
                echo "<i title='" . $this->factory->__w("Edit row") . "' class='fa gseditrowbutton fa-pencil-square-o' target='container'></i>";
                echo "<i title='" . $this->factory->__w("Delete carousel") . "' class='fa gsoperatecell fa-trash-o' type='delete' target='container'></i>";
                echo "<i title='" . $this->factory->__w("Add row below") . "' class='fa fa-plus gsoperatecell' type='addafter' mode='INIT' target='container'></i>";
                echo "</span>";
            } else if ($parent && $parent->mode == "ROTATING") {
                echo "<span class='gseditrowbuttons'>";
                echo "<i title='" . $this->factory->__w("Add row below") . "' class='fa fa-plus gsoperatecell' type='addbefore' mode='INIT' target='container'></i>";
                echo "<i title='" . $this->factory->__w("Delete carousel") . "' class='fa gsoperatecell fa-trash-o' type='delete' target='container'></i>";
                echo "<i title='" . $this->factory->__w("Add row below") . "' class='fa fa-plus gsoperatecell' type='addafter' mode='INIT' target='container'></i>";
                echo "</span>";
            }
        }
    }

    public function printEasyModeLayer($edit, $cell, $parent) {
        if ($edit) {
            echo "<div class='gseasymode' cellid='" . $cell->cellId . "'>";
            echo "<div class='gseasymodeinner'>";
            $this->printEasyModeEdit($cell, $parent);
            echo "</div>";
            echo "</div>";

            echo "<span class='gscellsettings'>";
            echo "<i class='fa fa-cogs'  title='" . $this->factory->__w("Cell settings") . "' style='cursor:pointer;'></i>";
            echo "</span>";
        }
    }

    public function resizeContainer($cell) {
        ?>
        <script>
            var container = $('.gscontainercell[cellid="<? echo $cell->cellId; ?>"]');
            var origwidth = container.attr('outerwidth');
            var ratio = $(window).width() / 500;
            var newheight = parseInt(container.height() * ratio);
            container.css('height', newheight);
            container.css('min-height', newheight);
            container.find('.gsrotatingrow').css('height', newheight);
            container.find('.gsrotatingrow').css('min-height', newheight);

            container.find('.gsfloatingframe').each(function () {
                var position = $(this).position();
                var left = parseInt(position.left * ratio);
                var top = parseInt(position.top * ratio);
                var width = parseInt($(this).width() * ratio);
                var height = parseInt($(this).height() * ratio);

                $(this).css('left', left + "px");
                $(this).css('top', top + "px");
                $(this).css('width', width + "px");
                $(this).css('height', height + "px");

                $(this).find('.ContentManager span').each(function () {
                    var size = $(this).css('font-size');
                    size = size.replace("px", "");
                    size = parseInt(size * ratio);
                    $(this).css('font-size', size);
                });
            });
        </script>
        <?
    }

    public function printFloatingHeader($cell, $floatData, $parent) {
        if ($cell->mode == "FLOATING") {

            $floatingClass = "";
            if ($this->factory->isEditorMode() && !$this->factory->isMobile()) {
                $floatingClass = "gsfloatingbox";
            }

            $style = "position:absolute;width:" . $floatData->width . "px;height: " . $floatData->height . "px;top: " . $floatData->top . "px;left:" . $floatData->left . "px";
            echo "<div style='$style' class='$floatingClass gsfloatingframe' cellid='" . $cell->cellId . "'>";
            echo "<div class='gsfloatingheader'>";
            echo "<span style='float:left;'>" . $this->printEasyModeEdit($cell, $parent, true) . "</span>";
            echo "</div>";
        }
    }

    public function printFloatingEnd($cell) {
        if ($cell->mode === "FLOATING") {
            //End of floatingbox.
            echo "</div>";
            if (!$cell->floatingData->pinned) {
                $this->makeDraggable($cell);
            }
        }
    }

    public function editCarouselForMobile() {
        return isset($_SESSION['gsrotatingmodemobile']);
    }

    public function getCellsToPrint($cells, $mode) {
        
        if($this->factory->isMobile()) {
            $result = array();
            foreach($cells as $innercell) {
                if(!$innercell->hideOnMobile) {
                    $result[] = $innercell;
                }
            }

            return $result;
        }
        
        if($mode == "ROTATING" && $this->editCarouselForMobile()) {
            $result = array();
            foreach($cells as $innercell) {
                if(!$innercell->hideOnMobile) {
                    $result[] = $innercell;
                }
            }
            return $result;
        }
        
        $result = array();
        foreach($cells as $cell) {
            if(!$cell->hideOnDesktop) {
                $result[] = $cell;
            }
        }
        
        return $result;
    }

}
