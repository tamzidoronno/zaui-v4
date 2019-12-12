<?php

/* @var $this ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointNewSale */
chdir("../../../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton(false);
echo $factory->getApi()->getPosManager()->getProductCountForPgaTab($_POST['tabid']);