<?php
    include '../loader.php';
    $factory = IocContainer::getFactorySingelton();
    $now = time();
    
    if (isset($_SESSION['discard_after']) && $now > $_SESSION['discard_after']) {
        session_unset();
        session_destroy();
        die("Timed out");
    }

    if (isset($_SESSION['discard_after'])) {
        $_SESSION['discard_after'] = $now + ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::$sessionTimeout;
    }
    
    $factory->run(true);
?>