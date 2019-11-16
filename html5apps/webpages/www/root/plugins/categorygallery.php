<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir('../');
require '../vendor/autoload.php';
include '../common/autoloader.php';

$gallery = new CategoryGallery();
if ($_GET['event'] == "save") {
    $gallery->saveData($_GET['id'], $_POST['data']);
}
