<?php
class core_accountingmanager_SavedOrderFile extends core_common_DataCommon  {
	/** @var String */
	public $result;

	/** @var String */
	public $type;

	/** @var String */
	public $transferred;

	/** @var String */
	public $startDate;

	/** @var String */
	public $endDate;

	/** @var String */
	public $subtype;

	/** @var String */
	public $amountEx;

	/** @var String */
	public $amountInc;

	/** @var String */
	public $amountExDebet;

	/** @var String */
	public $amountIncDebet;

	/** @var String */
	public $sumAmountExOrderLines;

	/** @var String */
	public $sumAmountIncOrderLines;

	/** @var String */
	public $onlyPositiveLinesEx;

	/** @var String */
	public $onlyPositiveLinesInc;

	/** @var String */
	public $amountOnOrder;

	/** @var String */
	public $tamperedOrders;

	/** @var String */
	public $orders;

	/** @var String */
	public $ordersTriedButFailed;

	/** @var String */
	public $configId;

	/** @var String */
	public $transferId;

	/** @var String */
	public $ordersNow;

	/** @var String */
	public $lastFinalized;

	/** @var String */
	public $base64Excel;

	/** @var String */
	public $numberOfOrdersNow;

}
?>