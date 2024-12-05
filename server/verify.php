<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$email = mysqli_real_escape_string($link,$_POST['email']);
$pass = mysqli_real_escape_string($link,$_POST['pass']);
$password=password_hash($pass, PASSWORD_DEFAULT);
// Attempt insert query execution
$sql = "select * from `users` where `email`='$email'";

if($result = mysqli_query($link, $sql)){
    if(mysqli_num_rows($result) > 0){
	while($row = mysqli_fetch_assoc($result)){
	    if($pass===$row['password']){
		echo json_encode(array(
        	"status" => "success",
        	"message" => "Login successful",
		"user_fname" => $row['firstName'],
		"user_lname" => $row['lastName'],
		"user_id" => $row['id'],
		"image"=>$row['image']
   		));
		mysqli_close($link);
		exit();
	    } else{
		echo json_encode(array(
        	"status" => "error",
        	"message" => "Неверный пароль"
   		));
		mysqli_close($link);
		exit();
	    }
	}
        
    } 
    else{
        echo json_encode(array(
        "status" => "error",
        "message" => "Нет такого пользователя"
   	));
	mysqli_close($link);
	exit();
    }
} else{
    	echo json_encode(array(
        "status" => "error",
        "message" => "SQL does not working"
   	));
	mysqli_close($link);
	exit();
}

// Close connection
mysqli_close($link);
exit();
?>