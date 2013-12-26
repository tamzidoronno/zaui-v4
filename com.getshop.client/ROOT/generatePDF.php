<?php
include '../loader.php';
include('../classes/mpdf/mpdf.php');
$type = $_GET['type'];
$factory = IocContainer::getFactorySingelton();
if($type == "contentmanager") {
    $id = $_GET['id'];
    $content = $factory->getApi()->getContentManager()->getContent($id);
}

$mpdf=new mPDF();
$mpdf->WriteHTML($content);
$mpdf->Output("document.pdf",'I');   exit;

?>
