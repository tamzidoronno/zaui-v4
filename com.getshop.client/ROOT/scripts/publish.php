<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$envs = $factory->getApi()->getStoreManager()->getAllEnvironments();

if (isset($_POST['submit'])) {
    $factory->getApi()->getStoreManager()->syncData($_POST['env'], $_POST['username'], $_POST['password']);
}

?>


<div style="padding: 20px; text-align: center;">
    <form method='post'>
    Select env
    <br/>
    <select name="env">
        <?
        foreach ($envs as $env) {
            echo "<option value='$env'>$env</option>";
        }?>
    </select>
    
    <br/>
    <br/>Username:
    <br/><input type="textfield" name="username" />
    <br/>Password:
    <br/><input type="textfield" name="password" />
    <br/><br/><input type="submit" name="submit" name="startSync"/>
</div>