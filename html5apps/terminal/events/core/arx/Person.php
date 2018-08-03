<?php
class core_arx_Person {
	/** @var String */
	public $id;

	/** @var String */
	public $lastName;

	/** @var String */
	public $firstName;

	/** @var core_arx_AccessCategory[] */
	public $accessCategories;

	/** @var String */
	public $endDate;

	/** @var core_arx_Card[] */
	public $cards;

	/** @var String */
	public $deleted;

}
?>