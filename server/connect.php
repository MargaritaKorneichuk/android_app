<?php error_reporting(E_ALL);
ini_set('display_errors', 1);
$link = mysqli_connect("192.168.248.150", "root", "", "cozyCorner");
 
// Check connection
if($link === false){
    die("ERROR: Could not connect. " . mysqli_connect_error());
}
?>