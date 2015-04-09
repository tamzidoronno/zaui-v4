<?
$factory = IocContainer::getFactorySingelton();
$settings = $factory->getApi()->getPageManager()->getApplicationsBasedOnApplicationSettingsId("8cc26060-eef2-48ac-8174-914f533dc7ed");
$instances = [];
foreach ($settings as $setting) {
    $instances[] = $factory->getApplicationPool()->createAppInstance($setting);
}

$activeInstance = null;
foreach ($instances as $instance) {
    if ($instance->getCurrentUser()) {
        $activeInstance = $instance;
    }
}

if (!$activeInstance) {
    return;
}

echo "\n" . '<script type="text/javascript" src="/js/jquery-1.9.0.js""></script>';
?>
<style>
    body {
        margin: 0px;
        padding: 0px;
    }
    .bosch_header {
        height: 50px;
        background-color: #FFF;
        color: #333;
        background: linear-gradient(to bottom, #EEE, #BBB);
        position: fixed;
        width: 100%;
        line-height: 50px;
        padding-left: 10px;
    }
    
    .logout {
        position: absolute;
        right: 0px;
        top: 0px;
        height: 50px;
        border-left: solid 1px #CCC;
        
        padding-left: 10px;
        padding-right: 20px;
        background-color: #EEE;
        cursor: pointer;
    }
</style>
<head>
    <meta charset="UTF-8">
</head>    
    
<div class="bosch_header">
    <?
    $totalSeconds = $activeInstance->getCurrentUser()->secondsRemaining;
    $hours = $totalSeconds / 60 / 60;
    $hours = floor($hours);
    $minutes = ($totalSeconds / 60) - ($hours*60);
    $minutes = floor($minutes);
    $text = $hours > 0 ? "$hours timer og $minutes minutter" : "$minutes minutter";
    ?>
    <span>Din tidsperiode utløper om <? echo $text; ?></span>
    <a href='/bosch/boschlogout.php'>
        <div class="logout">
            Logg ut 
        </div>
    </a>
</div>

<script>
    var pinger = function() {
        $.get( "/bosch/ping.php", function( data ) {
            var hours = parseInt(data) / 3600;
            var minutes = parseInt(data) / 60;
             
            hours = Math.floor(hours);
            minutes = Math.floor(minutes) - (hours*60);
            
            if (hours > 0) {
                $('.bosch_header span').html('Din tidsperiode utløper om '+hours+' timer og '+minutes+' minutter ');
            } else {
                $('.bosch_header span').html('Din tidsperiode utløper '+minutes+' minutter ');
            }
            
            if (hours <= 0 && minutes <= 0) {
                alert('Din tidsperiode har utløpt');
                document.location = '/bosch/boschlogout.php';
            }
        });
        
        setTimeout(pinger, 5000);
    };
    
    pinger();
</script>
    