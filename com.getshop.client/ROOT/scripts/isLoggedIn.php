<?php
chdir("../");
include '../loader.php';
session_start();
$base = new FactoryBase();
echo $base->getApi()->getUserManager()->getPingoutTime();
?>