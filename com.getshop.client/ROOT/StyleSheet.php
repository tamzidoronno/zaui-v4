<?php
session_start();
include '../loader.php';
IocContainer::getFactorySingelton()->getStyleSheet()->render();

$color = IocContainer::getFactorySingelton()->getConfigurationFlag("color");
$bg = IocContainer::getFactorySingelton()->getConfigurationFlag("bgimage");

if($bg) {
    echo "body { background-image: url(\"skin/default/bgs/$bg.png\"); }";
} else {
    echo "body { background-image: none; }";
}
if($color) {
    $theme = IocContainer::getFactorySingelton()->getApplicationPool()->getSelectedThemeApp();
    ApplicationHelper::printColorStyle($theme->applicationSettings, $color);
}
?>
