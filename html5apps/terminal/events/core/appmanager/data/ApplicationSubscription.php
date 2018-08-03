<?php
class core_appmanager_data_ApplicationSubscription extends core_common_DataCommon  {
	/** @var String */
	public $appSettingsId;

	/** @var String */
	public $from_date;

	/** @var String */
	public $to_date;

	/** @var String */
	public $payedfor;

	/** @var core_appmanager_data_Application */
	public $app;

	/** @var String */
	public $numberOfInstancesAdded;

}
?>