<?php
class core_ordermanager_data_Payment {
	/** @var String */
	public $paymentType;

	/** @var String */
	public $paymentFee;

	/** @var core_productmanager_data_TaxGroup */
	public $paymentFeeTaxGroup;

	/** @var String */
	public $transactionLog;

	/** @var String */
	public $captured;

	/** @var String */
	public $callBackParameters;

}
?>