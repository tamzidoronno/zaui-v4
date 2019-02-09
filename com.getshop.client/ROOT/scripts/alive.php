<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton(false);
echo $factory->getApi()->getUserManager()->isLoggedIn();