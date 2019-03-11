<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
if(isset($_GET['iaccept']) && $_GET['iaccept'] == "true") {
    $factory->getApi()->getStoreManager()->acceptGDPR();
}
$store = $factory->getStore();


?>

<head>
    <meta name="viewport" content="width=device-width, user-scalable=no">
</head>
<style>
    body { background-color:#3a99d7; }
    input { padding: 3px; width: 80%; font-size: 16px; }
</style>
<style type="text/css">
    .button {
        -moz-box-shadow:inset 0px 1px 0px 0px #97c4fe;
        -webkit-box-shadow:inset 0px 1px 0px 0px #97c4fe;
        box-shadow:inset 0px 1px 0px 0px #97c4fe;
        background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #3d94f6), color-stop(1, #1e62d0) );
        background:-moz-linear-gradient( center top, #3d94f6 5%, #1e62d0 100% );
        filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#3d94f6', endColorstr='#1e62d0');
        background-color:#3d94f6;
        -webkit-border-top-left-radius:2px;
        -moz-border-radius-topleft:2px;
        border-top-left-radius:2px;
        -webkit-border-top-right-radius:2px;
        -moz-border-radius-topright:2px;
        border-top-right-radius:2px;
        -webkit-border-bottom-right-radius:2px;
        -moz-border-radius-bottomright:2px;
        border-bottom-right-radius:2px;
        -webkit-border-bottom-left-radius:2px;
        -moz-border-radius-bottomleft:2px;
        border-bottom-left-radius:2px;
        text-indent:0;
        display:inline-block;
        color:#ffffff;
        font-family:Arial;
        font-size:16px;
        font-weight:bold;
        font-style:normal;
        height:35px;
        line-height:35px;
        width:82%;
        text-decoration:none;
        text-align:center;
        text-shadow:1px 1px 0px #1570cd;
    }
    .button:hover {
        background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #1e62d0), color-stop(1, #3d94f6) );
        background:-moz-linear-gradient( center top, #1e62d0 5%, #3d94f6 100% );
        filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#1e62d0', endColorstr='#3d94f6');
        background-color:#1e62d0;
    }.button:active {
        position:relative;
        top:1px;
    }</style>

<div style="text-align: center;padding:10px;">
    <img src="https://www.getshop.com/displayImage.php?id=78c64104-ffe0-45d0-a554-87573d34ae7f&height=100&width=100"></img>
    <?php
        if($store->acceptedByUser) {
            echo "<br><br><div style='text-align: center; font-size: 30px;'>Thank you for accepting so we can continue to serve you.</div>";
            if($factory->getApi()->getUserManager()->getLoggedOnUser()->type == 100) {
                $usr = $factory->getApi()->getUserManager()->getUserById($store->acceptedByUser);
                echo "<div>Where accepted at : " . date("d.m.Y H:i", strtotime($store->acceptedGDPRDate)) . " by " . $usr->fullName . "</div>";
            }
        }

    ?>
    <form action="" method="POST" id="createstore">
        <bR><bR>
        <div style="max-width: 800px; width:100%; display:inline-block; background-color:#FFF; padding: 20px; box-shadow: 0px 0px 2px #000; border-radius: 5px; text-align:left;">
            
            <?php
            $user = $factory->getApi()->getUserManager()->getLoggedOnUser();
            ?>
                <div style='text-align:center;'>
                    <h1>GDPR</h1>
                </div>
                    <?php
                    if(!$user || $user->type < 10) {
                        echo "To accept the GDPR you have to be logged on to the system as an administration.<br>";
                        echo "Please log on by clicking <a href='/login.php'>here</a> first.";
                    } else {
                        ?>
                        <div>
                            The general data protection regulation will be enforced at 25. may. 2018 all over the EU.<br>
                            For more information about the GDPR have a look <a href='https://www.eugdpr.org/' target='_new'>here</a>.<br>
                            <br>
                            Related to this GetShop is obligated to inform their customers about GetShop's part in the GDPR directive.<br>
                            The GetShop platform need to harvest data for customers all over the world for you and we need your consensus for doing so.<br>
                            <br>
                            The following personal information will be stored at GetShop servers as a service for you<br>
                            <ul>
                                <li>Full name</li>
                                <li>Address</li>
                                <li>Phone number</li>
                                <li>Email</li>
                                <li>Booking related data; stay periode, room category and room number.</li>
                            </ul>
                            As a processor we are also obligated to inform you about your rights related to this subject:<br>
                            <ul>
                                <li>GetShop will not process any data not related to what we have informed about above.</li>
                                <li>GetShop will not transfer its data to a third country or an international organisation outside of Europe.</li>
                                <li>If nessesary to transfer data to third parties outside of the EU GetShop will inform you about this breach before processing the data.</li>
                                <li>GetShop will commit itself to process the data correctly and take every steps nessesary to make sure the data is kept safe for the public interest.</li>
                                <li>GetShop will take all steps nessesary to make sure unauthorized persons do not gain access to the data.</li>
                                <li>GetShop will takes all measures required pursuant to Article 32 of the GDPR.</li>
                                <li>GetShop will respects the conditions referred to in paragraphs 2 and 4 in the GDPR for engaging another processor.</li>
                                <li>GetShop will provide all tools nessesary for you to be able to forfill its commitment to the GDPR.</li>
<!--                                <li>GetShop will assist you to make sure you are compliant with articles 32 to 36 in the GDPR.</li>-->
                                <li>If you choose to leave GetShop we will encrypt or delete all data related your hotel / website.</li>
                                <li>GetShop will provide all your data by request.</li>
                            </ul> 
                            
                        </div>
                        <br>
                        <?php if(!$store->acceptedByUser) { ?>
                            <div style='text-align: center;'>
                                <a href='?iaccept=true'>
                                    <span style='border: solid 1px #bbb;padding: 10px; cursor:pointer;background-color:purple; color:#fff;'>On behalf of my business I accept that GetShop can handle this data information for me.</span>
                                </a>
                            </div>
                    <? } ?>
                <? } ?>
        </div>
</div>