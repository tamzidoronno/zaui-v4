<?php
header("Pragma: no-cache");
header('Expires: 0');
header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
header('Content-Description: File Transfer');
header('Content-Type: text/csv');
header("Content-Disposition: attachment; filename=file.csv");
header('Content-Transfer-Encoding: binary'); 

echo chr(0xEF) . chr(0xBB) . chr(0xBF);

$start = urldecode($_GET['from']);
$end = urldecode($_GET['to']);

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton(true, false);

$results = $factory->getApi()->getCalendarManager()->getDetailedStatisticGroupedByLocations($start, $end);

$grouped = [];
foreach ($results as $result) {
    $grouped[$result->locationId][] = $result;
}

$groups = [];
$firstEntry = array_values($grouped)[0];
foreach ($firstEntry as $groupedEntry) {
    $groups[] = $groupedEntry->group;
}

// Title normal list
echo "Location";
foreach ($groups as $group) {
    echo ",".$group->groupName;
}
echo "\n";

function getCount($entries, $group, $watinglist=false) {
    foreach ($entries as $entry) {
        if ($entry->group->id == $group->id) {
            return $watinglist ? $entry->waitingList : $entry->signedOn;
        }
    }
    
    return 0;
}

$locations = $factory->getApi()->getCalendarManager()->getAllLocations();


function getLocation($locations, $locationId) {
    foreach ($locations as $location) {
        if ($location->id == $locationId) {
            return $location;
        }
    }
    
    return null;
}


foreach ($grouped as $locationId => $entryResults) {
    $location = getLocation($locations, $locationId);
    echo str_replace(",", " - ", $location->location);
    
    foreach ($groups as $group) {
        echo ",".getCount($entryResults, $group);
    }
    
    echo "\n";
}


echo "\n";
echo "\n";
echo "Waitinglist";
echo "\n";

foreach ($grouped as $locationId => $entryResults) {
    $location = getLocation($locations, $locationId);
    echo str_replace(",", " - ", $location->location);
    
    foreach ($groups as $group) {
        echo ",".getCount($entryResults, $group, true);
    }
    
    echo "\n";
}
?>