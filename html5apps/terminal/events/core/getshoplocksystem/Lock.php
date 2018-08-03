<?php
class core_getshoplocksystem_Lock {
	/** @var String */
	public $typeOfLock;

	/** @var String */
	public $connectedToServerId;

	/** @var String */
	public $id;

	/** @var java.util.Map */
	public $userSlots;

	/** @var String */
	public $dontUpdateUntil;

	/** @var String */
	public $lastStartedUpdating;

	/** @var core_getshoplocksystem_UserSlot[] */
	public $toRemove;

	/** @var core_getshoplocksystem_UserSlot[] */
	public $toUpdate;

	/** @var core_getshoplocksystem_UserSlot[] */
	public $inUse;

	/** @var String */
	public $maxnumberOfCodes;

	/** @var String */
	public $name;

	/** @var String */
	public $codeSize;

	/** @var String */
	public $routing;

}
?>