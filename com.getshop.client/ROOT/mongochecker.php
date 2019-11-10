<?php
$filter = array(
    'replSetGetStatus' => 1,
); 

$options = array();

$servers = array();
$servers[] = "27014";
$servers[] = "27015";
$servers[] = "27016";

$query = new MongoDB\Driver\Command($filter);
foreach($servers as $server) {
    try {
        $manager = new MongoDB\Driver\Manager("mongodb://10.0.7.33:".$server);
        $cursor = $manager->executeCommand('admin', $query);
        $result = array();
        foreach ($cursor as $document) {
            $result[] = $document;
        }
        $object = $result[0];

        $result = array();

        $min = null;
        $max = null;
        foreach($object->members as $member) {
            $ts = $member->optime->ts;
            $timestamp = $ts->getTimestamp();
            $name = $member->name;
            $result[$name] = $timestamp;
            if(!is_int($min) || $min > $timestamp) {
                $min = $timestamp;
            }
            if(!is_int($max) || $max < $timestamp) {
                $max = $timestamp;
            }
        }

        $diff = $max-$min;
        $result["diff"] = $diff;

        if($diff < 10000) {
            echo "<h1>Replication $server OK ($diff)</h1>";
        } else {
            echo "REPLICATION IS NOT OK.";
            header($_SERVER['SERVER_PROTOCOL'] . ' 500 Internal Server Error', true, 500);
        }
    }catch(MongoDB\Driver\Exception\ConnectionTimeoutException $e) {
            echo "Replication node is down.";
        header($_SERVER['SERVER_PROTOCOL'] . ' 500 Internal Server Error', true, 500);
    }
}
?>
