<?php
class core_resturantmanager_ResturantTable extends core_common_DataCommon  {
	/** @var String */
	public $table;

	/** @var String */
	public $savedCartIds;

	/** @var String */
	public $currentCartId;

	/** @var String */
	public $name;

	/** @var core_cartmanager_data_Cart */
	public $currentCart;

}
?>