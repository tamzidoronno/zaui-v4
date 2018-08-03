<?php
class core_bookingengine_data_Booking extends core_common_DataCommon  {
	/** @var String */
	public $bookingItemId;

	/** @var String */
	public $incrementalBookingId;

	/** @var String */
	public $bookingDeleted;

	/** @var String */
	public $bookingItemTypeId;

	/** @var String */
	public $prevAssignedBookingItemId;

	/** @var String */
	public $startDate;

	/** @var String */
	public $endDate;

	/** @var String */
	public $needConfirmation;

	/** @var String */
	public $externalReference;

	/** @var String */
	public $userId;

	/** @var String */
	public $source;

	/** @var String */
	public $doneByUserId;

	/** @var String */
	public $doneByImpersonator;

}
?>