<?php
class core_trackandtrace_PooledDestionation extends core_common_DataCommon  {
	/** @var String */
	public $destionationId;

	/** @var String */
	public $pooledByUserId;

	/** @var String */
	public $originalRouteId;

	/** @var core_trackandtrace_Destination */
	public $destination;

	/** @var core_trackandtrace_Route */
	public $originalRoute;

}
?>