<?php
$dbhost = '45.79.128.82';
$dbuser = 'root';
$dbpass = 'Stop_It';

$conn = mysql_connect($dbhost, $dbuser, $dbpass) or die('Error connecting to mysql');

$dbname = 'petstore';
mysql_select_db($dbname);
?>

