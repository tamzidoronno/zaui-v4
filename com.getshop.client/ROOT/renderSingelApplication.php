<?php
include '../loader.php';

$factory = IocContainer::getFactorySingelton();
$appSettings = $factory->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($_GET['id']);
$instance = $factory->getApplicationPool()->createAppInstance($appSettings);
$instance->renderApplication();
?>