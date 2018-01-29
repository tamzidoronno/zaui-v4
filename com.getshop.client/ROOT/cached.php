<?php
include '../loader.php';
session_start();
$id = $_POST['core']['appid'];
$id = $_SESSION['cachedClasses'][$id];

$application = new $id();
$application->{$_POST['event']}();

?>
