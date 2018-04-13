<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$users = $factory->getApi()->getUserManager()->getAllUsers();
$tests = $factory->getApi()->getQuestBackManager()->getAllTests();
$cats = $factory->getApi()->getQuestBackManager()->getCategories();

function getAverage($answers) {
    $sum = 0;
    foreach ($answers as $ans) {
        $sum += $ans->percentageOfCorrect;
    }
    
    return (int)($sum / count($answers));
}

function getValue($a) {
    return str_replace(',', '', $a).";";
}


echo ";;;;";
foreach ($cats as $cat) {
    echo getValue($cat->name);
}
echo "<br/>";

$userResult = array();
foreach ($users as $user) {
    
    foreach ($tests as $test) {
        $resToPrint = "";
        $resToPrint .= getValue($user->id);
        $resToPrint .= getValue($user->fullName);
        $foundAny = false;
        
        $resToPrint .= getValue($test->id);
        $resToPrint .= getValue($test->name);
        $result = $factory->getApi()->getQuestBackManager()->getTestResultForUser($test->id, $user->id);
        
        if (count($result->answers) > 0) {
            $parents = array();
            $grouped = array();
            foreach ($result->answers as $answer) {
                $parents[$answer->parent->id] = $answer->parent;
                $grouped[$answer->parent->id][] = $answer;
            }
            
            foreach ($cats as $cat) {
                if (isset($grouped[$cat->id]) && count($grouped[$cat->id]) > 0) {
                    $avg = getAverage($grouped[$cat->id]);
                    $resToPrint .= getValue($avg);
                    $foundAny = true;
                } else {
                    $resToPrint .= getValue("");
                }
            }
        }
        
        if ($foundAny) {
            echo $resToPrint."<br/>";
        }   
    }
}