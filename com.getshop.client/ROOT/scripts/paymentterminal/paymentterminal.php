<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
include("header.php");
?>
<head>
    <link href="standardcss.css" rel="stylesheet">
</head>
<body>
    <div class="paymentterminal">
        <div class="logorow">
            <span class="logo"></span>
        </div>
            <bR>
            <bR>
        <h1><?php echo $factory->__w("Welcome, book your stay here!"); ?></h1>
            <bR>
            <bR>

        <a href="startbooking.php"><span class="nextpagebutton"><?php echo $factory->__w("Book a stay"); ?></span></a>
        <span class="nextpagebutton"><?php echo $factory->__w("Already booked?"); ?></span>
        <div class="explaintext">
           <?php echo $factory->__w("* This is a self service check in machine. After booking you will recieve a sms and email with the code for your room."); ?>
        </div>
    </div>
</body>

<script>
    
setTimeout(function() {
    window.location.href="paymentterminal.php";
}, "3600000");
</script>
