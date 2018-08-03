<?php
class core_asanamanager_AsanaTask {
	/** @var String */
	public $id;

	/** @var String */
	public $name;

	/** @var String */
	public $created_at;

	/** @var String */
	public $modified_at;

	/** @var String */
	public $notes;

	/** @var String */
	public $completed;

	/** @var String */
	public $assignee_status;

	/** @var String */
	public $completed_at;

	/** @var String */
	public $assignee;

	/** @var String */
	public $projectId;

	/** @var core_asanamanager_AsanaCustomField[] */
	public $custom_fields;

}
?>