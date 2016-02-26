<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$method = $_GET['method'];
$manager = "get".$_GET['manager'];
$arg1 = null;
$arg2 = null;
$arg3 = null;
$arg4 = null;
$arg5 = null;
header("Content-Type: application/json");
if(isset($_GET['arg1']) && $_GET['arg1'] != "null") { $arg1 = $_GET['arg1']; }
if(isset($_GET['arg2']) && $_GET['arg2'] != "null") { $arg2 = $_GET['arg2']; }
if(isset($_GET['arg3']) && $_GET['arg3'] != "null") { $arg3 = $_GET['arg3']; }
if(isset($_GET['arg4']) && $_GET['arg4'] != "null") { $arg4 = $_GET['arg4']; }
if(isset($_GET['arg5']) && $_GET['arg5'] != "null") { $arg5 = $_GET['arg5']; }

$res = $factory->getApi()->{$manager}()->{$method}($arg1, $arg2, $arg3, $arg4, $arg5);
    
echo json_encode($res);
?>