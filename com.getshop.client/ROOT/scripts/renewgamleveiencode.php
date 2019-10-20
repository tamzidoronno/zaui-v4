<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getUserManager()->logOn("entrancegamleveien@getshop.com", "asdfijas9d8ifajsodfiashdfiuahsdf");
$factory->getApi()->getPmsManager()->generateNewCodeForRoom("default", "dae637c6-17ad-4def-b203-7c1e74836186");
$booking = $factory->getApi()->getPmsManager()->getBookingFromRoom("default", "dae637c6-17ad-4def-b203-7c1e74836186");
$room = $booking->rooms[0];

$factory->getApi()->getMessageManager()->sendMail("kai@getshop.com", "Kai", "New Code", "New code: ".$room->code, "post@getshop.com", "GetShop");
$factory->getApi()->getMessageManager()->sendMail("chris@getshop.com", "Kai", "New Code", "New code: ".$room->code, "post@getshop.com", "GetShop");

$factory->getApi()->getUserManager()->logout();