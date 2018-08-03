<?php
session_start();
include 'classes/autoload.php';
$router = new Router();
$page = $router->getPage();
$page->preRun();
$page->read_csv_translation();
$page->render();
?>