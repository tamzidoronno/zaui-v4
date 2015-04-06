
<form action='' method='POST'>
    <div style="padding: 20px;">
        Skriv inn eposten i feltet her og trykk enter -> <input type='text' name="email" placeholder="epost@ogsend.no">
    </div>
</form>
<?
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$api = $factory->getApi();
?>

<? $path = "http://www.wh.no/scripts/whmal/"; 

 ob_start();
?>

<table width='700' cellspacing='0' cellpadding='0'>
    <tr style='background-color:#e1d3c3' align='center'>
        <td style='padding: 20px;'><img src='<? echo $path . "logo.png"; ?>'></td>
    </tr>
    <tr style='background-color:#e1d3c3' align='center'>
        <td><img src='<? echo $path . "mainimg.png"; ?>'></td>
    </tr>
    
    <tr style='background-color:#000000;' align='center'>
        <td>
            <table style='color:#e1d3c3;'>
                <tr>
                    <td style='font-size:20px; padding-top: 40px; padding-bottom: 40px;'>
                        VELKOMMEN TIL WILHELMSEN HOUSE APARTMENTS    
                    </td>
                </tr>
            </table>
            <table style='color:#e1d3c3;'>
                <tr>
                    <td width='50%' valign='top'>
                        <ul>
                            <li>49 rom for døgn- og langtidsleie i Tønsberg</li>
                            <li>Gangavstand til togstasjon, busstasjon og sentrum</li>
                            <li>Leie av konferanserom</li>
                            <li>Komfortable rom</li>
                            <li>Felles rom med atmosfære</li>
                            <li>Parkering</li>
                            <li>Helse, trening og velvære</li>
                            <li>Fleksible prismodeller</li>
                            <li>Café med matservering</li>
                        </ul>
                        <br>
                        <div  style='padding-left: 30px;' >
                            <a href='https://www.facebook.com/wilhelmsenhouse' style='color:#e1d3c3; text-decoration: none;'>Følg oss på Facebook</a>
                        </div>
                    </td>
                    <td valign='top' style='padding-left: 80px; padding-right: 50px; font-size: 12px;'>
                        
                            <img src='<? echo $path . "anette.png"; ?>'>
                       
                        <br>
                        Anette Aale<br>
                        Daglig leder / vertinne<br>
                        <br>
                        Telefon 97 13 50 00<br>
                        E-post anette@wh.no<br>
                        <br>
                        <div style='border-bottom: solid 1px #e1d3c3'></div>
                        <br>
                    </td>
                </tr>
            </table>
            
        </td>
    </tr>
    <tr style='background-color:#000000;'>
        <td>
            <table style='color:#e1d3c3;'>
                <tr>
                    <td style='padding-left: 30px;' width='350'><b>Bli bedriftskunde - ta kontakt med oss i dag.</td>
                    <td style='padding-left: 50px;'>NÅR DU TRENGER ROM</td>
                </tr>
            </table>
            <br><br><br>
        </td>
    </tr>
</table>


<br><br>
<table width='700' cellspacing='0' cellpadding='0'>
    <tr style='background-color:#e1d3c3' align='center'>
        <td colspan='2' style='padding: 20px;'><img src='<? echo $path . "logo.png"; ?>'></td>
    </tr>
    <tr style='background-color:#e1d3c3' align='center'>
        <td colspan='2'><img src='<? echo $path . "mainimg2.png"; ?>'></td>
    </tr>
    <tr style='background-color:#000000; color:#e1d3c3'>
        <td width='30%'>&nbsp;</td>
        <td width='70%'>
            <br>
            <br>
            <div style='font-size: 20px;'>NÅR DU TRENGER ROM</div><br>
            <br>
            - for å bo<br>
            <br>
            - i hjertet av Tønsberg<br>
            <br>
            - til dine ansatte og gjester<br>
            <br>
            - for avslapping og velvære<br>
            <br>
            - for deg selv<br>
            <br>
            - for å møte andre<br>
            <br>
            for den gode samtalen<br>
            <br>
            - for hygge
            <bR>
            <bR>
            <bR>
            <bR>
            <bR>
        </td>
    </tr>
</table>

<?
$content = ob_get_contents();
ob_clean();

$content = mb_convert_encoding ($content,"HTML-ENTITIES","UTF-8");

echo $content;

if(isset($_POST['email'])) {
    $to = $_POST['email'];
    $api->getMessageManager()->sendMail($to, "", "VELKOMMEN TIL WILHELMSEN HOUSE APARTMENTS", $content, "post@wh.no", "Anette Aale");
}

?>

