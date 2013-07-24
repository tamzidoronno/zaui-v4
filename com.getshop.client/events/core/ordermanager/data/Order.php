<?php
class core_ordermanager_data_Order extends core_common_DataCommon  {
	/** @var String */
	public $paymentType;

	/** @var core_ordermanager_data_Shipping */
	public $shipping;

	/** @var String */
	public $session;

	/** @var String */
	public $createdDate;

	/** @var String */
	public $userId;

	/** @var String */
	public $status;

	/** @var core_cartmanager_data_Cart */
	public $cart;

}
?>