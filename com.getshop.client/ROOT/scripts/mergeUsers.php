<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->mergeUsers("d3ace5a5-addd-440f-91b7-32d7f6a72327", ['c91dc937-2a7e-4826-9f3c-6269745412bd']);
$factory->getApi()->getUserManager()->mergeUsers("f3f51113-50e4-4242-9269-96b4a614888b", ['a31cfe6d-399e-4985-82f8-ccea5297a59c']);
$factory->getApi()->getUserManager()->mergeUsers("a2d2a59a-dfaf-403a-b4ca-aaf0d073bb69", ['74906ecd-ce23-47e7-aac1-69f4333ff415']);
$factory->getApi()->getUserManager()->mergeUsers("093a2f3b-b571-46c1-9cc7-8ebeefd0623d", ['7fc14571-0a8b-41fd-b2ac-3fe1979f55ea']);
?>