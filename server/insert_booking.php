<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$user_id = mysqli_real_escape_string($link,$_POST['user_id']);
$post_id = mysqli_real_escape_string($link,$_POST['post_id']);
$end = mysqli_real_escape_string($link,$_POST['end']);
$start = mysqli_real_escape_string($link,$_POST['start']);
$cost = mysqli_real_escape_string($link,$_POST['cost']);

$sql = "INSERT INTO `bookings` VALUES (NULL, '$user_id','$post_id','$start','$end','$cost','0')";
if(mysqli_query($link, $sql)){
   echo json_encode(array(
        "status" => "success",
        "message" => "Бронирование создано"
   ));
} else{
   echo json_encode(array(
        "status" => "error",
        "message" => "Произошла ошибка"
   ));
}
 
// Close connection
mysqli_close($link);
exit();
?>