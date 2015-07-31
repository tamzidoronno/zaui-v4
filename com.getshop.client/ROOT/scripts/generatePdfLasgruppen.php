<?php
if (isset($_GET['id'])) {
    session_id($_GET['id']);
}

session_start();
$data = json_decode($_SESSION['lasgruppen_pdf_data'], true);
$data = $data['data'];

$userLoggedIn = $data['userLoggedIn'];
$hidePinCode = $data['hidePinCode'];


if ($userLoggedIn) {
    $data['page1']['contact']['systemnumber'] = "";
    $data['page1']['contact']['name'] = "";
    $data['page1']['contact']['email'] = "";
    $data['page1']['contact']['cellphone'] = "";
    $data['page1']['invoice']['customerNumber'] = "";
    $data['page1']['invoice']['customerNumber'] = "";
}
?>

<head>
    <link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
    <meta charset="UTF-8">
    <style>
        body,
        html {
/*            font-family: 'Open Sans', sans-serif;*/
            margin: 0px;
            padding:  0px;
        }
        
        @page {
            color:#000;
        }
        
        .page {
            position: relative;
            padding-left: 0px;
            padding-right: 0px;
            padding-top: 0px;
            color:#000;
            height:1487px;
            width: 1060px;
            padding-left: 60px;
            padding-right: 60px;
            box-sizing: border-box;
            padding-top: 20px;
        }
        
        .logo {
            background-image: url('/showApplicationImages.php?appNamespace=ns_7004f275_a10f_4857_8255_843c2c7fb3ab&image=skin/images/logo_whiteback.png'); 
            width: 260px;
            height: 100px;
 
            background-position: center;
            background-repeat: no-repeat;
        }
        
        .header_text1 {
            position: absolute;
            right: 80px;
            top: 40px;
            width: 450px;
            font-size: 50px;
            font-weight: bold;
            text-align: right;
        }
        
        .row {
            margin-top: 10px;
            font-size: 0px;
        }
        
        .row_1 {
            margin-top: 20px;
            overflow: hidden;
        }

        .row_2 {
            overflow: hidden;
        }
        .row_3 {
            margin-top: 20px;
            overflow: hidden;
        }
        
        .row_4 {
            font-size: 22px;
        }
        
        .row_5 {
            color: #888;
            font-size: 16px;
            position: absolute;
            bottom: 10px;
        }
        
        
        .col {
            width: 50%;
            font-size: 22px;
            display: inline-block;
            box-sizing: border-box;
            vertical-align: top;
        }
        
        th,
        td {
            font-size: 19px;
            line-height: 20px;
        }
        
    </style>
</head>
<div class="page">
    <div class="logo"></div>
    <div class="header_text1">
        <?
        if ($userLoggedIn) {
            echo "Innlogget bestilling";
        } else {
            echo "Bestilling";
        }
        ?>
    </div>
    
    
    <?
    if ($userLoggedIn) {
    ?>
        <div class="row row_1">
            <div class="col">
                <b>Informasjon om bestiller</b>
                <div style="padding-left: 20px; ">
                    <?
                    echo $data['groupName'];
                    echo "<br/>".$data['fullName'];
                    echo "<br/>".$data['groupReference'];
                    ?>
                </div>
            </div>
        </div>
    <?
    }
    ?>
    <div class="row row_1">
        <div class="col">
            <b>Fakturainformasjon</b>
            <div style="padding-left: 20px; padding-top: 10px;">
                <? echo $data['page1']['invoice']['vatnumber']; ?>
                <? echo $data['page1']['invoice']['companyName'] ? "<br/>".$data['page1']['invoice']['companyName'] : ""; ?>
                <? echo $data['page1']['invoice']['address'] ? "<br/>".$data['page1']['invoice']['address'] : ""; ?>
                <? echo $data['page1']['invoice']['address2'] ? "<br/>".$data['page1']['invoice']['address2'] : ""; ?>
                <? echo $data['page1']['invoice']['postnumer'] ? "<br/>".$data['page1']['invoice']['postnumer'] : ""; ?>
                <? echo $data['page1']['invoice']['customerNumber'] ? "<br/>".$data['page1']['invoice']['customerNumber'] : ""; ?>
                <? echo $data['page1']['invoice']['city'] ? " ".$data['page1']['invoice']['city'] : ""; ?>
                <? echo $data['page1']['invoice']['reference'] ? "<br/> Referanse: ".$data['page1']['invoice']['reference'] : "" ; ?>
                <? echo $data['page1']['invoice']['email'] ? "<br/>Epost: ".$data['page1']['invoice']['email'] : ""; ?>
                <? echo $data['page1']['invoice']['cellphone'] ? "<br/>Telefon: ".$data['page1']['invoice']['cellphone'] : ""; ?>
            </div>
        </div>
        <div class="col">
            <b>Leveringsinformasjon</b>
            <div style="padding-left: 20px; padding-top: 10px;">
                <?
                if ($data['page3']['shipping'] == "Hentes i butikk") {
                    echo "Hentes i butikk: ". $data['page3']['storeDeliveryInformation']['store'];
                    echo "<br>Hentes av: ".$data['page3']['storeDeliveryInformation']['name'];
                    echo "<br>Molbilnr: ".$data['page3']['storeDeliveryInformation']['cellphone'];
                } else {
                    echo $data['page3']['deliveryInfo']['name'] ? $data['page3']['deliveryInfo']['name'] : "";
                    echo $data['page3']['deliveryInfo']['address'] ? "<br>".$data['page3']['deliveryInfo']['address'] : "";
                    echo $data['page3']['deliveryInfo']['address2'] ? "<br>".$data['page3']['deliveryInfo']['address2'] : "";
                    echo $data['page3']['deliveryInfo']['postnumber'] ? "<br>".$data['page3']['deliveryInfo']['postnumber'] : "";
                    echo $data['page3']['deliveryInfo']['city'] ? " ".$data['page3']['deliveryInfo']['city'] : "";
                    echo $data['page3']['deliveryInfo']['emailaddress'] ? "<br>Epost: ".$data['page3']['deliveryInfo']['emailaddress'] : "";
                    echo $data['page3']['deliveryInfo']['cellphone'] ? "<br> Telefon: ".$data['page3']['deliveryInfo']['cellphone'] : "";
                    echo $data['page3']['shipping'] ? "<br>Leveringsmåte: ".$data['page3']['shipping'] : "";
                }
                
                echo "<br/><br/><b>Dato: </b>". date('d/m-Y H:i');
                ?>
            </div>
        </div>
    </div>
    
    <div class="row row_2">
        <div class="col">
            <?
            if (!$userLoggedIn) {
            ?>
                <b>Informasjon om rekvirent</b>
                <div style="padding-left: 20px; padding-top: 10px;">
                    <?
                    echo $data['page1']['contact']['systemnumber'] ? "System: ".strtoupper($data['page1']['contact']['systemnumber']) : "";
                    echo $data['page1']['contact']['name'] ? "<br> Navn: ".$data['page1']['contact']['name'] : "";
                    echo $data['page1']['contact']['email'] ? "<br> E-Post: ".$data['page1']['contact']['email'] : "";
                    echo $data['page1']['contact']['cellphone'] ? "<br> Telefon: ".$data['page1']['contact']['cellphone'] : "";
                    ?>
                </div>
            <?
            }
            ?>
            
       </div>
        <?
        if ($data['page3']['extrainformation']) {
        ?>
        <div class="col">
            <b>Ekstra informasjon om levering</b>
            <div style="padding-left: 20px; padding-top: 10px;">
                <? echo nl2br($data['page3']['extrainformation']); ?>
            </div>
        </div>
        <?
        }
        ?>
    </div>
    
    <div class="row row_3">
        <div class="col">
            <b>Informasjon til systemavdelingen</b>
            <div style="padding-left: 20px; padding-top: 10px;">
                <? echo nl2br($data['page4']['orderExtraInfo']); ?>
            </div>
        </div>
        <div class="col">
            <? 
            if (!$userLoggedIn) {
            ?>
            <b>Bestillingsmetode</b>
            <div style="padding-left: 20px; padding-top: 10px;">
                <?
                if (isset($data['page4']['securitytype']) && $data['page4']['securitytype'] == "signature") {
                    echo "Signatur";

                    echo "<div style='height: 60px; border-bottom: solid 1px #00aad0;'></div>";
                    echo "<center><span style='color: #888; font-size: 15px;'>Signatur av bemyndiget person</span></center>";
                }

                if (isset($data['page4']['securitytype']) && $data['page4']['securitytype'] == "pincode") {
                    echo "Pinkode: ".$hidePinCode ? "******" : $data['page4']['pincode'];
                }

                if (!isset($data['page4']['securitytype'])) {
                    echo "Uten sikkerhet (kun sylinderer)";
                }
                ?>
            </div>
            <?
            }
            ?>
        </div>
    </div>
    
    <div class="row row_4">
        <div style='font-size: 18px; color: #666; border-bottom: solid 1px #00aad0; padding-bottom: 10px; margin-top: 20px;'>
        <?
        if (!$userLoggedIn) {
        ?>
        Skjema sendes til CERTEGO AS, Postboks 454 Brakerøya, 3002 Drammen eller skannes og sendes til <span style='text-decoration: underline;font-weight: bold; color: #ff6e00;'>system@certego.no</span>
        <? } ?>
        
        </div>
        <div>
            <?
            if (isset($data['page2']['keys']) && $data['page2']['keys'] == "true") {
                echo "<div style='margin-top: 5px; '><b>Nøkler</b></div>";
                echo "<table>";
                echo "<th style='width: 200px; text-align: left;' >Systemnummer</th><th style='width: 100px; text-align: center;'>Antall</th><th style='width: 100px; text-align: center;'>Merking</th>";
                foreach ($data['page2']['keys_setup'] as $keysetup) {
                    echo "<tr><td>".$keysetup['systemNumber']."</td><td style='text-align: center;'>".$keysetup['count']."</td><td style='text-align: center;'>".$keysetup['marking']."</td></tr>";
                }
                echo "</table>";
            }
            
            if (isset($data['page2']['cylinders']) && $data['page2']['cylinders'] == "true") {
                echo "<div style='margin-top: 15px;'><b>Sylindrer</b></div>";
                echo "<table width='100%'><tr>";
                echo "<td style='font-weight: bold; width: 90px; text-align: left; vertical-align: top; '>Systemnr.</td>";
                echo "<td style='font-weight: bold; width: 75px; text-align: center; vertical-align: top; ' >Antall</td>";
                echo "<td style='font-weight: bold; width: 155px; text-align: left; vertical-align: top; '>Type</td>";
                echo "<td style='font-weight: bold; width: 125px; vertical-align: top; '>Dørtykkelse</td>";
                echo "<td style='font-weight: bold; width: 175px; text-align: left; vertical-align: top; '>Merking</td>";
                echo "<td style='font-weight: bold; width: 180px; text-align: left; vertical-align: top; '>Overflate</td>";
                echo "<td style='font-weight: bold; '>Beskrivelse</td>";
                echo "</tr></table>";
                
                
                foreach ($data['page2']['cylinder_setup'] as $cylindersetup) {
                    echo "<div >";
                        echo "<div style='display: inline-block; vertical-align: top; width: 110px; text-align: left;'> ".$cylindersetup['systemNumber']."</div>";
                        echo "<div style='display: inline-block; vertical-align: top; width: 70px; text-align: left;'>".$cylindersetup['count']."</div>";
                        echo "<div style='display: inline-block; vertical-align: top; width: 155px; text-align: left;'>".$cylindersetup['cylinder_type']."</div>";
                        echo "<div style='display: inline-block; vertical-align: top; width: 130px;'>".$cylindersetup['door_thickness']."</div>";
                        echo "<div style='display: inline-block; vertical-align: top; width: 180px; text-align: left;'> ".$cylindersetup['keys_that_fits']."</div>";
                        echo "<div style='display: inline-block; vertical-align: top; width: 180px; text-align: left;'>".$cylindersetup['texture']."</div>";
                        echo "<div style='display: inline-block; width: 100px;'>".$cylindersetup['cylinder_description']."</td>";
                    echo "</div>";
                }
                
            }
            ?>
        </div>
    </div>
    
    <div class="row row_5">
        Normal leveringstid er 5 – 7 virkedager
        <br/>Låsgruppen forbeholder seg retten til å forespørre og innhente informasjon om fakturamottaker.
        <br/>Systemkomponenter som er produsert, kan ikke returneres eller avbestilles
        <br/>Krav om betaling vil bli rettet til systemeier dersom varer ikke blir hentet eller betalt av mottager.
        <br/>
        <br/>Hvis spørsmål om bestillingen, ta kontakt med system@certego.no
        <br/>
        <br/>CERTEGO AS, Postboks 454 Brakerøya, 3002 Drammen
    </div>
</div>