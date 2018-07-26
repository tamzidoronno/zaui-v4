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

$storeId = $factory->getApi()->getStoreManager()->getStoreId();
$cssStore = str_replace("-", "", $storeId);

include("header.php");
?>
<script>
    getshop_manuallycancelledbutton = false;
 </script>
<body>
    <head>
        <link rel="stylesheet" href="https://s3.amazonaws.com/icomoon.io/135206/GetShopIcons/style.css?tyxklk">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="<?php echo $endpoint; ?>/js/getshop.bookingembed.js"></script>
        <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
        <link href="standardcss.css" rel="stylesheet">
        <link href="storecss/<?php echo $cssStore; ?>.css" rel="stylesheet">
        <script src="<?php echo $endpoint; ?>/scripts/booking/bookingscripts.php"></script>
        <link rel="stylesheet" href="<?php echo $endpoint; ?>/scripts/booking/bookingstyles.php">
    </head>
    
    <div style='display:none' class='terminalpaymentprocess'>
        <span class='content'>
        <?php
        echo "<div style='padding:20px;'>".$factory->__w("Welcome") . " <span class='bookersname'></span></div>";
        echo "<div style='padding:20px;'>" . $factory->__w("You will recieve the code for the room by sms and email after the payment has been completed, please insert the card below and follow the instructions") . "</div>";
        ?>
        <div class='verifonefeedbackdata' style='font-size:12px;font-style: italic;padding-top: 10px; padding-bottom: 10px; display:none;'></div>
        <i class='fa fa-spin fa-spinner'></i>
        <br><br>
            <div style='text-align:center;margin-top: 10px;margin-bottom:10px;'>
                <span class='cancelpaymentbutton dopaymentbutton' style='border: solid 1px; padding: 10px; border-radius:5px;'><?php echo $factory->__w("Cancel payment process"); ?></span>
            </div>
        </span>
    </div>    

    <div class="paymentterminal">
        <a href='paymentterminal.php'><span class="cancelbutton"><?php echo $factory->__w("Cancel booking"); ?></span></a>
        <div style="margin:auto; width: 1200px; padding: 30px;">
            <div id='paymentprocesswindow'></div>
            <div class="logorow">
                <span class="logo"></span>
            </div>
            <div style='font-size: 40px;width:700px; display:inline-block; margin-bottom: 20px;'>
                <?php
                echo $factory->__w("Please enter the phone number given when booked or your booking reference");
                ?>
            </div>
            <input type='text' style='font-size:60px;width:700px;box-sizing: border-box;text-align: center;' id='bookingreferenceid'>
            <span class="nextpagebutton dopaymentbutton" style='width:700px;box-sizing: border-box; margin-top: 10px;'><?php echo $factory->__w("Pay for your stay"); ?></span>
            <div class='noresultfound' style='padding-top: 20px;'><?php echo $factory->__w("No result where found, please try another input or contact us"); ?></div>
        </div>

        <script>
            $('.cancelpaymentbutton').on('click', function() {
                getshop_manuallycancelledbutton = true;
                $.ajax('/scripts/bookingprocess.php?method=cancelPaymentProcess', {
                    dataType: 'jsonp',
                    data: {
                        "body" :  {
                            "terminalid" : localStorage.getItem("getshopterminalid")
                        }
                    },
                    success : function(res) {}
                    
                });
                
            });
            $('.dopaymentbutton').on('click', function() {
                $.ajax('/scripts/bookingprocess.php?method=startPaymentProcess', {
                    dataType: 'jsonp',
                    data: {
                        "body" :  {
                            "terminalid" : localStorage.getItem("getshopterminalid"),
                            "reference" : $('#bookingreferenceid').val()
                        }
                    },
                    success : function(res) {
                        if(!res) {
                            $('.noresultfound').show();
                            setTimeout(function() {
                                $('.noresultfound').fadeOut();
                            }, "5000");
                        } else {
                            getshop_currentorderid = res.orderId;
                            $('.bookersname').html(res.name);
                            $('.terminalpaymentprocess').show();
                        }
                    }
                    
                });
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
        getshop_redirectoToFront();
    }, "600000");
    
    $(document).on('mousedown', getshop_setTimeoutBooking);
    $(document).on('click', getshop_setTimeoutBooking);
    $(document).on('mouseup', getshop_setTimeoutBooking);
    
    function getshop_setTimeoutBooking() {
        clearTimeout(getshop_timeout);
        getshop_timeout = setTimeout(function() {
            getshop_redirectoToFront();
        }, "600000");
    }
</script>

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
    
    
getshop_WebSocketClient = {
    client: false, 
    listeners: [],
    
    connected: function() {
    },
    
    disconnected: function() {
        getshop_WebSocketClient.client = false;
        setTimeout(getshop_WebSocketClient.getClient, 1000);
    },
    
    handleMessage: function(msg) {
        var dataObject = JSON.parse(JSON.parse(msg.data));
        
        for (var i in getshop_WebSocketClient.listeners) {
            var listener = getshop_WebSocketClient.listeners[i];
            if (listener.dataObjectName === dataObject.coninicalName) {
                listener.callback(dataObject.payLoad);
            }
        }
    },
    
    getClient: function() {
//        var me = getshop_WebSocketClient;
        if (!getshop_WebSocketClient.client) {
            var endpoint = window.location.host;
            if(getshop_endpoint) {
                endpoint = getshop_endpoint;
            }
            this.socket = new WebSocket("ws://"+endpoint+":31330/");
            this.socket.onopen = getshop_WebSocketClient.connected;
            this.socket.onclose = function() {
                getshop_WebSocketClient.disconnected();
            };
            this.socket.onmessage = function(msg) {
                getshop_WebSocketClient.handleMessage(msg);
            };
        }
        
        return getshop_WebSocketClient.client;
    },
    
    addListener : function(dataObjectName, callback) {
        getshop_WebSocketClient.getClient(); 
        var listenObject = {
            dataObjectName : dataObjectName,
            callback: callback
        }
        
        getshop_WebSocketClient.listeners.push(listenObject);
   }
};


function getshop_displayVerifoneFeedBack(res) {
    
    if(res.msg === "payment failed") {
        if(getshop_manuallycancelledbutton) {
            window.location.href="paymentterminal.php";
            return;
        }
        alert('Payment failed, please try again!');
        $('.dopaymentbutton').click();
    }

    if(res.msg === "completed") {
        var tosend = {
            "orderId" :  getshop_currentorderid,
            "terminalId" : localStorage.getItem("getshopterminalid")
        }
        
        $.ajax(getshop_endpoint + '/scripts/bookingprocess.php?method=printReciept', {
            dataType: 'jsonp',
            data: {
                "body": tosend,
                "sessionid" : getshop_getsessionid()
            },
            success: function (res) {}
        });
        
        setTimeout(function() {
            alert('Thank you for your payment, the room number and code for the room will be sent to you by sms and email');
            window.location.href="paymentterminal.php";
        }, "2000");
    } else {
        $('.verifonefeedbackdata').show();
        $('.verifonefeedbackdata').html(res.msg);
    }
}

getshop_WebSocketClient.addListener("com.thundashop.core.verifonemanager.VerifoneFeedback", getshop_displayVerifoneFeedBack);

    
</script>
<style>
    .terminalpaymentprocess {  
        position: fixed;
        left: 0px;
        top: 0px;
        height: 100%;
        width: 100%;
        background-color: rgba(0,0,0,0.8);
        z-index: 5;
        text-align:center;
    }
    .terminalpaymentprocess .content {  
        background-color:#fff;
        width: 50%;
        display: inline-block;
        margin-top: 40px;
        font-size: 25px;
        padding: 10px;
    }
    
    .noresultfound { display:none; color:red; font-size: 20px; }
</style>