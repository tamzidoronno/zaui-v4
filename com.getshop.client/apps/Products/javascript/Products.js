app.Products = {
    /**
     * This data is populated from the server 
     * trought the Products application.
     * 
     * Available when "drawChart" function
     * is invoked
     */
    googleChartsData: null,
    currentProductId: null,
    init: function () {
        $(document).on('click', '#gss_gotoproduct', app.Products.goToProduct);
        $(document).on('click', '.gss_product_saveuploadimage', app.Products.uploadBoxClick);
        $(document).on('change', '#gss_filterproducts', app.Products.filterProducts);
    },
    goToProduct: function() {
        navigate('?page=' + $(this).attr('pageid'));
        getshop.Settings.showPage();
    },
    navigateToProduct: function(button, response) {
        app.Products.gssinterface.editProduct(response);
    },
    filterProducts: function() {
       var data = {
           filterCriteria : $(this).val()
       }
       getshop.Settings.post(data, "setFilterProducts");
    },
    productImageRemoved: function (field) {
        $(field).parent().fadeOut(100);
    },
    drawChart: function (div) {

//        ['January', 2, 1, 3],
//        ['Feburary', 6, 0, 3],
//        ['March', 5, 12,5],
//        ['April', 7, 5,3],
//        ['May', 10, 0,0],
//        ['June', 15, 2,6],
//        ['July', 12, 5,1],
//        ['August', 25, 2,5],
//        ['September', 30, 0,8,],
//        ['Oktober', 33, 0,5,],
//        ['November', 25, 0,8,],
//        ['December', 12, 10,3]
    
        if (!google || !google.visualization || !google.visualization.DataTable) {
            return;
        }
        
        var january = [__w('January')];
        var februrary = [__w('February')];
        var march = [__w('March')];
        var april = [__w('April')];
        var may = [__w('May')];
        var june = [__w('June')];
        var july = [__w('July')];
        var august = [__w('August')];
        var september = [__w('September')];
        var october = [__w('October')];
        var november = [__w('November')];
        var december = [__w('December')];
        
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Month');
        
        var subDiv = $('<div/>');
        subDiv.addClass('gss_sub_statistics');
        var i = 1;
        for (var productId in app.Products.googleChartsData) {
            var productData = app.Products.googleChartsData[productId];
            var productName = productData[13];
            subDiv.append("<br/> " + i + ". " + productName);
            data.addColumn('number', i);
            january.push(productData[1]);
            februrary.push(productData[2]);
            march.push(productData[3]);
            april.push(productData[4]);
            may.push(productData[5]);
            june.push(productData[6]);
            july.push(productData[7]);
            august.push(productData[8]);
            september.push(productData[9]);
            october.push(productData[10]);
            november.push(productData[11]);
            december.push(productData[12]);
            i++;
        }
        
        
        var rows = [january, februrary, februrary, march, april, may, june, july, august, september, october, november];
        data.addRows(rows);

        console.log(rows);
        // Set chart options
        var options = {
            'title': 'Most sold products',
            'width': 1120,
            'height': 200
        };

        var chart = new google.visualization.LineChart(div);
        chart.draw(data, options);

        $(div).parent().append(subDiv);

    },
    uploadBoxClick: function () {
        app.Products.currentProductId = $(this).attr('productid');
        $('#getshop_select_files_link').remove();
        $('#your-files').remove();

        var selectDialogueLink = $('<a href="" id="getshop_select_files_link">Select files</a>');
        var fileSelector = $('<input type="file" id="your-files" multiple/>');

        selectDialogueLink.click(function () {
            fileSelector.click();
        });
        $('body').append(fileSelector);
        $('body').append(selectDialogueLink);

        var control = document.getElementById("your-files");
        var me = this;

        control.addEventListener("change", function () {
            fileSelector.remove();
            app.Products.imageSelected(control.files);
        });

        selectDialogueLink.click();
        selectDialogueLink.remove();
    },
    imageSelected: function (files) {
        var file = files[0];
        var fileName = file.name;

        var reader = new FileReader();

        reader.onload = function (event) {
            var dataUri = event.target.result;

            var data = {
                productId: app.Products.currentProductId,
                fileBase64: dataUri,
                fileName: fileName
            };

            var field = $('<div/>');
            field.attr('gss_value', data.productId);
            field.attr('gss_view', "gss_product_thumbnails");
            field.attr('gss_fragment', "images");
            getshop.Settings.post(data, "saveImage", field);
        };

        reader.onerror = function (event) {
            console.error("File could not be read! Code " + event.target.error.code);
        };

        reader.readAsDataURL(file);
    },
}


app.Products.gssinterface = {
    editProduct: function (productId) {
        getshop.Settings.showSettings();
        getshop.Settings.setApplicationId('e073a75a-87c9-4d92-a73a-bc54feb7317f', function () {
            var data = {
                gss_fragment: 'editproduct',
                gss_view: 'gss_productwork_area',
                gss_value: productId
            }

            getshop.Settings.post({}, "gs_show_fragment", data);
        });
    },
    
    showProductManagement: function() {
        getshop.Settings.showSettings();
        getshop.Settings.setApplicationId('e073a75a-87c9-4d92-a73a-bc54feb7317f');
    }
}


app.Products.init();