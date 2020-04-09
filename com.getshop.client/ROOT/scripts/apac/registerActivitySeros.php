<?
/**
 * The GetShop software running on the PGA's or other Zwave Servers uses this
 * file to push activities from the lock system.
 * 
 * Parameters:
 *   - token = TokenId registered for each Zwave Gateway
 *   - deviceId = The id of the device 
 *   - slotId = The slot used for access
 *   - date = Timestamp of the event.
 */

session_start();
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$time = date("c", strtotime($_GET['date']));
$factory->getApi()->getGetShopLockSystemManager()->addTransactionHistorySeros($_GET['token'], $_GET['lockId'], $time, $_GET['keyId'], "1");
echo "OK";
?>