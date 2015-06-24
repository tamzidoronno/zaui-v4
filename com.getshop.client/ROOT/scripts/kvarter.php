<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getMessageManager()->sendSms("46774240", "This is just a test", 47);
?>