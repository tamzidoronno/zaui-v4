<?php
class core_pmsmanager_PmsAdditionalItemInformation extends core_common_DataCommon  {
	/** @var String */
	public $lastUsed;

	/** @var String */
	public $lastCleaned;

	/** @var String */
	public $cleaningDates;

	/** @var String */
	public $markedDirtyDatesLog;

	/** @var java.util.HashMap */
	public $inventory;

	/** @var String */
	public $cleanedByUser;

	/** @var String */
	public $itemId;

	/** @var String */
	public $squareMetres;

	/** @var String */
	public $hideFromCleaningProgram;

	/** @var String */
	public $textMessageDescription;

	/** @var String */
	public $isClean;

	/** @var String */
	public $isCleanNotToday;

	/** @var String */
	public $inUse;

	/** @var String */
	public $inUseByCleaning;

	/** @var String */
	public $closed;

	/** @var String */
	public $closedByCleaningProgram;

}
?>