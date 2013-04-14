<?php
session_start();
include '../loader.php';

if(isset($_GET['setTheeme'])) {
    $theeme = $_GET['setTheeme'];
} else {
    $theeme = "slick";
}

$factory = IocContainer::getFactorySingelton();
$factory->setTheeme($theeme);
?>
<link id='mainlessstyle' rel="stylesheet" type="text/css" media="all" href="StyleSheet.php">