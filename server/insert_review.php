<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$user_id = mysqli_real_escape_string($link,$_POST['user_id']);
$post_id = mysqli_real_escape_string($link,$_POST['post_id']);
$mark = mysqli_real_escape_string($link,$_POST['mark']);
$text = mysqli_real_escape_string($link,$_POST['text']);
$booking_id=mysqli_real_escape_string($link,$_POST['booking_id']);

$sql = "INSERT INTO `reviews` VALUES (NULL, '$user_id','$post_id','$mark','$text')";
if(mysqli_query($link, $sql)){
   $sql="update `bookings` set `hasReview`='1' where `id`='$booking_id'";
   if(mysqli_query($link, $sql)){
   echo json_encode(array(
        "status" => "success",
        "message" => "Отзыв отправлен"
   ));
   }
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