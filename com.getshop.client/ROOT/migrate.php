<?php
//mysql_connect("sql.nbtxathcx.net", "boggi", "8fbAhUt7B5");
mysql_connect("84.210.60.45", "readonly", "8fbAhUt7B5");
mysql_select_db("nbtx_yourcontests");

$query = mysql_query("select * from users");
while($row = mysql_fetch_assoc($query)) {
    echo $row['username'];
}
?>
