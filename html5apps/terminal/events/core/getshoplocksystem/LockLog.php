<?php
class core_getshoplocksystem_LockLog extends core_common_DataCommon  {
	/** @var String */
	public $lockId;

	/** @var String */
	public $timestamp;

	/** @var String */
	public $event;

	/** @var String */
	public $nameOfUser;

	/** @var core_getshoplocksystem_LockCode */
	public $lockCode;

}
?>