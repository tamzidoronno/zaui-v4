<?php
    include '../loader.php';
    $factory = IocContainer::getFactorySingelton();
    if (!isset($_SESSION['GS_InSession']) || !$_SESSION['GS_InSession']) {
        die("Timed out");
    }
    $factory->run(true);
?>