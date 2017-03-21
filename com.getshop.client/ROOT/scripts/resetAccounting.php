<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

if ($_GET['key'] !== "asdøfkajsdlfkjasdlkfj234i5ojroaisdjn2") {
    return;
}

$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$factory->getApi()->getAccountingManager()->resetAllAccounting();
echo "done";
?>