<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$factory->getApi()->getOrderManager()->setAllOrdersAsTransferedToAccountSystem();

?>