<?php
class core_trackandtrace_PickupOrder extends core_trackandtrace_TntOrder  {
	/** @var String */
	public $instruction;

	/** @var String */
	public $barcodeScanned;

	/** @var String */
	public $countedContainers;

	/** @var String */
	public $countedBundles;

	/** @var String */
	public $mustScanBarcode;

	/** @var String */
	public $returnLabelNumber;

	/** @var String */
	public $container;

	/** @var String */
	public $barcodeEnteredManually;

}
?>