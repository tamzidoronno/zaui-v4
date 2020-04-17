<?php
$factory = IocContainer::getFactorySingelton();
$multipleIds = (array)$factory->getApi()->getPaymentManager()->getMultiplePaymentMethods();
?>
<br><br><br>
<div>
    <?php
    echo "<h1>Please choose your preferred payment method</h1>";
    echo "<br>";
    foreach($multipleIds as $id) {
        $app = $factory->getApplicationPool()->getApplicationSetting($id);
        $instance = $factory->getFactory()->getApplicationPool()->createInstace($app);
        echo "<div><span class='choosebutton' onclick='window.location.href=\"/pr.php?id=".$_GET['id']."&chosenpaymentmethod=$id\"'>";
        $instance->printButton();
        echo "</span></div>";
    }
    ?>
</div>

<style>
    body {
        text-align: center;
        background-color: #23314e;
        color:#fff;
    }
    .choosebutton {
        position:relative;
        background-color: #4CAF50; /* Green */
        border: none;
        color: white;
        padding: 15px 32px;
        text-align: center;
        text-decoration: none;
        display: inline-block;
        font-size: 16px;
        margin: 4px 2px;
        cursor: pointer;
        
        background-color: #008CBA;
        width: 250px;
        margin-bottom: 20px;
        border-radius: 3px;
       
    }
</style>