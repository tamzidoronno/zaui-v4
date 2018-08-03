<?php
class core_cartmanager_data_Coupon extends core_common_DataCommon  {
	/** @var String */
	public $code;

	/** @var core_cartmanager_data_CouponType */
	public $type;

	/** @var String */
	public $amount;

	/** @var String */
	public $timesLeft;

	/** @var String */
	public $channel;

	/** @var String */
	public $description;

	/** @var core_pmsmanager_PmsRepeatingData */
	public $whenAvailable;

	/** @var String */
	public $pmsWhenAvailable;

	/** @var String */
	public $productsToSupport;

	/** @var core_cartmanager_data_AddonsInclude[] */
	public $addonsToInclude;

	/** @var String */
	public $priceCode;

	/** @var String */
	public $minDays;

	/** @var String */
	public $maxDays;

}
?>