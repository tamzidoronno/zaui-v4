<?php
$url = $_GET['url'];
$url = urldecode($url);

if(substr($url, 0, 24) == "https://www.auksjonen.no") {
    header("content-type:application/json");
    echo file_get_contents($url);
} else {
    echo "access denied";
}
?>

