<head>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="/js/chosen/chosen.jquery.min.js"></script>
    <script src="/js/chosen/chosen.css"></script>
    <link rel="stylesheet" type="text/css" href="/js/chosen/chosen.css">

</head>
<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
if(!$factory->getApi()->getUserManager()->isLoggedIn()) {
    echo "Not logged on, please logon <a href='/totp.php'>here</a>";
    return;
}
include("tickets.php");
include("leads.php");
include("customerstosetup.php");
?>
