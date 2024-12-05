<?php
if(file_exists('./connect.php')) include 'connect.php';

// Escape user inputs for security
$user_id = mysqli_real_escape_string($link,$_GET['user_id']);
// Attempt insert query execution
$sql = "CREATE TEMPORARY TABLE `temp` SELECT * FROM `chat` WHERE `chat`.`senderId` = '$user_id' OR `chat`.`receiverId` = '$user_id'";
$result = mysqli_query($link, $sql);
$sql = "CREATE TEMPORARY TABLE `temp2` SELECT * from `temp`";
$result = mysqli_query($link, $sql);
$sql = "CREATE TEMPORARY TABLE `temp3` SELECT `temp`.`id`,`temp`.`message`,`temp`.`timestamp`,users.id as u_id,users.firstName,users.lastName,users.image FROM `temp` join `users` on `temp`.`senderId`=users.id UNION SELECT `temp2`.`id`,`temp2`.`message`,`temp2`.`timestamp`,users.id as u_id,users.firstName,users.lastName,users.image FROM `temp2` join `users` on `temp2`.`receiverId`=users.id order by id desc";
$result = mysqli_query($link, $sql);
$posts=array();
$sql="SELECT * from `temp3` where u_id!='$user_id' group by u_id ORDER BY id desc";
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
