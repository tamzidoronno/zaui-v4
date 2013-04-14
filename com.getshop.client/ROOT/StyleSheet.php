<?php
session_start();
include '../loader.php';
IocContainer::getFactorySingelton()->getStyleSheet()->render();
?>
