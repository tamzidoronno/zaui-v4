<?php

class AppAreaHelper {

    public static $displayContent = true;

    public static function printRows($page, $numberOfEntries, $offset = 1, $rowWidth = null) {
        ob_start();
        $width = 100;
        if ($numberOfEntries == 2) {
            $width = 50;
        } else if ($numberOfEntries == 3) {
            $width = 33.3;
        } else if ($numberOfEntries == 4) {
            $width = 25;
        } else if ($numberOfEntries == 5) {
            $width = 20;
        }
        $numberOfEntries = $numberOfEntries + $offset - 1;
        ?>
        <?
        $index = 0;
        for ($i = $offset; $i <= $numberOfEntries; $i++) {
            $class = "gs_col c$i gs_row_$offset ";
            if ($i == $offset) {
                $class .= "gs_margin_right";
            } else if ($i == $numberOfEntries) {
                $class .= "gs_margin_left";
            } else {
                $class .= "gs_margin_left gs_margin_right";
            }
            if ($rowWidth != null && isset($rowWidth[$index])) {
                $width = $rowWidth[$index];
            }
            $index++;
            echo "<div row='$offset' style='width:$width%; box-sizing:border-box;-moz-box-sizing:border-box;' class='$class gs_row_cell inline'>";
            echo AppAreaHelper::printAppArea($page, "col_$i", false, false, false, "cell");
            echo "</div>";
        }
        ?>
        <?
        $result = ob_get_contents();
        ob_end_clean();
        return $result;
    }

    public static function printAppArea($page, $name, $include_bottom_margin = false, $include_right_margin = false, $include_left_margin = false, $type = "standard") {
        ob_start();
        $appClasses = "";
        $extraclass = count($page->getApplicationArea($name)->applications) == 0 ? "no_apps_on_area" : "";
        foreach ($page->getApplicationArea($name)->applications as $app) {
            $appClasses .= "apparea_app_" . $app->applicationSettings->appName;
        }
        ?>
        <div area="<? echo $name; ?>" class="<? echo $appClasses . " " . $extraclass; ?> applicationarea <?
        if ($include_bottom_margin) {
            echo " gs_margin_bottom";
        }
        if ($include_right_margin) {
            echo " gs_margin_right";
        }
        if ($include_left_margin) {
            echo " gs_margin_left";
        }
        ?>" type='<? echo $type; ?>'>
            <span class="gs_apparea_extra"></span>

            <?php
            if (AppAreaHelper::$displayContent) {
                $page->getApplicationArea($name)->render();
            }
            ?>
        </div>
        <?
        $result = ob_get_contents();
        ob_end_clean();
        return $result;
    }

    /**
     * 
     * @param core_pagemanager_data_PageArea $area
     * @param type $colCount
     * @param type $totalColCount
     * @param type $rowNumber
     * @param type $page
     */
    public static function printAppAreaNew($area, $colCount, $row, $rowNumber, $page) {
        $totalColCount = sizeof($row->areas);
        $name = "";
        $appClasses = "apparea_app_";
        $extraclass = "";
        $includeColumns = false;
        if($totalColCount > 1) {
            $includeColumns = true;
        }
        
        
        
        $width = round(100 / $totalColCount, 1);
        if(sizeof($row->rowWidth) > 0) {
            $width = $row->rowWidth[$colCount-1];
        }
        
        if ($colCount == 1 && $totalColCount != 1) {
            $extraclass = "gs_margin_right";
        } else if ($colCount == $totalColCount && $totalColCount != 1) {
            $extraclass = "gs_margin_left";
        } else if($colCount > 1) {
            $extraclass = "gs_margin_left gs_margin_right";
        }
        
        //Backward compability for design stuff.
        if(strpos($area->type, "col_") === 0) {
            $colCount = str_replace("col_", "",$area->type);
        }
        
        if($totalColCount > 1) {
            $extraclass .= " gs_row_cell";
        }
        $extraclass .= " gs_row_" . $rowNumber;
        
        $extraclass .= " ";

        if($includeColumns) {
            echo "<div row='$rowNumber' style='width:$width%; box-sizing:border-box;-moz-box-sizing:border-box;' class='gs_col c$colCount " . $extraclass . " gs_row_cell inline'>";
        }

        if($totalColCount <= 1) {
            $type = "standard";
        } else {
            $type = "cell";
        }
        
        $marginbottom = "";
        if($rowNumber < sizeof($page->layout->rows)-1) {
            $marginbottom = "gs_margin_bottom";
        }
        
        echo "<div area='".$area->type."' type='".$type."' class='applicationarea $marginbottom'>";
        $pagearea = new PageArea($page,$area);
        $pagearea->render();
        echo "</div>";
        if($includeColumns) {
            echo "</div>";
        }
    }

    public static function printAppRow($row, $rownumber, $page) {
        $factory = IocContainer::getFactorySingelton(false);
        $colnumber = 0;
        $colCount = 1;
        foreach ($row->areas as $area) {
            AppAreaHelper::printAppAreaNew($area, $colCount, $row, $rownumber, $page);
            $colCount++;
        }
    }

}
?>
