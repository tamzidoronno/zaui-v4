<?php
class core_appmanager_data_Application extends core_common_DataCommon  {
	/** @var String */
	public $connectedWidgets;

	/** @var String */
	public $appName;

	/** @var String */
	public $description;

	/** @var String */
	public $allowedAreas;

	/** @var String */
	public $isSingleton;

	/** @var String */
	public $renderStandalone;

	/** @var String */
	public $isPublic;

	/** @var String */
	public $isResponsive;

	/** @var String */
	public $price;

	/** @var String */
	public $userId;

	/** @var String */
	public $type;

	/** @var String */
	public $ownerStoreId;

	/** @var String */
	public $clonedFrom;

	/** @var String */
	public $trialPeriode;

	/** @var String */
	public $pageSingelton;

	/** @var String */
	public $hasDashBoard;

	/** @var String */
	public $defaultActivate;

	/** @var String */
	public $allowedStoreIds;

	/** @var String */
	public $apiCallsInUse;

	/** @var core_appmanager_data_ApplicationModule */
	public $applicationModule;

	/** @var String */
	public $activeAppOnModuleActivation;

	/** @var String */
	public $moduleId;

}
?>