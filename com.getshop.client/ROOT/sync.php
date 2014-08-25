<?php

include '../loader.php';
$factory = IocContainer::getFactorySingelton();


if (isset($_GET['action'])) {
    $factory->getApi()->getSedoxProductManager()->login($_GET['username'], $_GET['password']);
    $factory->getApi()->getSedoxProductManager()->sync($_GET['action']);
}

?>
