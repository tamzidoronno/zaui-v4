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
$factory->getApi()->getGetShopLockSystemManager()->addTransactionEntranceDoor($_GET['serverId'], $_GET['lockId'], $_GET['code']);
echo "OK";
?>