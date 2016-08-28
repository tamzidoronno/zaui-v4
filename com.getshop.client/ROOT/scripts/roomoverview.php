<?php
$user = $_GET['username'];
$pass = $_GET['password'];
$address = "www.wh.no";
header("Location: http://getshop.express#/login?username=$user&password=$pass&address=$address");
?>