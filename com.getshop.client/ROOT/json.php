<?php
echo $_GET['rewrite'];
    include '../loader.php';
    $factory = IocContainer::getFactorySingelton();
    $factory->run(true);
?>