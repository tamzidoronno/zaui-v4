<!--
To change this template, choose Tools | Templates
and open the template in the editor.
-->
<!DOCTYPE html>
<html>
    <head>
        <title>Auto Akademiet AS</title>

        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="apple-mobile-web-app-capable" content="yes" />
        
        <link rel="stylesheet" href="mobile/css/jquerymobile.css" />
        <script src="mobile/js/jquery.js"></script>
        <script src="mobile/js/jquerymobile.js"></script>
        
        <link rel="stylesheet" href="mobile/ical/css/master.css" type="text/css" media="screen" charset="utf-8" />
        <script src="mobile/ical/js/coda.js" type="text/javascript"></script>
        
        <link rel="stylesheet" href="mobile/css/theme.css" type="text/css" media="screen" charset="utf-8" />
    </head>
 
    <div data-role="page" id="home">
        <div data-role="header" data-theme="b">
            <h1>AutoAkademiet AS</h1>
            <div data-role="navbar">
                <ul>
                    <li><a href="#courses" data-transition="flip">Kurser</a></li>
                    <li><a href="#signup" data-transition="flip">Påmelding</a></li>
                </ul>
            </div>
        </div>
        
        <? 
            include 'front.php';
            include 'footer.html'; 
        ?>
    </div>
    
    <div data-role="page" id="courses">
        <? include 'header.html' ?>
        <? include 'courses.php' ?>
    </div>
    
    <div data-role="page" id="signup">
        <? include 'header.html' ?>
    </div>

    
    <?
    include 'coursepages.php';
    ?>
</html>