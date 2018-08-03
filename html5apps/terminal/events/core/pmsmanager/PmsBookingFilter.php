<?php
class core_pmsmanager_PmsBookingFilter extends core_common_DataCommon  {
	/** @var String */
	public $state;

	/** @var String */
	public $startDate;

	/** @var String */
	public $endDate;

	/** @var String */
	public $startDateAsText;

	/** @var String */
	public $endDateAsText;

	/** @var String */
	public $filterType;

	/** @var String */
	public $filterSubType;

	/** @var String */
	public $searchWord;

	/** @var String */
	public $bookingId;

	/** @var String */
	public $needToBeConfirmed;

	/** @var String */
	public $includeDeleted;

	/** @var String */
	public $onlyUntransferredToBookingCom;

	/** @var String */
	public $groupByBooking;

	/** @var String */
	public $sorting;

	/** @var String */
	public $userId;

	/** @var String */
	public $channel;

	/** @var String */
	public $interval;

	/** @var String */
	public $timeInterval;

	/** @var String */
	public $includeCleaningInformation;

	/** @var String */
	public $includeVirtual;

	/** @var String */
	public $removeClosedRooms;

	/** @var String */
	public $includeNonBookable;

	/** @var String */
	public $includeOrderStatistics;

	/** @var String */
	public $removeAddonsIncludedInRoomPrice;

	/** @var String */
	public $typeFilter;

	/** @var String */
	public $itemFilter;

	/** @var String */
	public $filterName;

	/** @var String */
	public $customers;

	/** @var String */
	public $addons;

	/** @var String */
	public $codes;

}
?>