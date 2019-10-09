<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();


if(isset($_GET['username'])) {
    $username = $_GET['username'];
    $password = $_GET['password'];
    $login = $factory->getFactory()->getApi()->getUserManager()->logOn($username, $password);
    if(!$login) {
        echo "Login failed.";
        return;
    }
}

if(isset($_GET['showhtml'])) {
    $types = $factory->getApi()->getBookingEngine()->getBookingItemTypes("default");
    $topostslist = array();
    foreach($types as $type) {
        $obj = new stdClass();
        $obj->categoryId = $type->id;
        $obj->price = "9999.33";
        $obj->date = date("d-m-Y", time()+86000*365);
        $topostslist[] = $obj;
    }
    ?>
    Prefilled example for updating prices:<br>
    <form action='updateprices.php' method='post'>
        <textarea style='width:800px; height: 400px;' name='prices'><?php echo json_encode($topostslist); ?></textarea><br>
        <input type='submit' style='width:800px;'>
    </form>
    <?php
} else {
    $prices = $_POST['prices'];
    if(!$prices) {
        echo "Prices post variable has not been pushed, please push all prices to the prices object.";
        return;
    }
    
    $entries = json_decode($prices);
    $toUpdateList = array();
    $failed = false;
    
    $types = $factory->getApi()->getBookingEngine()->getBookingItemTypes("default"); 
    $typeids = array();
    foreach($types as $t) {
        $typeids[] = $t->id;
    }
    
    foreach($entries as $entry) {
        $updateEntry = new core_pmsmanager_PmsPricingDayObject();
        
        if(date("d-m-Y", strtotime($entry->date)) != $entry->date) {
            echo "Invalid date format: " . $entry->date . " date format need to be : dd-mm-YYYY" . " (Category: " . $entry->categoryId .")" . "<br>";
            $failed = true;
        }
        if(!in_array($entry->categoryId, $typeids)) {
            echo "Invalid category id: " . $entry->categoryId . " does not exists." . "<br>";
            $failed = true;
        }
        
        if(!is_numeric($entry->price)) {
            echo "Invalid price: " . $entry->price . " for date ". $entry->date . " (Category: " . $entry->categoryId .")<br>";
            $failed = true;
        }
        
        
        $updateEntry->date = $entry->date;
        $updateEntry->typeId = $entry->categoryId;
        $updateEntry->newPrice = $entry->price;
        $toUpdateList[] = $updateEntry;
    }
    if($failed) {
        echo "Invalid input, can not proceed.";
    } else {
        $success = $factory->getApi()->getPmsManager()->updatePrices("default", $toUpdateList);
        if($success) {
            echo "OK";
        } else {
            echo "Internal server error occured at time: " .date("d.m.Y", time());
        }
    }
}
