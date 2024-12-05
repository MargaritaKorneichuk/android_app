<?php
if(file_exists('./connect.php')) include 'connect.php';

// Escape user inputs for security
$user_id = mysqli_real_escape_string($link,$_GET['user_id']);
// Attempt insert query execution
$sql = "CREATE TEMPORARY TABLE `temp` SELECT type,address,id as `p_id` from `posts`";
$posts=array();
if($result = mysqli_query($link, $sql)){
	$sql="SELECT * from `bookings`join `temp` on `temp`.p_id=bookings.post_id where bookings.user_id='$user_id'";
if($result = mysqli_query($link, $sql)){
    if(mysqli_num_rows($result) > 0){
	while($row = mysqli_fetch_assoc($result)){
	    array_push($posts,$row);
	}
    }
}
}
echo json_encode($posts);
// Close connection
mysqli_close($link);
exit();
?>