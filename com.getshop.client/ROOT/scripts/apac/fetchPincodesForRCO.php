<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

session_start();
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);

header('Content-Type: text/plain; charset=utf-8');

$servers = $factory->getApi()->getGetShopLockSystemManager()->getLockServers();
foreach ($servers as $server) {
    foreach ($server->locks as $lock) {
        $slotsWithCodes = array();
        foreach ($lock->userSlots as $slot) {
            if ($slot->code && $slot->code->pinCode) {
                $slotsWithCodes[] = $slot;
            }
        }
        
        if (!count($slotsWithCodes)) {
            continue;
        }
        
        $days = 5;
        foreach ($slotsWithCodes as $slot) {
            for ($i=0;$i<$days; $i++) {
                // Example: 20181205,0000,0000,Hotellrom 2,PINONLY,2567,,Rom 2,Stäkets bollklubb,2
                // 1 = date YYYYMMDD,HHMM,HHMM,LOCKNAME,PINONLY,CODE,,Slot: $i,GetShop,2
                $timeStr = 'now +'.$i.' days';
                $date = date('Ymd', strtotime($timeStr));
                $pincode = $slot->code->pinCode;
                echo "$date,0000,2400,$lock->name,PINONLY,$pincode,,Slot $slot->slotId,GetShop - $lock->name,2\n";
    //            die();    
            }
        }
    }
}