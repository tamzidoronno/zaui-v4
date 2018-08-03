<?php
class core_meca_data_MecaUser extends core_common_DataCommon  {
	/** @var String */
	public $userId;

	/** @var core_meca_data_Vehicle[] */
	public $vehicles;

	/** @var core_meca_data_Workshop */
	public $workshop;

	/** @var core_usermanager_data_User */
	public $getshopUser;

}
?>