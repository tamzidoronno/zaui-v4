<?php
class core_getshoplocksystem_LocstarLock extends core_getshoplocksystem_Lock  {
	/** @var String */
	public $zwaveDeviceId;

	/** @var String */
	public $prioritizeLockUpdate;

	/** @var String */
	public $currentlyUpdating;

	/** @var String */
	public $currentlyAttempt;

	/** @var String */
	public $dead;

	/** @var String */
	public $markedDateAtDate;

}
?>