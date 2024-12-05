<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Attempt insert query execution
$sql = "CREATE TEMPORARY TABLE `temp` SELECT sum(mark)/count(mark) as `rating`,post_id from `reviews` GROUP by post_id";
$posts=array();
if($result = mysqli_query($link, $sql)){
	$sql="SELECT * from `posts` left join `temp` on `temp`.post_id=posts.id where `posts`.visibility=1";
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