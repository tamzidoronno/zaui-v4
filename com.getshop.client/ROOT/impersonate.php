<?php
    include '../loader.php';
    $factory = IocContainer::getFactorySingelton();
    
    if (isset($_GET['action']) && $_GET['action'] == "cancel") {
        $factory->getApi()->getUserManager()->cancelImpersonating();
    } else {
        $factory->getApi()->getUserManager()->impersonateUser($_GET['userId']);
    }
    
    \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::refresh();
    
    $_SESSION['showadmin'] = false;
    echo "<script>";
    echo "document.location = '/index.php';";
    echo "</script>";
?>