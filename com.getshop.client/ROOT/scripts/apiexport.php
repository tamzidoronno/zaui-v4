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

if(validateDate($arg1)) { $arg1 = convertToJavaDate($arg1); }
if(validateDate($arg2)) { $arg2 = convertToJavaDate($arg2); }
if(validateDate($arg3)) { $arg3 = convertToJavaDate($arg3); }
if(validateDate($arg4)) { $arg4 = convertToJavaDate($arg4); }
if(validateDate($arg5)) { $arg5 = convertToJavaDate($arg5); }

if(isset($_GET['jsonarg1']) && $_GET['jsonarg1'] != "null") { $arg1 = json_decode($_GET['jsonarg1']); }
if(isset($_GET['jsonarg2']) && $_GET['jsonarg2'] != "null") { $arg2 = json_decode($_GET['jsonarg2']); }
if(isset($_GET['jsonarg3']) && $_GET['jsonarg3'] != "null") { $arg3 = json_decode($_GET['jsonarg3']); }
if(isset($_GET['jsonarg4']) && $_GET['jsonarg4'] != "null") { $arg4 = json_decode($_GET['jsonarg4']); }
if(isset($_GET['jsonarg5']) && $_GET['jsonarg5'] != "null") { $arg5 = json_decode($_GET['jsonarg5']); }


function convertToJavaDate($time) {
    $time = strtotime($time);
    return date("M d, Y h:i:s A", $time);
}        
        
file_put_contents("/tmp/test.txt", $arg1);
file_put_contents("/tmp/test.txt", $arg2);
file_put_contents("/tmp/test.txt", $arg3);
file_put_contents("/tmp/test.txt", $arg4);
file_put_contents("/tmp/test.txt", $arg5);

$res = $factory->getApi()->{$manager}()->{$method}($arg1, $arg2, $arg3, $arg4, $arg5);

function validateDate($date)
{
    $d = DateTime::createFromFormat('Y-m-d', $date);
    if($d && $d->format('Y-m-d') === $date) {
        return true;
    }
    $d = DateTime::createFromFormat('Y.m.d', $date);
    if($d && $d->format('Y.m.d') === $date) {
        return true;
    }
    $d = DateTime::createFromFormat('d.m.Y', $date);
    if($d && $d->format('d.m.Y') === $date) {
        return true;
    }
    $d = DateTime::createFromFormat('d-m-Y', $date);
    if($d && $d->format('d-m-Y') === $date) {
        return true;
    }
}

if(isset($_GET['callback'])) {
    echo $_GET['callback'] . '(' . json_encode($res) . ")";
} else {
    echo json_encode($res);
}
?>