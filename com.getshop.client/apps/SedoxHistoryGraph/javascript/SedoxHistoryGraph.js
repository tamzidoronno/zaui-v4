app.SedoxHistoryGraph = {
    drawItAll : function() {
        if (typeof(google) === "undefined" || typeof(google.visualization) === "undefined" || typeof(google.visualization.DataTable) === "undefined") {
            return;
        }

        var dataArray = [["", "", { role: "style" } ]];
        sedoxHistoryData.reverse();
        
        for (var i in sedoxHistoryData) {
            dataArray.push(sedoxHistoryData[i]);
        }
        
        var data = google.visualization.arrayToDataTable(dataArray);

        var options = {
            'title': '',
            'width': '100%',
            'height': 300
        };

        var div = document.getElementById("sedox_graph_history");
        var chart = new google.visualization.ColumnChart(div);
        chart.draw(data, options);

        $('.sedox_chart_the_loader').hide();
    }
}

        