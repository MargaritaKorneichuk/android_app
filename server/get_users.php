<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Attempt insert query execution
$user_id=mysqli_real_escape_string($link,$_GET['user_id']);
if($user_id==='0'){
	$sql = "SELECT * from `users`";
}else{
$sql="select id,firstName,lastName,image from `users` where id='$user_id'";
}
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