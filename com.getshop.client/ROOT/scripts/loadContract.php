<?php
if (isset($_GET['sessid'])) {
    session_id($_GET['sessid']);
}
session_start();

echo $_SESSION['contractLoaded'];

?>