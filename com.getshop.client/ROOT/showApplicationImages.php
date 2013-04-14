<?php
if (!isset($_GET['appNamespace']) || !isset($_GET['image'])) {
    echo "Usage: showCssImage.php?appName=appName&image=destinationToImage";
}

ob_start();
include '../loader.php';
session_cache_limiter('none');

$namespace = $_GET['appNamespace'];
$image = $_GET['image'];

if (strstr($image, "..") == true || strstr($namespace, "..") == true) {
    die("not allowed to have .. in image path");
}

$path = "../app/$namespace/".$_GET['image'];

if (file_exists($path)) {
    $mimeType = mime_content_type($path);
    if (!strstr($mimeType, "image")) {
        die("only images are allowed to be printed");
    }

    header("Content-type: ".$mimeType);
    echo file_get_contents($path);
}
?>