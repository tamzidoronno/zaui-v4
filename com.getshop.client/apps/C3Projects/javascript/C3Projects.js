app.C3Projects = {
    init: function() {
        PubSub.subscribe('GS_TOGGLE_CHANGED', app.C3Projects.onOffChanged);
        $(document).on('click', '.gss_c3_changeCost', app.C3Projects.changeCost);
        $(document).on('click', '.gss_c3_removeContract', app.C3Projects.removeContract);
    },
    
    removeContract: function() {
        var confirmed = confirm("Are you sure you want to remove this contract?");
        
        if (confirmed) {
            getshop.Settings.post({
                contractId : $(this).attr('contractId'),
                companyId : $(this).attr("companyId"),
                projectId : $(this).attr("projectId"),
                wpId : $(this).attr("wpId"),
                value2: $(this).attr("companyId"),
                gss_view : "c3projects_connected_companies",
                gss_fragment : "connectedProjects"
            }, "removeContract", {gss_overrideapp : '74d458f4-3203-4488-813d-65741a0213c9'});
        }
            
    },
    
    changeCost: function() {
        var currentCost = $(this).attr('currentValue');
        var startDate = prompt("Start date (dd.mm.yyyy)", $(this).attr('startDate'));
        var endDate = prompt("End date (dd.mm.yyyy)", $(this).attr('endDate'));
        var data = prompt("Contract value" , currentCost);
        if (data && startDate && endDate) {
            
            var parsedNumber = ""+parseInt(data, 10);
            if (data !== parsedNumber) {
                alert("Bruk kun heltall");
                return;
            }
            
            getshop.Settings.post({
                contractId : $(this).attr('contractId'),
                companyId : $(this).attr("companyId"),
                projectId : $(this).attr("projectId"),
                wpId : $(this).attr("wpId"),
                startDate: startDate,
                endDate: endDate,
                value2: $(this).attr("companyId"),
                gss_view : "c3projects_connected_companies",
                price: data,
                gss_fragment : "connectedProjects"
            }, "updateProjectCost", {gss_overrideapp : '74d458f4-3203-4488-813d-65741a0213c9'});
            
        }
    },
    
    onOffChanged: function(event, data) {
        var app = "<div class='app' gss_overrideapp='74d458f4-3203-4488-813d-65741a0213c9'><div class='field' gss_success_method='app.C3Projects.null'></div></div>";
        if ($(data.field).attr('c3projectcompany') === "true" && data.toggledByUser) {
            getshop.Settings.post({
                companyId : $(data.field).attr("companyId"),
                projectId : $(data.field).attr("projectId"),
                wpId : $(data.field).attr("wpId"),
                val: data.val,
                value2: $(data.field).attr("companyId"),
                gss_view : "c3projects_connected_companies",
                gss_fragment : "connectedProjects"
            }, "updateWorkPackageProjectCompany", {gss_overrideapp : '74d458f4-3203-4488-813d-65741a0213c9'});
        }
    }
}

app.C3Projects.init();