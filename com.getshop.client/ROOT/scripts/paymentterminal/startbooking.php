<?php
chdir("../../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton();
$isProdMode = $factory->isProductionMode();
$endpoint = "https://www.getshop.com";
$jsEnpoint = "https://www.getshop.com";
if(!$isProdMode) {
    $endpoint = "";
    $jsEnpoint = "http://" . $_SERVER['SERVER_NAME'] ."/";
}
include("header.php");
?>
<body>
    <head>
        <link rel="stylesheet" href="https://s3.amazonaws.com/icomoon.io/135206/GetShopIcons/style.css?tyxklk">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="<?php echo $endpoint; ?>/js/getshop.bookingembed.js"></script>
        <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
        <link href="standardcss.css" rel="stylesheet">
        <script src="<?php echo $endpoint; ?>/scripts/booking/bookingscripts.php"></script>
        <link rel="stylesheet" href="<?php echo $endpoint; ?>/scripts/booking/bookingstyles.php">
    </head>

    <div class="paymentterminal">
        <a href='paymentterminal.php'><span class="cancelbutton"><?php echo $factory->__w("Cancel booking"); ?></span></a>
        <div style="margin:auto; width: 1200px; padding: 30px;">
            
            <div class="logorow">
                <span class="logo"></span>
            </div>

            <h1><?php echo $factory->__w("Book your stay now"); ?></h1>
            <div id='bookingprocess'></div>
        </div>

        <script>
        $( "#bookingprocess" ).getshopbooking({
            "endpoint" : "<?php echo $endpoint; ?>",
            "viewmode" : "terminal",
            "jsendpoint" : "<?php echo $jsEnpoint; ?>",
            "terminalid" : localStorage.getItem("getshopterminalid")
        });
        
        if(!localStorage.getItem("getshopterminalid")) {
            alert('no getshopterminalid set: getshopterminalid');
        }
        </script>
    </div>
</body>
<?php
include("keyboard.php");
?>
<script>
    
    getshop_timeout = setTimeout(function() {
        window.location.href="paymentterminal.php";
    }, "600000");
    
    $(document).on('mousedown', getshop_setTimeoutBooking);
    $(document).on('click', getshop_setTimeoutBooking);
    $(document).on('mouseup', getshop_setTimeoutBooking);
    
    function getshop_setTimeoutBooking() {
        clearTimeout(getshop_timeout);
        getshop_timeout = setTimeout(function() {
            window.location.href="paymentterminal.php";
        }, "600000");
    }
</script>