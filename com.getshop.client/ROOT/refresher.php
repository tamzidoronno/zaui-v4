<?php
session_start();

if(isset($_SESSION['refreshposviewer'])) {
    unset($_SESSION['refreshposviewer']);
    echo "1";
} else {
    echo "0";
}

