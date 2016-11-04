<?php

    function cleanUpSession() {
        foreach ($_SESSION as $key => $value) {
            if (strpos($key, 'scope') === false) {
                unset($_SESSION[$key]);
            }
        }
    }

    include '../loader.php';
    $factory = IocContainer::getFactorySingelton();
    
    if (isset($_GET['action']) && $_GET['action'] == "cancel") {
        cleanUpSession();
        $factory->getApi()->getUserManager()->cancelImpersonating();
    } else {
        cleanUpSession();
        $factory->getApi()->getUserManager()->impersonateUser($_GET['userId']);
    }
    
    \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::refresh();
    
    $_SESSION['showadmin'] = false;
    
    echo "<script>";
    if (isset($_GET['page'])) {
        echo "document.location = '/index.php?page=".$_GET['page']."';";
    } else {
        echo "document.location = '/index.php';";
    }
    
    echo "</script>";
?>