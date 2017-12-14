<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getUserManager()->impersonateUser($_GET['userid']);
$userExport = new stdClass();
$userExport->userInfo = $factory->getApi()->getUserManager()->getLoggedOnUser();
$userExport->sedoxUserInfo = $factory->getApi()->getSedoxProductManager()->getSedoxUserAccount();
$emptyFilter = new \core_sedox_FilterData();
$emptyFilter->pageSize = 999999;
$userExport->files = $factory->getApi()->getSedoxProductManager()->getProductsFirstUploadedByCurrentUser($emptyFilter);
$factory->getApi()->getUserManager()->cancelImpersonating();
echo json_encode($userExport);
?>