<?php
class core_trackandtrace_Route extends core_common_DataCommon  {
	/** @var String */
	public $name;

	/** @var String */
	public $seq;

	/** @var String */
	public $shortDescription;

	/** @var String */
	public $instruction;

	/** @var String */
	public $instructionAccepted;

	/** @var core_trackandtrace_StartInfo */
	public $startInfo;

	/** @var core_trackandtrace_StartInfo */
	public $completedInfo;

	/** @var String */
	public $destinationIds;

	/** @var core_trackandtrace_DriverRouteLog[] */
	public $driverLogs;

	/** @var String */
	public $userIds;

	/** @var String */
	public $deliveryTime;

	/** @var core_trackandtrace_Destination[] */
	public $destinations;

	/** @var String */
	public $deliveryServiceDate;

	/** @var String */
	public $originalId;

	/** @var String */
	public $depotId;

	/** @var String */
	public $dirty;

	/** @var String */
	public $isVritual;

}
?>