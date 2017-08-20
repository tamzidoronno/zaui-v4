<?php
chdir("../");
include '../loader.php';
session_start();
$base = new FactoryBase();
$res = $base->getApi()->getUserManager()->getPingoutTime();
if(is_string($res) || is_numeric($res)) {
    echo $res;
}
?>