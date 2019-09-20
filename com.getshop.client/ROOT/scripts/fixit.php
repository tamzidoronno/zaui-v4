<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getOrderManager()->cleanupMessedUpOrderTransactionForForignCurrencyCreditNotes($_GET['password']);
?>