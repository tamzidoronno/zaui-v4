<?php
//crontabjob@smh.no
//"crontabjob12345"

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$factory->getApi()->getOrderManager()->setAllOrdersAsTransferedToAccountSystem();
?>