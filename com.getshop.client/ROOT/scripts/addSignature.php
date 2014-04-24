<?
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$id = "";
$signature = "";
if(isset($_POST['signature'])) {
    $signature = $_POST['signature'];
    $id = $_POST['id'];
    $factory->getApi()->getCalendarManager()->setSignature($id, $signature);
    echo "Updated";
}
?>

<form action="" method="post">
    <input type="text" name="id" width="100%" value="<? echo $id; ?>">
    <textarea name="signature" style="width:100%; height: 400px;"><? echo $signature; ?></textarea>
    <input type="submit">
</form>