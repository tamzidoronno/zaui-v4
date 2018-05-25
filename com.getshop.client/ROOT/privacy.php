<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
?>
<div style='max-width: 1000px; margin: 0 auto; padding-left: 20px; padding-right: 20px;'>
    <div>
        Language: <a href='/privacy.php?lang=no'>Norwegian</a> | <a href='/privacy.php?lang=en'>English</a>
    </div>
    <?
    $lang = isset($_GET['lang']) && $_GET['lang'] == "no" ? "no" : "en";
    include 'gdpr/'.$lang.'.phtml';
    ?>
</div>