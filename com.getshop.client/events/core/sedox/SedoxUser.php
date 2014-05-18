<?php
class core_sedox_SedoxUser extends core_common_DataCommon  {
	/** @var String */
	public $orders;

	/** @var core_sedox_SedoxCreditAccount */
	public $creditAccount;

	/** @var String */
	public $isNorwegian;

	/** @var String */
	public $isPassiveSlave;

	/** @var String */
	public $canUseExternalProgram;

	/** @var String */
	public $magentoId;

}
?>