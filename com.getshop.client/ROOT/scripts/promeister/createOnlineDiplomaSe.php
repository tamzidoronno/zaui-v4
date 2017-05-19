<?php

session_id($_GET['id']);
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$scorm = $factory->getApi()->getScormManager()->getScormForCurrentUser($_GET['packageId'], $_GET['userid']);
$scormCertificateContent = $factory->getApi()->getScormManager()->getScormCertificateContent($scorm->scormId);
$package = $factory->getApi()->getScormManager()->getPackage($scorm->scormId);
$user = $factory->getApi()->getUserManager()->getUserById($_GET['userid']);
?>


<style>
    
    body { 
        margin: 0px; padding: 0px; font-family: arial;
        font-family: 'Open Sans', sans-serif;
    }
    
    .page {
        width: 1049px; 
        height: 1484px; 
        box-sizing: border-box; 
        font-size: 20px; 
        background-size: 1050px 1485px; 
        background-image: url("/displayImage.php?id=babf491e-8d4e-4875-adb2-51ee1197de11&width=1160");
        position: relative;
    }
    
   .title1 { 
        padding-top: 550px; 
        font-size: 20px; 
        font-size: 40px; 
        text-align: center;
    }
    .username {
        text-align: center;
        font-size: 35px; 
        padding-bottom: 0px;
        text-transform: uppercase;
        padding-top: 50px;
    }
    .companyname {
        text-align: center;
        font-size: 35px; 
        text-transform: uppercase;
    }
    .extrainfo {
        padding-top: 50px;
        text-align: center;
    }
    .scormPackageName {
        text-align: center;
        font-size: 50px;
        padding-top: 50px;
        padding-bottom: 50px;
    }
    .certifcateconent {
        padding-top: 20px;
        text-align: center;
    }
    
    .footer {
        position: absolute;
        bottom: 50px;
        border-bottom: solid 5px #000;
        width: 979px;
        margin-left: 40px;
    }
    
    .butterfly {
        position: absolute;
        right: 20px;
    }
    .footer .date {
        float: right;
        padding-right: 10px;
    }
</style>

<link href='https://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>

<body >
    
        <div class="page">
            <div class="title1">Kursintyg <?  echo $package->name; ?></div>    
            <div class='username'>
                <? echo $user->fullName; ?>
            </div>
            <div class='companyname'>
                <? echo $user->companyObject ? $user->companyObject->name : ""; ?>
            </div>
            <div class='certifcateconent'>
                <? 
                echo nl2br($scormCertificateContent->content);
                ?>
            </div>
            <div class='footer'>
                Genomförd utbildning
                <div class='date'>
                    Datum: <? echo date('Y-m-d', strtotime($scorm->passedDate)); ?>
                </div>
            </div>
        </div>    
   
</body>