<?php
class core_bookingengine_data_BookingEngineConfiguration extends core_common_DataCommon  {
	/** @var String */
	public $confirmationRequired;

	/** @var core_bookingengine_data_RegistrationRules */
	public $rules;

	/** @var core_pmsmanager_TimeRepeaterData */
	public $openingHours;

	/** @var java.util.LinkedHashMap */
	public $openingHoursData;

}
?>