<?php
class core_trackandtrace_Destination extends core_common_DataCommon  {
	/** @var String */
	public $companyIds;

	/** @var core_usermanager_data_Company */
	public $company;

	/** @var core_trackandtrace_StartInfo */
	public $startInfo;

	/** @var String */
	public $taskIds;

	/** @var core_trackandtrace_Task[] */
	public $tasks;

	/** @var String */
	public $signatureImage;

	/** @var core_trackandtrace_TrackAndTraceSignature[] */
	public $signatures;

	/** @var String */
	public $typedNameForSignature;

	/** @var core_trackandtrace_SkipInfo */
	public $skipInfo;

	/** @var String */
	public $exceptionId;

	/** @var String */
	public $seq;

	/** @var String */
	public $podBarcode;

	/** @var String */
	public $deliveryInstruction;

	/** @var String */
	public $pickupInstruction;

	/** @var String */
	public $onDemandInstructions;

	/** @var String */
	public $extraInstructions;

	/** @var String */
	public $stopWindow;

	/** @var String */
	public $dirty;

	/** @var String */
	public $movedFromPool;

	/** @var String */
	public $signatureRequired;

	/** @var String */
	public $extraInstractionsRead;

	/** @var String */
	public $extraInstractionsReadDate;

}
?>