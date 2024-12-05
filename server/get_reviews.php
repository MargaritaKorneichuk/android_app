<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$post_id = mysqli_real_escape_string($link,$_GET['post_id']);
// Attempt insert query execution
$sql = "select `reviews`.`id`,`reviews`.`user_id`,`reviews`.`post_id`,`reviews`.`mark`,`reviews`.`text`,`users`.`firstName`,`users`.`lastName` from `reviews` join `users` on `users`.id=user_id where `post_id`='$post_id'";
$posts=array();
if($result = mysqli_query($link, $sql)){
    if(mysqli_num_rows($result) > 0){
	while($row = mysqli_fetch_assoc($result)){
	    array_push($posts,$row);
	}
        
    }
}
echo json_encode($posts);
// Close connection
mysqli_close($link);
exit();
?>