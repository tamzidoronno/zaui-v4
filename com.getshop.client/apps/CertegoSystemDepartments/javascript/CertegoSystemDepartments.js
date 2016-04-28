app.CertegoSystemDepartments = {
    init: function() {
        $(document).on('change', '.gs_select_certego_system_department', function() {
            $('#gs_save_connection_group').click()
        });
    }
}

app.CertegoSystemDepartments.init();