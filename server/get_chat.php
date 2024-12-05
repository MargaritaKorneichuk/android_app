<?php
if(file_exists('./connect.php')) include 'connect.php';

// Escape user inputs for security
$user_id = mysqli_real_escape_string($link,$_GET['user_id']);
$receiver_id=mysqli_real_escape_string($link,$_GET['receiver_id']);
// Attempt insert query execution
$sql = "select * from `chat` where (`senderId`='$receiver_id' and `receiverId`='$user_id') or (`senderId`='$user_id' and `receiverId`='$receiver_id')";
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