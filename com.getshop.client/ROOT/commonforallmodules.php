<?php
$_SESSION['hasoverdueinvoices'] = false;
/*
 *
 * Removed this check as a whole 2921-06-21
 *
$factory = IocContainer::getFactorySingelton();
if(!\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
    return;
}
$storeId = $factory->getStore()->id;
$useCache = false;
if(isset($_SESSION['fetchedoverdue_lasttime'])) {
    $time = $_SESSION['fetchedoverdue_lasttime'];
    $diff = time() - $time;
    if($diff < 240) {
        $useCache = true;
    }
}
if($useCache) {
    $fetched = $_SESSION['fetchedoverdue'];
} else {
    $fetched = file_get_contents("https://system.getshop.com/scripts/apiexport.php?method=getUnpaidInvoicesForStore&manager=SystemManager&arg1=".$storeId);
    $_SESSION['fetchedoverdue'] = $fetched;
    $_SESSION['fetchedoverdue_lasttime'] = time();
}


$_SESSION['hasoverdueinvoices'] = false;

if($fetched && $fetched != "null" && $fetched != "[]") {
    $_SESSION['hasoverdueinvoices'] = true;
    echo "<div class='warnsystemshutdownduebills' style='background-color:red; text-align:center; color:#fff;padding: 10px; font-size: 16px;position:fixed;bottom: 0px;width:100%;z-index:1000; cursor:pointer;' onclick=\"$(this).hide()\">";
    $fetched = json_decode($fetched);
    $ids = array();
    foreach($fetched as $f) {
        $ids[] = $f->incrementOrderId . " (" . $f->daysDue . " days overdue)";
    }
    echo "Your system is in danger of being shut down due to unpaid invoices, please pay invoice: " . join(" <b>AND</b> ", $ids);
    echo "</div>";
}
*/
?>
