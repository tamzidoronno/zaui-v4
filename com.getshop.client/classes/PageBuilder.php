<?php

class PageBuilder {
    /* @var $layout core_pagemanager_data_PageLayout */

    private $layout;
    private $type;
    private $page;
    private $factory;
    private $includePreviewText = true;

    /**
     * @param type $layout
     * @param core_pagemanager_data_PageLayout $layout
     */
    function __construct($layout, $type, $page) {
        $this->layout = $layout;
        $this->type = $type;
        $this->page = $page;
        $this->factory = IocContainer::getFactorySingelton();
    }

    function getPredefinedPage($type) {
        $res = new PredefinedPagesConfig();
        switch($type) {
            case "standard": 
               return $res->getStandardPages();
            case "contact":
                return $res->getContactPages();
            case "map":
                return $res->getMapPages();
                
        }
        return array();
    }
    
    function printPredefinedPagePreview($predefined) {
        echo "<span class='layoutpreview' pagetype='standard' config='".json_encode($predefined)."' row_size='".sizeof($predefined)."'>";
        foreach ($predefined as $row) {
            foreach ($row as $cell) {
                echo "<span class='icon_container' rowsize='".sizeof($row)."'>";
                switch($cell) {
                    case "text":
                        echo '<i class="fa fa-align-justify"></i>';
                        break;
                    case "image":
                        echo '<i class="fa fa-picture-o"></i>';
                        break;
                    case "map":
                        echo '<i class="fa fa-globe"></i>';
                        break;
                    case "movie":
                        echo '<i class="fa fa-youtube-play"></i>';
                        break;
                    case "contact":
                        echo '<i class="fa fa-envelope"></i>';
                        break;
                    default;
                        echo $cell . " ";
                }
                echo "</span>";
            }
            echo "<br>";
        }
        echo "</span>";
    }
    
    function saveBuildLayout($layout) {
        $_SESSION['layoutBuilded'] = serialize($layout);
        $_SESSION['layoutBuildedType'] = $this->type;
    }

    function updateLayoutConfig() {
        if (isset($_POST['data']['updatelayout'])) {
            $this->type = -1;
            if (isset($_POST['data']['leftsidebarcount']))
                $this->layout->leftSideBar = $_POST['data']['leftsidebarcount'];
            if (isset($_POST['data']['rightsidebarcount']))
                $this->layout->rightSideBar = $_POST['data']['rightsidebarcount'];
            if (isset($_POST['data']['layout'])) {
                $this->resetBuildLayout();
                $this->type = $_POST['data']['layout'];
                $this->layout = $this->convertToNewLayout($this->type);
            }
            if (isset($_POST['data']['numberofcells'])) {
                $index = $_POST['data']['index'];
                $this->layout->rows[$index]->numberOfCells = $_POST['data']['numberofcells'];
                $this->layout->rows[$index]->rowWidth = array();
            }
            if (isset($_POST['data']['adjustment'])) {
                $index = $_POST['data']['index'];
                $this->layout->rows[$index]->rowWidth = $_POST['data']['adjustment'];
            }

            if (isset($_POST['data']['rowscount'])) {
                for ($i = 0; $i < $_POST['data']['rowscount']; $i++) {
                    if (!isset($this->layout->rows[$i])) {
                        $this->layout->rows[$i] = $this->createRow(1);
                        if(!isset($this->layout->sortedRows) || sizeof($this->layout->sortedRows) == 0) {
                            foreach($this->layout->rows as $index => $row) {
                                $this->layout->sortedRows[] = $row->rowId;
                            }
                        } else {
                            $this->layout->sortedRows[] = $this->layout->rows[$i]->rowId;
                        }
                    }
                }
                $i = 0;
                if (sizeof($this->layout->rows) >= $_POST['data']['rowscount']) {
                    foreach ($this->layout->rows as $index => $row) {
                        $i++;
                        if ($i > $_POST['data']['rowscount']) {
                            if (($key = array_search($this->layout->rows[$index]->rowId, $this->layout->sortedRows)) !== false) {
                                unset($this->layout->sortedRows[$key]);
                            }
                            unset($this->layout->rows[$index]);
                        }
                    }
                }
            }
        }
        return $this->layout;
    }

    function activateBuildLayoutMode() {
        if (!isset($_SESSION['layoutBuilded'])) {
            return;
        } else {
            $this->layout = unserialize($_SESSION['layoutBuilded']);
            $this->type = $_SESSION['layoutBuildedType'];
        }
    }

    function resetBuildLayout() {
        unset($_SESSION['layoutBuilded']);
        unset($_SESSION['layoutBuildedType']);
    }

    function build() {
        if ($this->type >= 0) {
            $this->convertToNewLayout(false);
        }
        if(!$this->layout || sizeof($this->layout->rows) == 0) {
            echo "<center>";
            echo "<div class='no_page_layout'>";
            echo "<div>".$this->factory->__f("Before you can add content to this page, you will have to set up a layout for this page.")."</div>";
            echo "<div class='click'>".$this->factory->__f("Open page configuration")."</div>";
            echo "</div>";
            echo "</center>";
            $page = $this->factory->getPage()->backendPage;
            if(!$page->beenLoaded) {
                $page->beenLoaded = true;
                $this->factory->getApi()->getPageManager()->savePage($page);
                echo "<script>";
                echo "thundashop.MainMenu.showPageLayoutSelection();";
                echo "</script>";
            }
        } else {
            $this->printLayout();
        }
    }

    function printSuggestions() {
        $this->includePreviewText = false;
        $currentLayout = $this->layout;
        echo "<table>";
        echo "<tr>";
        $row = 1;
        for ($i = 1; $i <= 30; $i++) {
            $this->layout = $this->convertToNewLayout($i);
            if ($this->layout) {
                echo "<td valign='top'>";
                echo "<div class='suggestion_layout' type='" . $i . "'>";
                $this->printPreview();
                echo "</div>";
                echo "</td>";
                if($row % 3 == 0) {
                    echo "</tr><tr>";
                }
                $row++;
            }
        }
        echo "</tr>";
        echo "</table>";

        $this->layout = $currentLayout;
    }

    function printPreview() {
        $hassidebar = false;
        echo "<div class='row_option_panel' row='1'>";
        if ($this->includePreviewText) {
            echo "<select id='numberofcells'>";
            for ($i = 1; $i <= 5; $i++) {
                echo "<option value='$i'>$i " . $this->factory->__f("number of cells") . "</option>";
            }
            echo "</select>";
        }
        echo "</div>";

        if ($this->layout->leftSideBar > 0 || $this->layout->rightSideBar > 0) {
            $hassidebar = true;
        }
        echo "<div class='headerpreview'>";
        if ($this->includePreviewText) {
            echo $this->factory->__f("Header");
        }
        echo "</div>";
        if ($hassidebar) {
            echo "<table width='100%'>";
            echo "<tr>";
            if ($this->layout->leftSideBar > 0) {
                echo "<td valign='top' width='" . $this->layout->leftSideBarWidth . "%'>";
                for ($i = 1; $i <= $this->layout->leftSideBar; $i++) {
                    echo "<div class='previewrow cell sidebar'>" . $this->getPreviewText() . "</div>";
                }
                echo "</td>";
            }
            echo "<td valign='top'>";
            $this->printPreviewRows();
            echo "</td>";
            if ($this->layout->rightSideBar > 0) {
                echo "<td valign='top' width='" . $this->layout->rightSideBarWidth . "%'>";
                for ($i = 1; $i <= $this->layout->rightSideBar; $i++) {
                    echo "<div class='previewrow cell sidebar'>" . $this->getPreviewText() . "</div>";
                }
                echo "</td>";
            }
            echo "</tr>";
            echo "</table>";
        } else {
            $this->printPreviewRows();
        }
        echo "<div class='footerpreview'>";
        if ($this->includePreviewText) {
            echo $this->factory->__f("Footer");
        }
        echo "</div>";
    }

    public function convertToNewLayout($type) {
        if (!$type) {
            $type = $this->type;
        }
        $layout = $this->layout;
        switch ($type) {
            case 1:
                $layout = $this->createLayout(1, 1);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                break;
            case 2:
                $layout = $this->createLayout(1, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                break;
            case 3:
                $layout = $this->createLayout(0, 1);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                break;
            case 4:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                break;
            case 7:
                $layout = $this->createLayout(0, 3);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                break;
            case 8:
                $layout = $this->createLayout(0, 2);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                break;
            case 9:
                $layout = $this->createLayout(0, 1);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                break;
            case 10:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(3);
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                break;
            case 11:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(3);
                $layout->rows[] = $this->createRow(1);
                break;
            case 12:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(3);
                break;
            case 13:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(1);
                break;
            case 13:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(1);
                break;
            case 14:
                $layout = $this->createLayout(0, 2);
                $layout->rightSideBarWidth = 48;
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                break;
            case 15:
                $layout = $this->createLayout(0, 0);
                $layout->leftSideBarWidth = 48;
                $layout->rows = array();
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(2);
                break;
            case 16:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                break;
            case 17:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(1);
                break;
            case 18:
                $layout = $this->createLayout(1, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(1);
                break;
            case 19:
                $layout = $this->createLayout(1, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                break;
            case 20:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                break;
            case 21:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                break;
            case 22:
                $layout = $this->createLayout(1, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(2);
                break;
            case 23:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(3);
                $layout->rows[] = $this->createRow(3);
                break;
            case 24:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(1);
                break;
            case 25:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(3);
                $layout->rows[] = $this->createRow(1);
                break;
            case 26:
//                $layout = $this->createLayout(1, 0);
//                $layout->rows = array();
//                $layout->rows[] = $this->createRow(1);
//                $layout->rows[] = $this->createRow(2);
//                $layout->rows[] = $this->createRow(1);
                break;
            case 27:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(2);
                break;
            case 28:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(2);
                break;
            case 29:
                $layout = $this->createLayout(0, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(1);
                break;
            default:
                if ($type >= 0)
                    $layout = null;
        }
        $this->layout = $layout;
        return $layout;
    }

    public function addPredefinedContent($type,$config) {
        $siteBuilder = new SiteBuilder();
        $siteBuilder->clearPage();
        
        $rowcount = 1;
        $cellcount = 1;
        $area = "";
        foreach($config as $row) {
            foreach($row as $cell) {
                if(sizeof($row) == 1) {
                    $area = "main_" . $rowcount;
                    $rowcount++;
                } else {
                    $area = "col_" . $cellcount;
                    $cellcount++;
                }
                switch($cell) {
                    case "text":
                        $siteBuilder->addContentManager("test", $area, $type);
                        break;
                    case "image":
                        $siteBuilder->addImageDisplayer("test", $area, $type);
                        break;
                    case "movie":
                        $siteBuilder->addYouTube("", $area, $type);
                        break;
                    case "map":
                        $siteBuilder->addMap($area);
                        break;
                }
            }
        }
        
    }
    
    public function buildPredefinedPage($config) {
        $siteBuilder = new SiteBuilder();
        $siteBuilder->clearPage();
        $layout = $this->createLayout(0, 0);
        foreach($config as $row) {
            $layout->rows[] = $this->createRow(sizeof($row));
        }
        return $layout;
    }
    
    public function createRow($numberOfCells) {
        $row = new core_pagemanager_data_RowLayout();
        $row->marginBottom = -1;
        $row->numberOfCells = $numberOfCells;
        $row->marginTop = -1;
        $row->rowId = sprintf('%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
                // 32 bits for "time_low"
                mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0x0fff) | 0x4000, mt_rand(0, 0x3fff) | 0x8000, mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff)
        );
        return $row;
    }

    public function printLayout() {
        $hassidebar = false;
        if ($this->layout->leftSideBar > 0 || $this->layout->rightSideBar > 0) {
            $hassidebar = true;
        }
        if (!$hassidebar) {
            $this->printNoSideBarsLayout();
        } else {
            $this->printWithSideBars();
        }
    }

    public function createLayout($leftSidebar, $rightSidebar) {
        $layout = new core_pagemanager_data_PageLayout();
        $layout->leftSideBar = $leftSidebar;
        $layout->rightSideBar = $rightSidebar;
        $layout->leftSideBarWidth = 20;
        $layout->rightSideBarWidth = 20;
        $layout->marginLeftSideBar = -1;
        $layout->marginRightSideBar = -1;
        return $layout;
    }

    public function printNoSideBarsLayout() {
        $rownumber = 1;
        $cellcount = 1;
        $maincount = 1;
        $rows = array();
        foreach ($this->layout->rows as $row) {
            /* @var $row core_pagemanager_data_RowLayout */
            ob_start();
            ?>
            <div class="gs_row gs_outer r<? echo $rownumber; ?>" row="<? echo $rownumber; ?>" rowid="<? echo $row->rowId; ?>" style='<? echo $row->outercss; ?>'>
                <div class='gs_inner' style='<? echo $row->innercss; ?>'>
                    <?
                    if ($row->numberOfCells == 1) {
                        echo AppAreaHelper::printAppArea($this->page, "main_" . $maincount);
                        $maincount++;
                    } else {
                        echo AppAreaHelper::printRows($this->page, $row->numberOfCells, $cellcount, $row->rowWidth);
                        $cellcount += $row->numberOfCells;
                    }
                    ?>
                </div>
            </div>
            <?
            $rows[$row->rowId] = ob_get_contents();
            ob_end_clean();
            $rownumber++;
        }
        $this->sortAndPrintRows($rows);
    }

    public function printWithSideBars() {
        $colcount = 1;
        $cellcount = 1;
        $maincount = 1;
        $rows = array();
        ?>
        <div class='gs_row r1 gs_outer'>
            <div class='gs_inner'>
                <table width="100%" cellspacing="0" cellpadding="0">
                    <tr>
        <? if ($this->layout->leftSideBar > 0) { ?>
                            <td width="<? echo $this->layout->leftSideBarWidth; ?>%" valign="top" class='gs_col c<? echo $colcount; ?> gs_margin_right'>
                            <?
                            $colcount++;
                            for ($i = 1; $i <= $this->layout->leftSideBar; $i++) {
                                echo AppAreaHelper::printAppArea($this->page, "left_" . $i);
                            }
                            ?>
                            </td>
                            <? } ?>
                        <td valign="top" class='gs_col c<? echo $colcount; ?> gs_margin_right gs_margin_left'>
                        <?
                        $rowsprinted = 0;
                        $colcount++;
                        foreach ($this->layout->rows as $row) {
                            ob_start();
                            $class = "";
                            $rowsprinted++;

                            if ($rowsprinted != sizeof($this->layout->rows)) {
                                $class = "gs_margin_bottom";
                            }
                            echo "<div class='$class'>";
                            /* @var $row core_pagemanager_data_RowLayout */
                            if ($row->numberOfCells == 1) {
                                echo AppAreaHelper::printAppArea($this->page, "main_" . $maincount);
                                $maincount++;
                            } else {
                                echo AppAreaHelper::printRows($this->page, $row->numberOfCells, $cellcount, $row->rowWidth);
                                $cellcount += $row->numberOfCells;
                            }
                            echo "</div>";
                            $rows[$row->rowId] = ob_get_contents();
                            ob_end_clean();
                        }
                        $this->sortAndPrintRows($rows);
                        ?>
                        </td>
                            <? if ($this->layout->rightSideBar > 0) { ?>
                            <td width="<? echo $this->layout->rightSideBarWidth; ?>%" valign="top" class='gs_col c<? echo $colcount; ?> gs_margin_left'>
                            <?
                            $colcount++;
                            for ($i = 1; $i <= $this->layout->rightSideBar; $i++) {
                                echo AppAreaHelper::printAppArea($this->page, "right_" . $i, true, false, true);
                            }
                            ?>
                            </td>
                            <? } ?>
                    </tr>
                </table>
            </div>
        </div>
        <?
    }

    /**
     * 
     * @param core_pagemanager_data_RowLayout $row
     */
    public function printPreviewRow($row, $index) {
        $rowString = "";
        $rowString .= "<li class='outer-row-container' rowid='" . $row->rowId . "'>";
        if ($this->includePreviewText) {
            $rowString .= '<i class="fa fa-arrows-v"></i>';
            $rowString .= "<span title='" . $this->factory->__f("Row options") . "' class='fa fa-cog row_option' index='$index' cells='" . $row->numberOfCells . "'></span>";
        }
        if ($row->numberOfCells == 1) {
            $rowString .= "<div class='previewrow row'>" . $this->getPreviewText() . "</div>";
        } else {
            $rowString .= "<div class='previewrowcontainer' index='$index'>";
            for ($i = 1; $i <= $row->numberOfCells; $i++) {
                $width = (100 / $row->numberOfCells);
                $margin = 0;
                if ($i != $row->numberOfCells) {
                    $margin = 5;
                }
                if (isset($row->rowWidth[$i - 1])) {
                    $width = $row->rowWidth[$i - 1];
                }

                $rowString .= "<div style='$i; width: $width%; box-sizing:border-box;-moz-box-sizing:border-box;display:inline-block;padding-right:" . $margin . "px;'>";
                $rowString .= "<div class='previewrow cell' cellnumber='$i' percentage='$width'>" . $this->getPreviewText() . "</div>";
                $rowString .= "</div>";
            }
            $rowString .= "</div>";
        }
        $rowString .= "<div class='gs_bottom'></div>";
        $rowString .= "</li>";
        return $rowString;
    }

    public function getPreviewText() {
        if ($this->includePreviewText) {
            return $this->factory->__f("Content<br>area");
        }
        return "";
    }

    public function reorderRows($rowOrder) {
        $this->layout->sortedRows = $rowOrder;
    }

    public function getLayout() {
        return $this->layout;
    }

    private function printPreviewRows() {
        echo "<ul class='sortable_layout_rows'>";
        $rowarray = [];
        foreach ($this->layout->rows as $index => $row) {
            $rowarray[$row->rowId] = $this->printPreviewRow($row, $index);
        }
        if (isset($this->layout->sortedRows) && sizeof($this->layout->sortedRows) > 0) {
            foreach ($this->layout->sortedRows as $rowid) {
                echo $rowarray[$rowid];
            }
        } else {
            foreach ($rowarray as $row) {
                echo $row;
            }
        }
        echo "</ul>";
    }

    public function sortAndPrintRows($rows) {
        if (isset($this->layout->rows) && sizeof($this->layout->sortedRows)) {
            foreach ($this->layout->sortedRows as $rowid) {
                echo $rows[$rowid];
            }
        } else {
            foreach ($rows as $row) {
                echo $row;
            }
        }
    }

}
?>
