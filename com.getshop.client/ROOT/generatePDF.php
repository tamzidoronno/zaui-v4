<?php
include '../loader.php';
include('../classes/mpdf/mpdf.php');
$type = $_GET['type'];
$factory = IocContainer::getFactorySingelton();
if($type == "contentmanager") {
    $id = $_GET['id'];
    $logo = $factory->getApplicationPool()->getApplicationsInstancesByNamespace("ns_974beda7_eb6e_4474_b991_5dbc9d24db8e");
    $logo = $logo[0];
    $imageId = $factory->getApi()->getLogoManager()->getLogo()->LogoId;
    $address = $factory->getStore()->webAddressPrimary;
    $div = "<div style='background-color: #000; padding: 5px; margin-bottom: 20px; text-align: center;'><img style='' src='http://$address/displayImage.php?id=$imageId'/></div>";
    $content = $div.$factory->getApi()->getContentManager()->getContent($id);
}

$mpdf=new mPDF();
$mpdf->WriteHTML($content);
$mpdf->Output("document.pdf",'I');   
exit;

?>