<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$report = new ns_02b94bcd_39b9_41aa_b40c_348a27ca5d9d\PmsConference();
$report->printReport();
?>