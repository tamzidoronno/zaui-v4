<?php

class PageBuilder {
    /* @var $layout core_pagemanager_data_PageLayout */

    private $layout;
    private $type;
    private $page;

    /**
     * @param type $layout
     * @param core_pagemanager_data_PageLayout $layout
     */
    function __construct($layout, $type, $page) {
        $this->layout = $layout;
        $this->type = $type;
        $this->page = $page;
    }

    function build() {
        if ($this->type >= 0) {
            $this->convertToNewLayout();
        }
        $this->printLayout();
    }

    public function convertToNewLayout() {
        switch ($this->type) {
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
                echo "No layout for type: " . $this->type;
        }
        $this->layout = $layout;
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
                        AppAreaHelper::printRows($this->page, $row->numberOfCells, $cellcount);
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
                                    AppAreaHelper::printRows($this->page, $row->numberOfCells, $cellcount);
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

}
?>
