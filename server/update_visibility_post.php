<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$id = mysqli_real_escape_string($link,$_POST['id']);
$vis = mysqli_real_escape_string($link,$_POST['vis']);

$sql = "update `posts` set `visibility`='$vis' where `id`='$id'";

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