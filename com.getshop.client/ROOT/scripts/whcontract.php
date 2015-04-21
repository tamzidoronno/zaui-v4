<?
header("Content-type:application/pdf");
header("Content-Disposition:attachment;filename='rentalterms.pdf'");

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$contract = $factory->getApi()->getHotelBookingManager()->getCurrentRentalTermsContract();
echo base64_decode($contract);
?>