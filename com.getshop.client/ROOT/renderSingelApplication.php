<?php
include '../loader.php';

$factory = IocContainer::getFactorySingelton();

echo "<div style='padding: 20px;'>";

// ProMeister Logo :D
if ($factory->getStore()->id == "6524eb45-fa17-4e8c-95a5-7387d602a69b") {
    echo "<img src='http://promeisterse30.getshop.com/displayImage.php?id=b67ff4c6-8759-44a8-a1ce-68276013bce0&width=247'/>";
    echo "<br/>";
    echo "<br/>";
    echo "<br/>";
}

$appSettings = $factory->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($_GET['id']);
$instance = $factory->getApplicationPool()->createAppInstance($appSettings);
$instance->renderApplication();
echo "</div>";
?>