<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$sender_id = mysqli_real_escape_string($link,$_POST['senderId']);
$receiver_id = mysqli_real_escape_string($link,$_POST['receiverId']);
$text = mysqli_real_escape_string($link,$_POST['message']);
$timestamp=mysqli_real_escape_string($link,$_POST['timestamp']);

$sql = "INSERT INTO `chat` VALUES (NULL, '$sender_id','$receiver_id','$text','$timestamp')";
if(mysqli_query($link, $sql)){
   echo json_encode(array(
        "status" => "success",
        "message" => "Сообщение отправлено"
   ));
   }
else{
   echo json_encode(array(
        "status" => "error",
        "message" => "Произошла ошибка"
   ));
}
 
// Close connection
mysqli_close($link);
exit();
?>