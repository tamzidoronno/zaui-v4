<?php
if (isset($_GET['id'])) {
    session_id($_GET['id']);
}

session_start();

echo $_SESSION['contractLoaded'];

?>