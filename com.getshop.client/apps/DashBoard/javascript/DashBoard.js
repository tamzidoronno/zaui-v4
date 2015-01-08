app.DashBoard = {
    init: function() {
        $(document).on('mouseenter', '.gss_statistic_tab_button', app.DashBoard.showYearsMenu);
        $(document).on('mouseleave', '.gss_statistic_tab_button', app.DashBoard.hideYearsMenu);
    },
    showYearsMenu: function() {
        $(this).find('.gss_statistic_years').show();
    },
    hideYearsMenu: function() {
        $(this).find('.gss_statistic_years').hide();
    },
    drawChart: function (div) {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Month');
        data.addColumn('number', 'Logins');
        data.addRows([
            ['January', app.DashBoard.logins[0]],
            ['Feburary', app.DashBoard.logins[1]],
            ['March', app.DashBoard.logins[2]],
            ['April', app.DashBoard.logins[3]],
            ['May', app.DashBoard.logins[4]],
            ['June', app.DashBoard.logins[5]],
            ['July', app.DashBoard.logins[6]],
            ['August', app.DashBoard.logins[7]],
            ['September', app.DashBoard.logins[8]],
            ['Oktober', app.DashBoard.logins[9]],
            ['November', app.DashBoard.logins[10]],
            ['December', app.DashBoard.logins[11]]
        ]);

        // Set chart options
        var options = {
            'title': 'Your sales',
            'width': 1120,
            'height': 200
        };

        var chart = new google.visualization.LineChart(div);
        chart.draw(data, options);
        
    }
};

app.DashBoard.init();