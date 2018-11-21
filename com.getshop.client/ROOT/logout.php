<?php
session_start();

include '../loader.php';
IocContainer::getFactorySingelton()->getApi()->getUserManager()->logout();

session_destroy();
if (isset($_GET['redirectto'])) {
    header("location:".$_GET['redirectto']);
} else if (isset($_GET['goBackToHome'])) {
    header("location:/");
} else {
    header("location:login.php");
}
?>