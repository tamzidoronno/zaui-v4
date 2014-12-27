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
        data.addColumn('number', 'Badges');
        data.addRows([
            ['January', 2, 1],
            ['Feburary', 6, 0],
            ['March', 5, 12],
            ['April', 7, 5],
            ['May', 10, 0],
            ['June', 15, 2],
            ['July', 12, 5],
            ['August', 25, 2],
            ['September', 30, 0],
            ['Oktober', 33, 0],
            ['November', 25, 0],
            ['December', 12, 10]
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