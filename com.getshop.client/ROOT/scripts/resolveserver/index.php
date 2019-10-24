<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$servers = $factory->getApi()->getGetShopLockSystemManager()->getLockServers();
$name = "";
foreach($servers as $server) {
    if($server->id == $_GET['id']) {
        $ip = $server->hostname;
        $name = $server->givenName;
    }
}

if($_GET['pingserver'] && $_GET['pingserver'] == "true") {
    $port = 22;
    $waitTimeoutInSeconds = 1; 
    if($fp = @fsockopen($ip,$port,$errCode,$errStr,$waitTimeoutInSeconds)){   
        echo "yes";
    } else {
        echo date("d.m.y H:i:s");
    } 
    @fclose($fp);
    exit(0);
}
?>

<html>
    <head>
        <script src='/js/jquery-1.9.0.js'></script>
    </head>
    <body style='background-color:#3a99d7; margin: 30px;'>
        <div style='background-color:#fff; max-width:800px;padding: 5px; border-radius: 5px;margin:auto;'>
            <div style='border-bottom: solid 1px; color:#fff; background-color:red;box-sizing: border-box; padding: 5px; font-size: 26px; text-align: center; width:100%;'>Your server is offline, last checked <span id='lastcheckedfield'>....</span></div>
            <?php 
            if(isset($_GET['step']) && $_GET['step'] == 2) {
                include('scripts/resolveserver/step2.php');
            } else if(isset($_GET['step']) && $_GET['step'] == 3) {
                include('scripts/resolveserver/step3.php');
            } else if(isset($_GET['step']) && $_GET['step'] == 4) {
                include('scripts/resolveserver/step4.php');
            } else if(isset($_GET['step']) && $_GET['step'] == "startrecovery") {
                include('scripts/resolveserver/startrecovery.php');
            } else if(isset($_GET['step']) && $_GET['step'] == "wait") {
                include('scripts/resolveserver/wait.php');
            } else {
                include('scripts/resolveserver/step1.php');
            }
            ?>
        </div>
    </body>
</html>

<script>
    $(function() {
        setInterval(function() {
            var done = $.get("index.php?id=<?php echo $_GET['id']; ?>&pingserver=true", function(res) {
                if(res === "yes") {
                    window.location.href='backonline.php';
                } else {
                    $('#lastcheckedfield').html(res);
                }
            });
        }, "2000");
    });
</script>