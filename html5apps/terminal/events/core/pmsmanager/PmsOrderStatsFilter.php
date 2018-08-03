<?php
class core_pmsmanager_PmsOrderStatsFilter extends core_common_DataCommon  {
	/** @var String */
	public $name;

	/** @var String */
	public $start;

	/** @var String */
	public $end;

	/** @var core_pmsmanager_PmsPaymentMethods[] */
	public $methods;

	/** @var String */
	public $displayType;

	/** @var String */
	public $priceType;

	/** @var String */
	public $includeVirtual;

	/** @var String */
	public $shiftHours;

	/** @var String */
	public $savedPaymentMethod;

	/** @var String */
	public $channel;

}
?>