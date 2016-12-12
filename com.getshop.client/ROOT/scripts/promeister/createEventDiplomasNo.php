<?php
session_id($_GET['id']);
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$eventId = $_GET['eventId'];
$manager = $factory->getApi()->getEventBookingManager();
$event = $manager->getEvent("booking", $eventId);

$users = [];
if (isset($_GET['userId'])) { 
    $userToUse = $factory->getApi()->getUserManager()->getUserWithPermissionCheck($_GET['userId']);
    $users[] = $userToUse;
} else {
    $usersUnfiltered = $manager->getUsersForEvent("booking", $eventId);
    foreach ($usersUnfiltered as $user) {
        $participatedStatus = @$event->participationStatus->{$user->id};

        if ($participatedStatus !== "not_participated" && $participatedStatus !== "participated_50") {
            $users[] = $user;
        }
    }
}


$eventHelder = $factory->getApi()->getUserManager()->getUserById($event->eventHelderUserId);
$eventHelderName = "N/A";
$eventHelderId = "";

if ($eventHelder) {
    $eventHelderId = $eventHelder->id;
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
        padding-top: 2px;
        font-size: 22px;
        text-transform: uppercase;
    }
    
    .signature {
        height: 100px;
        margin-top: 0px;
        
    }
    .date {
        padding-top: 60px;
    }
    
    .descriptiontitle {
        text-align: center;
        margin-top: 50px;
        font-size: 30px;
    }
    
    .description {
        text-align: center;
        height: 100px;
        font-size: 18px;
        padding-left: 50px;
        padding-right: 50px;
        
    }
</style>

<link href='https://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>

<body >
    <?
    foreach ($users as $user) {
        $certificate = $factory->getApi()->getEventBookingManager()->getCertificateForEvent("booking", $eventId, $user->id);
        $signatureImageId = "";
        if ($certificate && $certificate->signatures  && isset($certificate->signatures->{$eventHelderId})) {
            $signatureImageId = $certificate->signatures->{$eventHelderId};
        }
    ?>
        <div class="page">
            <div class="row title1"> <? echo $event->bookingItemType->name; ?></div>    

            <div class="row candidatename"> <? echo $user->fullName; ?></div>
            <div class="row companyname"><? echo @$user->companyObject->name; ?></div>
            
            <div class="descriptiontitle">Kursinnhold</div>
            <div class="row description">
                <? echo $event->bookingItemType->description; ?>
            </div>
            
            <div class="row date"><? echo $event->location->name; ?> den <? echo date("d. M Y", strtotime($event->mainStartDate)); ?></div>
            
            <div class="row signature">
                <?
                if ($signatureImageId) {
                ?>
                    <img width='300' src='/displayImage.php?id=<? echo $signatureImageId; ?>'/>
                <?
                }
                ?>
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

    
