<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$code = $_GET['code'];
$state = $_GET['state'];



?>