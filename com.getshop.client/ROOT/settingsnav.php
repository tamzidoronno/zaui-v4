<?php
session_start();
include '../loader.php';
$settingsFactory = IocContainer::getSettingsFactorySingleton();
$settingsFactory->initApp();
echo $settingsFactory->json();
?>