<?php
session_id($_GET['id']);
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$eventId = $_GET['eventId'];
$manager = $factory->getApi()->getEventBookingManager();
$event = $manager->getEvent("booking", $eventId);
$users = $manager->getUsersForEvent("booking", $eventId);
$eventHelder = $factory->getApi()->getUserManager()->getUserById($event->eventHelderUserId);
$eventHelderName = "N/A";

if ($eventHelder) {
    $eventHelderName = $eventHelder->fullName;
}

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
        background-image: url("/displayImage.php?id=babf491e-8d4e-4875-adb2-51ee1197de11&width=1160")
    }
    
    .row {
        width: 1049px; 
        text-align: center; 
        padding-left: 100px; 
        padding-right: 100px; 
        box-sizing: border-box;
    }
    
    .title1 { 
        padding-top: 550px; 
        font-size: 20px; 
        font-size: 40px; 
        font-weight: bold;
    }
    
    .candidatename {
        padding-top: 50px;
        font-size: 30px;
        text-transform: uppercase;
    }
    
    .companyname {
        padding-top: 5px;
        font-size: 30px;
        text-transform: uppercase;
    }
    
    .signature {
        height: 130px;
        margin-top: 100px;
    }
    .date {
        padding-top: 60px;
    }
</style>

<link href='https://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>

<body >
    <?
    foreach ($users as $user) {
    ?>
        <div class="page">
            <div class="row title1"> Kursintyg <? echo $event->bookingItemType->name; ?></div>    

            <div class="row candidatename"> <? echo $user->fullName; ?></div>
            <div class="row companyname"><? echo @$user->companyObject->name; ?></div>
            
            <div class="row date"><? echo $event->location->name." ".$event->subLocation->name; ?> den <? echo date("d. M Y", strtotime($event->mainStartDate)); ?></div>
            
            <div class="row signature">
                
            </div>
                
            <div class="row eventHeloder">
                <? echo $eventHelderName; ?>
                <br/> ProMeister Academy
            </div>
            
            
        </div>    
    <?
    }
    ?>
</body>

    
