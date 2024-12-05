<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$user_id = mysqli_real_escape_string($link,$_POST['user_id']);
$receiver_id=mysqli_real_escape_string($link,$_POST['receiver_id']);
$sql = "delete from `chat` where (`receiverId`='$receiver_id' and `senderId`='$user_id')or(`receiverId`='$user_id' and `senderId`='$receiver_id')";

if($result = mysqli_query($link, $sql)){
    echo json_encode(array(
    "status" => "success",
    "message" => "Чат удален"
    ));
    mysqli_close($link);
    exit();
} else{
    echo json_encode(array(
    "status" => "error",
    "message" => "Ошибка при удалении"
    ));
    mysqli_close($link);
    exit();
}

// Close connection
mysqli_close($link);
exit();
?>