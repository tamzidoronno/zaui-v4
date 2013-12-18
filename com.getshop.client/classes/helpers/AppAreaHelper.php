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
        $numberOfEntries = $numberOfEntries + $offset-1;
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
                    if($rowWidth != null && isset($rowWidth[$index])) {
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
        ?>
        <div area="<? echo $name; ?>" class="applicationarea <?
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
        <?php 
            if(AppAreaHelper::$displayContent) {
                $page->getApplicationArea($name)->render(); 
            }
            ?>
        </div>
        <?
        $result = ob_get_contents();
        ob_end_clean();
        return $result;
        
    }

}
?>
