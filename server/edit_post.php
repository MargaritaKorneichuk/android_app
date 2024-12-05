<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$type = mysqli_real_escape_string($link,$_POST['type']);
$address = mysqli_real_escape_string($link,$_POST['address']);
$price = mysqli_real_escape_string($link,$_POST['cost']);
$description = mysqli_real_escape_string($link,$_POST['desc']);
$requirements = mysqli_real_escape_string($link,$_POST['req']);
$beds = mysqli_real_escape_string($link,$_POST['beds']);
$places = mysqli_real_escape_string($link,$_POST['places']);
$rent_type = mysqli_real_escape_string($link,$_POST['rent_type']);
$metres = mysqli_real_escape_string($link,$_POST['metres']);
$id=mysqli_real_escape_string($link,$_POST['id']);

$sql = "update `posts` set `type`='$type', `address`='$address', `price`='$price', `description`='$description', `requirements`='$requirements', `beds`='$beds', `places`='$places', `rent_type`='$rent_type', `metres`='$metres' where `id`='$id'";
if(mysqli_query($link, $sql)){
   echo json_encode(array(
        "status" => "success",
        "message" => "Данные обновлены"
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