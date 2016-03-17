<?php

/* 
 * This is a simple way to create excel reports.
 * 1. Post the array of data that you wish to create en file of
 * 
 * $_POST['filename'] 
 * $_POST['data'] (jsonecoded data)
 * 
 */

include 'excel.php';
chdir("../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton();

header("Content-Type:   application/vnd.ms-excel; charset=ISO-8859-1");
header("Expires: 0");
header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
header("Cache-Control: private", false);

$xls = new Excel($_POST['filename']);

$data = base64_decode($_POST['data']);
$data = json_decode($data);

foreach ($data as $row) {
    $xls->home();
    foreach ($row as $cell) {
        $column = mb_convert_encoding($cell, "ISO-8859-1", "UTF-8");
        $xls->label($column);
        $xls->right();
    }
    $xls->down();
}

$xls->send();