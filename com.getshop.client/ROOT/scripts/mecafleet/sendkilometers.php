<?php

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();


?>

<html>
    <head>
        <link rel="stylesheet" href="mecafleet.css"/>
        <meta name="viewport" content="width=device-width, minimal-ui, initial-scale=1.0, maximum-scale=1.0, user-scalable=no", target-densitydpi="device-dpi" />
    </head>
    <body>
        <div class="gs_header">
            <img src="images/mecalogo.png" style="width: 200px; padding-top: 33px;"/>
        </div>
        
        
        <div class="mainview">
            
            <?
            if (isset($_POST['newkilometers'])) {
                $factory->getApi()->getMecaManager()->sendKilometers($_POST['cellphone'], $_POST['newkilometers']);
            ?>
                <div style="font-size: 25px; text-align: center; margin-top: 30px;">
                    Takk for registeringen.
                </div>
            <?
            } else {
            ?>
                <form id='form' method='post'>
                    <input type='hidden' name='cellphone' value="<? echo $_GET['cellphone']; ?>"/>
                    <div class='topinfobox'>
                        Registrer kilometerstand på ditt kjøretøy her.
                    </div>
                    <br/>
                    <br/>
                    <input type='number' name='newkilometers' />
                    <br/>
                    <br/>
                    <div class='button' onclick='document.getElementById("form").submit();'>Send inn</div>
                </form>
            <?
            }
            ?>
        </div>	
        
    </body>
</html>