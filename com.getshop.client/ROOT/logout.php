<?php
session_start();
session_destroy();
if (isset($_GET['goBackToHome'])) {
    header("location:/");
} else {
    header("location:login.php");
}
?>
