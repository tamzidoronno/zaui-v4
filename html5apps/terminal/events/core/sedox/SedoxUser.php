<?php
class core_sedox_SedoxUser extends core_common_DataCommon  {
	/** @var core_sedox_SedoxOrder[] */
	public $orders;

	/** @var core_sedox_SedoxCreditAccount */
	public $creditAccount;

	/** @var String */
	public $isNorwegian;

	/** @var String */
	public $isPassiveSlave;

	/** @var String */
	public $badCustomer;

	/** @var String */
	public $canUseExternalProgram;

	/** @var String */
	public $magentoId;

	/** @var core_sedox_SedoxCreditOrder[] */
	public $creditOrders;

	/** @var core_sedox_SedoxEvcCreditOrder[] */
	public $evcCreditOrders;

	/** @var String */
	public $isActiveDelevoper;

	/** @var String */
	public $masterUserId;

	/** @var String */
	public $slaveIncome;

	/** @var String */
	public $pushoverId;

	/** @var String */
	public $fixedPrice;

	/** @var String */
	public $evcCurrentBalance;

	/** @var String */
	public $comment;

	/** @var String */
	public $evcId;

}
?>