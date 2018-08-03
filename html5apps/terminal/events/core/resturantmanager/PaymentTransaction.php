<?php
class core_resturantmanager_PaymentTransaction extends core_common_DataCommon  {
	/** @var String */
	public $status;

	/** @var String */
	public $userId;

	/** @var core_resturantmanager_ResturantCartItem[] */
	public $cartItems;

	/** @var String */
	public $orderId;

}
?>