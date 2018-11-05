<?php
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of GetShopModuleTable
 *
 * @author ktonder
 */
class GetShopModuleTable {

    private $application;
    private $manangerName;
    private $functionName;
    private $attributes;
    private $extraData;
    private $args;
    private $avoidAutoExpanding = false;
    public $loadContentInOverlay = false;
    private $sortByColumn = "";
    private $sortingAscending = true;
    private $sortingArray = array();
    private $appendClass = "";
    private $matchOnField = "";
    private $checkForField = "";
    private $appendClassToRows;
    private $appendClassToRowsClass;

    function __construct(\ApplicationBase $application, $managerName, $functionName, $args, $attributes, $extraData = null) {
        $this->attributes = $attributes;
        $this->application = $application;
        $this->manangerName = $managerName;
        $this->functionName = $functionName;
        $this->extraData = $extraData;
        $this->args = $args;
    }

    public function render() {
        $this->uuid = uniqid();
        $this->loadData();
        $this->clearJavaScriptData();
        $this->renderTable();
        $this->printJavaScript();
    }

    private function loadData() {
        if (isset($this->data) && $this->data) {
            return;
        }

        $api = $this->application->getApi();
        $managerName = "get" . $this->manangerName;

        if (!method_exists($api, $managerName)) {
            return;
        }

        $res = $api->$managerName();

        if (!method_exists($res, $this->functionName)) {
            return;
        }
        if ($this->functionName) {
            $this->data = call_user_func_array(array($res, $this->functionName), $this->args);
        }
    }

    private function renderTable($renderPaging = false) {

        if(isset($_SESSION['lastsorttype']) && !isset($_POST['data']['column'])) {
            $this->sortByColumn = $_SESSION['lastsorttype'];
            $this->sortingAscending = $_SESSION['lastsorttypeasc'] == "true";
        }
        
        if (isset($_POST['event']) && $_POST['event'] == "sortGetShopTable") {
            $_SESSION['lastsorttype'] = $_POST['data']['column'];
            $this->sortByColumn = $_POST['data']['column'];
            $this->sortingAscending = false;
            $_SESSION['lastsorttypeasc'] = "false";
            if ($_POST['data']['sorting'] == "asc") {
                $this->sortingAscending = true;
                $_SESSION['lastsorttypeasc'] = "true";
            }
        }

        $dataToPrint = $this->formatDataToPrint();
        $dataToPrint = $this->sortData($dataToPrint);
        
        echo "<div class='GetShopModuleTable' identifier='" . $this->getIdentifier() . "' method='" . $this->manangerName . "_" . $this->functionName . "'>";

        echo "<div class='attributeheader datarow'>";
        $i = 1;
        foreach ($this->attributes as $attribute) {
            if ($attribute[1] !== "gs_hidden") {

                $sort = "";
                $sortClass = "";
                if ($this->hasSorting($attribute[0])) {
                    if ($this->sortByColumn == $attribute[0]) {
                        if ($this->sortingAscending) {
                            $sort = "<i class='fa fa-sort-desc'></i> ";
                        } else {
                            $sort = "<i class='fa fa-sort-asc'></i> ";
                        }
                    } else {
                        $sort = "<i class='fa fa-sort'></i> ";
                    }
                    $sortClass = "hassorting";
                }

                echo "<div class='headercol col col_$i col_$attribute[0] $sortClass' index='" . $attribute[0] . "'>$sort" . "$attribute[1]</div>";
            }
            $i++;
        }
        echo "</div>";

        if (!$this->data || count($this->data) < 1) {
            echo "<div class='nodata'>No data found</div>";
        } else {
            $j = 1;
            foreach ($dataToPrint as $data) {
                $odd = $j % 2 ? "odd" : "even";
                $active = $this->shouldShowRow($j);
                $activeClass = $active ? "active" : "";
                
                $loadInOverlay = "";
                if($this->loadContentInOverlay) {
                    $loadInOverlay = "loadContentInOverlay";
                }
                
                $highlightrow = "";
                if(isset($data[$this->checkForField]) && $data[$this->checkForField] == $this->matchOnField) {
                    $highlightrow = $this->appendClass;
                }
                
                if(!$this->appendClassToRows) {
                    $this->appendClassToRows = array();
                }
                $rowsHighlighting = "";
                if(in_array($j, $this->appendClassToRows)) {
                    $rowsHighlighting = $this->appendClassToRowsClass;
                }
                
                echo "<div class='datarow $odd $activeClass $loadInOverlay $highlightrow $rowsHighlighting' rownumber='$j'>";
                echo "<div class='datarow_inner'>";
                
                $i = 1;

                foreach ($this->attributes as $attribute) {
                    $val = $data[$attribute[0]];
                    if (isset($attribute[1]) && $attribute[1] !== "gs_hidden") {
                        echo "<div class='col col_$i col_$attribute[0]' index='" . $attribute[0] . "'>$val</div>";
                    }
                    $i++;
                }

                if ($this->extraData != null) {
                    $data = array_merge($data, $this->extraData);
                }

                $this->printJavaScriptData($data, $j);
                echo "</div>";

                if ($active) {
                    echo "<div class='datarow_extended_content' style='display:block;'>";
                    $this->renderTableContent($data, $j);
                    echo "</div>";
                } else {
                    echo "<div class='datarow_extended_content'></div>";
                }

                echo "</div>";
                $j++;
            }
        }

        if ($renderPaging) {
            $this->renderPaging();
        }
        $this->printSortingScript();
        echo "</div>";
    }

    private function printJavaScriptData($data, $rowNumber) {
        $functionName = $this->getFunctionName();

        if (!method_exists($this->application, $functionName)) {
            return;
        }
        ?>
        <script>
            gs_modules_data_array['<? echo $this->getIdentifier(); ?>']['<? echo $rowNumber; ?>'] = <? echo json_encode($data); ?>
        </script>
        <?
    }

    private function printJavaScript() {
        $functionName = $this->getIdentifier();

        if (!method_exists($this->application, $functionName)) {
            return;
        }
    }

    private function getIdentifier() {
        return $this->manangerName . "_" . $this->functionName . "_" . $this->uuid;
    }

    public function clearJavaScriptData() {
        ?>
        <script>
            if (typeof (gs_modules_data_array) === "undefined") {
                gs_modules_data_array = {};
            }

            gs_modules_data_array['<? echo $this->getIdentifier(); ?>'] = {};
        </script>

        <?
    }

    public function setData($data) {
        $this->data = $data;
    }

    public function setExpandOnRow($i) {
        $_SESSION['gs_moduletable_' . $this->getFunctionName()]['rownumber'] = $i;
    }
    
    private function shouldShowRow($rownumber) {
        if ($this->avoidAutoExpanding) {
            return false;
        }

        if (!isset($_SESSION['gs_moduletable_' . $this->getFunctionName()])) {
            return false;
        }
        $sessionData = $_SESSION['gs_moduletable_' . $this->getFunctionName()];

        if ($sessionData['rownumber'] == $rownumber) {
            return true;
        }

        return false;
    }

    private function renderTableContent($attribute, $rownumber) {
        $sessionData = $_SESSION['gs_moduletable_' . $this->getFunctionName()];
        $_POST['data'] = $attribute;

        if (isset($sessionData['index'])) {
            $_POST['data']['gscolumn'] = $sessionData['index'];
        }

        $functioName = $this->getFunctionName();
        $this->application->$functioName();
    }

    public function getFunctionName() {
        return $this->manangerName . "_" . $this->functionName;
    }

    public function avoidAutoExpanding() {
        $this->avoidAutoExpanding = true;
    }

    public static function formatDate($date) {
        if(!$date) {
            return "";
        }
        if (is_numeric($date)) {
            return "<div class='rowdate1' getshop_sorting='$date'>" . date("d.m.y", $date / 1000) . "</div><div class='rowdate2'>" . date("H:i", $date / 1000) . "</div>";
        }
        return "<div class='rowdate1' getshop_sorting='".strtotime($date)."'>" . date("d.m.y", strtotime($date)) . "</div><div class='rowdate2'>" . date("H:i", strtotime($date)) . "</div>";
    }

    public function renderPagedTable() {
        $this->uuid = uniqid();
        $this->loadPagedData();
        $this->clearJavaScriptData();
        $this->renderTable(true);
        $this->printJavaScript();
    }

    public function loadPagedData() {
        $this->setPageDataToSession();
        $this->args[0]->pageNumber = $this->getCurrentPageNumber();
        $this->args[0]->pageSize = $this->getCurrentPageSize();

        $this->loadData();
        $this->pagedInfo = $this->data;
        $this->data = $this->pagedInfo->datas;
    }

    private function renderPaging() {
        echo "<div class='pagingrow'>";

        $leftAndRightCount = 4;
        $disabeled = $this->pagedInfo->currentPageNumber > 1 ? "" : "disabled";
        echo "<div class='pagenumber gsicon-chevron-left-circle $disabeled'></div>";

        $start = $this->pagedInfo->currentPageNumber - $leftAndRightCount;
        if ($start < 1) {
            $start = 1;
        }


        $end = $this->pagedInfo->currentPageNumber + $leftAndRightCount + 1;
        
        if ($end > $this->pagedInfo->totalPages) {
            $end = $this->pagedInfo->totalPages;
        }

        if ($start > 1) {
            if ($end <= $this->pagedInfo->totalPages) {
                echo $this->getNumber(1) . "...";
            }
        }

        for ($i = $start; $i <= $end; $i++) {
            echo $this->getNumber($i);
        }

        if ($end < $this->pagedInfo->totalPages) {
            echo "..." . $this->getNumber($this->pagedInfo->totalPages);
        }

        $disabeled = $this->pagedInfo->currentPageNumber < $this->pagedInfo->totalPages ? "" : "disabled";
        echo "<div class='pagenumber gsicon-chevron-right-circle $disabeled'></div>";

        echo "</div>";

        echo "<div class='pagingrow'>";
        echo $this->application->__f("Page size") . ": ";
        $this->printPageSize(5) . ",";
        $this->printPageSize(10) . ",";
        $this->printPageSize(15) . ",";
        $this->printPageSize(30) . ",";
        $this->printPageSize(50) . ",";
        $this->printPageSize(100);
        echo "</div>";
    }

    public function getNumber($i) {
        $activeClass = $this->pagedInfo->currentPageNumber == $i ? "active" : "";
        return "<div gsclick='handleGetShopModulePaging' newpagenumber='$i' class='pagenumber $activeClass'>$i</div>";
    }

    public function setPageDataToSession() {
        if (isset($_POST['data']['newpagenumber'])) {
            $_SESSION['gs_moduletable_' . $this->getFunctionName() . "_pagenumber"] = $_POST['data']['newpagenumber'];
        }

        if (isset($_POST['data']['newpagesize'])) {
            $_SESSION['gs_moduletable_' . $this->getFunctionName() . "_pagesize"] = $_POST['data']['newpagesize'];
        }
    }

    public function getCurrentPageNumber() {
        if (isset($_SESSION['gs_moduletable_' . $this->getFunctionName() . "_pagenumber"])) {
            return $_SESSION['gs_moduletable_' . $this->getFunctionName() . "_pagenumber"];
        }

        return 1;
    }
    
    public function setCurrentPageNumber($number) {
        $_SESSION['gs_moduletable_' . $this->getFunctionName() . "_pagenumber"] = $number;
    }

    public function getCurrentPageSize() {
        if (isset($_SESSION['gs_moduletable_' . $this->getFunctionName() . "_pagesize"])) {
            return $_SESSION['gs_moduletable_' . $this->getFunctionName() . "_pagesize"];
        }

        return 20;
    }

    public function printPageSize($pageSize) {
        $activeClass = $this->getCurrentPageSize() == $pageSize ? "active" : "";
        echo "<span class='pagenumber $activeClass' gsclick='handleGetShopModulePaging' newpagesize='$pageSize' >$pageSize</span>";
    }

    public function getDate() {
        return $this->data;
    }

    public function sortByColumn($colname, $acending = true) {
        $this->sortByColumn = $colname;
        $this->sortingAscending = $acending;
    }

    private function sortData($dataToPrint) {
        if (!$this->sortByColumn) {
            return $dataToPrint;
        }

        $columnName = $this->sortByColumn;
        $direction = $this->sortingAscending;

        usort($dataToPrint, function($a, $b) use ($columnName, $direction) {
            $aval = 0;
            $bval = 0;
            if(isset($a[$columnName])) {
                $aval = $this->getSortingValue($a[$columnName]);
            }
            if(isset($b[$columnName])) {
                $bval = $this->getSortingValue($b[$columnName]);
            }
                
            if ($direction) {
                if (is_numeric($aval) && is_numeric($bval)) {
                    return floatval($aval) - floatval($bval);
                } else {
                    return strcmp($aval, $bval);
                }
            } else {
                if (is_numeric($aval) && is_numeric($bval)) {
                    return floatval($bval) - floatval($aval);
                } else {
                    return strcmp($bval, $aval);
                }
            }
        });

        return $dataToPrint;
    }

    public function getSortingValue($val) {
        if(stristr($val, "getshop_sorting")) {
            $val = substr($val, strpos($val, "getshop_sorting")+17);
            if(strpos($val, "'") > 0) {
                $val = substr($val, 0, strpos($val, "'"));
            }
            if(strpos($val, "\"") > 0) {
                $val = substr($val, 0, strpos($val, "\""));
            }
        }
        return $val;
    }
    
    public function formatDataToPrint() {
        $retData = array();

        foreach ($this->data as $data) {
            $postArray = array();

            foreach ($this->attributes as $attribute) {
                $val = "";
                if (!isset($attribute[3])) {
                    $val = $data->{$attribute[2]};
                } else {
                    $functionName = $attribute[3];
                    $colVal = isset($attribute[2]) ? @$data->{$attribute[2]} : "";
                    $val = $this->application->$functionName($data, $colVal);
                }

                $postArray[$attribute[0]] = $val;
            }

            $retData[] = $postArray;
        }

        return $retData;
    }

    public function formatDataToSort() {
        $retData = array();

        foreach ($this->data as $data) {
            $postArray = array();

            foreach ($this->attributes as $attribute) {
                $val = $data->{$attribute[2]};
                $postArray[$attribute[0]] = $val;
            }

            $retData[] = $postArray;
        }

        return $retData;
    }

    public function hasSorting($attribute) {
        if (in_array($attribute, $this->sortingArray)) {
            return true;
        }
        return false;
    }

    public function setSorting($sortingArray) {
        $this->sortingArray = $sortingArray;
    }

    public function printSortingScript() {
        ?>
        <script>
            $('.GetShopModuleTable .hassorting').on('click', function () {
                var colheader = $(this);
                var sorting = "asc";
                if (colheader.find('.fa-sort-desc').length > 0) {
                    sorting = "desc";
                }
                var event = thundashop.Ajax.createEvent('', 'sortGetShopTable', $(this), {
                    "column": colheader.attr('index'),
                    "sorting": sorting
                });
                thundashop.Ajax.post(event);
            });
        </script>
        <?php
    }

    public function appendClassToRow($field, $match, $class) {
        $this->checkForField = $field;
        $this->matchOnField = $match;
        $this->appendClass = $class;
    }

    public function appendClassToRowNumbers($rows, $className) {
        $this->appendClassToRows = $rows;
        $this->appendClassToRowsClass = $className;
    }

}
