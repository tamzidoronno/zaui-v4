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
    
    function saveBuildLayout($layout) {
        $_SESSION['layoutBuilded'] = serialize($layout);
        $_SESSION['layoutBuildedType'] = $this->type;
    }
    
    function updateLayoutConfig() {
        if(isset($_POST['data']['updatelayout'])) {
            $this->type = -1;
            if(isset($_POST['data']['leftsidebarcount']))
                $this->layout->leftSideBar = $_POST['data']['leftsidebarcount'];
            if(isset($_POST['data']['rightsidebarcount']))
                $this->layout->rightSideBar = $_POST['data']['rightsidebarcount'];
            if(isset($_POST['data']['layout'])) {
                $this->resetBuildLayout();
                $this->type = $_POST['data']['layout'];
                $this->layout = $this->convertToNewLayout($this->type);
            }
            if(isset($_POST['data']['numberofcells'])) {
                $index = $_POST['data']['index'];
                $this->layout->rows[$index]->numberOfCells = $_POST['data']['numberofcells'];
                $this->layout->rows[$index]->rowWidth = array();
            }
            if(isset($_POST['data']['adjustment'])) {
                $index = $_POST['data']['index'];
                $this->layout->rows[$index]->rowWidth = $_POST['data']['adjustment'];
            }
            
            if(isset($_POST['data']['rowscount'])) {
                for($i = 0; $i < $_POST['data']['rowscount']; $i++) {
                    if(!isset($this->layout->rows[$i])) {
                        $this->layout->rows[$i] = $this->createRow(1);
                    }
                }
                $i = 0;
                if(sizeof($this->layout->rows) >= $_POST['data']['rowscount']) {
                    foreach($this->layout->rows as $index => $row) {
                        $i++;
                        if($i > $_POST['data']['rowscount']) {
                            unset($this->layout->rows[$index]);
                        }
                    }
                }
            }
        }
        return $this->layout;
    }
    
    function activateBuildLayoutMode() {
        if(!isset($_SESSION['layoutBuilded'])) {
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
        $this->printLayout();
    }
    
    function printSuggestions() {
        $this->includePreviewText = false;
        $currentLayout = $this->layout;
        
        for($i = 1; $i <= 30; $i++) {
            $this->layout = $this->convertToNewLayout($i);
            if($this->layout) {
                echo "<div class='suggestion_layout' type='".$i."'>";
                $this->printPreview();
                echo "</div>";
            }

        }
        
        $this->layout = $currentLayout;
        
    }
    
    function printPreview() {
        $hassidebar = false;
        echo "<div class='row_option_panel' row='1'>";
        if($this->includePreviewText) {
            echo "<select id='numberofcells'>";
            for($i = 1; $i <= 5; $i++) {
                echo "<option value='$i'>$i ".$this->factory->__f("number of cells")."</option>";
            }
            echo "</select>";
        }
        echo "</div>";
        
        if ($this->layout->leftSideBar > 0 || $this->layout->rightSideBar > 0) {
            $hassidebar = true;
        }
        echo "<div class='headerpreview'>";
        if($this->includePreviewText) {
            echo $this->factory->__f("Header");
        }
        echo "</div>";
        if($hassidebar) {
            echo "<table width='100%'>";
            echo "<tr>";
            if($this->layout->leftSideBar > 0) {
                echo "<td valign='top' width='".$this->layout->leftSideBarWidth."%'>";
                for($i = 1; $i <= $this->layout->leftSideBar; $i++) {
                    echo "<div class='previewrow cell sidebar'>".$this->getPreviewText()."</div>";
                }
                echo "</td>";
            }
            echo "<td valign='top'>";
            foreach($this->layout->rows as $index => $row) {
                $this->printPreviewRow($row, $index);
            }
            echo "</td>";
            if($this->layout->rightSideBar > 0) {
                echo "<td valign='top' width='".$this->layout->rightSideBarWidth."%'>";
                for($i = 1; $i <= $this->layout->rightSideBar; $i++) {
                    echo "<div class='previewrow cell sidebar'>".$this->getPreviewText()."</div>";
                }
                echo "</td>";
            }
            echo "</tr>";
            echo "</table>";
        } else {
            foreach($this->layout->rows as $index => $row) {
                $this->printPreviewRow($row, $index);
            }
        }
        echo "<div class='footerpreview'>";
        if($this->includePreviewText) {
            echo $this->factory->__f("Footer");
        }
        echo "</div>";
    }

    public function convertToNewLayout($type) {
        if(!$type) {
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
                $layout = $this->createLayout(1, 0);
                $layout->rows = array();
                $layout->rows[] = $this->createRow(1);
                $layout->rows[] = $this->createRow(2);
                $layout->rows[] = $this->createRow(1);
                break;
            default:
                if($type >= 0)
                    $layout = null;
        }
        $this->layout = $layout;
        return $layout;
    }

    public function createRow($numberOfCells) {
        $row = new core_pagemanager_data_RowLayout();
        $row->marginBottom = -1;
        $row->numberOfCells = $numberOfCells;
        $row->marginTop = -1;
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
        foreach ($this->layout->rows as $row) {
            /* @var $row core_pagemanager_data_RowLayout */
            ?>
            <div class="gs_row r<? echo $rownumber; ?> gs_outer">
                <div class='gs_inner'>
                    <?
                    if ($row->numberOfCells == 1) {
                        AppAreaHelper::printAppArea($this->page, "main_" . $maincount);
                        $maincount++;
                    } else {
                        AppAreaHelper::printRows($this->page, $row->numberOfCells, $cellcount, $row->rowWidth);
                        $cellcount += $row->numberOfCells;
                    }
                    ?>
                </div>
            </div>
            <?
            $rownumber++;
        }
    }

    public function printWithSideBars() {
        $colcount = 1;
        $cellcount = 1;
        $maincount = 1;
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
                                    AppAreaHelper::printAppArea($this->page, "left_" . $i);
                                }
                                ?>
                            </td>
                        <? } ?>
                        <td valign="top" class='gs_col c<? echo $colcount; ?> gs_margin_right gs_margin_left'>
                            <?
                            $colcount++;
                            $rowsprinted = 0;
                            foreach ($this->layout->rows as $row) {
                                $class = "";
                                $rowsprinted++;
                                
                                if($rowsprinted != sizeof($this->layout->rows)) {
                                    $class = "gs_margin_bottom";
                                }
                                echo "<div class='$class'>";
                                /* @var $row core_pagemanager_data_RowLayout */
                                if ($row->numberOfCells == 1) {
                                    AppAreaHelper::printAppArea($this->page, "main_" . $maincount);
                                    $maincount++;
                                } else {
                                    AppAreaHelper::printRows($this->page, $row->numberOfCells, $cellcount, $row->rowWidth);
                                    $cellcount += $row->numberOfCells;
                                }
                                echo "</div>";
                            }
                            ?>
                        </td>
                        <? if ($this->layout->rightSideBar > 0) { ?>
                            <td width="<? echo $this->layout->rightSideBarWidth; ?>%" valign="top" class='gs_col c<? echo $colcount; ?> gs_margin_left'>
                                <?
                                $colcount++;
                                for ($i = 1; $i <= $this->layout->rightSideBar; $i++) {
                                    AppAreaHelper::printAppArea($this->page, "right_" . $i, true, false, true);
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
        if($this->includePreviewText) {
            echo "<span title='".$this->factory->__f("Row options")."' class='fa fa-cog row_option' index='$index' cells='". $row->numberOfCells."'></span>";
        }
        if($row->numberOfCells == 1) {
            echo "<div class='previewrow row'>".$this->getPreviewText()."</div>";
        } else {
           echo "<div class='previewrowcontainer' index='$index'>";
           for($i = 1; $i <= $row->numberOfCells; $i++) {
               $width = (100 / $row->numberOfCells);
               $margin = 0;
               if($i != $row->numberOfCells) {
                   $margin = 5;
               }
               if(isset($row->rowWidth[$i-1])) {
                   $width = $row->rowWidth[$i-1];
               }
               
                echo "<div style='$i; width: $width%; box-sizing:border-box;-moz-box-sizing:border-box;display:inline-block;padding-right:".$margin."px;'>";
                echo "<div class='previewrow cell' cellnumber='$i' percentage='$width'>".$this->getPreviewText()."</div>";
                echo "</div>";
           }
            echo "</div>";
           echo "<div class='gs_bottom'></div>";
        }
    }

    public function getPreviewText() {
        if($this->includePreviewText) {
            return $this->factory->__f("Content<br>area");
        }
        return "";
    }

}
?>
