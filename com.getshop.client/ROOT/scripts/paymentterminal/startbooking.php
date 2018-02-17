<?php
chdir("../../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton();
$isProdMode = $factory->isProductionMode();
$endpoint = "https://www.getshop.com";
if(!$isProdMode) {
    $endpoint = "";
}
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
        <a href='paymentterminal.php'><span class="cancelbutton">Cancel booking</span></a>
        <div style="margin:auto; width: 1200px; padding: 30px;">
            
            <div class="logorow">
                <span class="logo"></span>
            </div>

            
            <h1>Book your stay now</h1>
            <div id='bookingprocess'></div>
        </div>

        <script>
        $( "#bookingprocess" ).getshopbooking({
            "endpoint" : "<?php echo $endpoint; ?>",
            "viewmode" : "terminal",
            "terminalid" : "029df902-0e66-445d-84b2-cdae3fc371d7"
        });
        </script>
    </div>
</body>

