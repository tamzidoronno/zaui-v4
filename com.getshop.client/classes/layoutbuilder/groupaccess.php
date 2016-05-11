<?php

/* @var $this Page */

/* @var $factory Factory */
$factory = $this->factory;

$groups = $factory->getApi()->getUserManager()->getAllGroups();
?>
<div class='groupAccess' style='border: Solid 1px #DDD; margin: 20px; padding: 20px; text-align: center;'>
    <? echo $factory->__f("If no group is selected all groups will have access to this row/cell by default"); ?>
</div>
<?
foreach ($groups as $group) {
    echo "<br/><input type='checkbox' gsname='$group->id'/> ".$group->groupName;
}
?>

