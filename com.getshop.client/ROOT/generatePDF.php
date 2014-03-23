<?php
header('Content-type: application/pdf');
header('Content-Disposition: attachment; filename="file.pdf"');

include '../loader.php';
include('../classes/mpdf/mpdf.php');
$type = $_GET['type'];
$factory = IocContainer::getFactorySingelton();
if($type == "contentmanager") {
    $id = $_GET['id'];
    $logo = $factory->getApplicationPool()->getApplicationsInstancesByNamespace("ns_974beda7_eb6e_4474_b991_5dbc9d24db8e");
    $logo = $logo[0];
    $imageId = $factory->getApi()->getLogoManager()->getLogo()->LogoId;
    
    if ($factory->getStore()->id == "2fac0e57-de1d-4fdf-b7e4-5f93e3225445") {
        $imageId = "d1f46c58-0f53-498a-8f8c-a47d0012cb91";
    }
    
    $address = $factory->getStore()->webAddressPrimary;
    
    $div = "<div style='margin-bottom: 20px; '><img src='http://$address/displayImage.php?id=$imageId'/></div>";
    $content = $div.$factory->getApi()->getContentManager()->getContent($id);
}

if ($type == "calendar") {
    ob_start();
    $app = $factory->getApplicationPool()->getApplicationInstance($_GET['id']);
    $app->renderList(true);
    $content = "<b>".$factory->__f("Date").": ".date("d")."/".date("m")."-".date("Y")."</b>";
    $content .= ob_get_contents();
    
    ob_end_clean();
}

$mpdf=new mPDF();
$mpdf->WriteHTML($content);
$mpdf->Output("document.pdf",'I');   
exit;

?>