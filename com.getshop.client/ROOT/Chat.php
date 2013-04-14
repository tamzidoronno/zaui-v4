<?php
include '../loader.php';
IocContainer::getFactorySingelton(false);
$chat = new ns_2afb045b_fa01_4398_8582_33f212bb8db8\Chat();
$chat->$_POST['event']();
?>