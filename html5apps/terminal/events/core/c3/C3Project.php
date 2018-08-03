<?php
class core_c3_C3Project extends core_common_DataCommon  {
	/** @var String */
	public $name;

	/** @var String */
	public $projectNumber;

	/** @var String */
	public $projectOwner;

	/** @var String */
	public $workPackages;

	/** @var core_c3_C3ProjectPeriode */
	public $currentProjectPeriode;

	/** @var java.util.HashMap */
	public $activatedCompanies;

}
?>