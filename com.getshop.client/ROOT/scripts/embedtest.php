<body>
    <head>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
        <script src="https://www.getshop.com/js/getshop.bookingembed.js"></script>
        <script src="https://www.getshop.com/scripts/booking/bookingscripts.php"></script>
        <link rel="stylesheet" href="https://www.getshop.com/scripts/booking/bookingstyles.php">
        <link href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
    </head>

    <div id='bookingprocess'></div>
    <script>
    $( "#bookingprocess" ).getshopbooking({
        "endpoint" : "https://www.getshop.com"
    });
    </script>
</body>
