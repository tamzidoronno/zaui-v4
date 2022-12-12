<?php
class zauiactivity_dto_BookingZauiActivityItem extends core_pmsmanager_PmsBookingAddonItem  {
	/** @var String */
	public $id;

	/** @var String */
	public $zauiActivityId;

	/** @var String */
	public $octoProductId;

	/** @var String */
	public $optionTitle;

	/** @var String */
	public $optionId;

	/** @var String */
	public $availabilityId;

	/** @var zauiactivity_dto_Unit[] */
	public $units;

	/** @var zauiactivity_dto_Pricing */
	public $pricing;

	/** @var String */
	public $unpaidAmount;

	/** @var String */
	public $notes;

	/** @var String */
	public $localDateTimeStart;

	/** @var String */
	public $localDateTimeEnd;

	/** @var String */
	public $supplierId;

	/** @var String */
	public $supplierName;

	/** @var zauiactivity_dto_OctoBooking */
	public $octoBooking;

}
?>