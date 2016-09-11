app.C3Projects = {
    init: function() {
        PubSub.subscribe('GS_TOGGLE_CHANGED', app.C3Projects.onOffChanged);
        $(document).on('click', '.gss_c3_changeCost', app.C3Projects.changeCost);
    },
    
    changeCost: function() {
        var year = $(this).attr('year');
        var currentCost = $(this).attr('currentValue');
        var data = prompt("Project cost for " + year, currentCost);
        if (data) {
            
            var parsedNumber = ""+parseInt(data, 10);
            if (data !== parsedNumber) {
                alert("Bruk kun heltall");
                return;
            }
            
            getshop.Settings.post({
                companyId : $(this).attr("companyId"),
                projectId : $(this).attr("projectId"),
                wpId : $(this).attr("wpId"),
                year: year,
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