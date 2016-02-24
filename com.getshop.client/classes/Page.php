<?php

class Page {

    var $javapage;
    /*  @var $factory Factory */
    var $factory;
    var $flatCellList = array();
    var $themeApp = null;

    /**
     * 
     * @param type $javapage
     * @param Factory $factory
     */
    function __construct($javapage, $factory) {
        $this->javapage = $javapage;
        $this->factory = $factory;
        $themeApp = $factory->getApi()->getStoreApplicationPool()->getThemeApplication();
        if ($themeApp) {
            $this->themeApp = $this->factory->getApplicationPool()->createInstace($themeApp);
        }
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
        $gs_page_type = $this->javapage->type;
        
        $editormodeclass = "";
        $canChangeLayout = "";
        if($this->factory->isEditorMode()) {
            $editormodeclass = "gseditormode";
        }
        
        if(@\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->canChangeLayout) {
            $canChangeLayout = "gscanchangelayout";
        }

        
        
        $loggedIn = ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null ? "true" : "false";

        $timeout = "";
        if ($loggedIn == "true") {
            $timeout = $this->factory->getApi()->getUserManager()->getUserById(ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id)->sessionTimeOut;
        }
        
        if($this->factory->isEditorMode()) {
            $this->includeLayoutHistory();
        }
        
        
        echo "<div class='gsbody_inner $editormodeclass $canChangeLayout' gsStoreId='".$this->factory->getStore()->id."' pageId='" . $this->getId() . "' gspagetype='$gs_page_type' userLoggedIn='$loggedIn' userTimeout='$timeout'>";
        if (!$this->factory->isMobile()) {
            echo "<div class='gsarea' area='header'>";
            $edited = $this->printArea($layout->areas->{'header'}, true);
            $editingHeader = false;
            if ($edited) {
                $editingHeader = true;
                $editedCellid = $edited;
            }
            echo "</div>";
            echo "<div class='gsarea' area='body'>";
                $leftBarEnabled = $this->javapage->leftSideBar;
                if ($leftBarEnabled)  {
                    echo "<div class='gsarea left_side_bar' area='".$this->javapage->leftSideBarName."'><div class='left_side_bar_inner'>";
                    $edited = $this->printArea($layout->areas->{$this->javapage->leftSideBarName});
                    echo "</div></div>";
                    
                    echo "<div class='gs_main_column'>";
                    $edited = $this->printArea($layout->areas->{'body'});
                    echo "</div>";
                } else {
                    $edited = $this->printArea($layout->areas->{'body'});
                }
                
                if ($edited) {
                    $editedCellid = $edited;
                }

            echo "</div>";

            echo "<div class='gsarea' area='footer'>";
            $edited = $this->printArea($layout->areas->{'footer'});
            if(sizeof($layout->areas->{'footer'}) == 0 && $this->factory->isEditorMode()) {
                echo "<center><a style='color:blue; cursor:pointer;' gstype='clicksubmit' method='createFooter' gsname='type' gsvalue='FOOTER'>Create footer.</a></center>";
            }
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
                    echo ".gscontainer.gsrotating .gscell.gsdepth_1 { background-color:#bbb !important; padding-top: 20px; padding-bottom: 20px; }";
                    echo ".gscontainer.gsrotating .gsinner.gsdepth_1 { max-width: 500px !important; background-color:#fff; }";
                    echo "</style>";
                }

                $this->displayResizing();
                $this->printApplicationAddCellRow();
                $this->addCellConfigPanel();
                $this->addCellResizingPanel();
                $this->addCellLayouts();
                if (isset($editingHeader)) {
                    $this->printEditingInfo($editingHeader);
                }
                $this->makeCarouselMenuDraggable();
            }

            if ($editedCellid == null) {
//                echo "<script>$('.gsiseditingprepend').remove();</script>";
            }
        } else {
            echo "<div class='gsbody' pageId='" . $this->getId() . "'>";
            $cells = $this->factory->getApi()->getPageManager()->getMobileBody($this->getId());
            echo "<div class='header gsmobileheaderfull'>";
            $this->printArea($layout->areas->{'header'}, 0, null);
            echo "</div>";
            $this->printMobileHeader($layout->areas->{'header'});
            echo "<div class='gsmobilebody'>";
            $this->printArea($cells, 0, null);
            echo "</div>";
            echo "<div class='footer gsmobilefooter'>";
            $this->printArea($layout->areas->{'footer'}, 0, null);
            echo "</div>";
            $this->printCss($layout->areas->{'body'});
            echo "</div>";
            $this->printMobileMenu($layout->areas->{'header'});
        }
        echo "</div>";
        echo "<script>";
        echo "$(function() { $('.pagetitle').html('".$this->factory->getPageTitle()."'); });";
        echo "</script>";
        
        echo '<script>resizeLeftBar();</script>';
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

        $this->printLanguageSelection();
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
        $loggedIn = ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null;
        echo "<span class='gsmobilemenuentry gsmobilemenubox gsslideright'>";
        
            if ($loggedIn) {
                echo "<i class='fa fa-lock' id='getshop_logout'></i>";
                echo IocContainer::getFactorySingelton()->__f("Logout");
            } else {
                echo "<i class='fa fa-caret-right'></i>";
                echo "Hide";
            }
        echo "</span>";
        echo "</div>";
    }

    private function printCss($areas) {
        foreach ($areas as $area) {

            $styles = $area->styles;

            if($this->factory->isMobile()) {
                $lines = explode("\n", $styles);
                $newstyle = "";
                $found = false;
                foreach($lines as $line) {
                    if(stristr($line, ".gscell")) {
                        $found = false;
                    }
                    if(stristr($line, ".gsucell")) {
                        $found = true;
                    }
                    if((stristr($line, "padding-left") || stristr($line, "padding-right")) && $found) {
                        continue;
                    }
                    $newstyle .= $line . "\n";
                }
                $styles = $newstyle;
            }
            
            if (isset($area->styles) && $area->styles) {
                $area->styles = str_replace("{incrementcellid}", $area->incrementalCellId, $area->styles);
                echo "<style cellid='" . $area->cellId . "'>" . $styles . "</style>" . "\n";
            }
            if (isset($area->back) && $area->back->styles) {
                $area->back->styles = str_replace("{incrementcellid}", $area->back->incrementalCellId, $area->back->styles);
                echo "<style cellid='" . $area->back->cellId . "'>" . $area->back->styles . "</style>" . "\n";
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
            
            <?
            $options = array();
            $options["slideleft"] = $this->factory->__w("Slide left");
            $options["slideright"] = $this->factory->__w("Slide right");
            $options["fade"] = $this->factory->__w("Fade");
            ?>
            
            <select style="width: 100%;" class="gscarouseltype">
                <? 
                foreach($options as $key => $val) {
                    $selected = "";
                    if($cell->carouselConfig->type == $key) {
                        $selected = "SELECTED";
                    }
                    echo "<option value='$key' $selected>$val</option>";
                }
                ?>
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
                <tr>
                    <td><? echo $this->factory->__w("Display numbers on dots"); ?></td>
                    <?
                    $displayNumbers= "";
                    if($cell->carouselConfig->displayNumbersOnDots) {
                        $displayNumbers = "CHECKED";
                    }
                    ?>
                    <td align="right"><input type="checkbox" class="gscarouselnumberconfig" <? echo $displayNumbers; ?>></td>
                </tr>
                <tr>
                    <td><? echo $this->factory->__w("Do not rotate"); ?></td>
                    <?
                    $displayNumbers= "";
                    if($cell->carouselConfig->avoidRotate) {
                        $displayNumbers = "CHECKED";
                    }
                    ?>
                    <td align="right"><input type="checkbox" class="gsavoidrotate" <? echo $displayNumbers; ?>></td>
                </tr>
                <tr>
                    <td><? echo $this->factory->__w("Navigate on mouseover"); ?></td>
                    <?
                    $displayNumbers= "";
                    if($cell->carouselConfig->navigateOnMouseOver) {
                        $displayNumbers = "CHECKED";
                    }
                    ?>
                    <td align="right"><input type="checkbox" class="gsnavonmouseover" <? echo $displayNumbers; ?>></td>
                </tr>
                <tr>
                    <td><? echo $this->factory->__w("Keep aspect ratio"); ?></td>
                    <?
                    $keepAspect= "";
                    if($cell->carouselConfig->keepAspect) {
                        $keepAspect = "CHECKED";
                    }
                    ?>
                    <td align="right"><input type="checkbox" class="gskeepaspect" <? echo $keepAspect; ?>></td>
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

    private function renderApplication($cell, $depth = 0) {
        $instance = $this->factory->getApplicationPool()->getApplicationInstance($cell->appId);
        if ($instance) {
            $instance->setDepth($depth);
            $instance->setCell($cell);
            $instance->renderApplication();
        }
    }

    function printCell($cell, $count, $depth, $totalcells, $edit, $parent, $header=false) {
        if ($this->factory->isMobile() && $cell->hideOnMobile) {
             return;
        }
        
        if(!$this->factory->isEditorMode() && $cell->link) {
            echo "<a href='" . $cell->link . "'>";  
        }
        
        if(!$this->hasPermissionsOnCell($cell)) {
            return;
        }
        
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
        $selectedCell = "";
        
        if ($cell->mode == "ROTATING") {
            $additionalinfo = "height='" . $cell->carouselConfig->height . "' timer='" . $cell->carouselConfig->time . "' type='" . $cell->carouselConfig->type . "'";
            if ($this->editCarouselForMobile() || $this->factory->isMobile()) {
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

        $pagewidthclass = "";
        if (($depth == 0 && $cell->mode != "ROTATING") || ($depth == 1 && $parent->mode == "ROTATING")) {
            $pagewidthclass = "gs_page_width";
        }

        $gsrowmode = "";
        if ($parent != null && $parent->mode == "ROTATING") {
            $selectedCell = $this->isCarouselSelected($cell, $parent, "gsselectedcarouselrow");
            $gsrowmode = "gsrotatingrow";
        }
        if ($parent != null && $parent->mode == "TAB") {
            $selectedCell = $this->isCarouselSelected($cell, $parent, "gstabrowselected");
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

        if ($cell->mode == "ROTATING" && $this->editCarouselForMobile() && !$this->factory->isMobile()) {
            echo "<div class='gsmobilecarouseleditdesc'>" . $this->factory->__w("Add slides for carousel to mobile view") . "</div>";
        }

        if($this->factory->isMobile() && (!$parent || !$parent->keepOriginalLayoutOnMobile)) {
            $styles = "";
        }
        $gslayoutbox = "";
        if($depth == 1) {
            $gslayoutbox = "gslayoutbox";
        }

        $keepMobile = "data-keepOnMobile='true'";
        if(!$cell->keepOriginalLayoutOnMobile) {
            $keepMobile = "data-keepOnMobile='false'";
        }
        
        $permissions = "";
        $permobject = $cell->settings;
        $permobject->{'link'} = $cell->link;
        $permobject->{'hideOnMobile'} = $cell->hideOnMobile;
        $permissions = "data-settings='".json_encode($cell->settings) . "'";
      
        $anchor = $cell->anchor;
        
        
        $themeClass = $cell->selectedThemeClass;
        
        if ($depth === 0) {
            echo "<div class='gsucell_extra_outer'>";
            $this->printEffectTrigger($cell, $depth, "outside");
            echo "<div class='gsucell_outer' cellid='$cell->cellId'>";
        }
        
        $lastInRow = (count(@$parent->cells) - 1) == $count ?  "gs_last_in_row" : "";
        $firstInRowClass = $count == 0 ?  "gs_first_in_row" : "";
        echo "<div selectedThemeClass='$themeClass' anchor='$anchor' $permissions $additionalinfo $styles width='$width' $keepMobile class='gsucell $themeClass $lastInRow $firstInRowClass $gslayoutbox $selectedCell $gscell $gsrowmode $container $marginsclasses $roweditouter gsdepth_$depth gscount_$count $mode gscell_" . $cell->incrementalCellId . "' incrementcellid='" . $cell->incrementalCellId . "' cellid='" . $cell->cellId . "' outerwidth='" . $cell->outerWidth . "' outerWidthWithMargins='" . $cell->outerWidthWithMargins . "'>";
        $this->printEffectTrigger($cell, $depth);
        
        if ($anchor) {
            echo "<a id='$anchor' name='$anchor'></a>";
        }
        
        if ($this->factory->isMobile() && $gsrowmode == "") {
            $this->printMobileAdminMenu($depth, $cell);
        }

        if ($this->themeApp == null || $this->themeApp->printArrowsOutSideOnCarousel()) {
            $this->printArrows($parent, $count, $totalcells);
        }

        $themeClassInner = $cell->selectedThemeClass ? $cell->selectedThemeClass."_inner" : "";
        $this->printRowEditButtons($depth, $edit, $cell);
        $this->printEasyModeLayer($edit, $cell, $parent);

        echo "<div $innerstyles class='$gscellinner $themeClassInner gsuicell $pagewidthclass gsdepth_$depth $container $rowedit gscount_$count gscell_" . $cell->incrementalCellId . "' totalcells='$totalcells'>";
        if ($header && $depth == 0 && $count == 0) {
            $this->printLanguageSelection();
        }
        
        if ($this->themeApp != null && !$this->themeApp->printArrowsOutSideOnCarousel()) {
            $this->printArrows($parent, $count, $totalcells);
        }

        
        if($this->factory->isEditorMode()) {
            $this->printCellBox($edit, $cell, $parent, $depth);
        }
        
        $this->printCellContent($cell, $parent, $edit, $totalcells, $count, $depth);

        
        echo "</div>";
        echo "</div>";
        if ($depth === 0) {
            echo "</div>";
            echo "</div>";
        }

        $this->printFloatingEnd($cell);
        if ($cell->mode == "ROTATING" && $this->factory->isMobile()) {
            $this->resizeContainer($cell);
        }
        
        if(!$this->factory->isEditorMode() && $cell->link) {
            echo "</a>";
        }
        
        $this->printEffectTriggerLoaded($cell, $depth);
        return true;
    }
    
    private function printArrows($parent, $count, $totalcells) {
        if ($parent != null && $parent->mode === "ROTATING") {
            
            $firsRow = $count > 0 ? "" : "gs_first_in_carousel";
            $last = $count + 1 < $totalcells ? "" : "gs_last_in_carousel";
            echo "<div class='gsrotateleft gsrotatearrow $firsRow'><i class='fa fa-arrow-circle-left'></i></div>";
            echo "<div class='gsrotateright gsrotatearrow $last'><i class='fa fa-arrow-circle-right'></i></div>";
        }
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
                <div class='gslinkcell'><i class='fa fa-link'></i><? echo $this->factory->__w("Navigate on cell"); ?></div>
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
                <span class='tabbtn' target='cellsettings'><? echo $this->factory->__w("Settings"); ?></span>
                <span class='tabbtn' target='effects'><? echo $this->factory->__w("Effects"); ?></span>
            </div>
            <div class='gspage' target='effects' style='padding: 10px;'>
                <? include("layoutbuilder/effects.php"); ?>
            </div>
            <div class='gspage' target='cellsettings' style='padding: 10px;'>
                <? include("layoutbuilder/settings.php"); ?>
            </div>
            <div class='gspage' target='css'>
                <div id="cellcsseditor" style="width:500px; height: 400px;">
                </div>
            </div>
            <div class='gspage' target='background' style="padding: 10px;">
                <? include("layoutbuilder/designsettings.php"); ?>
            </div>

            <div style="border-top: solid 1px #bbb;">
                <input type="button" value="<? echo $this->factory->__w("Save changes"); ?>" class="modifybutton gssavechanges" style="float:right;">
            </div>
        </span>
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

    public function renderApplicationSimple($appInstanceId, $fromAppBase) {
        $cell = $fromAppBase->getCell();
        $cell = json_encode($cell);
        $cell = json_decode($cell);
        $depth = $fromAppBase->getDepth();
        $cell->appId = $appInstanceId;
        $this->printApplicationArea($cell, $depth);
    }
    
    public function printApplicationArea($cell, $depth) {
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
            echo "<div class='gsaddcontenttext'>";
            echo "</div>";
            echo "<i title='Add content' class='fa fa-plus-circle gs_show_application_add_list' $show></i> ";
            echo "<i title='Change layout' class='fa fa-th gs_change_cell_layoutbutton' $show></i> ";
            echo "<i title='Delete this cell' class='fa fa-trash gs_drop_cell' $show></i>";
            echo "</span>";
        } else {
            $this->renderApplication($cell, $depth);
        }

        echo "</div>";
    }

    public function printContainerSettings($doCarousel, $cell, $depth) {
        $config = $cell->carouselConfig;
        $height = $config->height;
        if ($this->factory->isMobile() || $this->editCarouselForMobile()) {
            $height = $config->heightMobile;
        }
        
        if($config->keepAspect && !$this->factory->isMobile()) {
            ?>
            <script>
//                $(function() {
                    var origWindowWidth = <? echo $config->windowWidth; ?>;
                    var origHeight = <? echo $config->height; ?>;
                    var innerWidth = <? echo $config->innerWidth; ?>;

                    var aspectRatio =  origHeight / origWindowWidth;
                    var newHeight = $(window).width() * aspectRatio;
                    var heightDiff = newHeight / origHeight;
                    var innerWidthChange = $('.gs_page_width').width() / innerWidth;

                    $('.gscontainercell[cellid="<? echo $cell->cellId; ?>"]').height(newHeight);
                    $('.gscontainercell[cellid="<? echo $cell->cellId; ?>"] .gsrotatingrow').height(newHeight);
                    $('.gscontainercell[cellid="<? echo $cell->cellId; ?>"] .gsrotatingrow').css('min-height',newHeight);

                    $('.gscontainercell[cellid="<? echo $cell->cellId; ?>"]').find('.gsfloatingframe').each(function() {
                        
                        //Calculate left position
                        var left = $(this).position().left;
                        var newLeft = left * innerWidthChange;
                        if((newLeft + $(this).width()) > $(window).width()) {
                            newLeft = $('.gs_page_width').width() - $(this).width();
                        }
                        $(this).css('left',newLeft);
                        
                        
                        //Calculate top postion
                        var curTop = $(this).position().top;
                        if(heightDiff > 0) {
                            curTop *= heightDiff;
                        }
                        $(this).css('top',curTop);
                    });
//                });
            </script>
            <?
        } else if($this->factory->isMobile()) {
            ?>
            <script>
                $('.gscontainercell[cellid="<? echo $cell->cellId; ?>"]').find('.gsfloatingframe').each(function() {
                    var element = $(this);
                    setTimeout(function() {
                        var left = element.position().left;
                        if((left + element.width()) > $(window).width()) {
                            left = $(window).width() - (element.width());
                        }
                        element.css('left',left);
                    }, "500");
                });
            </script>
            <?
        }
        
        ?>
        <script>
            $(function() {
                var cellid = "<? echo $cell->cellId; ?>";
                <? if ($doCarousel) { ?>
                        thundashop.framework.activateCarousel($(".gsrotating[cellid='<? echo $cell->cellId; ?>']"), <? echo $config->time; ?>);
                <? } else { ?>
                        if (!thundashop.framework.activeContainerCellId[cellid]) {
                            thundashop.framework.setActiveContainerCellId('<? echo $cell->cells[0]->cellId; ?>', cellid);
                        }
                <? } ?>
            })
        </script>

        <style>
            .gsrotating[cellid='<? echo $cell->cellId; ?>'] {  width: 100%; height: <? echo $height; ?>px; }
            .gsrotating[cellid='<? echo $cell->cellId; ?>'] .gscell.gsdepth_<? echo $depth; ?> { width:100%; min-height: <? echo $height; ?>px; height: <? echo $height; ?>px; }
            .gsrotating[cellid='<? echo $cell->cellId; ?>'] .gsinner.gsdepth_<? echo $depth; ?> { height: 100%; }
            <? if (($config->type === "fade" || !$config->type)) { ?>
                .gsrotating[cellid='<? echo $cell->cellId; ?>'] .gscell {
                    -webkit-transition: opacity 1s ease-in-out;
                    -moz-transition: opacity 1s ease-in-out;
                    -ms-transition: opacity 1s ease-in-out;
                    -o-transition: opacity 1s ease-in-out;
                    transition: opacity 1s ease-in-out;
                }
            <? } ?>
        </style>
        <?
    }

    public function printArea($rowsToPrint, $header=false) {
        
        if($this->factory->isEditorMode() && !$rowsToPrint) {
            echo "<div class='gscell'>";
            echo "<div class='gsemptyarea gsinner'>";
            echo "<span class='shop_button'>".$this->factory->__f("Create the first row")."</span>";
            echo "</div>";
            echo "</div>";
            return;
        }
        
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
            $this->printCell($row, $count, 0, 0, $isedit, null, $header);
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
        $hideBg = "";
        if (!$this->factory->isEditorMode() || $this->factory->isMobile()) {
            return;
        }
        if ($this->editCarouselForMobile()) {
            $hideBg = "data-hideouterbg='true'";
        }
        
        ?>
        <span class='gscaraouselmenu'>
            <div class='gscaraouselmenuheader'><? echo $this->factory->__w("Carousel menu"); ?></div>
            <i class="gsoperatecell fa fa-arrow-left" type="moveup" target="selectedcell" title='<? echo $this->factory->__w("Move slide to the left"); ?>'></i>
            <i class='fa fa-plus-circle gsoperatecell' type='addfloating' title='<? echo $this->factory->__w("Add content to slider"); ?>'></i>
            <i class="fa fa-image gs_resizing" <? echo $hideBg; ?> type="delete" title="<? echo $this->factory->__w("Background image / styling"); ?>"></i>
            <i class='fa fa-cogs carouselsettings' title='<? echo $this->factory->__w("Carousel settings"); ?>' style='cursor:pointer;'></i>
            <i class="gsoperatecell fa fa-trash-o" target="selectedcell" type="delete" title='<? echo $this->factory->__w("Delete selected slide"); ?>'></i>
            <i class="gssetslidemodemobile fa fa-mobile" title='<? echo $this->factory->__w("Configure mobile slides"); ?>'></i>
            <i class="gsoperatecell fa fa-arrow-right" type="movedown" target="selectedcell" title='<? echo $this->factory->__w("Move slide to the right"); ?>'></i>
        </span>
        <?
    }

    /**
     * 
     * @param type $totalcells
     * @param type $count
     * @param type $cellid
     * @param core_pagemanager_data_CarouselConfig $config
     * @return type
     */
    public function printCarouselDots($totalcells, $count, $cellid, $config) {
        $editdots = "";
        if ($this->factory->isEditorMode() && !$this->factory->isMobile()) {
            $editdots = "gscarouseldotseditmode";
        }
        
        if(!$this->factory->isEditorMode() && $totalcells == 1) {
            return;
        }
        
        $spaceleft = "style='margin-left:-" . ($totalcells*15)/2 . "px;'";
        
        echo "<div class='gscarouseldots $editdots' $spaceleft>";
        $number = 0;
        for ($i = 0; $i < $totalcells; $i++) {
            $number++;
            $activeCirle = "";
            if ($count == $i) {
                $activeCirle = "activecarousel gsactivecell";
            }
            
            $navmouseover = "";
            if($config->navigateOnMouseOver) {
                $navmouseover = "gsnavcarouselonmouseover";
            }
            
            echo "<i class='fa fa-circle gscarouseldot $activeCirle $navmouseover' cellid='$cellid'>";
            if($config->displayNumbersOnDots) {
                echo  "<span class='gscarouselnumber'>". $number . "</span>";
            }
           echo "</i>";
        }
        if ($this->factory->isEditorMode() && !$this->factory->isMobile()) {
            if ($this->editCarouselForMobile()) {
                echo "<i class='fa fa-plus addcarouselrow gsoperatecell' type='addrow' target='container' mode='rowmobile' title='" . $this->factory->__w("Add another slider") . "'></i>";
            } else {
                echo "<i class='fa fa-plus addcarouselrow gsoperatecell' type='addrow' target='container' title='" . $this->factory->__w("Add another slider") . "'></i>";
            }
            echo "<i class='fa fa-warning' title='" . $this->factory->__w("The carousel is not rotating while logged in as administrator.") . "' style='cursor:pointer;'></i>";
        }
        echo "</div>";
    }

    public function printEasyModeEdit($cell, $parent, $simple = false) {


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

    /**
     * 
     * @param core_pagemanager_data_PageCell $cell
     * @param core_pagemanager_data_PageCell $parent
     * @param core_pagemanager_data_PageCell $edit
     * @param type $totalcells
     * @param type $count
     * @param type $depth
     * @return type
     */
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

        if ($parent != null && $parent->mode == "TAB") {
            echo "<div class='gstabcontent'>";
        }

        if (sizeof($cell->cells) > 0) {
            $counter = 0;
            $depthprint = $depth + 1;

            $cellsToPrint = $this->getCellsToPrint($cell->cells, $cell->mode);
            foreach ($cellsToPrint as $innercell) {
                /* @var $cellsToPrint core_pagemanager_data_PageCell */
                if($innercell->mode == "FLIP") {
                    $this->printFlipBoxes($innercell, $counter, $depthprint, sizeof($cellsToPrint), $edit, $cell);
                } else {
                    $this->printCell($innercell, $counter, $depthprint, sizeof($cellsToPrint), $edit, $cell);
                }
                $counter++;
            }

            if (sizeof($cellsToPrint) == 0 && $this->editCarouselForMobile()) {
                echo "<div class='gsinner' style='text-align:center;clear:both;'>";
                echo "<br>";
                echo "You do not have any slides created yet..";
                echo "<br><br>";
                echo "<i class='fa fa-plus gsoperatecell shop_button' type='addrow' mode='rowmobile' target='container' title='" . $this->factory->__w("Add another slide") . "'>".$this->factory->__w("Create a new slide")."</i> ";
                echo "</div>";
            }
            if ($cell->mode == "ROTATING" || $cell->mode == "TAB") {
                $doCarousel = (!$edit && $cell->mode == "ROTATING");
                if ($this->factory->isEditorMode()) {
                    $doCarousel = false;
                }
                if($cell->carouselConfig->avoidRotate) {
                    $doCarousel = false;
                }
                
                $this->printContainerSettings($doCarousel, $cell, $depthprint);
            }
            echo "<div style='clear:both;'></div>";
        } else {
            $this->printApplicationArea($cell, $depth);
        }
        if ($parent != null && $parent->mode == "TAB") {
            echo "</div>";
        }
        if ($cell->mode == "ROTATING") {
            $this->addCarouselSettingsPanel($cell);
        }
        if ($parent != null && $parent->mode === "ROTATING") {
            $displayNumbers = $parent->carouselConfig->displayNumbersOnDots;
            $this->printCarourselMenu();
            $this->printCarouselDots($totalcells, $count, $cell->cellId, $parent->carouselConfig);
        }
    }

    public function printCellBox($edit, $cell, $parent, $depth) {
        if ($this->factory->isMobile()) {
            return false;
        }
        if ($parent && $parent->mode == "ROTATING") {
            return false;
        }
        if ($parent && $parent->mode == "TAB") {
            return false;
        }

        if($depth <= 1 && \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->canChangeLayout) {
            echo "<div style='position:absolute;width:100%; bottom: -1px;' class='gscellbox' cellid='" . $cell->cellId . "'>";
            echo "<div class='gscellheadermin'><i class='fa fa-external-link-square'></i></div>";
            echo "<div class='gscellboxheader'>";
            echo "<span style='float:left;'>" . $this->printEasyModeEdit($cell, $parent, true) . "</span>";
            echo "</div></div>";
        } else if($depth > 1) {
            echo "<div class='gscellboxinner gs_resizing' cellid='" . $cell->cellId . "'><i class='fa fa-image'></i></div>";
            
        }
    }

    private function flattenCells($cells) {
        foreach ($cells as $cell) {
            $this->flatCellList[$cell->cellId] = $cell;
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
        if($this->factory->isMobile()) {
            return;
        }
        if ($depth < 1 && !$edit && $this->factory->isEditorMode() && \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->canChangeLayout) {
            $target = "";
            if ($parent && $parent->mode == "TAB") {
                $target = "target='container'";
            }
            if ($parent && $parent->mode == "ROTATING") {
                $target = "target='container'";
            }

            if($depth == 1 && ($parent->mode != "TAB" &&  $parent->mode != "ROTATING")) {
                return;
            }
            
            echo "<span class='gseditrowbuttons'>";
            echo "<span class='fa-stack'>";
            echo '<i class="fa fa-circle fa-stack-2x"></i>';
            echo "<i title='" . $this->factory->__w("Move row up") . "' class='fa fa-arrow-up gsoperatecell fa-stack-1x' type='moveup' mode='INIT' $target></i>";
            echo "</span>";

            echo "<span class='fa-stack'>";
            echo '<i class="fa fa-circle fa-stack-2x"></i>';
            echo "<i title='" . $this->factory->__w("Delete row") . "' class='fa gsoperatecell fa-trash-o  fa-stack-1x'  type='delete' $target></i>";
            echo "</span>";

            echo "<span class='fa-stack gsadvancedlayoutmode'>";
            echo '<i class="fa fa-circle fa-stack-2x"></i>';
            echo "<i title='" . $this->factory->__w("Edit row") . "' class='fa gseditrowbutton fa-pencil-square-o  fa-stack-1x' $target></i>";
            echo "</span>";

            echo "<span class='fa-stack'>";
            echo '<i class="fa fa-circle fa-stack-2x"></i>';
            echo "<i title='" . $this->factory->__w("Style row") . "' class='fa fa-image gs_resizing fa-stack-1x' type='addafter' mode='INIT' $target></i>";
            echo "</span>";

            echo "<span class='fa-stack'>";
            echo '<i class="fa fa-circle fa-stack-2x"></i>';
            echo "<i title='" . $this->factory->__w("Add row below") . "' class='fa fa-plus simpleaddrow fa-stack-1x' type='addafter' mode='INIT' $target></i>";
            echo "</span>";

            echo "<span class='fa-stack'>";
            echo '<i class="fa fa-circle fa-stack-2x"></i>';
            echo "<i title='" . $this->factory->__w("Move row down") . "' class='fa fa-arrow-down gsoperatecell fa-stack-1x' type='movedown' mode='INIT' $target></i>";
            echo "</span>";


            echo "</span>";
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
            function resizeContainerCell(cellid) {
                var container = $('.gscontainercell[cellid="'+cellid+'"]');
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
            }
            
            resizeContainerCell('<? echo $cell->cellId; ?>');
            
            window.addEventListener('orientationchange', function() {
                resizeContainerCell('<? echo $cell->cellId; ?>');
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

        if ($this->factory->isMobile()) {
            $result = array();
            foreach ($cells as $innercell) {
                if (!$innercell->hideOnMobile) {
                    $result[] = $innercell;
                }
            }

            return $result;
        }

        if ($mode == "ROTATING" && $this->editCarouselForMobile()) {
            $result = array();
            foreach ($cells as $innercell) {
                if (!$innercell->hideOnMobile) {
                    $result[] = $innercell;
                }
            }
            return $result;
        }

        $result = array();
        foreach ($cells as $cell) {
            if (!$cell->hideOnDesktop) {
                $result[] = $cell;
            }
        }

        return $result;
    }

    public function isCarouselSelected($cell, $parent, $className) {
        $selectedPosition = false;
        if(isset($_SESSION['gscontainerposition'][$parent->cellId])) {
            $selectedPosition = $_SESSION['gscontainerposition'][$parent->cellId];
        }
        $found = false;
        
        $firstId = $parent->cells[0]->cellId;
        if(($this->factory->isMobile() || $this->editCarouselForMobile())) {
            foreach($parent->cells as $tmpcell) {
                if(($this->factory->isMobile() || $this->editCarouselForMobile()) && !$tmpcell->hideOnMobile) {
                    $firstId = $tmpcell->cellId;
                    break;
                }
            }
        }
        
        if($selectedPosition) {
            foreach($parent->cells as $tmpcell) {
                /* @var $tmpcell core_pagemanager_data_PageCell */
                if(($this->factory->isMobile() || $this->editCarouselForMobile()) && $tmpcell->hideOnMobile) {
                    continue;
                }
                
                if($tmpcell->cellId == $_SESSION['gscontainerposition'][$parent->cellId]) {
                    $found = true;
                }
            }
            if($cell->cellId == $selectedPosition) {
                return $className;
            }
        }        
        
        if(!$found) {
            if($firstId == $cell->cellId) {
                return $className;
            }
        }
        return "";
    }

    public function addCellLayouts() {
        ?>
        <div class="gscelllayouts">
            <i class='fa fa-close gs_close_cell_layoutbutton'></i>
            <i class='fa fa-caret-up'></i>
            <div style="text-align:center;padding-bottom: 10px;">
                        Choose a layout for this cell that suits you.
                    </div>

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                    </div>
                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow">
                            <div class="gscelllayoutcol gswidth_33 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_33 gscelllayout"></div>
                            <div class="gscelllayoutcol gswidth_33 gscelllayout"></div>
                        </div>
                    </div>
                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow">
                            <div class="gscelllayoutcol gswidth_25 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_25 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_25 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_25 gscelllayout "></div>
                        </div>
                    </div>
                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow">
                            <div class="gscelllayoutcol gswidth_20 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_20 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_20 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_20 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_20 gscelllayout "></div>
                        </div>
                    </div>

            
                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow gscelllayout"></div>
                        <div class="gscelllayoutrow gscelllayout"></div>
                    </div>
            

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                        <div class="gscelllayoutrow gscelllayout"></div>
                    </div>

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow gscelllayout"></div>
                        <div class="gscelllayoutrow">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                    </div>
            

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow gscelllayout gscelllayoutheight_3"></div>
                        <div class="gscelllayoutrow gscelllayout gscelllayoutheight_3"></div>
                        <div class="gscelllayoutrow gscelllayout gscelllayoutheight_3"></div>
                    </div>

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow gscelllayoutheight_3">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                        <div class="gscelllayoutrow gscelllayout gscelllayoutheight_3"></div>
                        <div class="gscelllayoutrow gscelllayout gscelllayoutheight_3"></div>
                    </div>

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow gscelllayoutheight_3">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                        <div class="gscelllayoutrow gscelllayoutheight_3">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                        <div class="gscelllayoutrow gscelllayoutheight_3">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                    </div>

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow gscelllayoutheight_3">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                        <div class="gscelllayoutrow gscelllayout gscelllayoutheight_3"></div>
                        <div class="gscelllayoutrow gscelllayoutheight_3">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                    </div>

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow gscelllayout gscelllayoutheight_3"></div>
                        <div class="gscelllayoutrow gscelllayout gscelllayoutheight_3"></div>
                        <div class="gscelllayoutrow gscelllayoutheight_3">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout "></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                    </div>

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow gscelllayout"></div>
                        <div class="gscelllayoutrow gscelllayout"></div>
                    </div>

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                        <div class="gscelllayoutrow">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                    </div>

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow gscelllayout"></div>
                        <div class="gscelllayoutrow">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                    </div>

                    <div class="gscelllayoutbox">
                        <div class="gscelllayoutrow">
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                            <div class="gscelllayoutcol gswidth_50 gscelllayout"></div>
                        </div>
                        <div class="gscelllayoutrow gscelllayout"></div>
                    </div>

                </div>
        <?
    }

    public function hasPermissionsOnCell($cell) {
        $user = ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        if($user && ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator() && $user->showHiddenFields) {
            return true;
        }
        
        if($user && !$cell->settings->displayWhenLoggedOn) {
            return false;
        }
        if(!$user && !$cell->settings->displayWhenLoggedOut) {
            return false;
        }
        
        if($cell->settings->editorLevel == 0) {
            return true;
        }
        
        if($user && $user->type >= $cell->settings->editorLevel) {
            return true;
        }
        return false;
    }

    public function printLanguageSelection() {
        $languages = $this->factory->getLanguageCodes();
        if (count($languages)) {
            $mainLangCode = $this->factory->getMainLanguage();
            echo "<div class='gs_language_selection'>";
            echo "<a href='?setLanguage=$mainLangCode'><div class='gs_language_code gs_lang_code_$mainLangCode'>".$mainLangCode."</div></a>";
            foreach ($languages as $lang) {
                echo "<a href='?setLanguage=$lang'><div class='gs_language_code gs_lang_code_$lang'>".$lang."</div></a>";
            }
            echo "</div>";
        }
    }

    public function printEffectTrigger($cell, $depth, $type="normal") {
        if (!$this->factory->isEffectsEnabled()) {
            return;
        }
        
        if ($depth == 0) {
            echo "<div class='spacer s0 getshopScrollMagicTriggerRow' type='$type' id='scrollmagic_trigger_$cell->cellId' cellId='$cell->cellId'></div>";
        }
    }

    public function printEffectTriggerLoaded($cell, $depth) {
        if (!$this->factory->isEffectsEnabled()) {
            return;
        }
        
        $cellId = $cell->cellId;
        echo "<script>getshopScrollMagic.rowLoaded('$cellId');</script>";
    }


    /**
     * 
     * @param core_pagemanager_data_PageCell $innercell
     * @param core_pagemanager_data_PageCell $parent
     * @param core_pagemanager_data_PageCell $edit
     * @param type $totalcells
     * @param type $count
     * @param type $depth
     * @return type
     */
    public function printFlipBoxes($innercell, $counter, $depthprint, $size, $edit, $cell) {
        ?>
        <div class='gsflipcard' flipcardid="<? echo $innercell->cellId; ?>" fliptype='<? echo $cell->settings->isFlipping; ?>'> 
          <div class="front gsflipfront"> 
            <?
                $this->printCell($innercell->cells[0], $counter, $depthprint, $size, $edit, $cell);
            ?>
            </div> 
            <div class="back gsflipback">
            <?
                $this->printCell($innercell->cells[1], $counter, $depthprint, $size, $edit, $cell);
            ?>
            </div> 
          </div>
       <?php
    }

    public function includeLayoutHistory() {
        echo "<div class='gslayouthistory'>";
        echo "<div style='text-align:center;'>Layout history</div>";
        echo "<hr>";
        foreach($this->javapage->layoutBackups as $key) {
            echo "<div class='gschangelayoutfromtime' time='$key'>";
            echo "select - " . date("d.m.y H:i:s", ($key/1000));
            echo "</div>";
        }
        echo "</div>";
        echo "<script>";
        echo "if(thundashop.framework.historyboxshown) { thundashop.framework.gslayouthistory(); }";
        echo "</script>";
    }

    public function isLastInRow($cell) {
        
    }

    public function renderModal($areaname) {
        $this->factory->getApi()->getPageManager()->createModal($areaname);
        
        $notChachedJavPage = $this->factory->getApi()->getPageManager()->getPage($this->getId());
        $page = new Page($notChachedJavPage, $this->factory);
        $layout = $page->javapage->layout;
        
        echo "<div class='gsarea gs_modalouter' area='$areaname'>";
            echo "<div class='".$areaname."_inner gs_modalinner gs_page_width'>";
            echo "<div class='gs_close_modal' gsclick='closemodal'><i class='fa fa-close'></i></div>";
            $page->printArea($layout->areas->{$areaname}, true);
            echo "</div>";
        echo "</div>";

    }
}

