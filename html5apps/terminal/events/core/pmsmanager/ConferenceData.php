<?php
class core_pmsmanager_ConferenceData extends core_common_DataCommon  {
	/** @var String */
	public $note;

	/** @var String */
	public $nameOfEvent;

	/** @var String */
	public $attendeesCount;

	/** @var String */
	public $date;

	/** @var core_pmsmanager_ConferenceDataDay[] */
	public $days;

	/** @var String */
	public $bookingId;

}
?>