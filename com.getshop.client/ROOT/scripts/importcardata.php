<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$filter = array();
$filter[] = "har vi dessverre ingen løsning for";
$filter[] = "*";
$filter[] = "henting";
$filter[] = "datatuning";
$filter[] = "modell G / H";
$filter[] = "uten elektronisk motorcomputer";

$row = 1;
if (($handle = fopen("scripts/tuningliste.csv", "r")) !== FALSE) {
    $resultList = array();
    while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
        $num = count($data);
        $row++;
        $found = false;
        $heading = false;
        if(stristr($data[1], "bilmerke")) {
            $merke = $data[0];
            $found = true;
            $heading = true;
        }
        if(strlen(trim($data[1])) == 0 || stristr($data[1], "ytelse")) {
            $modell = $data[0];
            $found = true;
            $heading = true;
        }
        
        if(!$heading) {
            if(!stristr($data[1], "kW")) {
                continue;
            }
        }
        if(strlen(trim($data[0])) < 3) {
            continue;
        }
        
        $break = false;
        foreach($data as $entry) {
            foreach($filter as $filterEntry) {
                if(stristr($entry, $filterEntry)) {
                    $break=true;
                }
            }
        }
        if($break || !$modell) {
            continue;
        }
        if($data[0] == $modell) {
            continue;
        }
        
        $result = new core_cartuning_CarTuningData();
        $result->name = str_replace(",", ".", $data[0]) . " " . $data[1];
        $result->originalHp = getnumbers($data[2]);
        $result->originalNm = getnumbers($data[5]);
        $result->normalHp = intval(getnumbers($data[4]) * 0.95);
        $result->normalNw = intval(getnumbers($data[6]) * 0.95);
        $result->maxHp = intval(getnumbers($data[4]) * 1.05);
        $result->maxNw = intval(getnumbers($data[6]) * 1.05);
        $resultList[$merke][$modell][] = $result;
    }
    echo "<pre>";
//    print_r($resultList);
    fclose($handle);
    
    $carlist = array();
    foreach($resultList as $merke => $list) {
        $modellist = new core_cartuning_CarTuningData();
        $modellist->name = $merke;
        $sublist = array();
        foreach($list as $modell => $entry) {
            $modellentry = new core_cartuning_CarTuningData();
            $modellentry->name = $modell;
            $modellentry->subEntries = $entry;
            $sublist[] = $modellentry;
        }
        $modellist->subEntries = $sublist;
    $carlist[] = $modellist;
    }
    print_r($carlist);
    $factory->getApi()->getCarTuningManager()->saveCarTuningData($carlist);
}

function getnumbers($str) {
    preg_match_all('/([\d]+)/', $str, $match);

   return $match[0][0];
}

?>
