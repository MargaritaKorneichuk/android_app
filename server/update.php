<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$user_id = mysqli_real_escape_string($link,$_POST['user_id']);
$first_name = mysqli_real_escape_string($link,$_POST['fname']);
$last_name = mysqli_real_escape_string($link,$_POST['lname']);
$email = mysqli_real_escape_string($link,$_POST['email']);
$new_pass = mysqli_real_escape_string($link,$_POST['new_pass']);

if($new_pass===""){
    $sql = "update `users` set `firstName`='$first_name', `lastName`='$last_name', `email`='$email' where `id`='$user_id'";
}else{
    $sql = "update `users` set `firstName`='$first_name', `lastName`='$last_name', `email`='$email', `password`='$new_pass' where `id`='$user_id'";
}

if($result = mysqli_query($link, $sql)){
    echo json_encode(array(
    "status" => "success",
    "message" => "Данные обновлены"
    ));
    mysqli_close($link);
    exit();
} else{
    echo json_encode(array(
    "status" => "error",
    "message" => "Ошибка при обновлении"
    ));
    mysqli_close($link);
    exit();
}

// Close connection
mysqli_close($link);
exit();
?>