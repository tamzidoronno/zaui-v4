<?php

include '../loader.php';
$factory = IocContainer::getFactorySingelton();


if (isset($_GET['action'])) {
    $factory->getApi()->getSedoxProductManager()->sync($_GET['action']);
}

?>
