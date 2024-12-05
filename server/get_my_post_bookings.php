<?php
if(file_exists('./connect.php')) include 'connect.php';

// Escape user inputs for security
$post_id = mysqli_real_escape_string($link,$_GET['post_id']);
// Attempt insert query execution
$sql = "CREATE TEMPORARY TABLE `temp` SELECT firstName,lastName,id as `u_id` from `users`";
$posts=array();
if($result = mysqli_query($link, $sql)){
	$sql="CREATE TEMPORARY TABLE `temp2` SELECT type,address,id as `p_id` from `posts`";
	if($result=mysqli_query($link,$sql)){
		$sql="SELECT * from `bookings` join `temp` on `temp`.u_id=bookings.user_id join `temp2` on `temp2`.p_id=bookings.post_id where `post_id`='$post_id'";
		if($result = mysqli_query($link, $sql)){
    			if(mysqli_num_rows($result) > 0){
				while($row = mysqli_fetch_assoc($result)){
	    				array_push($posts,$row);
				}
    			}
		}
	}
}
echo json_encode($posts);
// Close connection
mysqli_close($link);
exit();
?>