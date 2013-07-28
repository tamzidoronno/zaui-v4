<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

if (isset($_GET['setGroup'])) {
    $_SESSION['group'] = $_GET['setGroup'];
    header('location:booking.php');
}

function getLogo(   ) {
    $api = IocContainer::getFactorySingelton()->getApi();
    return $api->getLogoManager()->getLogo()->LogoId;
}

function getEntries(Factory $factory) {
    $entries = $factory->getApi()->getCalendarManager()->getEntries((int)date('Y'), (int)date('m'), (int)date('d'));
    
    $jsonarray = array();
    foreach ($entries as $entry) {
        /* @var $entry core_calendarmanager_data_Entry */
        $json = array();
        $json["description"] = $entry->description;
        $json["starttime"] = $entry->starttime;
        $json["location"] = $entry->location;
        $json["stoptime"] = $entry->stoptime;
        $json["entryId"] = $entry->entryId;
        $json["maxAttendees"] = $entry->maxAttendees;
        $json["attendees"] = $entry->attendees;
        $json["userId"] = $entry->userId;
        $json["title"] = $entry->title;
        $json["day"] = $entry->day;
        $json["month"] = $entry->month;
        $json["year"] = $entry->year;
        $jsonarray[] = $json;
    }
    return json_encode($jsonarray);
}

if (isset($_GET['register']) && $_GET['register'] == true) {
    $apps = $factory->getApi()->getPageManager()->getApplicationsBasedOnApplicationSettingsId("74ea4e90-2d5a-4290-af0c-230a66e09c78");
    $application = $factory->getApplicationPool()->createAppInstance($apps[0]);
    $application->registerEvent($_POST);
    return;
}

$logo = getLogo($factory);
?>

<script type="text/javascript">
Config = {
    logoid: '<?php echo $logo; ?>',
    entries: <?php echo getEntries($factory); ?>
}
</script>

<style>
    .selectGroup {
        position: absolute;
        top: 0px;
        left: 0px;
        right: 0px;
        bottom: 0px;
        width: 100%;
        background-color: #000;
        z-index: 2;
        background: rgba(0, 0, 0, 0.5);
    }
    
    .selectGroup .inner {
        margin: 10 auto;
        text-align: center;
        padding-top: 10px;
    }

    
    .selectGroup .inner .selectbox {
        width: 90%;
        heigth: 80px;
        padding: 10px;
        cursor: pointer;
        border: solid 5px;
        border-radius: 10px;
        background: #FFF;
        opacity: inherit;
        margin: 10px auto;
        background: rgba(255, 255, 255, 1);
    }
</style>

<!DOCTYPE html> 
<html> 
    <head> 
        <meta charset="utf-8"> 
        <title><?php echo $factory->getStore()->configuration->shopName; ?></title> 
        <link rel="apple-touch-icon" href="/displayImage.php?id=<?php echo $logo; ?>&width=114&height=114"/>
        <script src="js/touch/sencha-touch-all.js" type="text/javascript"></script> 
        <link href="js/touch/resources/css/sencha-touch.css" rel="stylesheet" type="text/css" /> 
        <link href="js/touch/resources/css/addon.css" rel="stylesheet" type="text/css" /> 
        <script src="js/app/touch/booking/app.js" type="text/javascript"></script>
    </head> 
    <body></body> 
    <?
    $groups = $factory->getApi()->getUserManager()->getAllGroups();
    if (count($groups) > 0 && !isset($_SESSION['group'])) { ?>
        <div class='selectGroup'>
            <div class="inner">
                <?
                foreach ($groups as $group) {
                    $img = "";
                    if ($group->imageId != "") {
                        $img = '<img  border="none" width="150" src="displayImage.php?id='.$group->imageId.'"/>';
                    } else {
                        $img = $group->groupName;
                    }
                    $groupid = $group->id;
                    echo '<div class="selectbox"><a href="?setGroup='.$groupid.'"><div>'.$img.'</div></a></div>';
                }
                ?>
            </div>
        </div>
    <?
    }
    ?>
</html>