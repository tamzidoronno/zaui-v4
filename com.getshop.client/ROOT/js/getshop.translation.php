<?php
$fac = IocContainer::getFactorySingelton();
?>
<script>
thundashop.translation = {
success_saved:"<?php echo $fac->__("saved_to_db", 'common');?>",
not_available : "<?php echo $fac->__("option_not_available"); ?>",
disabled_option : "<?php echo $fac->__("disabled_option"); ?>",
settings_saved : "<?php echo $fac->__("settings_saved", 'common'); ?>",
save : "<?php echo $fac->__("save", 'common'); ?>",
confirm_delete_application : "<?php echo $fac->__("remove_app_warning"); ?>",
invalid_input : "<?php echo $fac->__("invalid_input", "common"); ?>",
invalid_size : "<?php echo $fac->__("size_only_number"); ?>",
updated : "<?php echo $fac->__("updated", 'common'); ?>",
settings_saved : "<?php echo $fac->__("settings_saved", 'common'); ?>",
invalid_delivery : "<?php echo $fac->__("invalid_delivery"); ?>",
select_delivery : "<?php echo $fac->__("select_delivery"); ?>",
invalid_operation : "<?php echo $fac->__("invalid_operation"); ?>",
cannot_active_option : "<?php echo $fac->__("withdraw_error"); ?>",
cannot_add_application : "<?php echo $fac->__("add_app_error"); ?>"
}
</script>