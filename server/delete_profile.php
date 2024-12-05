<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$user_id = mysqli_real_escape_string($link,$_POST['user_id']);
$sql = "delete from `users` where `id`='$user_id'";

if($result = mysqli_query($link, $sql)){
    echo json_encode(array(
    "status" => "success",
    "message" => "Аккаунт удален"
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