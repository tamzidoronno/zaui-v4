<?php
$url = $_GET['url'];
$decodedUrl = urldecode($url);
header("Location: ".$decodedUrl);
?>
