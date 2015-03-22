<?php
if (isset($_GET['id'])) {
    session_id($_GET['id']);
}

session_start();
$data = json_decode($_SESSION['lasgruppen_pdf_data'], true);
$data = $data['data'];
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
            height:1485px;
            width: 1050px;
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
            width: 250px;
            font-size: 50px;
            font-weight: bold;
        }
        
        .row {
            font-size: 0px;
        }
        
        .row_1 {
            margin-top: 20px;
            height: 280px;
            overflow: hidden;
        }

        .row_2 {
            height: 160px;
            overflow: hidden;
        }
        .row_3 {
            margin-top: 20px;
            height: 150px;
            overflow: hidden;
        }
        
        .row_4 {
            height: 550px;
            font-size: 22px;
        }
        
        .row_5 {
            color: #888;
            font-size: 16px;
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
            font-size: 20px;
        }
        
    </style>
</head>
<div class="page">
    <div class="logo"></div>
    <div class="header_text1">Bestilling</div>
    
    <div class="row row_1">
        <div class="col">
            <b>Fakturainformasjon</b>
            <div style="padding-left: 20px; padding-top: 10px;">
                <? echo $data['page1']['invoice']['vatnumber']; ?>
                <br/> <? echo $data['page1']['invoice']['companyName']; ?>
                <br/> <? echo $data['page1']['invoice']['address']; ?>
                <br/> <? echo $data['page1']['invoice']['postnumer']; ?>
                <br/> <? echo $data['page1']['invoice']['customerNumber']; ?>
                <br/> <? echo $data['page1']['invoice']['reference']; ?>
                <br/> <? echo $data['page1']['invoice']['email']; ?>
                <br/> <? echo $data['page1']['invoice']['cellphone']; ?>
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
                    echo $data['page3']['deliveryInfo']['name'];
                    echo "<br>".$data['page3']['deliveryInfo']['address'];
                    echo "<br>".$data['page3']['deliveryInfo']['postnumber'];
                    echo "<br>".$data['page3']['deliveryInfo']['emailaddress'];
                    echo "<br>".$data['page3']['deliveryInfo']['cellphone'];
                    echo "<br>".$data['page3']['shipping'];
                }
                ?>
            </div>
        </div>
    </div>
    
    <div class="row row_2">
        <div class="col">
            <b>Fakturainformasjon</b>
            <div style="padding-left: 20px; padding-top: 10px;">
                <?
                echo $data['page1']['contact']['systemnumber'];
                echo "<br>".$data['page1']['contact']['name'];
                echo "<br>".$data['page1']['contact']['email'];
                echo "<br>".$data['page1']['contact']['cellphone'];
                ?>
            </div>
            
       </div>
        <div class="col">
            <b>Ekstra informasjon om levering</b>
            <div style="padding-left: 20px; padding-top: 10px;">
                <? echo nl2br($data['page3']['extrainformation']); ?>
            </div>
        </div>
    </div>
    
    <div class="row row_3">
        <div class="col">
            <b>Informasjon til systemavdelingen</b>
            <div style="padding-left: 20px; padding-top: 10px;">
                <? echo nl2br($data['page4']['orderExtraInfo']); ?>
            </div>
        </div>
        <div class="col">
            <b>Bestillingsmetode</b>
            <div style="padding-left: 20px; padding-top: 10px;">
                <?
                if (isset($data['page4']['securitytype']) && $data['page4']['securitytype'] == "signature") {
                    echo "Signatur";
                    
                    echo "<div style='height: 60px; border-bottom: solid 1px #00aad0;'></div>";
                    echo "<center><span style='color: #888; font-size: 15px;'>Signatur av bemyndiget person</span></center>";
                }
                
                if (isset($data['page4']['securitytype']) && $data['page4']['securitytype'] == "pincode") {
                    echo "Pinkode: ".$data['page4']['pincode'];
                }
                
                if (!isset($data['page4']['securitytype'])) {
                    echo "Uten sikkerhet (kun sylinderer)";
                }
                ?>
            </div>
        </div>
    </div>
    
    <div class="row row_4">
        <div style='font-size: 18px; color: #666; border-bottom: solid 1px #00aad0; padding-bottom: 10px; margin-top: 20px;'>Skjema sendes til CERTEGO AS, Postboks 454 Brakerøya, 3002 Drammen eller skannes og sendes til <span style='text-decoration: underline;font-weight: bold; color: #ff6e00;'>system@certego.no</span></div>
        <div>
            <?
            if (isset($data['page2']['keys']) && $data['page2']['keys'] == "true") {
                echo "<br/>";
                echo "<b>Nøkler</b>";
                echo "<table>";
                echo "<th style='width: 200px; text-align: left;' >Systemnummer</th><th style='width: 100px; text-align: center;'>Antall</th><th style='width: 100px; text-align: center;'>Merking</th>";
                foreach ($data['page2']['keys_setup'] as $keysetup) {
                    echo "<tr><td>".$keysetup['systemNumber']."</td><td style='text-align: center;'>".$keysetup['count']."</td><td style='text-align: center;'>".$keysetup['marking']."</td></tr>";
                }
                echo "</table>";
            }
            
            if (isset($data['page2']['cylinders']) && $data['page2']['cylinders'] == "true") {
                echo "<br/>";
                echo "<b>Sylindrer</b>";
                echo "<table width='100%'>";
                echo "<th style='width: 60px; text-align: left;'>Systemnr.</th>";
                echo "<th style='width: 80px; text-align: center;' >Antall</th>";
                echo "<th style='width: 140px; text-align: left;'>Type</th>";
                echo "<th style='width: 140px;'>Dørtykkelse</th>";
                echo "<th style='width: 100px; text-align: left;'>Merking</th>";
                echo "<th style='width: 150px; text-align: left;'>Overflate</th>";
                echo "<th>Beskrivelse</th>";
                echo "</table>";
                
                
                foreach ($data['page2']['cylinder_setup'] as $cylindersetup) {
                    echo "<div style='height: 30px; overflow: hidden;'><table width='100%'>";
                    echo "<tr>";
                        echo "<td style='width: 100px; text-align: left;'> ".$cylindersetup['systemNumber']."</td>";
                        echo "<td style='width: 70px; text-align: left;'>".$cylindersetup['count']."</td>";
                        echo "<td style='width: 155px; text-align: left;'>".$cylindersetup['cylinder_type']."</td>";
                        echo "<td style='width: 120px;'>".$cylindersetup['door_thickness']."</td>";
                        echo "<td style='width: 100px; text-align: left;'> ".$cylindersetup['keys_that_fits']."</td>";
                        echo "<td style='width: 205px; text-align: left;'>".$cylindersetup['texture']."</td>";
                        echo "<td>".$cylindersetup['cylinder_description']."</td>";
                    echo "</tr>";
                    echo "</table></div>";
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