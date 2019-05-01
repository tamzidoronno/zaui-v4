<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir('../');
require '../vendor/autoload.php';
include '../common/autoloader.php';

$contentManager = new ContentManager();
if ($_GET['event'] == "save") {
    $contentManager->saveData($_GET['id'], $_POST['data']);
}

if ($_GET['event'] == 'load' || $_GET['event'] == "save") {
    ?>
    <textarea name="content" id="editor"><?php echo $contentManager->getData($_GET['id']); ?></textarea>
    <script>
        editor = CKEDITOR.replace( document.querySelector( '#editor' ) );
    </script>
    
    <?php
    if ($_GET['event'] == "save") {
        echo "<span style='color: green;'>Saved success</span><br/><br/>";
    }
    ?>
    <div class="button savetranslation" keyid="<?php echo $_GET['id']; ?>">
        Save
    </div>
    <?php
}