<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getListManager()->confirmEntry($_GET['id']);
echo "Din melding er nå bekreftet. Vi tar kontakt med deg så fort som mulig.<br>";
?>