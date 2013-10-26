<?php

class AppAreaHelper {

    public static function printRows($page, $numberOfEntries, $offset = 1) {
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
                for ($i = $offset; $i <= $numberOfEntries; $i++) {
                    $class = "gs_col c$i gs_row_$offset ";
                    if ($i == $offset) {
                        $class .= "gs_margin_right";
                    } else if ($i == $numberOfEntries) {
                        $class .= "gs_margin_left";
                    } else {
                        $class .= "gs_margin_left gs_margin_right";
                    }

                    echo "<div style='width:$width%; box-sizing:border-box;' class='$class gs_row_cell inline'>";
                    AppAreaHelper::printAppArea($page, "col_$i", false, false, false, "cell");
                    echo "</div>";
                }
                ?>
        <?
    }

    public static function printAppArea($page, $name, $include_bottom_margin = false, $include_right_margin = false, $include_left_margin = false, $type = "standard") {
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
        <?php $page->getApplicationArea($name)->render(); ?>
        </div>
        <?
    }

}
?>
