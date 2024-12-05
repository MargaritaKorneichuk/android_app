<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$first_name = mysqli_real_escape_string($link,$_POST['fname']);
$last_name = mysqli_real_escape_string($link,$_POST['lname']);
$email = mysqli_real_escape_string($link,$_POST['email']);
$pass = mysqli_real_escape_string($link,$_POST['pass']);
$image=mysqli_real_escape_string($link,$_POST['image']);

$sql = "SELECT * FROM `users` WHERE `email` = '$email'";
$result = mysqli_query($link, $sql);
if (mysqli_num_rows($result) > 0)
{
	echo json_encode(array(
            "status" => "error",
            "message" => "User already exists"
        ));
	mysqli_close($link);
        exit();
    }
$password = password_hash($pass, PASSWORD_DEFAULT);
// Attempt insert query execution
$sql = "INSERT INTO `users` VALUES (null, '$email','$pass','$first_name', '$last_name','$image')";
if(mysqli_query($link, $sql)){
   echo json_encode(array(
        "status" => "success",
        "message" => "Account has been created",
        "user_id" => mysqli_insert_id($link) . ""
   ));
} else{
   echo json_encode(array(
        "status" => "error",
        "message" => "Account has not been created"
   ));
}
 
// Close connection
mysqli_close($link);
exit();
?>