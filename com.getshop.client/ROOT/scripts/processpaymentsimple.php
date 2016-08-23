<?php

/**
 * Example: http://wilhelmsenhouse.3.0.local.getshop.com/scripts/processpaymentsimple.php?orderid=108205&name=default
 */
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$name = "";
if(isset($_GET['name'])) {
    $_SESSION['enginename'] = $_GET['name'];
}
if(isset($_SESSION['enginename'])) {
    $name = $_SESSION['enginename'];
}

if(isset($_GET['username'])) {
    $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
}
if(isset($_GET['bookingid'])) {
    $booking = $factory->getApi()->getPmsManager()->getBooking($name, $_GET['bookingid']);
}
$orderId = "";
$total = 0;
if(isset($_GET['orderid'])) {
    $orderId = $_GET['orderid'];
    $order = $factory->getApi()->getOrderManager()->getOrderByincrementOrderId($_GET['orderid']);
    $total = $factory->getApi()->getOrderManager()->getTotalAmount($order);
}

if(isset($_GET['action'])) {
    $userName = $factory->getApi()->getUserManager()->getLoggedOnUser()->fullName;
    echo "<center>";
    if($_GET['action'] == "Payment OK") {
        $order->status = 7;
        $order->payment->transactionLog->{time()*1000} = "Order marked as paid for manually, by: " .$userName;
        $booking = $factory->getApi()->getPmsManager()->getBookingWithOrderId($_GET['name'], $order->id);
        $factory->getApi()->getOrderManager()->saveOrder($order);
        $factory->getApi()->getWubookManager()->markCCInvalid($name, $booking->wubookreservationid);
        echo "Order has been marked as paid for.<br>";
    } else {
        $order->status = 3;
        $order->payment->transactionLog->{time()*1000} = "Failed marking as paid for, by: " .$userName;
        $factory->getApi()->getOrderManager()->saveOrder($order);
        $factory->getApi()->getWubookManager()->markCCInvalid($name, $rcode);
        echo "Order has been marked as failed.<br>";
    }
    echo "Action has been handled";
    echo "</center>";
    return;
}
if(!$factory->getApi()->getUserManager()->isLoggedIn()) {
    ?>
<center>
<form action="">
    <input type='hidden' name="orderid" value='<?php echo $orderId; ?>'><br>
    <input name="username" placeholder="Username"><br>
    <input name="password" type="password" placeholder="Password"><br>
    <input type="submit" value="Logg inn"></form>
</center>
    <?php
    return;
}
?>
<style>
    body { background-color:#000; }
</style>
<head>
    <title>Payment processor</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="../js/jquery-1.9.0.js"></script>
    <script type='application/javascript' src='../js/fastclick.js'></script>
</head>
<div style="margin:auto; width: 600px;margin-top: 20px; border-radius: 3px; background-color:#fff; padding: 20px; 
    <?php if(!$orderId) { echo "display:none;"; } ?>">
    <b>1. Log on to wubook</b><br>
    <form action="https://wubook.net/wauth/wauth/__login__/" method="post" target="_NEWR">
        <div id="login">
              <input type="text" name="username" value="KT012">
              <input autocomplete="off" type="password" name="password" value="" placeholder="wubook password">
              <button type="submit"><span>Sign in</span></button>
        </div>     
    </form>
    <b>2. Fetch the card information (at the bottom of the page)</b>
    <form action="https://wubook.net/bookings/1471684982/_submit" name="wform" method="post" target="_new2">
        <input type="hidden" name="qtype" value="2">
        <input type="password" name="cc_password" value="" placeholder="Credit card password"> 
        <button type="submit" class="submitBtn" value="Richiedi i Canali"><span>Show booking</span> </button>
    </form>

    <b>3.Log on to dibs:</b><br>
    <form id="doLogin" name="doLogin" action="https://payment.architrade.com/login/doLogin.action" method="post" target='_new3'>
        <input type="hidden" name="request_locale" value="no_NO" id="doLogin_request_locale">
        <input type="hidden" name="loginType" id="doLogin_loginTypeD2" value="customer">
        <input type="text" name="username" value="90217560" id="doLogin_username" class="inputClass">
        <input type="password" name="password" id="doLogin_password" class="inputClass" value='' placeholder="Dibs password">
        <input type="submit" id="doLogin_loginbox_login" name="loginbox.login" value="Log inn" class="submitClass">
    </form>
    4. In dibs: Go to : "Betalinger" -> "Virtuell Terminal" and enter <?php echo $order->incrementOrderId; ?> as orderid, <?php echo $total; ?> as amount, and the card information found on step 2, and click "Utfør betaling".<br>
    <br>
    <b>5. Success on payment?</b><br>
    <form action='' method='GET'>
        <input type='hidden' value='<?php echo $orderId; ?>' name='orderid'>
        <input type='hidden' value='<?php echo $_GET['name']; ?>' name='name'>
        <input type='submit' value='Payment OK' name='action'> - 
        <input type='submit' value='Payment FAILED' name='action'>
    </form>
</div>


<script>
    function doUpdateCard() {
        var val = $(this).val();
        var splitted = val.split("\n");
        for(var key in splitted) {
            var line = splitted[key];
            if(line.indexOf("Number:") >= 0) {
                var number = line.replace("Number: ", "");
                $('.card').val(number);
            }
            if(line.indexOf("Expring Date:") >= 0) {
                var exp = line.replace("Expring Date: ", "");
                var expdd = exp.split("/");
                $('.expmm').val(expdd[0]);
                $('.expyy').val(expdd[1]);
                $('.card').val(number);
            }
        }
    }
    
    $('.carddata_notparsed').on('keyup', doUpdateCard);
    $('.carddata_notparsed').on('change', doUpdateCard);
</script>