<?php

/**
 * Description of TheemeDisplayer
 *
 * @author boggi
 */
class TheemeDisplayer {

    var $theemes = array();

    function __construct() {
        
    }

    function printDesigns($clickable = true) {
        $width = 900;
        if($clickable) {
            $width = 800;
            $application = new \ns_00d8f5ce_ed17_4098_8925_5697f6159f66\LeftMenu();
            ?>
            <span style="position:absolute; right: 10px;" class="button manualcolor">
                <div class="rightglare"></div>
                <div class="filler"></div>
                <ins gstype="clicksubmit" method="displayColorPickers" gsname="id" informationtitle="Select your colors" gsvalue="somevalue" output="informationbox"><? echo $application->__f("Manually select a color"); ?></ins>
            </span>
            <?php
        }

        echo "<br>";
        $class = "theeme_selection";
        if (!$clickable) {
            $class = "";
        }

        $designs["071738:FFFFFF:071738:FFFFFF:FFFFF"] = "Future";
        $designs["000000:FFFFFF:000000:FFFFFF:FFFFF"] = "Black";
        $designs["D4A017:000000:D4A017:000000:FFFFF"] = "Gold";
        $designs["c44032:FFFFFF:c44032:FFFFFF:FFFFF"] = "Red";
        $designs["1a2579:eaeaea:1a2579:eaeaea:FFFFF"] = "Blue";
        $designs["b50daf:FFFFFF:b50daf:FFFFFF:FFFFF"] = "Pink";
        $designs["5da9d0:000000:5da9d0:000000:FFFFF"] = "Light-Blue";
        $designs["cdad8a:000000:cdad8a:000000:FFFFF"] = "Brown";
        $designs["e0d51f:000000:e0d51f:000000:FFFFF"] = "Yellow";
        $designs["008f00:000000:008f00:000000:FFFFF"] = "Green";
        $designs["cf880c:000000:cf880c:000000:FFFFF"] = "Orange";
        $designs["757c82:000000:757c82:000000:FFFFF"] = "Gray";

        echo "<table width='100%'>";
        echo "<tr>";
        $i = 0;
        foreach ($designs as $colors => $design) {
            echo "<td align='center'>";
            echo "<div style=''>" . $design;
            echo "</div>";

            if ($clickable) {
                echo "<a href='?setTheeme=slick&colors=$colors'>";
            }
            echo "<img style='cursor:pointer;' colors='$colors' src='/designimages/slick/" . strtolower($design) . ".png'><br><br></td>";
            if ($clickable) {
                echo "</a>";
            }
            $i++;
            if ($i % 2 == 0) {
                echo "</tr><tr>";
            }
        }
        echo "</tr>";
        echo "</table>";
    }

}
?>
