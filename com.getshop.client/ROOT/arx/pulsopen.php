<?php
$id = $_GET['id'];
$state = $_GET['state'];

chdir("../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getArxManager()->doorAction($id, $state);
?>

