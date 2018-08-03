<?php
class core_pmseventmanager_PmsBookingEventEntry extends core_common_DataCommon  {
	/** @var String */
	public $title;

	/** @var String */
	public $shortdesc;

	/** @var String */
	public $imageId;

	/** @var String */
	public $logoId;

	/** @var String */
	public $category;

	/** @var String */
	public $arrangedBy;

	/** @var String */
	public $location;

	/** @var String */
	public $contact;

	/** @var String */
	public $isDeleted;

	/** @var String */
	public $starttime;

	/** @var String */
	public $description;

	/** @var core_pmseventmanager_PmsBookingEventLink[] */
	public $lenker;

	/** @var core_pmsmanager_PmsBookingDateRange[] */
	public $dateRanges;

	/** @var String */
	public $roomNames;

	/** @var java.util.HashMap */
	public $overrideEntries;

	/** @var String */
	public $bookingId;

}
?>