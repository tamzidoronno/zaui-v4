<?php
class core_ordermanager_data_Order extends core_common_DataCommon  {
	/** @var String */
	public $paymentTransactionId;

	/** @var core_ordermanager_data_Shipping */
	public $shipping;

	/** @var core_ordermanager_data_Payment */
	public $payment;

	/** @var String */
	public $session;

	/** @var String */
	public $trackingNumber;

	/** @var String */
	public $incrementOrderId;

	/** @var String */
	public $reference;

	/** @var String */
	public $transferredToAccountingSystem;

	/** @var String */
	public $testOrder;

	/** @var String */
	public $captured;

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