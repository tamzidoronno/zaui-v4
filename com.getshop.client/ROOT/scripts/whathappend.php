<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

if (isset($_POST['checklist'])) {
    $d1 = $_POST['checklist'][0];
    $d2 = $_POST['checklist'][1];
    
    $result = $factory->getApi()->getDBBackupManager()->getDiff("com.thundashop.core.usermanager.data.User", $d1, $d2);
    echo $result;
    return;
}


$changes = $factory->getApi()->getDBBackupManager()->getChanges($_GET['className'], $_GET['entityId']);

if (!count($changes)) {
    echo "No changes";
    return;
}

?>
<form method="POST" action="whathappend.php">
<?
    echo "<input value='HEAD' name='checklist[]' type='checkbox'/> HEAD";

    foreach ($changes as $change) {
        $user = $factory->getApi()->getUserManager()->getUserById($change->byUserId);
        $fullName = $user ? $user->fullName : "N/A";
        echo "<br/><input name='checklist[]' value='$change->id' type='checkbox'/> $change->timeStamp | By: $fullName";
    }

    ?>

    <br/><br/> <input type="submit" value="Show diff"/>
</form>