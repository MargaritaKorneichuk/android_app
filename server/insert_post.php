<?php
if(file_exists('./connect.php')) include 'connect.php';
 
// Escape user inputs for security
$type = mysqli_real_escape_string($link,$_POST['type']);
$address = mysqli_real_escape_string($link,$_POST['address']);
$price = mysqli_real_escape_string($link,$_POST['cost']);
$description = mysqli_real_escape_string($link,$_POST['desc']);
$requirements = mysqli_real_escape_string($link,$_POST['req']);
$beds = mysqli_real_escape_string($link,$_POST['beds']);
$places = mysqli_real_escape_string($link,$_POST['places']);
$rent_type = mysqli_real_escape_string($link,$_POST['rent_type']);
$vis = mysqli_real_escape_string($link,$_POST['vis']);
$host_id = mysqli_real_escape_string($link,$_POST['host_id']);
$metres = mysqli_real_escape_string($link,$_POST['metres']);
$images = mysqli_real_escape_string($link,$_POST['images']);
$rating="0.0";
if($vis==="true"){
$vis_num=1;
}else{
$vis_num=0;
}

$sql = "INSERT INTO `posts` VALUES (NULL, '$type','$address','$metres','$price','$description','$requirements','$beds','$places','$rent_type','$vis_num','$host_id','$images',"")";
if(mysqli_query($link, $sql)){
   echo json_encode(array(
        "status" => "success",
        "message" => "Объявление создано"
   ));
} else{
   echo json_encode(array(
        "status" => "error",
        "message" => "Произошла ошибка"
   ));
}
 
// Close connection
mysqli_close($link);
exit();
?>