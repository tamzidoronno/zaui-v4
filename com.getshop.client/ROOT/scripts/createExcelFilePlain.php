<?php

/* 
 * This is a simple way to create excel reports.
 * 1. Post the array of data that you wish to create en file of
 * 
 * $_POST['filename'] 
 * $_POST['data'] (jsonecoded data)
 * 
 */


header("Content-Type:   application/vnd.ms-excel; charset=ISO-8859-1");
header("Content-Disposition: attachment; filename=".$_POST['filename']);  //File name extension was wrong
header("Expires: 0");
header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
header("Cache-Control: private", false);
$data = base64_decode($_POST['data']);
echo $data;