<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$contentManager = new ContentManager();
$router = new PageRouter();

?>

<div class="top_translation">
    <div class="closebutton">[ CLOSE ]</div>
    <div class="translation_leftmenu">
        <?php
        $keys = $contentManager->getAllKeysForPage($router->getCurrentPageName());
        foreach ($keys as $key) {
            echo "<div class='areaentry' keyid='$key'>".$key."</div>";
        }
        
        $keys = $contentManager->getAllKeysForPage("gs_common_area");
        foreach ($keys as $key) {
            echo "<div class='areaentry' keyid='$key'>".$key."</div>";
        }
        ?>
    </div>

    <div class="translation_leftmenu_mainarea">
        WORK AREA
    </div>
</div>
