<?php
class AppAreaHelper {
    public static function printAppArea($page, $name, $include_bottom_margin = false) {
        ?>
        <div area="<? echo $name; ?>" class="applicationarea <? if($include_bottom_margin) { echo "gs_margin_bottom"; } ?>">
            <?php $page->getApplicationArea($name)->render(); ?>
        </div>
        <?
    }
}
?>
