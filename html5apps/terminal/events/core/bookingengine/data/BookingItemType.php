<?php
class core_bookingengine_data_BookingItemType extends core_common_DataCommon  {
	/** @var String */
	public $name;

	/** @var String */
	public $productId;

	/** @var String */
	public $historicalProductIds;

	/** @var String */
	public $pageId;

	/** @var String */
	public $visibleForBooking;

	/** @var String */
	public $autoConfirm;

	/** @var String */
	public $addon;

	/** @var String */
	public $size;

	/** @var core_bookingengine_data_RegistrationRules */
	public $rules;

	/** @var core_pmsmanager_TimeRepeaterData */
	public $openingHours;

	/** @var java.util.HashMap */
	public $openingHoursData;

	/** @var String */
	public $order;

	/** @var String */
	public $description;

	/** @var String */
	public $group;

	/** @var String */
	public $capacity;

	/** @var String */
	public $minStay;

	/** @var String */
	public $eventItemGroup;

	/** @var String */
	public $systemCategory;

}
?>